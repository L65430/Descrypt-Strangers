package com.example.administrator.finalprocject.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.administrator.finalprocject.StaticValues.StaticValues;

import org.bouncycastle.crypto.params.ParametersWithSalt;

import java.util.Date;

/**
 * Created by Administrator on 2016/9/1 0001.
 */
//里面存一些共同的东西
public class SharedPreferenceUtil {
    SharedPreferences sp;
    SharedPreferences.Editor editor;//用来编辑的

    public SharedPreferenceUtil(Context context) {
        sp = context.getSharedPreferences(StaticValues.sharePreName, context.MODE_PRIVATE);
        //sharedpreference背后是用xml来存储数据的

        //私有模式，只能被该应用本身调用，所有的数据都是存在一个sharedPreference里面，然后名字是固定的e
        editor = sp.edit();
    }

    //保存
    public void SaveValue(String key, String value)
    {
        editor.putString(key,value);
        editor.commit();
    }//保存的是键值对这样的关系
    //获取值
    public String GetValue(String key)
    {
        return sp.getString(key,"");
    }
    public String getId()
    {
        return sp.getString("id","");
    }

    public String getEmail()
    {
        return sp.getString(StaticValues.userEmail,"");
    }

    public void setEmail(String email) {
        editor.putString(StaticValues.userEmail, email);
        editor.commit();
    }

    // 用户的昵称
    public String getName() {
        return sp.getString("name", "Test");
    }

    public void setName(String name) {
        editor.putString("name", name);
        editor.commit();
    }

    // 用户的密码
    public void setPasswd(String passwd) {
        editor.putString(StaticValues.userPasswd, passwd);
        editor.commit();
    }

    public String getPasswd() {
        return sp.getString(StaticValues.userPasswd, "");
    }

    // 用户性别
    public void setSex(boolean sex) {
        editor.putBoolean("sex", sex);
        editor.commit();
    }

    public boolean getSex() {
        return sp.getBoolean("sex", true);
    }

    // 是否签到
    public void setIsReport(boolean isReport) {
        editor.putBoolean("isReport", isReport);
        editor.commit();
    }

    public boolean getIsReport() {
        return sp.getBoolean("isReport", false);
    }

    //签到日期，因为一天只签到一次
    public void setReportDate(){
        editor.putString("ReportDate", new Date() + "");
        editor.commit();
    }

    public String getReportDate(){
        return sp.getString("ReportDate", "");
    }


    // 是否在后台运行标记
    public void setSignature(String s) {
        editor.putString("Signature", s);
        editor.commit();
    }

    public String getSignature() {
        return sp.getString("Signature", "helloworld");
    }

    // 是否在后台运行标记
    public void setIsBR(boolean isStart) {
        editor.putBoolean("isStart", isStart);
        editor.commit();
    }

    public boolean getIsBR() {
        return sp.getBoolean("isStart", false);
    }

    // 是否第一次运行本应用
    public void setIsFirst(boolean isFirst) {
        editor.putBoolean("isFirst", isFirst);
        editor.commit();
    }

    public boolean getisFirst() {
        return sp.getBoolean("isFirst", true);
    }

    // ////////////////////////////////////////////////////////
    // 是否有提示音
    public void setIsRing(boolean isRing) {
        editor.putBoolean("isRing", isRing);
        editor.commit();
    }

    public boolean getIsRing() {
        return sp.getBoolean("isRing", true);
    }

    // 是否有震动
    public void setIsVibration(boolean isVibration) {
        editor.putBoolean("isVibration", isVibration);
        editor.commit();
    }

    public boolean getIsVibration() {
        return sp.getBoolean("isVibration", true);
    }

    // 是否分享位置
    public void setIsShareLoc(boolean isShareLoc) {
        editor.putBoolean("isShareLoc", isShareLoc);
        editor.commit();
    }

    public boolean getIsShareLoc() {
        return sp.getBoolean("isShareLoc", true);
    }

    //////////////////////////////////////////////////////////



    // 是否有通知栏提示
    public void setIsNotice(boolean isNotice) {
        editor.putBoolean("isNotice", isNotice);
        editor.commit();
    }

    public boolean getIsNotice() {
        return sp.getBoolean("isNotice", true);
    }


}
