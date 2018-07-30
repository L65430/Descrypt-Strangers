import Keys.CSKeys;
import Keys.JSONKeys;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sun.glass.ui.SystemClipboard;
import server_utils.FileTools;
import server_utils.StaticValue;
import sun.security.pkcs11.wrapper.CK_SSL3_KEY_MAT_OUT;

import javax.xml.transform.sax.SAXTransformerFactory;

/**
 * Created by L on 2016/10/8.
 */
//转换包的方法
public class MsgTranceUtil {
    public static MsgTranceUtil getInstance() {
        return new MsgTranceUtil();
    }

    public static void main(String[]args)
    {
        System.out.print("hello wold");
    }

    /**
     * 数据库存数消息转化为网络传输消息
     *
     * @param msgDb
     * @return
     */
    public CSmessage Trance_Db2Net(MsgDb msgDb) {
        CSmessage moMsg = new CSmessage();
        switch (msgDb.msgType) {
            case CSKeys.CHATING_TEXT_MSG:
                moMsg.symbol = '+';
                moMsg.msgJson = msgDb.msgJson;
                break;
            case CSKeys.CHATING_IMAGE_MSG:
                JSONObject json = JSON.parseObject(msgDb.msgJson);
                moMsg.symbol = '-';
                String imagePath = json.getString(JSONKeys.imagePath);
                moMsg.msgBytes = FileTools.getInstance().getMultyFileBytes(
                        imagePath);
                json.remove(JSONKeys.imagePath);
                moMsg.msgJson = json.toJSONString();
                //删除本地缓存图片
                FileTools.getInstance().deleteFile(imagePath);
                break;
            case CSKeys.CHATING_VOICE_MSG:
                moMsg.symbol = '-';
                JSONObject json2 = JSON.parseObject(msgDb.msgJson);
                String voicePath = json2.getString(JSONKeys.voicePath);
                moMsg.msgBytes = FileTools.getInstance().getMultyFileBytes(
                        voicePath);
                json2.remove(JSONKeys.voicePath);
                moMsg.msgJson = json2.toJSONString();
                FileTools.getInstance().deleteFile(voicePath);
                break;

            case CSKeys.ADD_FRIEND:
                moMsg.symbol = '+';
                moMsg.msgJson = msgDb.msgJson;
                break;

            case CSKeys.RESET_HEAD:
                JSONObject resetHeadjson = JSON.parseObject(msgDb.msgJson);
                moMsg.symbol = '-';
                String headPath = resetHeadjson.getString(JSONKeys.imagePath);
                moMsg.msgBytes = FileTools.getInstance().getMultyFileBytes(
                        headPath);
                resetHeadjson.remove(JSONKeys.imagePath);
                moMsg.msgJson = resetHeadjson.toJSONString();
                //删除本地缓存图片
                FileTools.getInstance().deleteFile(headPath);
                break;

        }
        return moMsg;
    }

    /**
     * 网络消息转化为数据库存储消息
     *
     * @param moMsg
     * @return
     */
    public MsgDb Trance_Net2Db(CSmessage moMsg) {
        MsgDb msgDb = new MsgDb();
        JSONObject json = JSON.parseObject(moMsg.msgJson);
        int msgtype = json.getIntValue(JSONKeys.msgType);
        switch (msgtype) {
            case CSKeys.CHATING_TEXT_MSG:
                break;
            case CSKeys.CHATING_IMAGE_MSG:
                String imagePath = StaticValue.MSG_CACHE_IMA_P_PATH
                        + json.getString(JSONKeys.userId) + "_"
                        + System.currentTimeMillis() + ".png";
                FileTools.getInstance().saveMultyFile(imagePath, moMsg.msgBytes);
                json.put(JSONKeys.imagePath, imagePath);
                break;
            case CSKeys.CHATING_VOICE_MSG:
                String voicePath = StaticValue.MSG_CACHE_VOI_P_PATH
                        + json.getString(JSONKeys.userId) + "_"
                        + System.currentTimeMillis() + ".amr";
                FileTools.getInstance().saveMultyFile(voicePath, moMsg.msgBytes);
                json.put(JSONKeys.voicePath, voicePath);
                break;
            case CSKeys.ADD_FRIEND:
//			String headPath = StaticValues.HEAD_P_PATH
//			+ json.getString(JSONKeys.userId) + ".png";
//			json.put(JSONKeys.imagePath, headPath);
                break;

            case CSKeys.RESET_HEAD:
                String headPath = StaticValue.MSG_CACHE_IMA_P_PATH
                        + json.getString(JSONKeys.userId) + "_"
                        + System.currentTimeMillis() + ".png";
                FileTools.getInstance().saveMultyFile(headPath, moMsg.msgBytes);
                json.put(JSONKeys.imagePath, headPath);
                break;

        }
        msgDb.msgType = msgtype;
        msgDb.msgJson = json.toJSONString();
        return msgDb;
    }
}
