package com.example.administrator.finalprocject.Handler;

import android.content.BroadcastReceiver;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.example.administrator.finalprocject.AllMessage.CSmessage;
import com.example.administrator.finalprocject.Info.ClientManger;
import com.example.administrator.finalprocject.StaticValues.CSKeys;
import com.example.administrator.finalprocject.StaticValues.JSONKeys;
import com.example.administrator.finalprocject.StaticValues.StaticValues;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.statemachine.ConsumeToEndOfSessionDecodingState;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Administrator on 2016/8/29 0029.
 */
//客户端处理业务逻辑
public class ClientMessageHandler extends IoHandlerAdapter{
      private Handler handler;
      public ClientMessageHandler(Handler handler)
      {
          this.handler=handler;
      }

    @Override
    public void sessionCreated(IoSession ioSession) throws Exception {
            super.sessionCreated(ioSession);
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        super.sessionOpened(session);
        Message msg = new Message();
        msg.what = StaticValues.SESSION_OPENED;//
        handler.sendMessage(msg);
    }

    @Override
    public void sessionClosed(IoSession ioSession) throws Exception {
        super.sessionClosed(ioSession);
        if(ClientManger.isOnline){
            ClientManger.isOnline = false;//当用户在线时，和服务器会话中断时
            Message msg = new Message();
            msg.what = CSKeys.CONNECT_DOWN;
            handler.sendMessage(msg);//这个线程发送是发送给handler，看哪里设置了handler就发送到哪里去
//			MainActivity.mp0.updateHeader();
//			Log.i("--","添加header提示");
        }
    }

    @Override
    public void sessionIdle(IoSession ioSession, IdleStatus idleStatus) throws Exception {

    }

    public void exceptionCaught(IoSession session, Throwable cause)
            throws Exception {
        super.exceptionCaught(session, cause);
        cause.printStackTrace();
    }

    @Override
    public void messageReceived(IoSession ioSession, Object message) throws Exception {
          super.messageReceived(ioSession,message);
        CSmessage cSmessage=(CSmessage)message;
        com.alibaba.fastjson.JSONObject msgJson = JSON.parseObject(cSmessage.msgJson);//里面放的是一些解释性的东西，一些标记
        Message message1=new Message();
        Log.i("--", "客户端收到： "+cSmessage.toString());
        if(cSmessage.symbol=='+')
        {
            switch (msgJson.getInteger(JSONKeys.msgType))
            {
                case CSKeys.REGISTER_SUCCESS:
                    message1.what=CSKeys.REGISTER_SUCCESS;//发送消息给handler
                    break;
                case CSKeys.REGISTER_FAILED:
                    message1.what=CSKeys.REGISTER_FAILED;
                    break;
                case CSKeys.LOGIN_SUPER_HEAD:
                    message1.what=CSKeys.LOGIN_SUPER_HEAD;
                    message1.obj=cSmessage;
                    break;
                case CSKeys.LOGIN_SUPER_FAILED:
                    message1.what=CSKeys.LOGIN_SUPER_FAILED;
                    break;
                case CSKeys.GETA_FRIEND_INFO_HEAD:
                    message1.what=CSKeys.GETA_FRIEND_INFO_HEAD;
                    message1.obj=cSmessage;
                    break;
                case CSKeys.ADD_FRIEND_SUCCESS:
                    message1.what=CSKeys.ADD_FRIEND_SUCCESS;
                    break;
                case CSKeys.ADD_FRIEND_FAILED:
                    message1.what=CSKeys.ADD_FRIEND;
                    break;
                case CSKeys.LOGIN_SUCCESS:
                    message1.what=CSKeys.LOGIN_SUCCESS;
                    break;
                case CSKeys.LOGIN_FAILED:
                    message1.what=CSKeys.LOGIN_FAILED;
                    break;
                case CSKeys.CONNECT_DOWN:
                    message1.what=CSKeys.CONNECT_DOWN;
                    break;
                case CSKeys.FIND_PASSWD_SUCCESS:
                    message1.what=CSKeys.FIND_PASSWD_SUCCESS;
                    break;
                case CSKeys.FIND_PASSWD_FAILED:
                    message1.what=CSKeys.FIND_PASSWD_SUCCESS;
                    break;
                case CSKeys.CREATE_GROUP_FAILED:
                    message1.what=CSKeys.CREATE_GROUP_FAILED;
                    break;
                case CSKeys.CREATE_GROUP_SUCCESS:
                    message1.what=CSKeys.CREATE_GROUP_SUCCESS;
                    break;
                case CSKeys.CHATING_TEXT_MSG:
                    // 文本信息:
                    message1.what = CSKeys.CHATING_TEXT_MSG;
                    message1.obj = cSmessage;//只有这些要重新传给mainactivity的东西才有重新传回来的需要
                    break;
                case CSKeys.STRANGERS_LIST_ONEKM:
                    // 一公里内陌生人列表:
                    message1.what = CSKeys.STRANGERS_LIST_ONEKM;
                    message1.obj = cSmessage;//陌生人列表
                    break;
                case CSKeys.STRANGERS_LIST_MORE:
                    // 大于一公里内陌生人列表:
                    message1.what = CSKeys.STRANGERS_LIST_MORE;
                    message1.obj = cSmessage;//陌生人列表
                    break;
                case CSKeys.FRIEND_ID_LIST:
                    message1.what=CSKeys.FRIEND_ID_LIST;
                    message1.obj=cSmessage.msgJson;
                    break;//获得好友列表
            }
        } else if (cSmessage.symbol == '-') {
            switch (msgJson.getInteger(JSONKeys.msgType)) {
                case CSKeys.LOGIN_SUPER_NOHEAD:
                    message1.what = CSKeys.LOGIN_SUPER_NOHEAD;
                    message1.obj =cSmessage;//包含用户头像（本地没有头像，
                    break;
                case CSKeys.GETA_FRIEND_INFO_NOHEAD:
                    message1.what = CSKeys.GETA_FRIEND_INFO_NOHEAD;
                    message1.obj =cSmessage;//好友的个人信息,包含好友图像,本地无头像，服务器要发头像，所以整个都需要
                    break;

                case CSKeys.GET_STRANGER_INFO:
                    message1.what = CSKeys.GET_STRANGER_INFO;
                    message1.obj = cSmessage;//陌生人的个人信息
                    break;


                case CSKeys.CHATING_IMAGE_MSG:
                    // 图片消息:
                    message1.what = CSKeys.CHATING_IMAGE_MSG;
                    message1.obj = cSmessage;
                    break;

                case CSKeys.ADD_FRIEND:
                    // 添加好友信息（别人添加我）:
                    message1.what = CSKeys.ADD_FRIEND;
                    message1.obj = cSmessage;
                    break;

                case CSKeys.CHATING_VOICE_MSG:
                    // 语音消息:
                    message1.what = CSKeys.CHATING_VOICE_MSG;
                    message1.obj =cSmessage;
                    break;

                case CSKeys.INVITE_TO_GROUP:
                    message1.what =CSKeys.INVITE_TO_GROUP;
                    message1.obj = cSmessage;
                    break;
                case CSKeys.RESET_HEAD:
                    message1.what = CSKeys.RESET_HEAD;
                    message1.obj = cSmessage;
                    break;

                case CSKeys.CREATE_GROUP_SUCCESS:
                    // 添加好友信息（别人添加我）:
                    message1.what = CSKeys.CREATE_GROUP_SUCCESS;
                    message1.obj = cSmessage;
                    break;
            }
        }
        handler.sendMessage(message1);//这是这个线程得到消息然后发送给消息队列的意思,这是客户端程序的入口
    }


    @Override
    public void messageSent(IoSession ioSession, Object message) throws Exception {
         super.messageSent(ioSession,message);
    }

    @Override
    public void inputClosed(IoSession ioSession) throws Exception {
        super.inputClosed(ioSession);
    }



}
