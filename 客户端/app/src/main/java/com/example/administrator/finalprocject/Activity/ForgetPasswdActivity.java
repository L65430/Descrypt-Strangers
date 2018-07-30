package com.example.administrator.finalprocject.Activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.administrator.finalprocject.AllMessage.CSmessage;
import com.example.administrator.finalprocject.Codecfactory.Lcoderfactory;
import com.example.administrator.finalprocject.Handler.ClientMessageHandler;
import com.example.administrator.finalprocject.R;
import com.example.administrator.finalprocject.StaticValues.CSKeys;
import com.example.administrator.finalprocject.StaticValues.JSONKeys;
import com.example.administrator.finalprocject.StaticValues.StaticValues;
import com.example.administrator.finalprocject.Utils.ForCheck;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;
import java.nio.channels.ClosedSelectorException;
import java.util.regex.Pattern;

public class ForgetPasswdActivity extends Activity {
    private EditText et_email;
    private Button btn_submit;
    String email = null;
    String code = null;

    private IoSession session;
    private Context context;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_forget_passwd);
        et_email = (EditText) findViewById(R.id.forgetpass_et_emiladdress);
        context = this;
        handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case CSKeys.FIND_PASSWD_SUCCESS:
                        Toast.makeText(context, "请查看邮箱", Toast.LENGTH_LONG).show();//消息以邮件形式发送过来
                        ClosedSession();
                        finish();
                        break;
                    case CSKeys.FIND_PASSWD_FAILED:
                        Toast.makeText(context, "查找密码失败", Toast.LENGTH_LONG).show();
                        ClosedSession();
                        break;
                    //这些信息都是通过iosession发送过来的
                }
            }
        };
    }


    //提交信息
    public void onSubmit(View v) {
        email = et_email.getText().toString();
        if (email.equals("") || !ForCheck.EmailFormat(email)) {
            Toast.makeText(context, "邮箱为空或邮箱格式不正确，请重新输入", Toast.LENGTH_SHORT)
                    .show();
            et_email.setText("");
            return;
        } else {
            // 发送消息
            new ConnectServerThread().start();
            try {
                Thread.sleep(2000);
                CSmessage cSmessage = new CSmessage();
                JSONObject Json = new JSONObject();
                Json.put(JSONKeys.msgType,CSKeys.FIND_PASSWD);
                Json.put(JSONKeys.userEmail, email);
                cSmessage.msgJson = Json.toJSONString();
                cSmessage.symbol = '+';
                session.write(cSmessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //返回
    public void onBack(View v)
    {
        finish();
    }


    //连接服务器，获取信息
    private class ConnectServerThread extends Thread {
        public void run() {
            NioSocketConnector connector = new NioSocketConnector();
            connector.setHandler(new ClientMessageHandler(handler));
            connector.getFilterChain().addLast("code",
                    new ProtocolCodecFilter(new Lcoderfactory()));
            try {
                ConnectFuture future = connector.connect(new InetSocketAddress(
                        StaticValues.SERVER_IP, StaticValues.SERVER_PORT));
                future.awaitUninterruptibly();
                session = future.getSession();
                System.out.println("session is ok" + session);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void ClosedSession()
    {
        if(session!=null&&!session.isClosing())
        {
            session.close();
        }
    }
}
