package com.example.administrator.finalprocject.StaticValues;

/**
 * Created by Administrator on 2016/8/30 0030.
 */
public interface StaticValues {
    int FACE_GETTED = 100; // 点击表情
    int VOICE_REC_TIME = 101;// 录音时，实时更新录音时间

    int SESSION_OPENED = 102;// 和服务器连接成功

    // String SERVER_IP = "115.28.34.148";
    String SERVER_IP="115.28.34.148";//肯定可以直接连接外网的
    //	String SERVER_IP = "10.50.44.130";
    int SERVER_PORT = 9090;

    String VOICEPATH = "/sdcard/L/voiceRecord/";// 存放语音消息的文件夹
    String IMAGEPATH = "/sdcard/L/imageRecord/";// 存放图片消息的文件夹，图片消息都放在这里面的//肯定是需要sd卡的
    String USER_HEADPATH = "/sdcard/L/userHead/";// 用户头像文件夹
    String MSG_TEXT = "/sdcard/L/msgText/";//导出的text消息文件

    String sharePreName = "com.L";// SharePreferance保存名
    String userEmail = "userEmail";// SharePreferance保存键
    String userPasswd = "userPasswd";// SharePreferance保存键

    int DEL_EDIT_TEXT = 3333;//删除

    int NET_DELAY = 3500;//网络延迟



}