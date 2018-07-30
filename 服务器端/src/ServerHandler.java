import Keys.CSKeys;
import Keys.JSONKeys;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sun.javaws.exceptions.ExitException;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.omg.PortableServer.SERVANT_RETENTION_POLICY_ID;

/**
 * Created by L on 2016/10/10.
 */
public class ServerHandler extends IoHandlerAdapter{
    private IoSession session;

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        super.exceptionCaught(session, cause);
    }

    @Override
    public void inputClosed(IoSession session) throws Exception {
        super.inputClosed(session);
    }

    public void messageReceived(IoSession session, Object message)
            throws Exception {
        super.messageReceived(session, message);
        this.session = session;
        CSmessage moMoMsg = (CSmessage) message;
        JSONObject msgJson = JSON.parseObject(moMoMsg.msgJson);
//		System.out.println("接收到消息");
        // ManageClientSession.addSession("9091", session);
//		System.out.println("服务器接收:" + moMoMsg.toString());
        if (moMoMsg.symbol == '+') {
            switch (msgJson.getInteger(JSONKeys.msgType)) {
                case CSKeys.FIND_PASSWD:
                    // 找回密码:
                    ServerUtils.getInstance().handleFindPasswd(session, moMoMsg);
                    break;

                // ----------------修改个人信息--------------------------------//
                case CSKeys.RESET_PASSWD:
                    // 重置密码:
                    ServerUtils.getInstance().handleResetPasswd(session, moMoMsg);
                    break;

                case CSKeys.RESET_USERNAME:
                    // 修改用户名:
                    ServerUtils.getInstance().handleResetUserInfo(moMoMsg);
                    break;
                case CSKeys.RESET_SEX:
                    // 修改性别:
                    ServerUtils.getInstance().handleResetUserInfo(moMoMsg);
                    break;
                case CSKeys.RESET_BIRTHDAY:
                    // 修改生日:
                    ServerUtils.getInstance().handleResetUserInfo(moMoMsg);
                    break;
                case CSKeys.RESET_SIGNATUE:
                    // 修改个人签名:
                    ServerUtils.getInstance().handleResetUserInfo(moMoMsg);
                    break;
                // ----------------修改个人信息--------------------------------//

                // ------------------登录---------------------------//
                case CSKeys.LOGIN:
                    // 用户登录:
                    ServerUtils.getInstance().handleLogin(session, moMoMsg);
                    break;
                case CSKeys.LOGIN_SUPER_HEAD:
                    // 用户超级登录有头像:
                    ServerUtils.getInstance().handleLoginSuper(session, moMoMsg);
                    break;
                case CSKeys.LOGIN_SUPER_NOHEAD:
                    // 用户超级登录无头像:
                    ServerUtils.getInstance().handleLoginSuper(session, moMoMsg);
                    break;
                // ------------------登录---------------------------//

                case CSKeys.CHATING_TEXT_MSG:
                    // 文本信息:
                    ServerUtils.getInstance().handleChatMsg(moMoMsg);
                    break;

                // ------------------位置相关服务-----------------------//
                case CSKeys.LOCATION:
                    // 用户地理位置消息
                    ServerUtils.getInstance().handleLocation(moMoMsg);
                    break;
                case CSKeys.GET_STRANGERS_LOC_ONEKM:
                    // 请求得到周围一公里陌生人位置
                    ServerUtils.getInstance().handleGetStrangersLoc(moMoMsg);
                    break;

                case CSKeys.GET_STRANGERS_LOC_MORE:
                    // 请求得到周围大于一公里内陌生人位置
                    ServerUtils.getInstance().handleGetStrangersLoc(moMoMsg);
                    break;

                case CSKeys.GET_STRANGER_INFO:
                    // 请求得到周围大于一公里内陌生人信息
                    ServerUtils.getInstance().handleGetFriendInfo(session, moMoMsg);
                    break;
                // ------------------位置相关服务-----------------------//



                case CSKeys.GETA_FRIEND_INFO_HEAD:
                    // 请求得到一个陌生人的具体信息,不用发头像
                    ServerUtils.getInstance().handleGetFriendInfo(session, moMoMsg);
                    break;
                case CSKeys.GETA_FRIEND_INFO_NOHEAD:
                    // 请求得到一个陌生人的具体信息，要发头像
                    ServerUtils.getInstance().handleGetFriendInfo(session, moMoMsg);
                    break;

                case CSKeys.ADD_FRIEND:
                    // 添加好友
                    ServerUtils.getInstance().handleAddFriend(session, moMoMsg);
                    break;
                case CSKeys.DELETE_FRIEND:
                    // 删除好友
                    ServerUtils.getInstance().handleDeleteFriend(session, moMoMsg);
                    break;

                case CSKeys.GET_FRIEND_ID_LIST:
                    // 获得好友Id列表
                    ServerUtils.getInstance().handleGetFriendIdList(session, moMoMsg);
                    break;

                case CSKeys.REBACK:
                    // 用户反馈
                    ServerUtils.getInstance().handleReback(moMoMsg);
                    break;

                case CSKeys.SIGN:
                    // 用户签到
                    ServerUtils.getInstance().handleVitality(moMoMsg);
                    break;
//                case CSKeys.PASS_GAME:
//                    // 用户签到
//                    ServerUtils.getInstance().handleVitality(moMoMsg);
//                    break;

                case CSKeys.AGREEE_TO_GROUP:
                    ServerUtils.getInstance().handleRecieveToGroup(moMoMsg);
                    break;

            }

        } else if (moMoMsg.symbol == '-') {
            switch (msgJson.getInteger(JSONKeys.msgType)) {
                case CSKeys.REGISTER:
                    // 注册:
                    ServerUtils.getInstance().handleRegister(session, moMoMsg);
                    //还要在这里注册Game
                    ServerUtils.getInstance().handleRegisterGame(session,moMoMsg);
                    break;
                case CSKeys.RESET_HEAD:
                    // 修改头像:
                    ServerUtils.getInstance().handleResetHead(session, moMoMsg);
                    break;
                case CSKeys.CHATING_IMAGE_MSG:
                    // 图片消息:
                    ServerUtils.getInstance().handleChatMsg(moMoMsg);
                    break;
                case CSKeys.CHATING_VOICE_MSG:
                    // 语音消息:
                    ServerUtils.getInstance().handleChatMsg(moMoMsg);
                    break;

                case CSKeys.CREATE_GROUP:
                    // 注册:
                    ServerUtils.getInstance().handleCreateGroup(session, moMoMsg);
                    break;
            }

        }

    }

    public void messageSent(IoSession session,Object message)throws Exception
    {
        super.messageSent(session,message);
    }

    @Override
    public void sessionClosed(IoSession session)throws Exception
    {
        super.sessionClosed(session);
        long sessionId=session.getId();
        if(ManagerIdSessions.isContainsId(sessionId))
        {
            String userId=ManagerIdSessions.getUserId(sessionId);
            ManagerIdSessions.deleteUserId(sessionId);//删除这个会话
            ManagerClientSession.deleteSession(userId);
            ManagerLocMap.deleteOneUser(userId);
        }
    }


    @Override
    public void sessionCreated(IoSession session) throws Exception {
        super.sessionCreated(session);
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status)
            throws Exception {
        super.sessionIdle(session, status);
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        super.sessionOpened(session);
//		System.out.println("连接+" + session);
    }



}
