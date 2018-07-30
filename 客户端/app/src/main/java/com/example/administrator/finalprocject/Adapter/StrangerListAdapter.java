package com.example.administrator.finalprocject.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.baidu.mtjstatsdk.HeadObject;
import com.example.administrator.finalprocject.Info.StrangerBean;
import com.example.administrator.finalprocject.R;

import org.bouncycastle.asn1.cmp.InfoTypeAndValue;

import java.util.List;

/**
 * Created by Administrator on 2016/9/25 0025.
 */
public class StrangerListAdapter extends BaseAdapter {
    private Context context;
    private List<StrangerBean> list;//用来存放消息的
    public StrangerListAdapter(Context context,List<StrangerBean>list)
    {
        this.context=context;
        this.list=list;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        viewHolder holder;
        if(convertView==null)
        {
            LayoutInflater inflater=LayoutInflater.from(context);
            convertView=inflater.inflate(R.layout.stranger_item,parent,false);
            holder=new viewHolder();
            holder.tvName=(TextView)convertView.findViewById(R.id.friend_name);
            holder.tvLocation=(TextView)convertView.findViewById(R.id.friend_loc);
            convertView.setTag(holder);//就是把holder放进去，下次直接取出来就好了
        }
        else
        {
            holder=(viewHolder)convertView.getTag();
        }
        holder.tvName.setText(list.get(position).strangerName);
        holder.tvLocation.setText(list.get(position).strangerLoc);
        return convertView;
    }

    private class viewHolder
    {
        TextView tvName;
        TextView tvLocation;
    }
}
