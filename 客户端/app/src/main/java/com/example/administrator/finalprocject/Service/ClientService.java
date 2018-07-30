package com.example.administrator.finalprocject.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.example.administrator.finalprocject.Activity.MainActivity;
import com.example.administrator.finalprocject.AllMessage.CSmessage;
import com.example.administrator.finalprocject.Codecfactory.Lcoderfactory;
import com.example.administrator.finalprocject.Handler.ClientMessageHandler;
import com.example.administrator.finalprocject.Info.ClientManger;
import com.example.administrator.finalprocject.StaticValues.CSKeys;
import com.example.administrator.finalprocject.StaticValues.JSONKeys;
import com.example.administrator.finalprocject.StaticValues.StaticValues;
import com.example.administrator.finalprocject.Utils.FileUtils;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;

public class ClientService extends Service {

    private MyBinder mBinder;

    @Override
    public void onCreate() {
        super.onCreate();
        mBinder = new MyBinder();
//		Log.i("--", "创建service");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {//里面放的是其他线程过来的消息内容
//		Log.i("--", "收到任务");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CloseSession();
//		Log.i("--", "退出service");
    }

    /**
     * 连接服务器
     */
    private class ConnectServerThread extends Thread {//放在另一个线程里面
        public void run() {
            NioSocketConnector connector = new NioSocketConnector();//这就是mina框架的使用方法
            connector.setHandler(new ClientMessageHandler(MainActivity.handler));//传到mainactivity的handler里面,这是传入传出的口
            connector.getFilterChain().addLast("codec",
                    new ProtocolCodecFilter(new Lcoderfactory()));//指定编码，过滤器
            try {
                ConnectFuture future = connector.connect(new InetSocketAddress(
                        StaticValues.SERVER_IP, StaticValues.SERVER_PORT));//连接建立完成了//这里完成了连接，服务器都是同一个
                future.awaitUninterruptibly();//等待连接建立
                MainActivity.session = future.getSession();//这是创建一个连接实例的意思
//				System.out
//						.println("session is ok" + MainActivity.ClientSession);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }//这只是用来创建连接用的


    private void CloseSession() {
        if (MainActivity.session != null
                && !MainActivity.session.isClosing()) {
            MainActivity.session.close();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("--", "绑定service");
        return mBinder;//返回了Binder,此时客户端就可以调用binder里面有的service的方法了,但是其他activity
        //如果要调用这个binde
    }

    public class MyBinder extends Binder {//通过binder来传递方法，在mainactivity里面调用binder，
        //再使用里面的方法
        /**
         * 连接服务器
         */
        public void ConnectServer(){
            new ConnectServerThread().start();
        }




        /**
         * 超级登录,得到长连接
         */
        public void SuperLogin() {
            try {
                CSmessage moMoMsg = new CSmessage();
                JSONObject Json = new JSONObject();
                Json.put(JSONKeys.userEmail, ClientManger.clientEmail);
                // 本地有用户头像，不需要从服务器获取
                if (FileUtils.isFileexist(StaticValues.USER_HEADPATH
                        + ClientManger.clientEmail + ".png")) {
                    Json.put(JSONKeys.msgType, CSKeys.LOGIN_SUPER_HEAD);//这个head就是指没有本地图像的意思，需要服务器去获取
                }
                // 本地无，需获取
                else {
                    Json.put(JSONKeys.msgType, CSKeys.LOGIN_SUPER_NOHEAD);
                }
                moMoMsg.msgJson = Json.toJSONString();
                moMoMsg.symbol = '+';
                MainActivity.session.write(moMoMsg);//表示向服务器发送这个请求
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 发送请求从服务器获取周围一公里内陌生人数据
         *
         * @author Administrator
         *
         */
        public void getStrangerListOneKm() {// 当前已经加载的数据条数
            CSmessage moMsg = new CSmessage();
            JSONObject json = new JSONObject();
            json.put(JSONKeys.msgType, CSKeys.GET_STRANGERS_LOC_ONEKM);
            json.put(JSONKeys.userId, ClientManger.clientId);
            json.put(JSONKeys.loc_province,ClientManger.province);
            json.put(JSONKeys.loc_Longitude, ClientManger.Longitude);
            json.put(JSONKeys.loc_Latitude,ClientManger.Latitude);
            moMsg.symbol = '+';
            moMsg.msgJson = json.toJSONString();
            if (MainActivity.session != null
                    && !MainActivity.session.isClosing()) {
                MainActivity.session.write(moMsg);//通过这个东西来进行发送
            }
        }

        /**
         * 大于一公里的陌生人
         *
         *
         */
        public void getStrangerListMore(int disRange) {// 当前已经加载的数据条数
            CSmessage moMsg = new CSmessage();
            JSONObject json = new JSONObject();
            json.put(JSONKeys.msgType, CSKeys.GET_STRANGERS_LOC_MORE);
            json.put(JSONKeys.userId,ClientManger.clientId);
            json.put(JSONKeys.distRange, disRange);
            json.put(JSONKeys.loc_province,ClientManger.province);
            json.put(JSONKeys.loc_Longitude, ClientManger.Longitude);
            json.put(JSONKeys.loc_Latitude, ClientManger.Latitude);
            moMsg.symbol = '+';
            moMsg.msgJson = json.toJSONString();
            if (MainActivity.session != null
                    && !MainActivity.session.isClosing()) {
                MainActivity.session.write(moMsg);
            }
        }

        /**
         * 发送位置消息
         */
        public void sendLocation() {
            CSmessage loc_msg = new CSmessage();
            loc_msg.symbol = '+';
            JSONObject loc_json = new JSONObject();
            loc_json.put(JSONKeys.msgType, CSKeys.LOCATION);
            loc_json.put(JSONKeys.userId, ClientManger.clientId);
            loc_json.put(JSONKeys.loc_province, ClientManger.province);//都是本机登录的账号的各种信息
            loc_json.put(JSONKeys.loc_Longitude, ClientManger.Longitude);
            loc_json.put(JSONKeys.loc_Latitude, ClientManger.Latitude);
            loc_msg.msgJson = loc_json.toJSONString();
            if (MainActivity.session != null
                    && !MainActivity.session.isClosing())
                MainActivity.session.write(loc_msg);
            Log.i("--", "发送前 ： " + loc_json.toJSONString());
        }

        /**
         * 请求获取一个陌生人的个人信息
         *
         * @param friendId
         */
        public void getStrangerInfo(String friendId) {
            Log.i("--", "getFriendInfo 陌生人id =   ：" + friendId);
            CSmessage msg = new CSmessage();
            msg.symbol = '+';
            JSONObject json = new JSONObject();
            json.put(JSONKeys.msgType, CSKeys.GET_STRANGER_INFO);// 需要获取头像
            json.put(JSONKeys.userId,ClientManger.clientId);
            json.put(JSONKeys.friendId, friendId);
            msg.msgJson = json.toJSONString();
            if (MainActivity.session != null
                    && !MainActivity.session.isClosing())
                MainActivity.session.write(msg);
        }//都是向服务器发起请求

        /**
         * 添加好友
         *
         * @param friendId
         */
        public void addFriend(String friendId) {
            CSmessage msg = new CSmessage();
            msg.symbol = '+';
            JSONObject json = new JSONObject();
            json.put(JSONKeys.msgType, CSKeys.ADD_FRIEND);// 添加好友，是一个用户向另一个用户要求添加好友，然后发起请求方不能信息，接收方可以看到消息
            json.put(JSONKeys.userId, ClientManger.clientId);
            json.put(JSONKeys.friendId, friendId);//虽然发起请求的时候是匿名的，但是id我们是知道的
            msg.msgJson = json.toJSONString();
            if (MainActivity.session!= null
                    && !MainActivity.session.isClosing())
                MainActivity.session.write(msg);
        }

        /**
         * 删除好友,只发送消息
         *
         * @param friendId
         */
        public void deleteFriend(String friendId) {
            CSmessage msg = new CSmessage();//客户端和服务器之间交互用的包
            msg.symbol = '+';
            JSONObject json = new JSONObject();
            json.put(JSONKeys.msgType, CSKeys.DELETE_FRIEND);// 添加好友
            json.put(JSONKeys.userId, ClientManger.clientId);
            json.put(JSONKeys.friendId, friendId);
            msg.msgJson = json.toJSONString();
            if (MainActivity.session != null
                    && !MainActivity.session.isClosing())
                MainActivity.session.write(msg);
        }



        /**
         * 想服务器获取好友Id列表(先得到Id，然后查看本地是否有该好友的头像，没有就要像服务器请求)
         */
        public void getFriendIdList() {
            CSmessage msg = new CSmessage();
            msg.symbol = '+';
            JSONObject json = new JSONObject();
            json.put(JSONKeys.msgType, CSKeys.GET_FRIEND_ID_LIST);//
            json.put(JSONKeys.userId, ClientManger.clientId);
            msg.msgJson = json.toJSONString();
            if (MainActivity.session != null
                    && !MainActivity.session.isClosing())
                MainActivity.session.write(msg);
        }

        /**
         * 获得好友列表
         *
         * @param IdStr
         */
        public void getFriendList(String IdStr) {
            Log.i("--", "好友Id列表 ：" + IdStr);
            if (!IdStr.equals("none")) {
                String[] Ids = IdStr.split(",");
                for (String friendId : Ids) {
                    CSmessage msg = new CSmessage();
                    msg.symbol = '+';
                    JSONObject json = new JSONObject();
                    json.put(JSONKeys.userId, ClientManger.clientId);
                    json.put(JSONKeys.friendId, friendId);

                    if (FileUtils.isFileexist(StaticValues.USER_HEADPATH
                            + friendId + ".png")) {
                        json.put(JSONKeys.msgType,
                                CSKeys.GETA_FRIEND_INFO_HEAD);// 不需要获取头像
                    } else {
                        json.put(JSONKeys.msgType,
                                CSKeys.GETA_FRIEND_INFO_NOHEAD);// 需要获取头像
                    }
                    msg.msgJson = json.toJSONString();//放到这里面，然后发送给服务器，服务器解析
                    if (MainActivity.session != null
                            && !MainActivity.session.isClosing())
                        MainActivity.session.write(msg);
                    Log.i("--", "获取好友信息： " + msg.msgJson);
                }
            }
        }

        /**
         * 修改个人信息,包括密码
         *
         * @param type
         * @param value
         */
        public void ResetUserInfo(int type, String value) {
            Log.i("--", "type = " + type + "value = " + value);
            CSmessage moMoMsg = new CSmessage();
            moMoMsg.symbol = '+';
            JSONObject Json = new JSONObject();
            Json.put(JSONKeys.userId, ClientManger.clientId);
            switch (type) {
                case CSKeys.RESET_USERNAME:
                    Json.put(JSONKeys.msgType, CSKeys.RESET_USERNAME);
                    Json.put(JSONKeys.userName, value);
                    break;
                case CSKeys.RESET_SEX:
                    Json.put(JSONKeys.msgType, CSKeys.RESET_SEX);
                    Json.put(JSONKeys.userSex, value);
                    break;
                case CSKeys.RESET_BIRTHDAY:
                    Json.put(JSONKeys.msgType, CSKeys.RESET_BIRTHDAY);
                    Json.put(JSONKeys.userBirthday, value);
                    break;
                case CSKeys.RESET_SIGNATUE:
                    Json.put(JSONKeys.msgType,CSKeys.RESET_SIGNATUE);
                    Json.put(JSONKeys.personSignature, value);
                    break;
                case CSKeys.RESET_PASSWD:
                    Json.put(JSONKeys.msgType, CSKeys.RESET_PASSWD);
                    Json.put(JSONKeys.userPasswd, value);
                    break;
            }
            moMoMsg.msgJson = Json.toJSONString();
            if (MainActivity.session != null
                    && !MainActivity.session.isClosing())
                MainActivity.session.write(moMoMsg);
        }

        /**
         * 修改头像
         * @param headBytes
         */
        public void ResetUserHead(byte[] headBytes) {
            CSmessage moMoMsg = new CSmessage();
            moMoMsg.symbol = '-';
            JSONObject Json = new JSONObject();
            Json.put(JSONKeys.msgType, CSKeys.RESET_HEAD);
            Json.put(JSONKeys.userId, ClientManger.clientId);
            moMoMsg.msgJson = Json.toJSONString();
            moMoMsg.msgBytes = headBytes;
            if (MainActivity.session != null
                    && !MainActivity.session.isClosing())
                MainActivity.session.write(moMoMsg);
        }


        /**
         * 发送反馈信息
         * @param value
         */
        public void SendReback(String value){
            CSmessage moMoMsg = new CSmessage();
            moMoMsg.symbol = '+';
            JSONObject Json = new JSONObject();
            Json.put(JSONKeys.msgType, CSKeys.REBACK);
            Json.put(JSONKeys.userId, ClientManger.clientId);
            Json.put(JSONKeys.msgCotent, value);
            moMoMsg.msgJson = Json.toJSONString();
            if (MainActivity.session != null
                    && !MainActivity.session.isClosing())
                MainActivity.session.write(moMoMsg);
        }

        /**
         * 发送聊天消息
         * @param moMsg
         */
        public void sendCharMsg(CSmessage moMsg){
            if (MainActivity.session != null
                    && !MainActivity.session.isClosing())
                MainActivity.session.write(moMsg);//反正是发送出去给服务器，服务器怎么搞要继续写，服务器要转给其他人,发送出去的时候都是写成CSmessage的形式的
        }

    }

}










