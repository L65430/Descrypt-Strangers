package com.example.administrator.finalprocject.Info;

/**
 * Created by Administrator on 2016/8/31 0031.
 */
//显示周围的陌生人用的
public class StrangerBean {
    public String strangerId;//陌生人Id
    public String strangerName;//陌生人姓名
    public String strangerLoc;//位置(直接显示多少公里之内的)   通过定位把周围的人的位置都发送给服务器
    //经纬度
    public double Longitude;
    public double Latitude;
    @Override
    public String toString() {
        return "StrangerBean [strangerId=" + strangerId + ", strangerName="
                + strangerName + ", strangerLoc=" + strangerLoc
                + ", Longitude=" + Longitude + ", Latitude=" + Latitude + "]";
    }

}