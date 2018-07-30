package com.example.administrator.finalprocject.Utils;

import android.app.Application;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MyLocationData;
import com.example.administrator.finalprocject.AllMessage.ClientHandleMessage;
import com.example.administrator.finalprocject.Info.ClientManger;

/**
 * Created by Administrator on 2016/10/1 0001.
 */
//application是程序开始就自动运行的
public class MyApplication extends Application {
    private static MyApplication myApplication;
    SharedPreferenceUtil mSpUtil;

    //和定位有关的
    public LocationClient mLocationClient;
    public MyLocationListener mMyLocationListener;
    BDLocation mlocation;//BDLocation获取定位地址

    public synchronized static MyApplication getInstance()
    {
        return myApplication;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        myApplication=this;
        initMap();
        SDKInitializer.initialize(this);//要先加这一句话，用来解析sdk
        initData();
    }

    private void initMap()
    {
        mLocationClient=new LocationClient(this.getApplicationContext());
        mMyLocationListener=new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);
        LocationClientOption option=new LocationClientOption();
        option.setOpenGps(true);//打开GPS
        option.setCoorType("bd09ll");
        option.setScanSpan(5*60*1000);//设置定位时间间隔
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }

    private void initData()
    {
        mSpUtil=new SharedPreferenceUtil(this);
    }

    public synchronized SharedPreferenceUtil getSpUtil()
    {
        if(mSpUtil==null)
        {
            mSpUtil=new SharedPreferenceUtil(this);
        }
        return mSpUtil;
    }

    public synchronized BDLocation getMyLocation()
    {
        return mlocation;
    }

    //5分钟更新一次位置
    public class MyLocationListener implements BDLocationListener
    {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            mlocation=bdLocation;
            ClientManger.province=mlocation.getProvince();
            ClientManger.Longitude=mlocation.getLongitude();
            ClientManger.Latitude = mlocation.getLatitude();
            ClientManger.myLocation = mlocation.getAddrStr();

        }
    }
}
