package com.example.administrator.finalprocject.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
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
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.example.administrator.finalprocject.Activity.GameCenter;
import com.example.administrator.finalprocject.Activity.MainActivity;
import com.example.administrator.finalprocject.Activity.RobotActivity;
import com.example.administrator.finalprocject.Activity.StrangerGameActivity;
import com.example.administrator.finalprocject.Activity.StrangerListActivity;
import com.example.administrator.finalprocject.Info.ClientManger;
import com.example.administrator.finalprocject.Info.StrangerBean;
import com.example.administrator.finalprocject.R;
import com.example.administrator.finalprocject.Utils.MyApplication;
import com.example.administrator.finalprocject.Utils.ShakeListener;

import java.util.ArrayList;
import java.util.List;


public class MainPart3 extends android.app.Fragment {
    View view;
    Button game;
    Button robot;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("开始MainPart3的初始化");
        view = inflater.inflate(R.layout.fragment_main_part3, container, false);
        game=(Button)view.findViewById(R.id.game);
        game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),GameCenter.class);
                startActivity(intent);
            }
        });
        robot=(Button)view.findViewById(R.id.robot);
        robot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),RobotActivity.class);
                startActivity(intent);
            }
        });
        System.out.println("开始初始化MapView");
        return view;
    }
}




