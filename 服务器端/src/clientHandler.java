import Keys.CSKeys;
import Keys.JSONKeys;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sun.corba.se.impl.logging.IORSystemException;
import com.sun.xml.internal.bind.annotation.OverrideAnnotationOf;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.bouncycastle.jce.exception.ExtCertificateEncodingException;
import org.w3c.dom.css.CSSMediaRule;
import sun.security.pkcs11.wrapper.CK_SSL3_KEY_MAT_OUT;

/**
 * Created by L on 2016/10/7.
 */
public class clientHandler extends IoHandlerAdapter {
    public void exceptionCaught(IoSession arg0,Throwable arg1)throws Exception
    {
        arg1.printStackTrace();
    }

    public void messageRecevied(IoSession session, Object message)throws Exception
    {
        CSmessage cSmessage=(CSmessage)message;
        System.out.println("客户端接收："+cSmessage.toString());
        JSONObject jsonObject= JSON.parseObject(cSmessage.msgJson);
        if(cSmessage.symbol=='+')
        {
            switch (jsonObject.getInteger(JSONKeys.msgType))
            {
                case CSKeys.FIND_PASSWD_SUCCESS:
                    System.out.println("请查看注册邮箱");
                    break;
                case CSKeys.FIND_PASSWD_FAILED:
                    System.out.println("找回密码失败,请确认你输入的邮箱无误");
                    break;
                case CSKeys.RESET_PASSWD_FAILED:
                    System.out.println("密码修改失败");
                    break;
                case CSKeys.RESET_PASSWD_SUCCESS:
                    System.out.println("密码修改成功");
                    break;
                case CSKeys.RESET_HEAD_SUCCESS:
                    System.out.println("头像修改成功");
                    break;
                case CSKeys.RESET_HEAD_FAILED:
                    System.out.println("头像修改失败");
                    break;
                case CSKeys.LOGIN_SUCCESS:
                    System.out.println("登录成功");
                    break;
                case CSKeys.LOGIN_FAILED:
                    System.out.println("登录失败");
                    break;
            }
        }else if(cSmessage.symbol=='-')
        {
        }
    }
    @Override
    public void sessionClosed(IoSession session)throws Exception
    {
        super.sessionClosed(session);
    }

    @Override
    public void sessionOpened(IoSession session)throws Exception
    {
        CSmessage cSmessage= new CSmessage();
        JSONObject Json = new JSONObject();
        Json.put(JSONKeys.msgType, CSKeys.REBACK);
        Json.put(JSONKeys.userId, "9091");
        Json.put(JSONKeys.msgCotent, "its very good....");
        cSmessage.msgJson = Json.toJSONString();
        cSmessage.symbol = '+';
        session.write(cSmessage);
    }
}
