package com.example.administrator.finalprocject.Activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.location.LocationClient;
import com.example.administrator.finalprocject.Adapter.MsgContentAdapter;
import com.example.administrator.finalprocject.AllMessage.CSmessage;
import com.example.administrator.finalprocject.Fragment.MainPart0;
import com.example.administrator.finalprocject.Fragment.MainPart1;
import com.example.administrator.finalprocject.Fragment.MainPart2;
import com.example.administrator.finalprocject.Fragment.MainPart3;
import com.example.administrator.finalprocject.Info.Chatinfoentry;
import com.example.administrator.finalprocject.Info.ClientManger;
import com.example.administrator.finalprocject.Info.FriendInfo;
import com.example.administrator.finalprocject.Info.InvitationInfoEntity;
import com.example.administrator.finalprocject.Info.StrangerBean;
import com.example.administrator.finalprocject.R;
import com.example.administrator.finalprocject.Service.ClientService;
import com.example.administrator.finalprocject.Service.ClientService.MyBinder;
import com.example.administrator.finalprocject.StaticValues.CSKeys;
import com.example.administrator.finalprocject.StaticValues.JSONKeys;
import com.example.administrator.finalprocject.StaticValues.StaticValues;
import com.example.administrator.finalprocject.Utils.ChatDBUtils;
import com.example.administrator.finalprocject.Utils.ClientUtils;
import com.example.administrator.finalprocject.Utils.ImageUtil;
import com.example.administrator.finalprocject.Utils.MsgConvertionUtil;
import com.example.administrator.finalprocject.Utils.MyApplication;
import com.example.administrator.finalprocject.Utils.SharedPreferenceUtil;
import com.example.administrator.finalprocject.Utils.myDialog;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.handler.chain.IoHandlerChain;
import org.bouncycastle.crypto.ec.ECNewPublicKeyTransform;
import org.bouncycastle.crypto.tls.ConnectionEnd;
import org.w3c.dom.Text;

import java.io.File;
import java.security.spec.RSAMultiPrimePrivateCrtKeySpec;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    LocationClient mLocation;//位置监听模块
    SharedPreferenceUtil mSpUtil;

    GestureDetector gestureDetector;//手势识别系统
    MyOnClickListener myOnClickListener;
    private RelativeLayout topView;
    private RelativeLayout bottonView;
    ImageView ib_part0;
    ImageView ib_part1;
    ImageView ib_part2;
    ImageView ib_part3;
    Fragment[] fragmentarray;
    public static MainPart0 mp0;//最近联系人的界面
    public static MainPart1 mp1;//通讯录
    public static MainPart2 mp2;//寻找陌生人界面
    public static MainPart3 mp3;//个人中心界面
    Button btn_send;
    //侧边栏
    RelativeLayout rl_head;
    static ImageView main_iv_head;// 头像
    static TextView main_tv_nick;// 昵称
    static TextView main_tv_sign;// 个性签名
    RelativeLayout rl_loginout;
    RelativeLayout rl_exit;
    Button btn_setting;
    Button btn_reback;
    Button btn_report;
    ////////////////////////////////各种控件///////////////////////////
    public static MyBinder myBinder;
    public static IoSession session;
    private static myDialog initDialog;//初始化对话框
    private static NotificationManager notifier;//状态栏通知管理器
    public static MainActivity context;

    ///////////////////////////////标记////////////用来做判断/////
    private boolean isStart = false;
    private boolean canShake = false;
    private long mExitTime;//点击两次退出

    //消息提示
    public static SoundPool soundpool = null;//播放短小的音乐
    private static int msgcoming;//消息来了的提示音
    private static int voicerec;//录音提示
    private static int voicesend;//发送语音提示



    //service连接
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

        Log.i("--", "MainActivity-->onServiceConnected");
        myBinder = (MyBinder) service;
        myBinder.ConnectServer();
        initDialog.show();
    }//用来绑定binder的

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };
    public static Handler handler = new Handler() {//这些消息是由服务器发送回来的反馈，在ClientService里面规定了绑定到mainactivity的handler
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public void handleMessage(Message msg) {
            switch (msg.what) {//接收另一个线程发送过来的消息
                case StaticValues.SESSION_OPENED:
                    myBinder.SuperLogin();// 超级登录
                    break;

                case CSKeys.LOGIN_SUPER_HEAD:
                    // 登录成功，得到长连接，获取该用户相关信息
                    Toast.makeText(context, "连接成功", Toast.LENGTH_LONG).show();
                    ClientUtils.getInstance().saveClientInfo(context, msg);
                    initPinfo();
                    myBinder.getFriendIdList();// 发送请求获得好友列表,,,登录成功后再请求
                    // mp0.updateHeader();// 判断是否连接服务器了，
                    initDialog.dismiss();
                    break;
                case CSKeys.LOGIN_SUPER_NOHEAD:
                    // 登录成功，得到长连接，获取该用户相关信息
                    Toast.makeText(context, "连接成功", Toast.LENGTH_LONG).show();
                    ClientUtils.getInstance().saveClientInfo(context, msg);
                    initPinfo();
                    myBinder.getFriendIdList();// 发送请求获得好友列表,,,登录成功后再请求
                    // mp0.updateHeader();// 判断是否连接服务器了，
                    initDialog.dismiss();
                    break;
                case CSKeys.LOGIN_SUPER_FAILED:
                    // 登录成功，得到长连接，获取该用户相关信息
                    Toast.makeText(context, "和服务器连接失败", Toast.LENGTH_LONG).show();
                    initDialog.dismiss();
                    break;

                case CSKeys.NO_STRANGERS:
                    // 周围暂无陌生人
                    Toast.makeText(context, "周围暂无陌生人", Toast.LENGTH_LONG).show();
                    break;

                case CSKeys.STRANGERS_LIST_ONEKM:
                    // 周围陌生人列表
                    CSmessage moMsg = (CSmessage) msg.obj;
                    List<StrangerBean> strangerList = MsgConvertionUtil
                            .getInstance().getstrangerlist(moMsg);
                    mp2.setStrangerLoc(strangerList);
                    Message listMore1 = new Message();
                    listMore1.what = CSKeys.STRANGERS_LIST_ONEKM;
                    listMore1.obj = strangerList; //这里的strangerListmore就没有消息
                    StrangerListActivity.handler.sendMessage(listMore1);
                    break;
                case CSKeys.STRANGERS_LIST_MORE:
                    // 周围陌生人列表
                    CSmessage moMsg2 = (CSmessage) msg.obj;//这里传回来的就是空的，所以会出问题,是服务器的问题
                    List<StrangerBean> strangerListmore = MsgConvertionUtil
                            .getInstance().getstrangerlist(moMsg2);
                    Message listMore = new Message();
                    listMore.what = CSKeys.STRANGERS_LIST_MORE;
                    listMore.obj = strangerListmore; //这里的strangerListmore就没有消息
                    StrangerListActivity.handler.sendMessage(listMore);//从mainactivity获得消息，然后转发给这个activity
                    break;

                case CSKeys.GET_STRANGER_INFO:
                    // 陌生人个人信息
                    Message info = new Message();
                    info.what = CSKeys.GET_STRANGER_INFO;
                    info.obj = msg.obj;
                    FriendAddActivity.handler.sendMessage(info);
                    break;//这个陌生人的信息是由服务器提供的

                case CSKeys.ADD_FRIEND_SUCCESS:
                    // 添加好友成功:
                    Message notify1 = new Message();
                    notify1.what = CSKeys.ADD_FRIEND_SUCCESS;
                    FriendAddActivity.handler.sendMessage(notify1);
                    break;

                case CSKeys.ADD_FRIEND_FAILED:
                    // 添加失败:
                    Message notify2 = new Message();
                    notify2.what = CSKeys.ADD_FRIEND_FAILED;
                    FriendAddActivity.handler.sendMessage(notify2);//主线程
                    break;

                case CSKeys.FRIEND_ID_LIST:
                    // 好友Id列表:
                    JSONObject json = JSON.parseObject(msg.obj.toString());
                    String IdStr = json.getString(JSONKeys.friendIdList);
                    myBinder.getFriendList(IdStr);// 获得好友列表GETA_FRIEND_INFO_HEAD
                    break;

                case CSKeys.GETA_FRIEND_INFO_HEAD:
                    FriendInfo bean = MsgConvertionUtil.getInstance()
                            .getfriendinfo((CSmessage) msg.obj,context);//怀疑根本没有存入数据库
                    System.out.println("好友为"+bean.getFriendId());
                    mp1.AddAFriend(bean);
                    Log.i("--", "好友 ：" + bean.toString());
                    break;

                case CSKeys.GETA_FRIEND_INFO_NOHEAD:
                    FriendInfo bean2 = MsgConvertionUtil.getInstance()
                            .getfriendinfo((CSmessage) msg.obj,context);
                    mp1.AddAFriend(bean2);
                    Log.i("--", "好友 ：" + bean2.toString());
                    break;

                case CSKeys.ADD_FRIEND://被添加为好友
                    CSmessage addF = (CSmessage) msg.obj;
                    JSONObject addFjson = JSON.parseObject(addF.msgJson);//转换为json格式
                    Notification.Builder notification2 = new Notification.Builder(context);//因为handle是static的，所以并不能用getactivity
                    notification2.setContentText(addFjson.getString(JSONKeys.friendName) + "添加你为好友");
                    notification2.setContentTitle("添加好友");
                    notification2.setSmallIcon(R.drawable.imomo2);
                    notification2.setTicker("系统消息");
                    notification2.setAutoCancel(true);//点击后跳转到界面
                    notification2.setWhen(System.currentTimeMillis());
//                    Intent intent2 = new Intent(context, MainActivity.class);//之所以用context，就是因为不能用getactivity
//                    PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 0, intent2, PendingIntent.FLAG_CANCEL_CURRENT);//点击之后消失
//                    notification2.setContentIntent(pendingIntent2);
                    Notification nt2 = notification2.build();
                    notifier.notify(0, nt2);//它的新建在oncreate里,面进行,然后执行这个操作
                    FriendInfo addInfo = MsgConvertionUtil.getInstance().getfriendinfo(addF, context);
                    mp1.AddAFriend(addInfo);
                    MsgComeNotify();//更新
                    break;

                case CSKeys.CHATING_IMAGE_MSG://发送的是图片消息
                    HandleChatMsg(msg);
                    //要做的事情有判断震动还是提示音
                    MsgComeNotify();//更新消息
                    break;
                case CSKeys.CHATING_TEXT_MSG:
                    HandleChatMsg(msg);
                    MsgComeNotify();
                    break;
                case CSKeys.CHATING_VOICE_MSG:
                    HandleChatMsg(msg);
                    MsgComeNotify();
                    break;
                case CSKeys.RESET_PASSWD_SUCCESS:
                    Toast.makeText(context, "修改密码成功", Toast.LENGTH_SHORT).show();
                    break;//只是返回一个通知消息，通知是成功还是失败了
                case CSKeys.CREATE_GROUP_SUCCESS://创建群组,把陌生人拉到一起
                    Notification.Builder notification1 = new Notification.Builder(context);//因为handle是static的，所以并不能用getactivity
                    notification1.setContentText("群组创建成功");
                    notification1.setContentTitle("创建群组");
                    notification1.setSmallIcon(R.drawable.imomo2);
                    notification1.setTicker("新消息");
                    notification1.setAutoCancel(true);//点击后跳转到界面
                    notification1.setWhen(System.currentTimeMillis());
//                    Intent intent = new Intent(context, MainActivity.class);//之所以用context，就是因为不能用getactivity
//                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);//点击之消失
//                    notification1.setContentIntent(pendingIntent);
                    Notification nt = notification1.build();
                    notifier.notify(0, nt);//它的新建在oncreate里面进行,然后执行这个操作


                case CSKeys.CREATE_GROUP_FAILED:
                    Notification.Builder notification3 = new Notification.Builder(context);//因为handle是static的，所以并不能用getactivity
                    notification3.setContentText("群组创建失败");
                    notification3.setContentTitle("创建群组");
                    notification3.setSmallIcon(R.drawable.imomo2);
                    notification3.setTicker("新消息");
                    notification3.setAutoCancel(true);//点击后跳转到界面
                    notification3.setWhen(System.currentTimeMillis());
//                    Intent intent3 = new Intent(context, MainActivity.class);//之所以用context，就是因为不能用getactivity
//                    PendingIntent pendingIntent3 = PendingIntent.getActivity(context, 0, intent3, PendingIntent.FLAG_CANCEL_CURRENT);//点击之消失
//                    notification3.setContentIntent(pendingIntent3);
                    Notification nt3 = notification3.build();
                    notifier.notify(0, nt3);//它的新建在oncreate里面进行,然后执行这个操作
                    break;

                case CSKeys.INVITE_TO_GROUP:
                    CSmessage invite = (CSmessage) msg.obj;
                    handleSystemMsg(msg);
                    break;

                case CSKeys.RESET_HEAD:
                    CSmessage resetHead = (CSmessage) msg.obj;
                    JSONObject resetHeadJson = JSON.parseObject(resetHead.msgJson);
                    String friendId = resetHeadJson.getString(JSONKeys.friendId);
                    ImageUtil.getInstance().saveImage(context, resetHead.msgBytes, StaticValues.USER_HEADPATH + friendId + ".png");
                    break;
                case CSKeys.CONNECT_DOWN://连接断开
                    ClientManger.isOnline = false;
                    Notification.Builder notification4 = new Notification.Builder(context);//因为handle是static的，所以并不能用getactivity
                    notification4.setContentText("服务器断开连接，请检查网络");
                    notification4.setContentTitle("系统消息");
                    notification4.setSmallIcon(R.drawable.imomo2);
                    notification4.setTicker("新消息");
                    notification4.setAutoCancel(true);//点击后跳转到界面
                    notification4.setWhen(System.currentTimeMillis());
//                    Intent intent4 = new Intent(context, MainActivity.class);//之所以用context，就是因为不能用getactivity
//                    PendingIntent pendingIntent4 = PendingIntent.getActivity(context, 0, intent4, PendingIntent.FLAG_CANCEL_CURRENT);//点击之消失
//                    notification4.setContentIntent(pendingIntent4);
                    Notification nt4 = notification4.build();
                    notifier.notify(0, nt4);//它的新建在oncreate里面进行,然后执行这个操作
                    break;

            }
        }
    };

    //新消息提醒
    public static void MsgComeNotify() {
        if (ClientManger.isRing) {
           playSound(1);//提示音
        }
        if (ClientManger.isVibration) {
            Longvibrate();//震动
        }

    }

    //创建群组成功，添加群组到最近联系人中
    public static void createGroupSuccess(Message msg) {
        CSmessage csmessage = (CSmessage) msg.obj;
        JSONObject json = JSONObject.parseObject(csmessage.msgJson);
        String groupId = json.getString(JSONKeys.groupId);
        String groupName = json.getString(JSONKeys.groupName);
        String groupTopic = json.getString(JSONKeys.groupName);
        String groupIconPath = json.getString(StaticValues.USER_HEADPATH + groupId + ".png");//这是设置好的位置，谁是群主头像放在谁那里保管
        ImageUtil.getInstance().saveImage(context, csmessage.msgBytes, groupIconPath);//这里面放置的是群头像
        Chatinfoentry groupChat = new Chatinfoentry();//显示聊天的类型
        groupChat.setFriendId(groupId);
        groupChat.setFriendName(groupName);
        groupChat.setChatContent("");
        groupChat.setChatCreatTime("");
        groupChat.setMsg_num(0);
        groupChat.setMsgtype(2);
        MainActivity.mp0.addrecentchatitem(groupChat);
    }

    //处理系统消息
    public static void handleSystemMsg(Message msg) {
        CSmessage csmessage = (CSmessage) msg.obj;
        JSONObject json = JSONObject.parseObject(csmessage.msgJson);
        ChatDBUtils db = new ChatDBUtils(context);
        String systemId = "100000";//系统消息都默认为10000
        if (mp0.isExistsInchat(systemId))//如果已经是好友了
        {
            mp0.updaterecentchatitem(systemId, ClientUtils.getNowTime(), "有好友邀请您进入群组聊天", false);
        } else {
            //添加到最近联系人中
            Chatinfoentry entity = new Chatinfoentry();
            entity.setChatContent("有好友邀请您加入群组聊天");
            entity.setFriendId(systemId);// 系统消息Id
            entity.setFriendName("系统消息");
            entity.setChatCreatTime(ClientUtils.getNowTime());
            entity.setMsg_num(1);
            entity.setMsgtype(1);
            mp0.addrecentchatitem(entity);
        }

        InvitationInfoEntity invitation = MsgConvertionUtil.getInstance().getinvitefromnet(context, csmessage);//获得邀请内容
        if (!db.isTableExit("invitations")) {
            db.createInviteTable();//专门用来存放系统消息
        }
        db.insertinvite(invitation);
    }

    //处理接收的聊天消息
    public static void HandleChatMsg(Message msg) {
        CSmessage csmessage = (CSmessage) msg.obj;
        JSONObject json = JSONObject.parseObject(csmessage.msgJson);
        String friendId = json.getString(JSONKeys.userId);//因为接受的时候friendid应该是userid
        ChatDBUtils db =new ChatDBUtils(context);
        int type = json.getIntValue(JSONKeys.msgType);
        if (mp0.isExistsInchat(friendId)) {
            if (mp0.getChatingId().equals(friendId))//如果正在聊天
            {
                ChatActivity.AddMsgItem(MsgConvertionUtil.getInstance().Convert_Net2Client(context, csmessage, true));
                if (!db.isTableExit("msg" + ClientManger.clientId + "_" + friendId)) {
                    db.createtable(friendId);
                }
                db.savechat(MsgConvertionUtil.getInstance().Convert_Net2Db(1, 1, context, csmessage), friendId);
            } else {
                if (!db.isTableExit("msg" + ClientManger.clientId + "_" + friendId)) {
                    db.createtable(friendId);
                }//如果不在最近联系人中
                db.savechat(MsgConvertionUtil.getInstance().Convert_Net2Db(1,0,context,csmessage), friendId);
                String content = "";
                if (type == CSKeys.CHATING_IMAGE_MSG) {
                    content = "[图片]";
                } else if (type == CSKeys.CHATING_TEXT_MSG) {
                    content = json.getString(JSONKeys.msgCotent);
                } else if (type == CSKeys.CHATING_VOICE_MSG) {
                    content = "[语音]";
                }
                mp0.updaterecentchatitem(friendId, json.getString(JSONKeys.sendTime), content, false);
            }
        } else//不在最近联系人中，所以要创建最近联系人
        {
            if (!db.isTableExit("msg" + ClientManger.clientId + "_"
                    + friendId))//不在最近联系人中，消息保存到数据库
            {
                db.createtable(friendId);
            }
            db.savechat(MsgConvertionUtil.getInstance().Convert_Net2Db(1, 0, context, csmessage), friendId);
            //添加到最近联系人中
            Chatinfoentry entity = new Chatinfoentry();
            if (type == CSKeys.CHATING_IMAGE_MSG) {
                entity.setChatContent("[图片]");
            } else if (type == CSKeys.CHATING_VOICE_MSG) {
                entity.setChatContent("[语音]");
            } else if (type == CSKeys.CHATING_TEXT_MSG) {
                entity.setChatContent(json.getString(JSONKeys.msgCotent));
            }
            entity.setFriendId(friendId);
            entity.setFriendName(mp1.getFriendName(friendId));
            entity.setChatCreatTime(json.getString(JSONKeys.sendTime));
            entity.setMsg_num(1);
            int isGroupMsg = 0;
            if (json.containsKey(JSONKeys.isGroupMsg))//判断有没有指定的键，如果有再去取值
            {
                isGroupMsg = json.getInteger(JSONKeys.isGroupMsg);
            }
            if (isGroupMsg == 0) {//单人聊天消息
                entity.setMsgtype(0);
            } else if (isGroupMsg == CSKeys.GROUP_MSG) {
                //多人聊天
                entity.setMsgtype(2);
            }
        }

    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    //初始化控件
    private void initview() {
        myOnClickListener = new MyOnClickListener();
        // topView = (RelativeLayout) findViewById(R.id.main_layout_main);
        // bottonView = (RelativeLayout) findViewById(R.id.main_layout_edge);
        ib_part0 = (ImageView) findViewById(R.id.main_ib_part0);
        ib_part1 = (ImageView) findViewById(R.id.main_ib_part1);
        ib_part2 = (ImageView) findViewById(R.id.main_ib_part2);//三个下面的图标
        ib_part3=  (ImageView)findViewById(R.id.main_ib_part3);

        ib_part0.setOnClickListener(myOnClickListener);//先建立监听器，写成onclicklistener，然后设置监听器
        ib_part1.setOnClickListener(myOnClickListener);
        ib_part2.setOnClickListener(myOnClickListener);
        ib_part3.setOnClickListener(myOnClickListener);

        main_tv_nick = (TextView) findViewById(R.id.main_tv_nick);//昵称
        main_tv_sign = (TextView) findViewById(R.id.main_tv_sign);//签名

        //跳转到个人信息
        main_iv_head = (ImageView) findViewById(R.id.main_iv_head);
        main_iv_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(MainActivity.this, PersonalInfoActivity.class
                );
                startActivity(intent);//只有点击它的时候是跳转进去创建，其他都没有，所以肯定是不行的，所以要规定一开始就创建好
            }
        });

        //注销
        rl_loginout = (RelativeLayout) findViewById(R.id.main_rl_loginout);
        rl_loginout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = null;
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("注销").setMessage("确定注销？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(context, SignInActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog = builder.create();//之前只是设定了属性，并没有创建，此时才是创建
                dialog.show();
            }
        });

        //退出
        rl_exit = (RelativeLayout) findViewById(R.id.main_rl_exit);
        rl_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Dialog dialog = null;
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("退出").setMessage("确认退出?");
                builder.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // 断开服务
                                android.os.Process
                                        .killProcess(android.os.Process.myPid());
                            }
                        });
                builder.setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        });
                dialog = builder.create();
                dialog.show();
            }
        });

        //设置
        btn_setting = (Button) findViewById(R.id.main_btn_setting);
        btn_setting.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(MainActivity.this,
                        SettingActivity.class);
                startActivity(intent);
            }
        });

        //反馈
        btn_reback = (Button) findViewById(R.id.main_btn_reback);
        btn_reback.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(MainActivity.this,
                        RebackActivity.class);
                startActivity(intent);
            }
        });

        btn_report = (Button) findViewById(R.id.main_btn_report);

        //初始化个人设置
        mSpUtil = new SharedPreferenceUtil(context);
        initSettings();
        boolean isReported = mSpUtil.getIsReport();//判断有没有签到
        String ReportDate = mSpUtil.getReportDate();
        if (ReportDate.equals(new Date()) && isReported) {
            isReported = true;
        }
        if (!isReported) {
            //没签到
            btn_report.setBackgroundResource(R.drawable.bg_btn_mini);
            btn_report.setTextColor(Color.WHITE);
            btn_report.setText("签到");
        } else {
            btn_report.setBackgroundResource(R.drawable.bg_btn_mini_nor);
            btn_report.setTextColor(Color.rgb(128, 128, 128));
            btn_report.setText("已签到");
        }

        btn_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClientManger.isOnline) {
                    if (mSpUtil.getIsReport())//sharedpreference，别的activity传过来的
                    {
                        Toast.makeText(context, "已签到", Toast.LENGTH_LONG)
                                .show();
                    } else {
                        //如果没有签到就设置签到
                        mSpUtil.setIsReport(true);
                        mSpUtil.setReportDate();
                        btn_report
                                .setBackgroundResource(R.drawable.bg_btn_mini_nor);
                        btn_report.setTextColor(Color.rgb(128, 128, 128));
                        btn_report.setText("已签到");
                        //myBinder.handleVitality(CSKeys.SIGN);这是关于活力值的属性，不需要
                    }
                } else {
                    Toast.makeText(context, "您处于离线状态，无法签到", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //初始化个人设置
    public void initSettings() {
        ClientManger.isRing = mSpUtil.getIsRing();
        ClientManger.isVibration = mSpUtil.getIsVibration();
        ClientManger.isShareLoc = mSpUtil.getIsShareLoc();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLocation != null) {
            mLocation.stop();
        }
        if (connection != null) {
            unbindService(connection);
        }
    }

    private void initFragment() {
        fragmentarray = new Fragment[4];
        mp0 = new MainPart0();
        mp1 = new MainPart1();//在这个时候就已经新建了
        mp2 = new MainPart2();
        mp3 = new MainPart3();
        fragmentarray[0] = mp0;
        fragmentarray[1] = mp1;
        fragmentarray[2] = mp2;//估计是因为MainPart2还没有写完，还有个跳转没写
        fragmentarray[3] = mp3;
        fragmentarray[0].setRetainInstance(true);
        fragmentarray[1].setRetainInstance(true);
        fragmentarray[2].setRetainInstance(true);//这是用来保存之前的，不让他被销毁的！！！
        fragmentarray[3].setRetainInstance(true);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.main_layout_mid, fragmentarray[0]);
        ft.add(R.id.main_layout_mid, fragmentarray[1]);
        ft.add(R.id.main_layout_mid, fragmentarray[2]);
        ft.add(R.id.main_layout_mid, fragmentarray[3]);

        ft.hide(fragmentarray[1]);
        ft.hide(fragmentarray[2]);
        ft.hide(fragmentarray[3]);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);//加载动画
        ft.commit();
    }

    private void initMap() {
        mLocation = ((MyApplication) getApplication()).mLocationClient;
        mLocation.start();
    }


    //初始化侧边栏
    public static void initPinfo() {
        main_tv_nick.setText(ClientManger.clientName);
        main_tv_sign.setText(ClientManger.personSignature);
        main_iv_head.setImageURI(Uri.fromFile(new File(StaticValues.USER_HEADPATH
                + ClientManger.clientEmail + ".png")));//注意这个过程
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        notifier = (NotificationManager) getSystemService(NOTIFICATION_SERVICE); //这个要在这里初始化才行
        context = this;
       // this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ClientUtils.getInstance().getClientuserEmain(context);//初始化用户useremail
        Intent bindIntent = new Intent(this, ClientService.class);
        bindService(bindIntent, connection, BIND_AUTO_CREATE);//绑定这个service之后马上停止
        initview();
        initFragment();
        initMap();//初始化地图

        //初始化音乐池
        soundpool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        //向音乐池中添加音乐
        msgcoming = soundpool.load(getApplicationContext(), R.raw.msgcoming, 0);//最后一个参数是优先级
        voicerec = soundpool.load(getApplicationContext(), R.raw.voicerec, 0);
        voicesend = soundpool.load(getApplicationContext(), R.raw.voicesend, 0);

        initDialog = new myDialog(context, "加载中...");//但是没有显示要用这个
    }

    //更新个人消息
    public static void refreshPinfo(int type, String value) {
        switch (type) {
            case CSKeys.RESET_USERNAME:
                main_tv_nick.setText(value);
                break;
            case CSKeys.RESET_SIGNATUE:
                main_tv_sign.setText(value);
                break;
            case CSKeys.RESET_HEAD:
                main_iv_head.setImageURI(Uri.fromFile(new File(value)));
                Log.i("--", "侧边栏头像修改成功");
                break;
        }
    }


    //点击事件
    private class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            canShake = false;
            ImageView ib = (ImageView) v;
            ib_part0.setImageResource(R.drawable.icon_chat_home);
            ib_part1.setImageResource(R.drawable.icon_myfriend_nor);
            ib_part2.setImageResource(R.drawable.icon_find_nor);
            ib_part3.setImageResource(R.drawable.personal_center8);

            if (ib.equals(ib_part0)) {
                System.out.println("点击模块零");
                ib_part0.setImageResource(R.drawable.icon_chat_home_press);//如果按下去会变颜色
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                // FragmentTransaction对fragment进行添加,移除,替换,以及执行其他动作，通过这种方法
                //获得一个fragmentTrransaction的实例
                ft.hide(fragmentarray[1]);
                ft.hide(fragmentarray[2]);
                ft.hide(fragmentarray[3]);
                ft.show(fragmentarray[0]);
                ft.setTransition(FragmentTransaction.TRANSIT_NONE);//这表示不添加动画
                ft.commit();//保存，因为相当于一个管理器
            }
            if (ib.equals(ib_part1)) {
                System.out.println("点击模块一");
                ib_part1.setImageResource(R.drawable.icon_myfriend_press);
                FragmentTransaction ft = getFragmentManager()
                        .beginTransaction();
                ft.hide(fragmentarray[0]);
                ft.hide(fragmentarray[2]);
                ft.hide(fragmentarray[3]);
                ft.show(fragmentarray[1]);
                ft.setTransition(FragmentTransaction.TRANSIT_NONE);
                ft.commit();

            }
            if (ib.equals(ib_part2)) {
                System.out.println("点击模块二");
                ib_part2.setImageResource(R.drawable.icon_find_press);
                FragmentTransaction ft = getFragmentManager()
                        .beginTransaction();
                ft.hide(fragmentarray[0]);
                ft.hide(fragmentarray[1]);
                ft.hide(fragmentarray[3]);
                ft.show(fragmentarray[2]);
                ft.setTransition(FragmentTransaction.TRANSIT_NONE);
                ft.commit();
                isStart = true;
                // if (mlocation!=null)
                // mp2.setMyLoc(mlocation);
            }
            if (ib.equals(ib_part3)){
                System.out.println("点击模块三");
                ib_part3.setImageResource(R.drawable.person_center_pressed);//如果按下去会变颜色
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                // FragmentTransaction对fragment进行添加,移除,替换,以及执行其他动作，通过这种方法
                //获得一个fragmentTrransaction的实例
                ft.hide(fragmentarray[1]);
                ft.hide(fragmentarray[0]);
                ft.hide(fragmentarray[2]);
                ft.show(fragmentarray[3]);
                ft.setTransition(FragmentTransaction.TRANSIT_NONE);//这表示不添加动画
                ft.commit();//保存，因为相当于一个管理器
            }
        }
    }

    //跳转到最近联系人的界面
    public void LocToMp0() {
        ib_part0.setImageResource(R.drawable.icon_chat_home_press);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.hide(fragmentarray[1]);
        ft.hide(fragmentarray[2]);
        ft.show(fragmentarray[0]);
        ft.setTransition(FragmentTransaction.TRANSIT_NONE);
        ft.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出",
                        Toast.LENGTH_LONG).show();
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //播放消息提示音
    public static void playSound(int id) {
        switch (id) {
            case 1:
                soundpool.play(msgcoming, 1, 1, 0, 0, 1);
                break;
            case 2:
                soundpool.play(voicerec, 1, 1, 0, 0, 1);
                break;
            case 3:
                soundpool.play(voicesend, 1, 1, 0, 0, 1);
                break;
        }
    }

    //震动
    public static void Longvibrate()
    {
        Vibrator vibe=(Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(500);
    }


}


