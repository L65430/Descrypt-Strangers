package com.example.administrator.finalprocject.Info;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.widget.Button;

import com.example.administrator.finalprocject.Fragment.RegStepOne;
import com.example.administrator.finalprocject.Fragment.RegStepThree;
import com.example.administrator.finalprocject.Fragment.RegStepTwo;

/**
 * Created by Administrator on 2016/10/4 0004.
 */

//和注册相关的信息
public class RegActivityData {
    public Fragment[] fragmentarray;
    public RegStepOne regStepOne;
    public RegStepTwo regStepTwo;
    public RegStepThree regStepThree;
    public Button btn_left;
    public Button btn_right;
    public int step;
    public String username;
    public String birday;
    public String password;
    public String sex;
    public Bitmap myBitmap;
    public FragmentManager manager;
}
