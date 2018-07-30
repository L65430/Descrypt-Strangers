package com.example.administrator.finalprocject.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.administrator.finalprocject.R;
import com.example.administrator.finalprocject.StaticValues.CSKeys;
import com.example.administrator.finalprocject.Utils.PasswordUtil;

import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public class Changepassword extends AppCompatActivity {
    ImageButton ib_back;
    EditText change_et_pwd1;
    String pwd1;
    EditText change_et_pwd2;
    String pwd2;
    Button btn_submit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_changepassword);

        ib_back=(ImageButton)findViewById(R.id.change_ib_back);
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //获取密码
        change_et_pwd1=(EditText)findViewById(R.id.change_et_pwd1);
        change_et_pwd2 = (EditText) findViewById(R.id.change_et_pwd2);

        //提交
        btn_submit=(Button)findViewById(R.id.change_btn_submit);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pwd1=change_et_pwd1.getText().toString().trim();
                if(pwd1.equals(""))
                {
                    Toast.makeText(Changepassword.this, "请输入密码", Toast.LENGTH_LONG).show();
                    return;
                }
                if(pwd1.length()<6)
                {
                    Toast.makeText(Changepassword.this, "密码小于6位", Toast.LENGTH_LONG).show();
                }
                pwd2=change_et_pwd2.getText().toString().trim();
                if(!pwd2.equals(pwd1))
                {
                    Toast.makeText(Changepassword.this, "两次密码输入不一致", Toast.LENGTH_LONG).show();
                    return;
                }
                MainActivity.myBinder.ResetUserInfo(CSKeys.RESET_PASSWD, PasswordUtil.toMD5(pwd2));
                finish();
            }
        });

    }
}
