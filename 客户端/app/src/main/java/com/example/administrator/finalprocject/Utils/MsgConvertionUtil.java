package com.example.administrator.finalprocject.Utils;

import android.content.Context;
import android.media.Image;
import android.provider.Settings;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.administrator.finalprocject.AllMessage.CSmessage;
import com.example.administrator.finalprocject.AllMessage.ChatMessage;
import com.example.administrator.finalprocject.AllMessage.ClientHandleMessage;
import com.example.administrator.finalprocject.Handler.ClientMessageHandler;
import com.example.administrator.finalprocject.Info.FriendInfo;
import com.example.administrator.finalprocject.Info.InvitationInfoEntity;
import com.example.administrator.finalprocject.Info.StrangerBean;
import com.example.administrator.finalprocject.StaticValues.CSKeys;
import com.example.administrator.finalprocject.StaticValues.JSONKeys;
import com.example.administrator.finalprocject.StaticValues.StaticValues;

import org.apache.mina.filter.codec.ProtocolCodecFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2016/8/30 0030.
 */
//用来对网络包，数据包和显示在界面上的包进行转换
public class MsgConvertionUtil {

    public static MsgConvertionUtil getInstance()//方法是写成static的
    {
      return new MsgConvertionUtil();
    }

    //网络输入包变成数据库储存的包
    public ChatMessage Convert_Net2Db(int isGetted, int isLooked, Context context, CSmessage cSmessage)
    {
        ChatMessage chatMessage=new ChatMessage();
        JSONObject jsonObject= JSON.parseObject(cSmessage.msgJson);//就是转变成json格式
        int type=jsonObject.getInteger(JSONKeys.msgType);
        String sendtime=jsonObject.getString(JSONKeys.sendTime);
        String Gettedid=jsonObject.getString(JSONKeys.friendId);
        String sendedid=jsonObject.getString(JSONKeys.userId);
        chatMessage.isGetted=isGetted;
        chatMessage.isLooked=isLooked;
        chatMessage.sendTime=sendtime;
        chatMessage.msgType=type;
        JSONObject jsonObject1=new JSONObject();//本地的存储数据类的json包中包含的东西（比如发给谁之类的）
        if(isGetted==1)//得到信息
        {
            jsonObject1.put(JSONKeys.userId,Gettedid);
            jsonObject1.put(JSONKeys.friendId,sendedid);
        }else
        {
            jsonObject1.put(JSONKeys.userId,sendedid);
            jsonObject1.put(JSONKeys.friendId,Gettedid);
        }
        switch (type)
        {
            case CSKeys.CHATING_TEXT_MSG://文字信息
                jsonObject1.put(JSONKeys.msgCotent,jsonObject.getString(JSONKeys.msgCotent));
                break;
            case CSKeys.CHATING_IMAGE_MSG:
                String imagePath = StaticValues.IMAGEPATH + sendedid+"_"+Gettedid + System.currentTimeMillis() + ".png";//这里的send和get是接收到的网络包中的
                ImageUtil.getInstance().saveImage(context,cSmessage.msgBytes, imagePath);//存储图片
                jsonObject1.put(JSONKeys.imagePath, imagePath);
                break;
            case CSKeys.CHATING_VOICE_MSG:
                String voicePath=StaticValues.VOICEPATH+sendedid+"_"+Gettedid+System.currentTimeMillis()+".amr";
                SoundUtil.getInstance().saveGettedVoice(context, cSmessage.msgBytes, voicePath);
                jsonObject1.put(JSONKeys.voicePath, voicePath);
                jsonObject1.put(JSONKeys.voiceTime,jsonObject.getIntValue(JSONKeys.voiceTime));
                break;
        }
        chatMessage.msgJson=jsonObject1.toJSONString();//转化为jsonString的形式
        return  chatMessage;
    }

    //网络传输消息转换成手机显示的消息
    //图片或语音文件名格式：<发送用户Id_接收用户Id+接收时间>
    public ClientHandleMessage Convert_Net2Client(Context context, CSmessage cSmessage, boolean isGetted) {//网络包
        ClientHandleMessage msgClient = new ClientHandleMessage();
        JSONObject json = JSON.parseObject(cSmessage.msgJson);
        int type = json.getInteger(JSONKeys.msgType);//这里是空的
        switch (type) {
            case CSKeys.CHATING_TEXT_MSG://如果是文字信息的话
                break;
            case CSKeys.CHATING_IMAGE_MSG:
                String imagePath = StaticValues.IMAGEPATH + json.getString(JSONKeys.userId)+"_"+json.getString(JSONKeys.friendId) + "_" + System.currentTimeMillis() + ".png";
                ImageUtil.getInstance().saveImage(context, cSmessage.msgBytes, imagePath);
                json.put(JSONKeys.imagePath, imagePath);
                break;
            case CSKeys.CHATING_VOICE_MSG:
                String voicePath = StaticValues.VOICEPATH + json.getString(JSONKeys.userId)+"_"+json.getString(JSONKeys.friendId) + "_" + System.currentTimeMillis() + ".amr";
                SoundUtil.getInstance().saveGettedVoice(context,cSmessage.msgBytes, voicePath);
                json.put(JSONKeys.voicePath, voicePath);
                break;
        }
        //如果是接收到的消息，好友Id是发送方Id==userId
        if(isGetted){
            json.put(JSONKeys.friendId,  json.getString(JSONKeys.userId));
        }
        msgClient.setGetted(isGetted);
        msgClient.setMsgJson(json.toJSONString());
        return msgClient;
    }

    //数据库存储消息转化成手机显示的消息
    public ClientHandleMessage convert_Db2Client(ChatMessage msgdb)
    {
        ClientHandleMessage clientHandleMessage=new ClientHandleMessage();
        JSONObject jsonDb=JSON.parseObject(msgdb.msgJson);
        jsonDb.put(JSONKeys.sendTime,msgdb.sendTime);
        jsonDb.put(JSONKeys.msgType,msgdb.msgType);
        if(msgdb.isGetted==0)
        {
            clientHandleMessage.setGetted(false);
        }else
        {
            clientHandleMessage.setGetted(true);
        }
        clientHandleMessage.setMsgJson(jsonDb.toJSONString());
        return clientHandleMessage;
    }

    //客户端消息转化为数据库消息，发送图片和语音信息的时候存储到数据库，因为路径都存在
    public ChatMessage ConvertClient2Db(ClientHandleMessage msgclient)
    {
        ChatMessage chatMessage=new ChatMessage();
        JSONObject jsonDb=new JSONObject();//这是要存储在ChatMessage中的json
        JSONObject json=JSON.parseObject(msgclient.getMsgJson());
        int type=json.getIntValue(JSONKeys.msgType);
        chatMessage.isGetted=0;
        chatMessage.msgType=type;
        chatMessage.isLooked=1;//自己发送的
        chatMessage.sendTime=json.getString(JSONKeys.sendTime);
        jsonDb.put(JSONKeys.sendTime,chatMessage.sendTime);
        jsonDb.put(JSONKeys.msgType,chatMessage.msgType);//（为了以防万一就存两次)
        jsonDb.put(JSONKeys.friendId,json.getString(JSONKeys.friendId));
        if(type==CSKeys.CHATING_IMAGE_MSG) {
            jsonDb.put(JSONKeys.imagePath, json.getString(JSONKeys.imagePath));
        }else if(type==CSKeys.CHATING_VOICE_MSG)
        {
            jsonDb.put(JSONKeys.voicePath,json.getString(JSONKeys.voicePath));
            jsonDb.put(JSONKeys.voiceTime,json.getString(JSONKeys.voiceTime));
        }
        chatMessage.msgJson=jsonDb.toJSONString();//因为这里面的json现在是String格式的
        return chatMessage;
    }


    //从接受的消息中提取出陌生人列表
    public List<StrangerBean> getstrangerlist(CSmessage cSmessage)
    {
        List<StrangerBean> strangerList=new ArrayList<>();
        JSONObject jsonObject=JSON.parseObject(cSmessage.msgJson);
        JSONArray jsonArray= JSON.parseArray(jsonObject.getString(JSONKeys.strangerList));//编程jsonarray的类型，每一个都是一个json类，里面有arraylist的内容
        for(int i=0;i<jsonArray.size();i++)
        {
            JSONObject object=jsonArray.getJSONObject(i);
            StrangerBean bean=new StrangerBean();
            bean.Latitude = object.getDoubleValue("Latitude");
            bean.Longitude = object.getDoubleValue("Longitude");
            bean.strangerId = object.getString("strangerId");
            bean.strangerName = object.getString("strangerName");
            bean.strangerLoc = object.getString("strangerLoc");
            strangerList.add(bean);
        }
        return strangerList;
    }

    //从列表中获取friendbean等
    public FriendInfo getfriendinfo(CSmessage cSmessage,Context context)
    {
         FriendInfo friendInfo=new FriendInfo();
         JSONObject jsonObject=JSON.parseObject(cSmessage.msgJson);
         int type=jsonObject.getIntValue(JSONKeys.msgType);
         String id=jsonObject.getString(JSONKeys.userId);//因为是接收到的消息
         String headpath=StaticValues.USER_HEADPATH+id+".png";
         String name=jsonObject.getString(JSONKeys.userName);
         String sex=jsonObject.getString(JSONKeys.userSex);
         String Birthday=jsonObject.getString(JSONKeys.userBirthday);
         String signature=jsonObject.getString(JSONKeys.personSignature);
         friendInfo.setFriendId(id);
         friendInfo.setFriendHeadPath(headpath);
         friendInfo.setFriendName(name);
         friendInfo.setFriendBirthday(Birthday);
         friendInfo.setFriendSex(sex);
         friendInfo.setFriendSignature(signature);
         if(type==CSKeys.ADD_FRIEND||type==CSKeys.GETA_FRIEND_INFO_NOHEAD)
         {
             ImageUtil.getInstance().saveImage(context,cSmessage.msgBytes,headpath);
         }
         return friendInfo;
    }

    //获得邀请内容，关于群组消息
    public InvitationInfoEntity getinvitefromnet(Context context,CSmessage invite)
    {
       InvitationInfoEntity entity=new InvitationInfoEntity();
        JSONObject jsonObject=JSON.parseObject(invite.msgJson);
        String groupid=jsonObject.getString(JSONKeys.groupId);
        String groupname=jsonObject.getString(JSONKeys.groupName);
        String invitorname=jsonObject.getString(JSONKeys.groupCreator);//暂时规定成只有群主才能邀请
        String topic=jsonObject.getString(JSONKeys.groupTopic);
        String groupiconpath=StaticValues.IMAGEPATH+groupid+".png";//把图片储存在这个位置的意思
        ImageUtil.getInstance().saveImage(context,invite.msgBytes,groupiconpath);
        entity.setGroupIconPath(groupiconpath);
        entity.setGroupId(groupid);
        entity.setGroupName(groupname);
        entity.setInvitorName(invitorname);
        entity.setTopic(topic);
        return entity;
    }




}
