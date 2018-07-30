package com.example.administrator.finalprocject.AllMessage;

/**
 * Created by Administrator on 2016/8/29 0029.
 */
//作为服务器和客户端之间交互的包
public class CSmessage {
    public char symbol;// 判断是否是纯文本文件
    //如果是+表示纯文本，如果是-表示有其他的东西，比如视频和语音等
    /** 包含非文本信息详情的解释 */
    public String msgJson;//里面附加了很多东西，比如type类型之类的,注意这里面数据不是以json格式发送的，所以得到之后要进行解码
    /** 非文本信息 */
    public byte[] msgBytes;//比如头像信息之类的，因为头像是用二进制流的方式发送的,头像是用msgbyte发送的

    @Override
    public String toString() {
        return "CSmessage [symbol=" + symbol + ", msgJson=" + msgJson + "]";
    }

}
