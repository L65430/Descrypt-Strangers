package com.example.administrator.finalprocject.Activity;

import android.app.Activity;
import android.os.Bundle;

import com.example.administrator.finalprocject.R;
//用来存放陌生人玩过的游戏，再次点击之后就跳转到网页进行游戏，游戏结束就跳转到原来的界面
public class StrangerGameActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stranger_game);

    }
}
