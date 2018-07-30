package com.example.administrator.finalprocject.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.administrator.finalprocject.Info.Chatinfoentry;
import com.example.administrator.finalprocject.Info.ClientManger;
import com.example.administrator.finalprocject.Info.StrangerBean;
import com.example.administrator.finalprocject.R;
import com.example.administrator.finalprocject.Utils.ChatDBUtils;
import com.example.administrator.finalprocject.Utils.SharedPreferenceUtil;
import com.example.administrator.finalprocject.Utils.SwitchButton;
import com.example.administrator.finalprocject.Utils.myDialog;
import com.google.android.gms.common.stats.ConnectionEvent;

import java.util.List;

//各类设置
public class SettingActivity extends Activity {
    ImageButton ib_back;
    RelativeLayout rl_NameManage;
    SwitchButton sb_voice;
    RelativeLayout rl_music;
    SwitchButton sb_shake;
    SwitchButton sb_AddressShow;
    RelativeLayout rl_changepasswd;
    SharedPreferenceUtil mSpUtil;

    boolean isRing;
    boolean isVibartion;
    boolean isShareLoc;

    Context context;


    //初始化设置
    public void  initSettins()
    {
        isRing= ClientManger.isRing;
        isVibartion=ClientManger.isVibration;
        isShareLoc=ClientManger.isShareLoc;

        sb_voice.setChecked(!isRing);
        sb_AddressShow.setChecked(!isShareLoc);
        sb_shake.setChecked(!isVibartion);

    }

    public void initview()
    {
        //是否开启声音
        sb_voice=(SwitchButton)findViewById(R.id.setting_sb_voice);

        //是否开启震动
        sb_shake=(SwitchButton)findViewById(R.id.setting_sb_shake);

        //是否开启位置信息
        sb_AddressShow=(SwitchButton)findViewById(R.id.setting_sb_addressshow);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_setting);
        context=this;
        mSpUtil=new SharedPreferenceUtil(SettingActivity.this);
        initview();
        initSettins();

        sb_voice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    mSpUtil.setIsRing(false);
                    ClientManger.isRing=false;
                }else
                {
                    mSpUtil.setIsRing(true);
                    ClientManger.isRing=true;
                }
            }
        });

        sb_shake.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked) {
                    mSpUtil.setIsVibration(false);
                    ClientManger.isVibration = false;
                    System.out.println("close");
                } else {
                    mSpUtil.setIsVibration(true);
                    ClientManger.isVibration = true;
                    System.out.println("open");
                }
            }
        });


        sb_AddressShow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    mSpUtil.setIsShareLoc(false);
                    ClientManger.isShareLoc = false;
                    System.out.println("close");
                } else {
                    mSpUtil.setIsShareLoc(true);
                    ClientManger.isShareLoc = true;
                    System.out.println("open");
                }
            }
        });
    }

    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.setting_ib_back:
                finish();
                break;
            case R.id.setting_rl_changepasswd:
                Intent intent=new Intent(SettingActivity.this,Changepassword.class);
                startActivity(intent);
                break;
            case R.id.rl_clearAllmsg:
                clearAllMsg();
                break;
            case R.id.rl_clearLatice://清除最近联系人
                clearLatice();
                break;
        }
    }

    //清空所有聊天记录
    public void clearAllMsg()
    {
        myDialog dialog=new myDialog(context,"清空中...");
        dialog.show();
        ChatDBUtils db=new ChatDBUtils(context);
        List<String> friendIds=MainActivity.mp1.getFriendsIds();
        if(friendIds.size()>0)
        {
            for(String Id:friendIds)
            {
                String tName="msg"+ClientManger.clientId+"_"+Id;
                if(db.isTableExit(tName))
                {
                    db.clearMsg(Id);
                }
            }
        }
        dialog.dismiss();
        Toast.makeText(context,"清空完成",Toast.LENGTH_LONG).show();
    }

    //清空最近联系人
    public void clearLatice()
    {
        myDialog dialog = new myDialog(context, "清空中...");
        dialog.show();
        MainActivity.mp0.clearrecent();
        dialog.dismiss();
        Toast.makeText(context, "清空完成", Toast.LENGTH_LONG).show();
    }
}
