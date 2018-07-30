package com.example.administrator.finalprocject.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

import com.example.administrator.finalprocject.Adapter.InvitationAdapter;
import com.example.administrator.finalprocject.Info.InvitationInfoEntity;
import com.example.administrator.finalprocject.R;
import com.example.administrator.finalprocject.Utils.ChatDBUtils;

import java.util.ArrayList;
import java.util.List;

public class InvitationActivity extends Activity {
    ListView lv;
    private static List<InvitationInfoEntity> coll;//邀请列表
    static InvitationAdapter invitationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_invitation);
        coll=new ArrayList<InvitationInfoEntity>();
        lv=(ListView)findViewById(R.id.invitation_lv);
        invitationAdapter = new InvitationAdapter(this, coll);
        invitationAdapter.setListView(lv);
        lv.setAdapter(invitationAdapter);

        getIvitFromDb();//初始化
    }

    //从数据库获得未处理的系统消息
    public void getIvitFromDb()
    {
        ChatDBUtils db=new ChatDBUtils(InvitationActivity.this);
        coll.add((InvitationInfoEntity) db.getInvits());
        invitationAdapter.notifyDataSetChanged();
    }

    public static void deleteInviteItem(String groupId){
        for(InvitationInfoEntity entity:coll)
        {
            if(entity.getGroupId().equals(groupId)) {
                coll.remove(entity);
                invitationAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    public void onBack(View v)
    {
        finish();
    }
}
