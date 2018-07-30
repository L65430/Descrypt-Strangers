package com.example.administrator.finalprocject.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.finalprocject.Info.FriendInfo;
import com.example.administrator.finalprocject.Info.Gameinfo;
import com.example.administrator.finalprocject.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by L on 2016/11/9.
 */
public class GameInfoAdapter extends BaseAdapter {
    final static class ViewHolder
    {
        ImageView Gameicon;
        TextView Gamename;
        TextView Bestcode;
    }
    private Context mcontext;
    private List<Gameinfo> list;

    public GameInfoAdapter(Context context,List<Gameinfo>list)
    {
        this.mcontext=context;
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
        ViewHolder viewHolder=null;
        final Gameinfo agameinfo=list.get(position);
        if(convertView==null)
        {
            viewHolder=new ViewHolder();
            convertView= LayoutInflater.from(mcontext).inflate(R.layout.game_item,null);
            viewHolder.Gameicon=(ImageView)convertView.findViewById(R.id.gameicon);
            viewHolder.Gamename = (TextView) convertView
                    .findViewById(R.id.game_name);
            viewHolder.Bestcode = (TextView) convertView
                    .findViewById(R.id.bestcode);
            convertView.setTag(viewHolder);//用settag的方式来取出viewholder
        }else
        {
            viewHolder=(ViewHolder)convertView.getTag();
        }

        viewHolder.Gameicon.setImageResource(agameinfo.getGameicon());
        viewHolder.Gamename.setText(agameinfo.getGamename());
        viewHolder.Bestcode.setText(agameinfo.getbestcode());
        return convertView;
    }

    public void updateListView(List<Gameinfo>list)
    {
        this.list=list;
        notifyDataSetChanged();
    }
}
