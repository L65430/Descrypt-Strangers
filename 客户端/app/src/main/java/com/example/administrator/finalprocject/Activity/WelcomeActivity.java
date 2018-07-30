package com.example.administrator.finalprocject.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.example.administrator.finalprocject.R;
import com.example.administrator.finalprocject.StaticValues.StaticValues;
import com.example.administrator.finalprocject.Utils.ChatDBUtils;
import com.example.administrator.finalprocject.Utils.SharedPreferenceUtil;

public class WelcomeActivity extends Activity {
    private int splashDelay=3000;//持续时间
    private ImageView iv_welcome;
    MyAnimationListener myAnimationListener;//动画监听器
    Intent intent;
    Intent intent_server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome);
        iv_welcome=(ImageView)findViewById(R.id.welcome_iv_welcome);
        myAnimationListener = new MyAnimationListener();
        startanimation();
        checkLogin();//在进入的时候就去检查登录

        ChatDBUtils db=new ChatDBUtils(this);
        if(!db.isTableExit("recent_chat_table"))
        {
            db.createLaticeChatTable();//创建聊天表

        }
    }


    private void checkLogin()
    {
        SharedPreferenceUtil util=new SharedPreferenceUtil(this);
        //因为sharedpreferenceUtil是把所有的数据存在同一个名字的sharedpreference里面，获取的时候很方便
        String userEmail=util.GetValue(StaticValues.userEmail);
        String userPasswd=util.GetValue(StaticValues.userPasswd);
        if ("".equals(userEmail) || "".equals(userPasswd)) {
            intent = new Intent(this, SignInActivity.class);//如果没有信息的话就跳转到signactivity里面去
        } else {
            intent = new Intent(this, MainActivity.class);
        }//这是从哪里传过来的
    }

    private void startanimation()
    {
        AnimationSet animationSet = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.2f, 1f, 1.2f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        scaleAnimation.setDuration(splashDelay);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setAnimationListener(myAnimationListener);
        animationSet.addAnimation(scaleAnimation);
        iv_welcome.startAnimation(animationSet);
    }

    private class MyAnimationListener implements Animation.AnimationListener
    {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                finish();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
}
