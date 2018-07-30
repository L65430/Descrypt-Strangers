package com.example.administrator.finalprocject.Info;

/**
 * Created by Administrator on 2016/8/29 0029.
 */
//用户的基本信息
public class ClientManger {
    public static String clientId = "";// 本机登录Id，都是static的
    public static String clientEmail = "";// 本机登录userEmail
    public static String clientName = "";// 本机登录用户名
    public static String clientSex = "";//
    public static String clientBirthday = "";
    public static String personSignature = "";// 个性签名（不存在说说什么的情况，但是有个性签名）（说说可以之后尝试加入）


    public static String province = "";// 用户所在省
    public static double Longitude;// 用户经纬度
    public static double Latitude;

    public static String myLocation;//具体地理位置

    public static boolean isOnline;// 记录在线状态


    public static boolean isRing;//是否提示响铃
    public static boolean isVibration;//是否开启震动
    public static boolean isShareLoc;//是否发送自己的位置

}
