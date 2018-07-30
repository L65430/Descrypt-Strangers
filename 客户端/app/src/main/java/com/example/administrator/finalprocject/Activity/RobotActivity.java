package com.example.administrator.finalprocject.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.administrator.finalprocject.Adapter.ChatMessageAdapter;
import com.example.administrator.finalprocject.AllMessage.ChatMessage;
import com.example.administrator.finalprocject.AllMessage.RobotMessage;
import com.example.administrator.finalprocject.R;
import com.example.administrator.finalprocject.Utils.ClientUtils;
import com.example.administrator.finalprocject.Utils.RobotUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

public class RobotActivity extends Activity {
    private ListView mMsgs;
    private ChatMessageAdapter mAdapter;
    private List<RobotMessage> mDatas;

    private EditText mInputMsg;
    private Button mSendMsg;

    private android.os.Handler mHandler=new android.os.Handler()
    {
        public void handleMessage(Message message)
        {
            RobotMessage fromMessage=(RobotMessage)message.obj;
            mDatas.add(fromMessage);
            mAdapter.notifyDataSetChanged();
            mMsgs.setSelection(mDatas.size()-1);
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_robot);

        initView();
        initDatas();
        initListener();

    }

    private void initListener()
    {
        mSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String tomsg=mInputMsg.getText().toString();
                if(TextUtils.isEmpty(tomsg))
                {
                    Toast.makeText(RobotActivity.this,"消息不能为空",Toast.LENGTH_LONG).show();
                    return;
                }

                RobotMessage tomessage=new RobotMessage();
                tomessage.setDate(ClientUtils.getNowTime());
                tomessage.setMsg(tomsg);
                tomessage.setType(RobotMessage.Type.OUTCOMING);
                mDatas.add(tomessage);
                mAdapter.notifyDataSetChanged();
                mMsgs.setSelection(mDatas.size()-1);

                mInputMsg.setText("");

                new Thread()
                {
                    public void run()
                    {
                        RobotMessage fromMessage = RobotUtils.sendMessage(tomsg);
                        Message m = Message.obtain();
                        m.obj = fromMessage;
                        mHandler.sendMessage(m);
                    };
                }.start();
            }
        });
    }

    private void initDatas()
    {
        mDatas = new ArrayList<RobotMessage>();
        mDatas.add(new RobotMessage("您好，小L在这呦", RobotMessage.Type.INCOMING, ClientUtils.getNowTime()));
        mAdapter = new ChatMessageAdapter(this, mDatas);
        mMsgs.setAdapter(mAdapter);
    }

    private void initView()
    {
        mMsgs = (ListView) findViewById(R.id.id_listview_msgs);
        mInputMsg = (EditText) findViewById(R.id.id_input_msg);
        mSendMsg = (Button) findViewById(R.id.id_send_msg);
    }

    public void onBack(View v)
    {
        finish();
    }
}
