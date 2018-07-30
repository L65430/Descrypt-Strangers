package com.example.administrator.finalprocject.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.example.administrator.finalprocject.Activity.InvitationActivity;
import com.example.administrator.finalprocject.Activity.MainActivity;
import com.example.administrator.finalprocject.AllMessage.CSmessage;
import com.example.administrator.finalprocject.Info.Chatinfoentry;
import com.example.administrator.finalprocject.Info.ClientManger;
import com.example.administrator.finalprocject.Info.InvitationInfoEntity;
import com.example.administrator.finalprocject.R;
import com.example.administrator.finalprocject.StaticValues.CSKeys;
import com.example.administrator.finalprocject.StaticValues.JSONKeys;
import com.example.administrator.finalprocject.Utils.ChatDBUtils;

import java.io.File;
import java.util.List;

/**
 * Created by L on 2016/10/6.
 */
public class InvitationAdapter extends BaseAdapter {
    private List<InvitationInfoEntity>coll;
    private LayoutInflater mInflater;
    private Context context;
    ListView listView;

    public InvitationAdapter(Context context,List<InvitationInfoEntity>coll)
    {
        this.coll=coll;
        this.context=context;
        mInflater=LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return coll.size();
    }

    @Override
    public Object getItem(int position) {
        return coll.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final InvitationInfoEntity entity = coll.get(position);
        if (mInflater == null) {
            System.out.println("mInflater为空");
            return null;
        }
        ViewHolderInvition viewHolder = new ViewHolderInvition();
        if (convertView == null)//先去寻找每一个view的布局
        {
            convertView = mInflater.inflate(R.layout.invitation_listview_item, null);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.iv_lv_it_icon);
            viewHolder.ivusername = (TextView) convertView.findViewById(R.id.iv_lv_it_ivusername);
            viewHolder.groupname = (TextView) convertView.findViewById(R.id.iv_lv_it_groupname);
            viewHolder.topic = (TextView) convertView.findViewById(R.id.iv_lv_it_topic);
            viewHolder.drop = (Button) convertView.findViewById(R.id.iv_lv_it_drop);
            viewHolder.agree = (Button) convertView.findViewById(R.id.iv_lv_it_agree);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderInvition) convertView.getTag();
        }
        viewHolder.icon.setImageURI(Uri.fromFile(new File(entity.getGroupIconPath())));
        viewHolder.ivusername.setText(entity.getInvitorName());
        viewHolder.groupname.setText(entity.getGroupName());
        viewHolder.topic.setText(entity.getTopic());

        viewHolder.agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CSmessage agreeMsg = new CSmessage();
                agreeMsg.symbol = '+';
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(JSONKeys.msgType, CSKeys.AGREEE_TO_GROUP);
                jsonObject.put(JSONKeys.userId, ClientManger.clientId);
                jsonObject.put(JSONKeys.groupId, entity.getGroupId());
                agreeMsg.msgJson = jsonObject.toJSONString();
                if (MainActivity.session != null && !MainActivity.session.isClosing()) {
                    MainActivity.session.close();
                }

                //在最近聊天列表里面增加一个群组聊天
                Chatinfoentry laticeItem = new Chatinfoentry();
                laticeItem.setFriendId(entity.getGroupId());
                laticeItem.setFriendName(entity.getGroupName());
                laticeItem.setChatContent("");
                laticeItem.setChatCreatTime("");
                laticeItem.setMsg_num(0);
                laticeItem.setMsgtype(2);//msgtype里面存放的是消息类型，为2表示的是群组消息
                MainActivity.mp0.addrecentchatitem(laticeItem);
                InvitationActivity.deleteInviteItem(entity.getGroupId());//从表面删除
                ChatDBUtils db = new ChatDBUtils(context);
                db.deleteInvite(entity.getGroupId());
            }
        });

        viewHolder.drop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatDBUtils db=new ChatDBUtils(context);
                db.deleteInvite(entity.getGroupId());
            }
        });

        return convertView;
    }

    public void setListView(ListView listView)
    {
        this.listView=listView;
    }

    private class ViewHolderInvition
    {
        ImageView icon;
        TextView ivusername;
        TextView groupname;
        TextView topic;
        Button drop;
        Button agree;

    }
}
