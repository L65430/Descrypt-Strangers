package com.example.administrator.finalprocject.AllMessage;

/**
 * Created by L on 2016/11/8.
 */
public class RobotMessage {
    private String msg;
    private Type type;
    private String date;//这个是日期

    public enum Type {
        INCOMING, OUTCOMING// 接收、发送
    }

    public RobotMessage() {
    }

    public RobotMessage(String msg, Type type, String date) {
        this.msg = msg;
        this.type = type;
        this.date = date;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
