package com.example.administrator.finalprocject.Adapter;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.test.suitebuilder.TestMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.finalprocject.AllMessage.ChatMessage;
import com.example.administrator.finalprocject.AllMessage.RobotMessage;
import com.example.administrator.finalprocject.Info.ClientManger;
import com.example.administrator.finalprocject.R;
import com.example.administrator.finalprocject.StaticValues.StaticValues;

import java.io.File;
import java.util.List;

/**
 * Created by L on 2016/11/8.
 */
public class ChatMessageAdapter extends BaseAdapter{
    private LayoutInflater mInflater;
    private List<RobotMessage>mDatas;
    private final int INCOMING = 0;// 接收
    private final int MYSEND = 1;// 发送

    public ChatMessageAdapter(Context context,List<RobotMessage>mDatas)
    {
        mInflater=LayoutInflater.from(context);
        this.mDatas=mDatas;
    }
    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        RobotMessage robotMessageMessage = mDatas.get(position);
        if (robotMessageMessage.getType() == RobotMessage.Type.INCOMING) {
            return INCOMING;
        }
        return MYSEND;
    }//获得这条信息的类型

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RobotMessage robotMessage=mDatas.get(position);
        ViewHolder viewHolder=null;
        if(convertView==null)
        {
            if(getItemViewType(position)==INCOMING)
            {
                convertView=mInflater.inflate(R.layout.chatting_item_msg_text_left,parent,false);
                viewHolder=new ViewHolder();
                viewHolder.mHead=(ImageView)convertView.findViewById(R.id.iv_userhead);
                viewHolder.mDate=(TextView)convertView.findViewById(R.id.tv_sendtime);
                viewHolder.mMsg=(TextView)convertView.findViewById(R.id.tv_chatcontent);
            }else
            {
                convertView=mInflater.inflate(R.layout.chatting_item_text_right,parent,false);
                viewHolder=new ViewHolder();
                viewHolder.mHead=(ImageView)convertView.findViewById(R.id.iv_userhead);
                viewHolder.mDate=(TextView) convertView.findViewById(R.id.tv_sendtime) ;
                viewHolder.mMsg=(TextView)convertView.findViewById(R.id.tv_chatcontent);
            }
            convertView.setTag(viewHolder);
        }else
        {
             viewHolder=(ViewHolder)convertView.getTag();
        }
        if(getItemViewType(position)==INCOMING)
        {
            viewHolder.mHead.setImageResource(R.drawable.appicon);
        }else
        {
                 String headPath = StaticValues.USER_HEADPATH
					+ ClientManger.clientEmail + ".png";
            viewHolder.mHead.setImageURI(Uri.fromFile(new File(headPath)));
        }

        viewHolder.mDate.setText(robotMessage.getDate());
        viewHolder.mMsg.setText(robotMessage.getMsg());
        return convertView;
    }//获得消息的类型

    private final class ViewHolder {
        ImageView mHead;
        TextView mDate;
        TextView mMsg;
    }//将view都打包起来
}
