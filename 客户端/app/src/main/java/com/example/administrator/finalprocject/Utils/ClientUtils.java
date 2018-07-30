package com.example.administrator.finalprocject.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import com.baidu.android.bba.common.util.Util;
import com.example.administrator.finalprocject.AllMessage.CSmessage;
import com.example.administrator.finalprocject.Info.ClientManger;
import com.example.administrator.finalprocject.Service.ClientService;
import com.example.administrator.finalprocject.StaticValues.CSKeys;
import com.example.administrator.finalprocject.StaticValues.JSONKeys;
import com.example.administrator.finalprocject.StaticValues.StaticValues;

import org.apache.mina.filter.firewall.ConnectionThrottleFilter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/9/1 0001.
 */
//保存用户相关信息的工具类
public class ClientUtils {
    public static ClientUtils clientUtils;
    public static ClientUtils getInstance()
    {
        if(clientUtils==null)
        {
            synchronized (ClientUtils.class)
            {
                if(clientUtils==null)
                {
                    clientUtils=new ClientUtils();
                }
            }
        }
        return  clientUtils;
    }

    //登录通过邮箱登录
    public void getClientuserEmain(Context context)
    {
        SharedPreferenceUtil util=new SharedPreferenceUtil(context);
        ClientManger.clientEmail=util.GetValue(StaticValues.userEmail);
        ClientManger.clientId= util.GetValue(ClientManger.clientEmail);//就是通过email进行登录

    }
    //保存用户信息，当前信息
    public void saveClientInfo(Context context, Message message)
    {
        ClientManger.isOnline=true;
        CSmessage cSmessage=(CSmessage)message.obj;//转化成网络包的像是
        JSONObject jsonObject=JSON.parseObject(cSmessage.msgJson);
        if(message.what== CSKeys.LOGIN_SUPER_NOHEAD)//从服务器获取头像，本地没有头像
        {
            ImageUtil.getInstance().saveImage(context,cSmessage.msgBytes,StaticValues.USER_HEADPATH+ClientManger.clientEmail+".png");
            //用来存放用户头像文件夹
        }
        ClientManger.clientId=jsonObject.getString(JSONKeys.userId);//id和账号其实是同一个
        ClientManger.clientName = jsonObject.getString(JSONKeys.userName);
        ClientManger.clientSex = jsonObject.getString(JSONKeys.userSex);
        ClientManger.clientBirthday =jsonObject.getString(JSONKeys.userBirthday);
        ClientManger.personSignature = jsonObject.getString(JSONKeys.personSignature);

        //保存email和id一一对应的关系
        new SharedPreferenceUtil(context).SaveValue(ClientManger.clientEmail,ClientManger.clientId);
    }

    public static String getNowTime()
    {
        SimpleDateFormat format=new SimpleDateFormat("yy-MM-dd HH:mm:ss");//把时间显示成这样的形式
        String date=format.format(new Date());
        return date;
    }
}
