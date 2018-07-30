package com.example.administrator.finalprocject.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.administrator.finalprocject.AllMessage.CSmessage;
import com.example.administrator.finalprocject.Info.FriendInfo;
import com.example.administrator.finalprocject.Info.InvitationInfoEntity;
import com.example.administrator.finalprocject.R;
import com.example.administrator.finalprocject.StaticValues.CSKeys;
import com.example.administrator.finalprocject.StaticValues.JSONKeys;
import com.example.administrator.finalprocject.StaticValues.StaticValues;
import com.example.administrator.finalprocject.Utils.ImageUtil;

import org.apache.http.conn.scheme.HostNameResolver;
import org.json.JSONObject;

//添加陌生人
public class FriendAddActivity extends Activity {
    ImageButton ib_back;
    TextView tv_setting;
    Button btn_add;

    static ImageView iv_headlogo;
    static TextView tv_nickname;
    static TextView tv_sex;
    static TextView tv_singnatrue;

    private String strangerId;
    static String name;
    static String sex;
    static String birthday;
    static String personSignature;

    static byte[] headByes;
    static Context context;

    public static Handler handler=new Handler()
    {
        public void handleMessage(Message msg)
        {
            if(msg.what== CSKeys.GET_STRANGER_INFO)
            {
                CSmessage friendInfo=(CSmessage)msg.obj;
                com.alibaba.fastjson.JSONObject json=JSON.parseObject(friendInfo.msgJson);
                name=json.getString(JSONKeys.userName);
                sex=json.getString(JSONKeys.userSex);
                birthday=json.getString(JSONKeys.userBirthday);
                personSignature=json.getString(JSONKeys.personSignature);
                tv_nickname.setText(name);
                tv_sex.setText(sex);
                tv_singnatrue.setText(personSignature);
                headByes = friendInfo.msgBytes;
                iv_headlogo.setImageBitmap(ImageUtil.getInstance().getBitMapFromByte(headByes));
            }else if(msg.what==CSKeys.ADD_FRIEND_SUCCESS)
            {
                Toast.makeText(context, "添加好友成功", Toast.LENGTH_LONG).show();
            }else if(msg.what==CSKeys.ADD_FRIEND_FAILED)
            {
                Toast.makeText(context, "添加好友失败", Toast.LENGTH_LONG).show();
            }
        }
    };


    public void initView()
    {
        iv_headlogo = (ImageView) findViewById(R.id.friendadd_iv_headlogo);
        tv_nickname = (TextView) findViewById(R.id.friendadd_tv_nickname);
        tv_sex = (TextView) findViewById(R.id.friendadd_tv_sexvalue);
        tv_singnatrue = (TextView) findViewById(R.id.friendadd_tv_singnatrue);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_add);
        context=this;
        Intent intent=getIntent();//获得从其他activity传过来的intent
        //很显然这个intent里面没有东西，strangerId是要别人传过来的
        strangerId=intent.getStringExtra("strangerId");
        initView();
        MainActivity.myBinder.getStrangerInfo(strangerId);

        ib_back=(ImageButton)findViewById(R.id.friendadd_ib_back);
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });

        btn_add = (Button) findViewById(R.id.friendadd_btn_friendadd);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.myBinder.addFriend(strangerId);
                //保存好友头像到本地
                String headPath= StaticValues.USER_HEADPATH+strangerId+".png";
                ImageUtil.getInstance().saveImage(FriendAddActivity.this,headByes,headPath);
                FriendInfo friendInfo=new FriendInfo();
                friendInfo.setFriendId(strangerId);
                friendInfo.setFriendName(name);
                friendInfo.setFriendSex(sex);
                friendInfo.setFriendBirthday(birthday);
                friendInfo.setFriendSignature(personSignature);
                friendInfo.setFriendHeadPath(headPath);

                MainActivity.mp1.AddAFriend(friendInfo);
            }
        });


    }
}
