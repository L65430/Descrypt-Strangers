package com.example.administrator.finalprocject.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.mapapi.map.Circle;
import com.example.administrator.finalprocject.AllMessage.CSmessage;
import com.example.administrator.finalprocject.Codecfactory.Lcoderfactory;
import com.example.administrator.finalprocject.Handler.ClientMessageHandler;
import com.example.administrator.finalprocject.R;
import com.example.administrator.finalprocject.StaticValues.CSKeys;
import com.example.administrator.finalprocject.StaticValues.JSONKeys;
import com.example.administrator.finalprocject.StaticValues.StaticValues;
import com.example.administrator.finalprocject.Utils.FileUtils;
import com.example.administrator.finalprocject.Utils.ForCheck;
import com.example.administrator.finalprocject.Utils.PasswordUtil;
import com.example.administrator.finalprocject.Utils.SharedPreferenceUtil;
import com.example.administrator.finalprocject.Utils.myDialog;
import com.example.administrator.finalprocject.View.CircleImageView;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.io.MacInputStream;
import org.bouncycastle.jce.exception.ExtCertificateEncodingException;

import java.io.File;
import java.io.PrintStream;
import java.net.InetSocketAddress;

public class SignInActivity extends Activity {
    Intent intent_server;//和服务器沟通
    private long mExitTime;
    Button btn_submit;
    Button btn_forget;//忘记密码
    CircleImageView userHead;//头像

    EditText et_userName;
    EditText et_pwd;
    private String userEmail;
    private String pwd;
    TextView tv_forgetpassword;
    TextView tv_registration;
    MyOnClickListener myOnClickListener;
    MyHandler myHandler = new MyHandler();
    private Messenger rMessenger = null;
    private Messenger mMessenger = null;
    private boolean mIsBind;
    private ServiceConnection serConn = null;

    private IoSession session;//mina框架
    private Context context;
    private myDialog loginlog;//对话框

    private Handler handler = new Handler() {
        //注意线程中的handler是一个对应一个的,Message发送出来之后有自己的target，就是处理它的handler，所以无论new 几个类都不要紧
        public void handleMessage(Message msg) {//这个方法是用来接受数据的
            switch (msg.what) {//是接收到的消息，但是发送是由client来做的
                case CSKeys.LOGIN_SUCCESS:
                    // 登录成功,跳到主界面,由于该activity destroy了，所以该session已经close了
                    // 保存相关信息到
                    SharedPreferenceUtil util = new SharedPreferenceUtil(context);
                    util.SaveValue(StaticValues.userEmail, userEmail);
                    util.SaveValue(StaticValues.userPasswd, pwd);

                    Intent intent = new Intent(context, MainActivity.class);
                    startActivity(intent);

                    loginlog.dismiss();

                    CloseSession();//及时关闭连接
                    finish();
                    break;
                case CSKeys.LOGIN_FAILED:
                    Toast.makeText(context,"登录失败",Toast.LENGTH_LONG).show();
                    loginlog.dismiss();
                    break;
                case StaticValues.SESSION_OPENED://开始会话
                    try
                    {
                        CSmessage cSmessage=new CSmessage();
                        JSONObject json=new JSONObject();
                        json.put(JSONKeys.msgType,CSKeys.LOGIN);
                        json.put(JSONKeys.userEmail,userEmail);
                        json.put(JSONKeys.userPasswd, PasswordUtil.toMD5(pwd));//进行过加密
                        cSmessage.msgJson=json.toJSONString();
                        cSmessage.symbol='+';
                        session.write(cSmessage);//将这些消息传递给服务器
                        //Mainactivity接收到消息然后传过来然后再传给服务器
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
            }
        };
    };//这是mina框架中说的用来接收服务器消息的部分

   @Override
   public boolean onKeyDown(int keyCode, KeyEvent event)
   {
       if (keyCode == KeyEvent.KEYCODE_BACK) {
           if ((System.currentTimeMillis() - mExitTime) > 2000) {
               Toast.makeText(getApplicationContext(), "再按一次退出",
                       Toast.LENGTH_SHORT).show();
               mExitTime = System.currentTimeMillis();
           } else {
               finish();
           }
           return true;
       }
       return super.onKeyDown(keyCode, event);
   }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_sign_in);
        context=this;
        initView();
    }

    private void initView()
    {
        userHead=(CircleImageView)findViewById(R.id.signin_iv_headicon);
        et_userName=(EditText)findViewById(R.id.signin_et_username);
        et_pwd = (EditText) findViewById(R.id.signin_et_pwd);
        btn_submit = (Button) findViewById(R.id.signin_btn_submit);
        btn_submit.setTag("btn_submit");
        tv_forgetpassword = (TextView) findViewById(R.id.signin_tv_forgetpassword);
        tv_forgetpassword.setTag("tv_forgetpassword");
        tv_registration = (TextView) findViewById(R.id.signin_tv_registration);
        tv_registration.setTag("tv_registration");
        myOnClickListener = new MyOnClickListener();

        btn_submit.setOnClickListener(myOnClickListener);
        tv_forgetpassword.setOnClickListener(myOnClickListener);
        tv_registration.setOnClickListener(myOnClickListener);

        et_userName.addTextChangedListener(new TextWatcher() {//输入内容改变的监听器
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String headPath=StaticValues.USER_HEADPATH+s+".png";
                    if(FileUtils.isFileexist(headPath))
                    {
                        userHead.setImageURI(Uri.fromFile(new File(headPath)));
                    }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private class MyHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            et_userName.setText((String)msg.obj);//接收到消息之后的处理，但是发送消息一般是在另一个线程
        }
    }//这个handler不知道用来干嘛的

    class MyOnClickListener implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {
            if(v.getId()==R.id.signin_btn_submit)
            {
                userEmail=et_userName.getText().toString().trim();
                pwd=et_pwd.getText().toString().trim();
                if (userEmail.equals("")) {
                    Toast.makeText(context, "账号不能为空", Toast.LENGTH_LONG).show();
                    return;
                }
                if(!ForCheck.EmailFormat(userEmail))
                {
                    Toast.makeText(context, "请输入注册邮箱", Toast.LENGTH_LONG).show();
                    et_userName.setText("");
                    return;
                }
                if (pwd.equals("")) {
                    Toast.makeText(context, "密码不能为空", Toast.LENGTH_LONG).show();
                    return;
                }
                //发送消息
                new ConnectServerThread().start();
                loginlog=new myDialog(context,"登录中...");
                loginlog.show();
             } else if (v.getId()==R.id.signin_tv_forgetpassword)
            {
               System.out.println("点击了忘记密码");
                Intent intent=new Intent(SignInActivity.this,ForgetPasswdActivity.class);
                startActivity(intent);
            }else if(v.getId()==R.id.signin_tv_registration)
            {
                Intent intent = new Intent(SignInActivity.this,
                        RegistrationActivity.class);
                startActivity(intent);
            }else
            {
                return;
            }
        }
    }

    //连接服务器
    private class ConnectServerThread extends Thread
    {
        public void run()
        {
            NioSocketConnector connector=new NioSocketConnector();
            connector.setHandler(new ClientMessageHandler(handler));//设定这个handler是用来接受服务器的消息的
            connector.getFilterChain().addLast("code",new ProtocolCodecFilter(new Lcoderfactory()));//解码器
            try
            {
                ConnectFuture future=connector.connect(new InetSocketAddress(StaticValues.SERVER_IP,StaticValues.SERVER_PORT));
                future.awaitUninterruptibly();//等待直到阻塞结束
                session=future.getSession();
                System.out.println("session is ok"+session);
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private void sendMessage()
    {
        Message msg=Message.obtain(null,1,"");//这是获得的消息类型
        msg.replyTo=mMessenger;//然后发送给mMessenger
        try
        {
            rMessenger.send(msg);
        }catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }

    private void CloseSession()
    {
        if(session!=null&&!session.isClosing())
        {
            session.close();
        }
    }
}
