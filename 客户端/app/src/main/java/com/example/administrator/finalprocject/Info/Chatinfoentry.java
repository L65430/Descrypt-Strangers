package com.example.administrator.finalprocject.Info;

/**
 * Created by Administrator on 2016/8/29 0029.
 */
//聊天信息
public class Chatinfoentry {
    String friendId;//好友名，或群Id
    String friendName;//用户名 或群組名（还可以是群组名的)
    String chatCreatTime;//最后一条聊天消息时间
    String chatContent;//最后一条聊天消息的内容
    int msg_num;//未读消息数
    /**0 = 普通聊天消息  1 = 系统消息，群组邀请 ; 2 = 群组聊天消息  **/
    int Msgtype;//消息类型

    public Chatinfoentry() {

    }

    public Chatinfoentry(String friendId, String friendName,
                          String chatCreatTime, String chatContent, int msg_num,
                          int Msgtype) {
        this.friendId = friendId;
        this.friendName = friendName;
        this.chatCreatTime = chatCreatTime;
        this.chatContent = chatContent;
        this.msg_num = msg_num;
        this.Msgtype = Msgtype;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getChatCreatTime() {
        return chatCreatTime;
    }

    public void setChatCreatTime(String chatCreatTime) {
        this.chatCreatTime = chatCreatTime;
    }

    public String getChatContent() {
        return chatContent;
    }

    public void setChatContent(String chatContent) {
        this.chatContent = chatContent;
    }

    public int getMsg_num() {
        return msg_num;
    }

    public void setMsg_num(int msg_num) {
        this.msg_num = msg_num;
    }

    public int getMsgtype() {
        return  Msgtype;
    }

    /**0 = 普通聊天消息  1 = 系统消息，群组邀请*/
    public void setMsgtype(int Msgtype) {
        this.Msgtype =  Msgtype;
    }

}