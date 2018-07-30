package com.example.administrator.finalprocject.Utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by Administrator on 2016/9/22 0022.
 */
public class ShakeListener implements SensorEventListener {
    String TAG = "ShakeListener";
    //速度,当速度达到这个值之后起作用
    private static final int SPEED_SHRESHOLD = 3000;
    //两次检测的时间间隔
    private static final int UPTATE_INTERVAL_TIME = 70;
    //传感器管理器
    private SensorManager sensorManager;
    //传感器
    private Sensor sensor;
    //重力感应监听器
    private OnShakeListener onshakeListener;
    //上下文
    private Context context;
    //手机上一个位置的重力感应坐标
    private float lastX;
    private float lastY;
    private float lastZ;
    //上次检测时间
    private long lastUpdateTime;

    //构造器
    public ShakeListener(Context c) {
        context = c;
    }

    //开始
    public void start() {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);//加速度感应器
        }
        if (sensor != null) {
            sensorManager.registerListener(this, sensor,
                    SensorManager.SENSOR_DELAY_GAME);
        }
    }

    //停止检测
    public void stop() {
        sensorManager.unregisterListener(this);
    }

    //设置重力感应监听器
    public void setOnshakeListener(OnShakeListener listener)
    {
        onshakeListener=listener;
    }



    //重力感应器感应获得变化数据
    @Override
    public void onSensorChanged(SensorEvent event) {
        //现在检测时间
        long currentUpdateTime=System.currentTimeMillis();
        //两次检测的时间间隔
        long timeInterval=currentUpdateTime-lastUpdateTime;
        //判断是否达到了检测时间间隔
        if(timeInterval<UPTATE_INTERVAL_TIME)
            return;
        lastUpdateTime=currentUpdateTime;
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        // 获得x,y,z的变化值
        float deltaX = x - lastX;
        float deltaY = y - lastY;
        float deltaZ = z - lastZ;
        // 将现在的坐标变成last坐标
        lastX = x;
        lastY = y;
        lastZ = z;
        double speed = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ
                * deltaZ)
                / timeInterval * 10000;
//		Log.v(TAG, "===========log===================");
        // 达到速度阀值，发出提示
        if (speed >= SPEED_SHRESHOLD) {
            onshakeListener.onShake();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    //摇晃监听接口
    public interface OnShakeListener
    {
        public void onShake();
    }
}
