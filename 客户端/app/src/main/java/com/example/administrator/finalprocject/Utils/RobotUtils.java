package com.example.administrator.finalprocject.Utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.administrator.finalprocject.AllMessage.RobotMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

/**
 * Created by L on 2016/11/8.
 */
public class RobotUtils {
    private static final String API_KEY="869ac4f24e44f110aeeb938724bd8436";
    public static RobotMessage sendMessage(String msg) {
        RobotMessage chatMessage = new RobotMessage();

        String jsonRes = doGet(msg);

//		Log.i("--", "图灵机器人：" + jsonRes);

        JSONObject json = JSON.parseObject(jsonRes);
        try {
            chatMessage.setMsg(json.getString("text"));
        } catch (Exception e) {
            chatMessage.setMsg("服务器繁忙，请稍后再试");
        }
        chatMessage.setDate(ClientUtils.getNowTime());
        chatMessage.setType(RobotMessage.Type.INCOMING);
        return chatMessage;
    }

    public static String doGet(String msg) {
        String result = "";
        String url = setParams(msg);
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        try {
            java.net.URL urlNet = new java.net.URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlNet
                    .openConnection();
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");

            is = conn.getInputStream();
            int len = -1;
            byte[] buf = new byte[1024];
            baos = new ByteArrayOutputStream();
            while ((len = is.read(buf)) != -1) {
                baos.write(buf, 0, len);
            }
            baos.flush();
            result = new String(baos.toByteArray());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null)
                    baos.close();
                if (is != null)
                    is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    private static String setParams(String INFO) {
        String url = "";
        try {
            url = "http://www.tuling123.com/openapi/api?key=" + API_KEY
                    + "&info=" + URLEncoder.encode(INFO, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }
}
