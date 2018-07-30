package com.example.administrator.finalprocject.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.finalprocject.Info.ClientManger;
import com.example.administrator.finalprocject.R;
import com.example.administrator.finalprocject.StaticValues.CSKeys;

public class SexEditActivity extends Activity {
    ImageButton ib_back;
    Button btn_submit;
    RelativeLayout rl_boy;
    ImageView iv_boy;
    TextView tv_boy;
    RelativeLayout rl_girl;
    ImageView iv_girl;
    TextView tv_girl;
    String sex= ClientManger.clientSex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_sex_edit);
        initview();
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rl_boy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_boy.setVisibility(View.VISIBLE);//可见的
                iv_girl.setVisibility(View.GONE);
                sex=tv_boy.getText().toString();
            }
        });
        rl_girl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_boy.setVisibility(View.GONE);
                iv_girl.setVisibility(View.VISIBLE);
                sex = tv_girl.getText().toString();
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.myBinder.ResetUserInfo(CSKeys.RESET_SEX,sex);//但是这部分没有保存下来
                ClientManger.clientSex=sex;
                PersonalInfoActivity.refreshClientInfo(CSKeys.RESET_SEX,sex);//更改之后sex变为了正确的值，但是储存的时候
                finish();
            }
        });


    }
    public void initview()
    {
        ib_back = (ImageButton) findViewById(R.id.sex_ib_back);
        iv_boy = (ImageView) findViewById(R.id.sex_boy_checked);
        iv_girl = (ImageView) findViewById(R.id.sex_girl_checked);
        tv_boy = (TextView) findViewById(R.id.sex_tv_boy);
        tv_girl = (TextView) findViewById(R.id.sex_tv_girl);
        rl_boy = (RelativeLayout) findViewById(R.id.sex_rl_boy);
        rl_girl = (RelativeLayout) findViewById(R.id.sex_rl_girl);
        btn_submit = (Button) findViewById(R.id.sex_btn_submit);
    }
}
