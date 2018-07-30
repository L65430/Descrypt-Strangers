package com.example.administrator.finalprocject.Activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.finalprocject.Info.Chatinfoentry;
import com.example.administrator.finalprocject.Info.ClientManger;
import com.example.administrator.finalprocject.Info.FriendInfo;
import com.example.administrator.finalprocject.R;
import com.example.administrator.finalprocject.Utils.ChatDBUtils;

import java.io.File;

public class FriendInfoActivity extends Activity {
    private FriendInfo bean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_friend_info);
        Intent intent = getIntent();
        bean = (FriendInfo) intent
                .getSerializableExtra("friendInfo");//从另一个界面转过来的，带上一个friendinfo数据

        ImageView iv_headlogo = (ImageView) findViewById(R.id.friendinfo_iv_headlogo);
        TextView tv_nickname = (TextView) findViewById(R.id.friendinfo_tv_nickname);
        TextView tv_sex = (TextView) findViewById(R.id.friendinfo_tv_sexvalue);
        TextView tv_singnatrue = (TextView) findViewById(R.id.friendinfo_tv_singnatrue);

        tv_nickname.setText(bean.getFriendName());
        tv_sex.setText(bean.getFriendSex());
        tv_singnatrue.setText(bean.getFriendSignature());
        iv_headlogo.setImageURI(Uri.fromFile(new File(bean.getFriendHeadPath())));

        final Button chat=(Button)findViewById(R.id.chat);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String friendId=bean.getFriendId();
                String friendName=bean.getFriendName();
                if(MainActivity.mp0.isExistsInchat(friendId))
                {
                    MainActivity.mp0.ResetNotReadMsg(friendId);//清空所有的未读消息
                }else
                {
                    Chatinfoentry chatinfoentry=new Chatinfoentry();//新增一条聊天记录
                    chatinfoentry.setMsg_num(0);
                    chatinfoentry.setChatCreatTime("");
                    chatinfoentry.setChatContent("");
                    chatinfoentry.setFriendId(friendId);
                    chatinfoentry.setMsgtype(0);
                    chatinfoentry.setFriendName(friendName);
                    MainActivity.mp0.addrecentchatitem(chatinfoentry);
                }
                Intent intent=new Intent(FriendInfoActivity.this,ChatActivity.class);
                intent.putExtra("friendId", friendId);
                intent.putExtra("friendName", friendName);
                startActivity(intent);
                finish();

            }
        });
        Button delete=(Button)findViewById(R.id.delete);
        //删除好友
        //发送通知给服务器，如果在最近联系人中，删除，包括本地的数据库，数据库中清楚表
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String friendId=bean.getFriendId();
                MainActivity.myBinder.deleteFriend(friendId);
                MainActivity.mp1.DeleteAFriend(friendId);//更新好友列表
                if(MainActivity.mp0.isExistsInchat(friendId))
                {
                    MainActivity.mp0.deleterecentchat(friendId);
                }
                ChatDBUtils chatDBUtils=new ChatDBUtils(FriendInfoActivity.this);
                String name="msg"+ ClientManger.clientId+"_"+friendId+".png";
                if(chatDBUtils.isTableExit(name))
                {
                    chatDBUtils.deletechatitem(friendId);
                }
                if(MainActivity.mp0.isExistsInchat(friendId))
                {
                    MainActivity.mp0.deleterecentchat(friendId);
                }
                finish();
            }
        });


    }
    public void onBack(View v)
    {
        finish();
    }

}
