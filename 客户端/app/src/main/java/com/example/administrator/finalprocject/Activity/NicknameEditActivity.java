package com.example.administrator.finalprocject.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.administrator.finalprocject.Info.ClientManger;
import com.example.administrator.finalprocject.R;
import com.example.administrator.finalprocject.StaticValues.CSKeys;

import org.bouncycastle.asn1.nist.NISTNamedCurves;

public class NicknameEditActivity extends Activity {
    ImageButton ib_back;
    Button btn_submit;
    EditText et_nick;
    String nickname = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nickname_edit);
        initview();
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
       btn_submit.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               nickname=et_nick.getText().toString();
               if(!nickname.equals(""))
               {
                   MainActivity.myBinder.ResetUserInfo(CSKeys.RESET_USERNAME,nickname);
                   ClientManger.clientName=nickname;
                   PersonalInfoActivity.refreshClientInfo(CSKeys.RESET_USERNAME,nickname);//点击的时候传入了nickname，也确实收到了
                   MainActivity.refreshPinfo(CSKeys.RESET_USERNAME, nickname);
                   finish();
               }
               else
               {
                   Toast.makeText(NicknameEditActivity.this,"请输入昵称",Toast.LENGTH_LONG).show();
               }
           }
       });

    }
    public void initview()
    {
        ib_back=(ImageButton)findViewById(R.id.nick_ib_back);
        et_nick = (EditText) findViewById(R.id.nick_et_nickname);
        btn_submit = (Button) findViewById(R.id.nick_submit);
    }
}
