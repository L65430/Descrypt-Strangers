package com.example.administrator.finalprocject.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.administrator.finalprocject.AllMessage.ChatMessage;
import com.example.administrator.finalprocject.AllMessage.ClientHandleMessage;
import com.example.administrator.finalprocject.Info.ClientManger;
import com.example.administrator.finalprocject.R;
import com.example.administrator.finalprocject.StaticValues.StaticValues;
import com.example.administrator.finalprocject.Utils.ChatDBUtils;
import com.example.administrator.finalprocject.Utils.FileUtils;
import com.example.administrator.finalprocject.Utils.myDialog;

import java.util.List;

public class FriendMsgSettingActivity extends Activity {
    String friendId;
    String friendName;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_msg_setting);
        context = this;
        Intent intent = getIntent();
        friendId = intent.getStringExtra("friendId");
        friendName = intent.getStringExtra("friendName");
    }

    public void onClick(View v) {
        if (v.getId() == R.id.setting_ib_back) {
            finish();
        }
        if (v.getId() == R.id.rl_clearMsg) {
            clearMsg();
        }
        if (v.getId() == R.id.rl_toTxt) {
            MsgToText();
        }
    }

    //清空消息
    public void clearMsg() {
        myDialog myDialog = new myDialog(context, "清空中...");
        myDialog.show();
        ChatDBUtils db = new ChatDBUtils(context);
        String name = "msg" + ClientManger.clientId + "_" + friendId;
        if (db.isTableExit(name)) {
            db.clearMsg(friendId);
            ChatActivity.clearMsg();
        }
        myDialog.dismiss();
        Toast.makeText(context, "清空完成", Toast.LENGTH_LONG).show();
    }

    //导出到本地文件中
    public void MsgToText() {
        myDialog dialog = new myDialog(context, "导出中....");
        dialog.show();
        ChatDBUtils db = new ChatDBUtils(context);
        //导出到本地文件中
        List<ChatMessage> list = db.getrecentmessage(friendId, 0, db.getmessagecount(friendId));
        try {
            if (list.size() > 0) {
                FileUtils.saveMsgToText(list, friendName);
                Toast.makeText(context, "已经保存到" + StaticValues.MSG_TEXT + "文件夹中", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context,"导出失败",Toast.LENGTH_LONG).show();
        }
        dialog.dismiss();
    }
}
