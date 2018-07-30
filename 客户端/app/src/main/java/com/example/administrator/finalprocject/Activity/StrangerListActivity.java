package com.example.administrator.finalprocject.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.administrator.finalprocject.Adapter.StrangerListAdapter;
import com.example.administrator.finalprocject.Info.StrangerBean;
import com.example.administrator.finalprocject.R;
import com.example.administrator.finalprocject.StaticValues.CSKeys;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

//陌生人列表，分批加载
public class StrangerListActivity extends Activity {
    static ListView StrangerLv;
    private static List<StrangerBean> data=new ArrayList<StrangerBean>();
    static StrangerListAdapter adapter;
    static View footer;
    private static boolean loadfinish=true;
    Context context;
    ImageButton btn_back;
    static int disRange=2;
    public static android.os.Handler handler=new android.os.Handler()
    {
        public void handleMessage(Message msg)
        {
            if(msg.what==CSKeys.STRANGERS_LIST_ONEKM)
            {
                data=((List<StrangerBean>)msg.obj);//重点是这个msg哪里得到的,这里的data的值为0，没有得到值
                if(adapter==null)
                {
                    return;
                }
                else
                {
                    adapter.notifyDataSetChanged();
                    if(StrangerLv.getFooterViewsCount()>0)
                    {
                        StrangerLv.removeFooterView(footer);//底部footerView实现上拉加载更多
                        disRange++;//页脚就是加在列表的最下方
                    }
                    loadfinish=true;
                }

            }
//            if(msg.what==CSKeys.STRANGERS_LIST_MORE)//客户端还是这个
//            {
//                data.addAll((List<StrangerBean>)msg.obj);//重点是这个msg哪里得到的,这里的data的值为0，没有得到值
//                adapter.notifyDataSetChanged();
//                if(StrangerLv.getFooterViewsCount()>0)
//                {
//                    StrangerLv.removeFooterView(footer);//底部footerView实现上拉加载更多
//                    disRange++;//页脚就是加在列表的最下方
//                }
//                loadfinish=true;
//            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//表示出现没有title的窗口
        setContentView(R.layout.activity_stranger_list);
        context=this;
        btn_back=(ImageButton)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        StrangerLv=(ListView)findViewById(R.id.lv_strList);
        StrangerLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String strangeId=data.get(position).strangerId;
                Intent intent=new Intent(context,FriendAddActivity.class);
                //然后就可以选择添加好友了
                intent.putExtra("strangerId",strangeId);
                startActivity(intent);
            }
        });

        StrangerLv.setOnScrollListener(new ScrollListener());
        footer=getLayoutInflater().inflate(R.layout.strangerlist_footer,null);
        MainActivity.myBinder.getStrangerListMore(disRange);

        adapter=new StrangerListAdapter(context,data);//前面放的是自己界面，后面放的是数据源
        StrangerLv.addFooterView(footer);//添加页脚，放在listview最后
        StrangerLv.setAdapter(adapter);
        StrangerLv.removeFooterView(footer);//移除最后的页脚
    }

    private final class ScrollListener implements AbsListView.OnScrollListener
    {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            Log.i("MainActivity", "onScrollStateChanged(scrollState="
                    + scrollState + ")");
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastItemid=StrangerLv.getLastVisiblePosition();//获取当前屏幕最后item的id
                if(lastItemid+1==totalItemCount)//达到数据最后一条记录
                {
                    if(totalItemCount>0)
                    {
                        if(loadfinish)
                        {
                            loadfinish=false;
                            StrangerLv.addFooterView(footer);
                            MainActivity.myBinder.getStrangerListMore(disRange);//向Mainactivity获取新的diarange
                        }
                    }
                }
        }
    }

}
