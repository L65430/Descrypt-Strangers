package com.example.administrator.finalprocject.Fragment;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.os.Message;

import android.support.v4.app.NotificationCompatSideChannelService;
import android.support.v4.view.GravityCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Gradient;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Text;
import com.baidu.mapapi.model.LatLng;
import com.example.administrator.finalprocject.Activity.FriendAddActivity;
import com.example.administrator.finalprocject.Activity.MainActivity;
import com.example.administrator.finalprocject.Activity.StrangerGameActivity;
import com.example.administrator.finalprocject.Activity.StrangerListActivity;
import com.example.administrator.finalprocject.Info.ClientManger;
import com.example.administrator.finalprocject.Info.StrangerBean;
import com.example.administrator.finalprocject.R;
import com.example.administrator.finalprocject.Utils.MyApplication;
import com.example.administrator.finalprocject.Utils.ShakeListener;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Created by Administrator on 2016/9/22 0022.
 */

//查找陌生人的界面
public class MainPart2 extends Fragment implements ShakeListener.OnShakeListener{
    View view;
    MapView mapView=null;
    BaiduMap mBaiduMap;
    boolean isFirstLoc;
    BitmapDescriptor mCurrentMarker;
    private MyLocationConfiguration.LocationMode mCurrentMode;
    private Marker mMarkerA;
    BitmapDescriptor bdA;
    BitmapDescriptor userX;
    MyLocationData locData;
    MyOnMarkerClickListener myOnMarkerClickListener;
    private List<BitmapDescriptor> coll;
    TextView tv;

    Button bt_str_list;
    ViewGroup anim;
    ShakeListener shakeListener;

    boolean isshaked;

    ImageView star0;
    ImageView star1;
    boolean isRy=true;
    int splashDelay=2000;

    ImageView createGroup;
    RelativeLayout msg;
    ImageView btn_del;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        System.out.println("开始MainPart2的初始化");
        view=inflater.inflate(R.layout.fragment_part2,container,false);
        System.out.println("开始初始化MapView");
        mapView=(MapView)view.findViewById(R.id.fra_p2_bmapView);//这里放的是百度地图的显示情况
        shakeListener=new ShakeListener(getActivity());
        shakeListener.setOnshakeListener(this);
        anim=(ViewGroup)view.findViewById(R.id.fra_p2_layout_anim);
        System.out.println("完成初始化MapView");
        mBaiduMap=mapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);//开启定位图层
        MapStatusUpdate msu= MapStatusUpdateFactory.zoomTo(14.0f);//设置地图放大比例
        mBaiduMap.setMapStatus(msu);
        isFirstLoc=true;
        mCurrentMarker=null;//开始的时候没有标记
        mCurrentMode= MyLocationConfiguration.LocationMode.NORMAL;
        bdA= BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);//标记点
        System.out.println("mLocClient启动");
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(mCurrentMode,true,null));//设置定位图层的配置
        myOnMarkerClickListener=new MyOnMarkerClickListener();
        mBaiduMap.setOnMarkerClickListener(myOnMarkerClickListener);

        //列表查看陌生人
        bt_str_list=(Button)view.findViewById(R.id.bt_str_list);
        bt_str_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), StrangerListActivity.class);
                startActivity(intent);
            }
        });

        coll=new ArrayList<BitmapDescriptor>();
        star0=(ImageView)view.findViewById(R.id.fra_p2_layout_start0);
        star1=(ImageView)view.findViewById(R.id.fra_p2_layout_start1);
        new Thread(new Runnable() {
            MyHandler myHandler=new MyHandler();
            @Override
            public void run() {
                while(isRy)
                {
                    int i=(int)(Math.random()*1000)+2000;
                    try
                    {
                        Thread.sleep(i);
                    }catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    Message msg=new Message();
                    msg.arg1=0;
                    myHandler.sendMessage(msg);
                }
            }
        }).start();

        new Thread(new Runnable() {
            MyHandler myHandler=new MyHandler();
            @Override
            public void run() {
                while(isRy)
                {
                    int i=(int)(Math.random()*1000)+3000;
                    try
                    {
                        Thread.sleep(i);
                    }catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    Message msg=new Message();
                    msg.arg1=1;
                    myHandler.sendMessage(msg);
                }

            }
        }).start();

        //设置群组的方法，就是可以把陌生人拉入一个讨论组，互相沟通
        createGroup=(ImageView)view.findViewById(R.id.fra_p2_createGroup);
        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 //Intent intent=new Intent(getActivity(),CreateGroupActivity.class);
                 //startActivity(intent);//把陌生人拉到一个群里进行聊天的一个设计
            }
        });

         msg=(RelativeLayout)view.findViewById(R.id.fra_p2_msg);
        btn_del=(ImageView)view.findViewById(R.id.fra_p2_del_msg);
        btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msg.setVisibility(View.GONE);
            }
        });

        isshaked=true;//已经摇晃过了
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        // 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        shakeListener.start();
        mapView.onResume();
    }


    @Override
    public void onDestroy() {
       super.onDestroy();
        //关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mapView.onDestroy();
        mapView=null;
        int n=coll.size();
        for(int i=0;i<n;i++)
        {
            coll.get(i).recycle();//这表示手动回收bitmap的空间
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        shakeListener.stop();
        mapView.onPause();
    }

    public void setMyLoc(BDLocation location)
    {
        if(location==null)
        {
            return;
        }
        MyLocationData locData=new MyLocationData.Builder()
                .accuracy(location.getRadius()).direction(100)
                .latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();
        mBaiduMap.setMyLocationData(locData);
        LatLng ll=new LatLng(location.getLatitude(),location.getLongitude());//构造经纬度
        LatLng stranger=new LatLng(location.getLatitude()+0.002,location.getLongitude()+0.02);//寻找附近的范围
        //可以考虑通关之后扩大搜索范围
        MapStatusUpdate u=MapStatusUpdateFactory.newLatLng(ll);//构造当前的位置
        System.out.println("执行位置更改动画");
        mBaiduMap.animateMapStatus(u);
    }

    //在地图上设置陌生人
    public void setStrangerLoc(List<StrangerBean>list)
    {
        mBaiduMap.clear();
        for(StrangerBean bean:list)
        {
            if(!MainActivity.mp1.isFriendExists(bean.strangerId))
            {
                Log.i("--","新增陌生人");
                LatLng ll=new LatLng(bean.Latitude,bean.Longitude);//新增经纬度
                TextView tv;
                tv=new TextView(getActivity());
                tv.setBackgroundResource(R.drawable.nickname_box);
                tv.setTextSize(20);
                tv.setIncludeFontPadding(false);
                tv.setText(bean.strangerName);
                tv.setTextColor(getResources().getColor(R.color.white));
                tv.setGravity(Gravity.CENTER);//设置文字居中
                userX=BitmapDescriptorFactory.fromView(tv);
                tv.destroyDrawingCache();
                tv=null;
                coll.add(userX);
                OverlayOptions oo=new MarkerOptions().position(ll).icon(userX)
                        .zIndex(9).draggable(true);//设置覆盖物
                mMarkerA=(Marker)(mBaiduMap.addOverlay(oo));
                Bundle b=new Bundle();
                b.putString("strangerName",bean.strangerName);
                b.putString("strangerId",bean.strangerId);
                mMarkerA.setExtraInfo(b);
            }
        }
    }

    private class MyOnMarkerClickListener implements BaiduMap.OnMarkerClickListener {
        @Override
        public boolean onMarkerClick(Marker marker) {
            Bundle b = marker.getExtraInfo();
            final String strangerId = b.getString("strangerId");
            //点击陌生人标记的时候要显示的消息
            new AlertDialog.Builder(getActivity()).setTitle("通知").setMessage("添加好友")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getActivity(), FriendAddActivity.class);//进入添加好友页面
                            startActivity(intent);
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
            {
                return true;
            }
        }
    }

    @Override
    public void onShake()
    {
        if(isshaked)
        {
            isshaked=false;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try
                    {
                        Thread.sleep(1000);
                    }catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    isshaked=true;
                }
            }).start();

            if(!isHidden())
            {
                isRy=false;
                anim.setVisibility(View.GONE);
                mapView.setVisibility(View.VISIBLE);//最终显示结果

                setMyLoc(((MyApplication) getActivity().getApplication())
                        .getMyLocation());
                if(ClientManger.isShareLoc)
                {
                    MainActivity.myBinder.sendLocation();//
                    MainActivity.myBinder.getStrangerListOneKm();//这样的话就可以获得一公里内的消息
                }else
                {
                    Dialog dialog=null;
                    AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                    builder.setTitle("提示").setMessage("请在设置中设置发送位置");
                    builder.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog=builder.create();
                    dialog.show();
                }
            }
        }
    }

    class MyHandler extends android.os.Handler {//注意handler

        Animation tanslateAnimation0;
        Animation tanslateAnimation1;

        public MyHandler() {
            tanslateAnimation0 = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 2f, Animation.RELATIVE_TO_SELF,
                    -1f, Animation.RELATIVE_TO_SELF, -1f,
                    Animation.RELATIVE_TO_SELF, 2f);
            tanslateAnimation0.setInterpolator(new AccelerateInterpolator());
            tanslateAnimation0.setDuration(splashDelay);
            tanslateAnimation0.setFillAfter(true);
            MyAnimationListener myAnimationListener0 = new MyAnimationListener();
            myAnimationListener0.setView(star0);
            tanslateAnimation0.setAnimationListener(myAnimationListener0);

            tanslateAnimation1 = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 2f, Animation.RELATIVE_TO_SELF,
                    -1f, Animation.RELATIVE_TO_SELF, -1f,
                    Animation.RELATIVE_TO_SELF, 3f);
            tanslateAnimation1.setInterpolator(new AccelerateInterpolator());
            tanslateAnimation1.setDuration(splashDelay);
            tanslateAnimation1.setFillAfter(true);
            MyAnimationListener myAnimationListener1 = new MyAnimationListener();
            myAnimationListener1.setView(star1);
            tanslateAnimation1.setAnimationListener(myAnimationListener1);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.arg1 == 0) {
                star0.startAnimation(tanslateAnimation0);
            } else {
                star1.startAnimation(tanslateAnimation1);
            }
        }
    }

    private class MyAnimationListener implements Animation.AnimationListener
    {
        ImageView view;
        public void setView(ImageView view)
        {
         this.view=view;
        }

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
           Animation alphaAnimation=new AlphaAnimation(1f,0f);
           alphaAnimation.setInterpolator(new AccelerateInterpolator());
            alphaAnimation.setDuration(splashDelay);
            alphaAnimation.setFillAfter(true);
            view.startAnimation(alphaAnimation);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

}