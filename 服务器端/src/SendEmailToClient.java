import com.oracle.xmlns.internal.webservices.jaxws_databinding.ExistingAnnotationsType;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Created by L on 2016/10/9.
 */
public class SendEmailToClient {
    Properties properties=null;
    Session session=null;
    Message message=null;
    Transport tran=null;//用来发送邮件的类

    public SendEmailToClient(String clientmailAddress,String subject,String content)
    {
        try
        {
            properties=new Properties();
            properties.setProperty("mail.transport.protocol","smtp");
            properties.setProperty("mail.smtp.auth", "true");//需要验证
            session=Session.getInstance(properties);
            session.setDebug(false);//控制台显示发送邮件的具体信息

            //邮件信息
            message=new MimeMessage(session);
            message.setFrom(new InternetAddress("1668718472@qq.com"));
            message.setText(content);
            message.setSubject(subject);

            //发送邮件
            tran=session.getTransport();
            //连接到qq邮箱
            tran.connect("smtp.qq.com",25,"1668718472@qq.com","6445029666A");
            tran.sendMessage(message,new Address[]{new InternetAddress(clientmailAddress)});
            tran.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
