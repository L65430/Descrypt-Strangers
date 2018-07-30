import Keys.CSKeys;
import Keys.JSONKeys;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.support.odps.udf.JSONArrayAdd;
import com.sun.corba.se.impl.logging.IORSystemException;
import com.sun.corba.se.spi.monitoring.MonitoringConstants;
import com.sun.org.apache.bcel.internal.generic.FREM;
import com.sun.org.apache.bcel.internal.generic.SIPUSH;
import com.sun.org.apache.regexp.internal.RE;
import com.sun.org.apache.xerces.internal.impl.dv.dtd.NOTATIONDatatypeValidator;
import com.sun.org.apache.xml.internal.serializer.utils.MsgKey;
import com.sun.webkit.dom.CSSStyleDeclarationImpl;
import com.sun.xml.internal.ws.server.provider.AsyncProviderInvokerTube;
import jdk.nashorn.internal.scripts.JO;
import org.apache.mina.core.session.IoSession;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import server_utils.FileTools;
import server_utils.StaticValue;
import sun.management.snmp.jvminstr.JvmOSImpl;
import sun.security.pkcs11.wrapper.CK_ECDH1_DERIVE_PARAMS;
import sun.security.pkcs11.wrapper.CK_SSL3_KEY_MAT_OUT;

import java.nio.file.attribute.GroupPrincipal;
import java.security.acl.LastOwnerException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by L on 2016/10/10.
 */
//各种处理
public class ServerUtils {
    static ServerUtils serverUtils;
    public static ServerUtils getInstance()
    {
        return  new ServerUtils();
    }

    //处理注册
    public void handleRegister(IoSession session,CSmessage cSmessage)
    {
        JSONObject json= JSON.parseObject(cSmessage.msgJson);
        SqlModel sqlModel=new SqlModel();
        String userEmail=json.getString(JSONKeys.userEmail);
        String userId=sqlModel.allocateId();//自动分配id
        String userHeadPath= StaticValue.HEAD_P_PATH+userId+".png";
        FileTools.getInstance().saveMultyFile(userHeadPath,cSmessage.msgBytes);
        String sql="insert into ClientMessage values (?,?,?,?,?,?,?,?,?)";
        String[] paras = { userId, userEmail, json.getString(JSONKeys.userName),
                json.getString(JSONKeys.userPasswd), userHeadPath,
                json.getString(JSONKeys.userSex),
                json.getString(JSONKeys.userBirthday), "" , 0+""};

        CSmessage notify=new CSmessage();
        notify.symbol='+';
        JSONObject NotifyJson=new JSONObject();
        if(sqlModel.updateDb(sql,paras))
        {
            NotifyJson.put(JSONKeys.msgType,CSKeys.REGISTER_SUCCESS);
            System.out.println("注册成功");
        }else
        {
            System.out.print("注册失败");
            NotifyJson.put(JSONKeys.msgType,CSKeys.REGISTER_FAILED);
        }
        notify.msgJson=NotifyJson.toJSONString();
        session.write(notify);//发送message消息，返回注册成功消息
    }

    public void handleRegisterGame(IoSession session,CSmessage cSmessage)
    {
        JSONObject json= JSON.parseObject(cSmessage.msgJson);
        SqlModel sqlModel=new SqlModel();
        String userEmail=json.getString(JSONKeys.userEmail);
        String userId=sqlModel.allocateId();//自动分配id
        String userHeadPath= StaticValue.HEAD_P_PATH+userId+".png";
        FileTools.getInstance().saveMultyFile(userHeadPath,cSmessage.msgBytes);
        String sql="insert into Game values (?,?,?,?,?)";
        String[] paras = { userEmail, "", "", "" , ""};

        CSmessage notify=new CSmessage();
        notify.symbol='+';
        JSONObject NotifyJson=new JSONObject();
        if(sqlModel.updateDb(sql,paras))
        {
            NotifyJson.put(JSONKeys.msgType,CSKeys.REGISTER_SUCCESS);
            System.out.println("注册游戏用户成功");
        }else
        {
            System.out.print("注册游戏用户失败");
            NotifyJson.put(JSONKeys.msgType,CSKeys.REGISTER_FAILED);
        }
        notify.msgJson=NotifyJson.toJSONString();
        session.write(notify);//发送message消息，返回游戏成功消息
    }

    //处理找回密码
    public void handleFindPasswd(IoSession ioSession,CSmessage cSmessage)
    {
        JSONObject jsonObject=JSON.parseObject(cSmessage.msgJson);
        String userEmail=jsonObject.getString(JSONKeys.userEmail);
        SqlModel sqlModel=new SqlModel();
        String userName=sqlModel.getUserName(userEmail,true);
        CSmessage notify=new CSmessage();
        notify.symbol='+';//传入的是文字信息
        JSONObject NotifyJson=new JSONObject();
        String newPwd=PasswordUtil.getInstance().createNewPwd();//密码是随机生成的
        if(!userName.equals("null")){
            String sql="update ClientMessage set userPasswd = ? where userEmail = ?";
            String []paras={PasswordUtil.getInstance().toMD5(newPwd),userEmail};
            if(sqlModel.updateDb(sql,paras))
            {
                new SendEmailToClient(userEmail, "找回密码", "尊敬的" + userName
                        + ":\n	您好，系统为您随机生成的密码是:" + newPwd + ",登录后请尽快修改密码!");
                NotifyJson.put(JSONKeys.msgType,CSKeys.FIND_PASSWD_SUCCESS);
            }else
            {
                NotifyJson.put(JSONKeys.msgType,CSKeys.FIND_PASSWD_FAILED);
            }
        }else
        {
            System.out.println("没有该用户");
            NotifyJson.put(JSONKeys.msgType, CSKeys.FIND_PASSWD_FAILED);
        }
        notify.msgJson=NotifyJson.toJSONString();
        ioSession.write(notify);
    }

    //处理重置密码
    public void handleResetPasswd(IoSession session, CSmessage moMoMsg) {
        JSONObject json = JSON.parseObject(moMoMsg.msgJson);
        String userId = json.getString(JSONKeys.userId);
        SqlModel model = new SqlModel();
        String sql = "update ClientMessage set userPasswd = ? where userId = ?";
        String[] paras = { json.getString(JSONKeys.userPasswd), userId };
        CSmessage Notify = new CSmessage();
        Notify.symbol = '+';
        JSONObject NotifyJson = new JSONObject();
        if (model.updateDb(sql, paras)) {
            System.out.println("修改密码成功");
            NotifyJson.put(JSONKeys.msgType, CSKeys.RESET_PASSWD_SUCCESS);
        } else {
            System.out.println("修改密码失败");
            NotifyJson.put(JSONKeys.msgType, CSKeys.RESET_PASSWD_FAILED);
        }
        Notify.msgJson = NotifyJson.toJSONString();
        session.write(Notify);
    }

    //处理修改用户名
    public void handleResetUserInfo(CSmessage cSmessage)
    {
       JSONObject jsonObject= JSON.parseObject(cSmessage.msgJson);
        String userId=jsonObject.getString(JSONKeys.userId);
        int type=jsonObject.getIntValue(JSONKeys.msgType);
        SqlModel sqlModel=new SqlModel();
        String sql="";
        String paras[]=new String[2];
        switch (type)
        {
            case CSKeys.RESET_USERNAME:
                sql = "update ClientMessage set userName = ? where userId = ?";
                paras[0] = jsonObject.getString(JSONKeys.userName);
                break;
            case CSKeys.RESET_SEX:
                sql = "update ClientMessage set userSex = ? where userId = ?";
                paras[0] = jsonObject.getString(JSONKeys.userSex);
                break;
            case CSKeys.RESET_BIRTHDAY:
                sql = "update ClientMessage set userBirthday = ? where userId = ?";
                paras[0] = jsonObject.getString(JSONKeys.userBirthday);
                break;
            case CSKeys.RESET_SIGNATUE:
                sql = "update ClientMessage set personSignature = ? where userId = ?";
                paras[0] = jsonObject.getString(JSONKeys.personSignature);
                break;
        }
        paras[1]=userId;
        if(sqlModel.updateDb(sql,paras))
        {
            System.out.println("修改用户信息成功");
        }else
        {
            System.out.println("修改用户信失败");
        }
    }

    //处理修改头像
    public void handleResetHead(IoSession session,CSmessage cSmessage)
    {
        JSONObject jsonObject=JSON.parseObject(cSmessage.msgJson);
        String userId= jsonObject.getString(JSONKeys.userId);
        String userHeadPath=StaticValue.HEAD_P_PATH+userId+".png";
        CSmessage Notify=new CSmessage();
        Notify.symbol='+';
        JSONObject NotifyJson=new JSONObject();
        try
        {
           FileTools.getInstance().saveMultyFile(userHeadPath,cSmessage.msgBytes);
            System.out.print("修改头像成功");
            NotifyJson.put(JSONKeys.msgType,CSKeys.RESET_HEAD_SUCCESS);

            SqlModel sqlModel=new SqlModel();
            String[] friendList=sqlModel.getFriendIds(userId);
            CSmessage resetHead=new CSmessage();
            resetHead.symbol='-';
            JSONObject resetHeadJson=new JSONObject();
            resetHeadJson.put(JSONKeys.msgType,CSKeys.RESET_HEAD);//此时还不知道是否成功了
            resetHeadJson.put(JSONKeys.friendId,userId);//通知好友是谁改头像
            resetHeadJson.put(JSONKeys.userId,userId);//是用户发的这条消息
            resetHead.msgJson=resetHeadJson.toJSONString();
            resetHead.msgBytes=cSmessage.msgBytes;//就是传进来的消息
            for(String friendId:friendList)
            {
                //判断是否在线
                if(ManagerClientSession.isContainsId(friendId))
                {
                    ManagerClientSession.getSession(friendId).write(resetHead);//发送给好友谁修改了头像
                }else
                {
                    if(!sqlModel.isTableExists("mc_"+friendId))
                        sqlModel.createCacheTable(friendId);//创建缓存数据库，上线之后再发送
                    MsgDb msgDb=MsgTranceUtil.getInstance().Trance_Net2Db(resetHead);//转换成数据库消息
                    if(sqlModel.insertCacheMsg(msgDb,friendId))
                    {
                        System.out.println("缓存成功");
                    }else
                        System.out.println("缓存失败");
                }
            }
        }catch (Exception e)
        {
            //修改失败
            NotifyJson.put(JSONKeys.msgType,CSKeys.RESET_HEAD_FAILED);
        }
        Notify.msgJson=NotifyJson.toJSONString();
        session.write(Notify);//这是发送给用户的，告诉用户他的修改信息失败了
    }

    //处理用户登录
    public void handleLogin(IoSession session,CSmessage cSmessage)
    {
        JSONObject jsonObject=JSON.parseObject(cSmessage.msgJson);
        CSmessage notify=new CSmessage();
        notify.symbol='+';
        JSONObject NotifyJson=new JSONObject();
        if(new SqlModel().checkUser(jsonObject.getString(JSONKeys.userEmail),jsonObject.getString(JSONKeys.userPasswd)))
        {
            NotifyJson.put(JSONKeys.msgType,CSKeys.LOGIN_SUCCESS);//登录陈宫
        }else
        {
            NotifyJson.put(JSONKeys.msgType,CSKeys.LOGIN_FAILED);
        }
        notify.msgJson=NotifyJson.toJSONString();
        session.write(notify);
    }

    //处理用户超级登录
    public void handleLoginSuper(IoSession session,CSmessage cSmessage)
    {
        JSONObject jsonObject=JSON.parseObject(cSmessage.msgJson);
        SqlModel model=new SqlModel();
        String userEmail=jsonObject.getString(JSONKeys.userEmail);
        int type=jsonObject.getIntValue(JSONKeys.msgType);
        JSONObject info=model.getUserInfo(userEmail,true);
        String userId=info.getString(JSONKeys.userId);
        CSmessage notify=new CSmessage();
        JSONObject NotifyJson=new JSONObject();
        if(info!=null)
        {
            ManagerIdSessions.addUserId(session.getId(),userId);
            ManagerClientSession.addSession(info.getString(JSONKeys.userId),session);
            NotifyJson.put(JSONKeys.userId,userId);
            NotifyJson.put(JSONKeys.userName,info.getString(JSONKeys.userName));
            NotifyJson.put(JSONKeys.userSex,info.getString(JSONKeys.userSex));
            NotifyJson.put(JSONKeys.userBirthday,info.getString(JSONKeys.userBirthday));
            NotifyJson.put(JSONKeys.personSignature,info.getString(JSONKeys.personSignature));
            if(type==CSKeys.LOGIN_SUPER_HEAD)
            {
                notify.symbol='+';
                NotifyJson.put(JSONKeys.msgType,CSKeys.LOGIN_SUPER_HEAD);
                System.out.println("用户本地有头像");
            }else if(type==CSKeys.LOGIN_SUPER_NOHEAD)
            {
                System.out.println("用户本地无头像");
                notify.symbol='-';
                NotifyJson.put(JSONKeys.msgType,CSKeys.LOGIN_SUPER_NOHEAD);
                notify.msgBytes=FileTools.getInstance().getMultyFileBytes(info.getString(JSONKeys.userHeadPath));
            }
        }else
        {
            NotifyJson.put(JSONKeys.msgType,CSKeys.LOGIN_SUPER_FAILED);
        }
        notify.msgJson=NotifyJson.toJSONString();
        session.write(notify);
        sendCacheMsg(session,userId,model);
    }

    //转发离线消息
    private void sendCacheMsg(IoSession session,String userId,SqlModel model)
    {
        if(model.isTableExists("mc_"+userId))
        {
            if(model.getMsgCount(userId)>0)//表示有离线消息
            {
                List<MsgDb> list=model.getCacheMsgs(userId);
                for(MsgDb msgDb:list)
                {
                    CSmessage cSmessage=MsgTranceUtil.getInstance().Trance_Db2Net(msgDb);//转化为网络消息
                    session.write(cSmessage);//每一次都转发
                }
                model.clearMsgCache(userId);//清空离线文件
            }
        }
    }

    //记录用户所在的位置
    public void handleLocation(CSmessage cSmessage)
    {
        JSONObject loc_json=JSON.parseObject(cSmessage.msgJson);
        String userId=loc_json.getString(JSONKeys.userId);
        String province=loc_json.getString(JSONKeys.loc_province);
        myLocationBean locationBean=new myLocationBean();
        locationBean.province=province;
        locationBean.longitude=loc_json.getDoubleValue(JSONKeys.loc_Longitude);
        locationBean.latitude=loc_json.getDoubleValue(JSONKeys.loc_Latitude);
        System.out.println(locationBean.toString());
        if(ManagerLocMap.isContainsProvince(province))
        {
           ManagerLocMap.getAProvinceLoc(province).addLocation(userId,
                    locationBean);// 添加到所在省
        }else
        {
            ManagerAProvinceLoc aProvinceLoc=new ManagerAProvinceLoc(province);
            aProvinceLoc.addLocation(userId,locationBean);
            ManagerLocMap.addAProvinceLoc(province,aProvinceLoc);
        }
    }

    //处理周围陌生人的位置
    public void handleGetStrangersLoc(CSmessage cSmessage)
        {
        JSONObject jsonObject=JSON.parseObject(cSmessage.msgJson);
        int type=jsonObject.getIntValue(JSONKeys.msgType);
        String userId=jsonObject.getString(JSONKeys.userId);
        String province=jsonObject.getString(JSONKeys.loc_province);
        double longitude=jsonObject.getDoubleValue(JSONKeys.loc_Longitude);
        double latitude= jsonObject.getDoubleValue(JSONKeys.loc_Latitude);
        CSmessage cSmessage1=new CSmessage();
        cSmessage1.symbol='+';
        JSONObject jsonSend=new JSONObject();

        if(type==CSKeys.GET_STRANGERS_LOC_ONEKM)
        {
           if(ManagerLocMap.isContainsProvince(province))
           {
               ManagerAProvinceLoc aProvinceLoc=ManagerLocMap.getAProvinceLoc(province);
               if(aProvinceLoc.getCount()>=1)
               {
                   List<StrangerBean>list=aProvinceLoc.getDisStrangers(true,userId,0);
                   JSONArray jsonArray=(JSONArray)JSONArray.toJSON(list);
                   jsonSend.put(JSONKeys.msgType,CSKeys.STRANGERS_LIST_ONEKM);
                   jsonSend.put(JSONKeys.strangerList,jsonArray);
               }else
               {
                   jsonSend.put(JSONKeys.msgType,CSKeys.NO_STRANGERS);
               }
           }
        }else if (type==CSKeys.GET_STRANGERS_LOC_MORE)
        {
            //大于一公里
            int distRange=jsonObject.getIntValue(JSONKeys.distRange);
            if(ManagerLocMap.isContainsProvince(province))
            {
                ManagerAProvinceLoc aProvinceLoc=ManagerLocMap.getAProvinceLoc(province);
                if(aProvinceLoc.getCount()>=1)
                {
                    List<StrangerBean>list=aProvinceLoc.getDisStrangers(false,userId,distRange);
                    JSONArray jsonArray=(JSONArray)JSONArray.toJSON(list);
                    jsonSend.put(JSONKeys.msgType,CSKeys.STRANGERS_LIST_MORE);
                    jsonSend.put(JSONKeys.strangerList,jsonArray);//jsonarray为空
                }else
                {
                    jsonSend.put(JSONKeys.msgType,CSKeys.NO_STRANGERS);
                }
            }else
            {
                jsonSend.put(JSONKeys.msgType,CSKeys.NO_STRANGERS);
            }
        }

        cSmessage1.msgJson=jsonSend.toJSONString();
        ManagerClientSession.getSession(userId).write(cSmessage1);
    }

      //处理请求得到一个陌生人的具体信息,有头像和无头像
    public void handleGetFriendInfo(IoSession session,CSmessage cSmessage)
    {
        JSONObject jsonObject=JSON.parseObject(cSmessage.msgJson);
        int type=jsonObject.getInteger(JSONKeys.msgType);
        String userId=jsonObject.getString(JSONKeys.userId);
        String friendId=jsonObject.getString(JSONKeys.friendId);
        SqlModel model=new SqlModel();
        JSONObject info=model.getUserInfo(friendId,false);
        CSmessage cSmessage1=new CSmessage();
        if(type==CSKeys.GETA_FRIEND_INFO_HEAD)
        {
            //本地有头像，所以不用传输头像
            cSmessage1.symbol='+';
            info.put(JSONKeys.msgType,CSKeys.GETA_FRIEND_INFO_HEAD);
        }else if(type==CSKeys.GETA_FRIEND_INFO_NOHEAD)
        {
            cSmessage1.symbol='-';
            info.put(JSONKeys.msgType,CSKeys.GETA_FRIEND_INFO_NOHEAD);
            String headPath=info.getString(JSONKeys.userHeadPath);
            cSmessage1.msgBytes=FileTools.getInstance().getMultyFileBytes(headPath);
        }else if(type==CSKeys.GET_STRANGER_INFO)
        {
            String headPath=info.getString(JSONKeys.userHeadPath);
            cSmessage1.msgBytes=FileTools.getInstance().getMultyFileBytes(headPath);
            cSmessage1.symbol='-';
            info.put(JSONKeys.msgType,CSKeys.GET_STRANGER_INFO);//获得头像
        }
        info.remove(JSONKeys.userHeadPath);
        cSmessage1.msgJson=info.toJSONString();
        session.write(cSmessage1);
    }

    //处理添加好友
    public void handleAddFriend(IoSession session,CSmessage cSmessage)
    {
        JSONObject jsonObject=JSON.parseObject(cSmessage.msgJson);
        String userId=jsonObject.getString(JSONKeys.userId);
        String friendId=jsonObject.getString(JSONKeys.friendId);
        CSmessage cSmessage1=new CSmessage();
        cSmessage1.symbol='+';
        JSONObject notify=new JSONObject();
        SqlModel model=new SqlModel();
        if(model.addFriend(userId, friendId))
        {
            //添加好友成功
            notify.put(JSONKeys.msgType,CSKeys.ADD_FRIEND_SUCCESS);

            //自动添加到对反搞得好友消息中
            model.addFriend(friendId,userId);//判断是否在线
            JSONObject info=model.getUserInfo(userId,false);//得到添加好友用户的信息
            CSmessage addmsg=new CSmessage();
            addmsg.symbol='-';
            String headPath=info.getString(JSONKeys.userHeadPath);
            addmsg.msgBytes=FileTools.getInstance().getMultyFileBytes(headPath);//转化为二进制流
            info.put(JSONKeys.msgType,CSKeys.ADD_FRIEND);
            info.remove(JSONKeys.userHeadPath);
            info.put(JSONKeys.friendId,userId);
            String friendName=model.getUserName(userId,false);
            info.put(JSONKeys.friendName,friendName);

            addmsg.msgJson=info.toJSONString();
            if(ManagerClientSession.isContainsId(friendId))
            {
                ManagerClientSession.getSession(friendId).write(addmsg);
                System.out.println("转发成功..");
            }else
            {
                if(!model.isTableExists("mc_"+friendId))
                {
                    model.createCacheTable(friendId);//创造缓存数据库
                }
                MsgDb msgDb=MsgTranceUtil.getInstance().Trance_Net2Db(addmsg);
                if(model.insertCacheMsg(msgDb,friendId))
                {
                    System.out.println("缓存成功");
                }else
                {
                    System.out.println("缓存失败");
                }
            }

        }else
        {
            notify.put(JSONKeys.msgType,CSKeys.ADD_FRIEND_FAILED);
        }
        cSmessage1.msgJson=notify.toJSONString();
        session.write(cSmessage1);
    }

    //删除好友
    public void handleDeleteFriend(IoSession session,CSmessage cSmessage)
    {
        JSONObject jsonObject=JSON.parseObject(cSmessage.msgJson);
        String userId=jsonObject.getString(JSONKeys.userId);
        String friendId= jsonObject.getString(JSONKeys.friendId);
        SqlModel model=new SqlModel();
        model.deleteFriend(userId,friendId);
        model.deleteFriend(friendId,userId);//相互接触双方关系
    }

    //处理获取好友Id列表
    public void handleGetFriendIdList(IoSession session,CSmessage cSmessage)
    {
        JSONObject jsonObject=JSON.parseObject(cSmessage.msgJson);
        String userId=jsonObject.getString(JSONKeys.userId);
        SqlModel model=new SqlModel();
        String []ids=model.getFriendIds(userId);//获取一个朋友的好友Id列表
        String IdList="";
        if(ids.length>0)
        {
            for(String string:ids)
            {
                IdList+=string+"";
            }
        }else
        {
            IdList="none";
        }
        CSmessage cSmessage1=new CSmessage();
        cSmessage.symbol='+';
        JSONObject jsonSend=new JSONObject();
        jsonSend.put(JSONKeys.msgType,CSKeys.FRIEND_ID_LIST);
        jsonSend.put(JSONKeys.friendIdList,IdList);
        cSmessage1.msgJson=jsonSend.toJSONString();
        session.write(cSmessage);
    }

    //处理用户反馈信息
    public void handleReback(CSmessage cSmessage)
    {
        JSONObject jsonObject=JSON.parseObject(cSmessage.msgJson);
        String userId=jsonObject.getString(JSONKeys.userId);
        String reback=jsonObject.getString(JSONKeys.msgCotent);
        FileTools.getInstance().saveReback(userId,reback);//不同用户的反馈信息
    }


    //创建多人聊天
    public void handleCreateGroup(IoSession session,CSmessage cSmessage)
    {
        JSONObject jsonObject=JSON.parseObject(cSmessage.msgJson);
        String userId=jsonObject.getString(JSONKeys.userId);
        String groupName = jsonObject.getString(JSONKeys.groupName);
        String groupTopic = jsonObject.getString(JSONKeys.groupTopic);
        String province = jsonObject.getString(JSONKeys.loc_province);
        SqlModel sqlModel=new SqlModel();

        GroupInfo gInfo=new GroupInfo();
        gInfo.creator=sqlModel.getUserName(userId,false);
        gInfo.createTime=System.currentTimeMillis();
        gInfo.groupId="group_"+userId+"_"+gInfo.createTime;//加上时间
        gInfo.groupTopic=groupTopic;

        String groupIcon=StaticValue.HEAD_P_PATH+gInfo.groupId+".png";
        FileTools.getInstance().saveMultyFile(groupIcon,cSmessage.msgBytes);

        //返回是否创建成功消息
        if(ManageGroups.addGroup(gInfo.groupId,gInfo))
        {
           //把创建用户添加到群组
            ManageGroups.getGroup(gInfo.groupId).joinGroup(userId);//把创建用户添加到群组
            if(!ManageGroups.isHaveGroup)
            {
                ManageGroups.keepWatching();//开启守护线程
            }

            CSmessage moMsg=new CSmessage();
            moMsg.symbol='-';//除了文字信息“+”以外其他的都是
            JSONObject jsonSend=new JSONObject();
            jsonSend.put(JSONKeys.msgType, CSKeys.CREATE_GROUP_SUCCESS);
            jsonSend.put(JSONKeys.groupId, gInfo.groupId);
            jsonSend.put(JSONKeys.groupName, gInfo.groupName);
            jsonSend.put(JSONKeys.groupTopic, gInfo.groupTopic);

            moMsg.msgBytes=FileTools.getInstance().getMultyFileBytes(groupIcon);
            moMsg.msgJson=jsonSend.toJSONString();
            session.write(moMsg);

            if(ManagerLocMap.isContainsProvince(province))
            {
                ManagerAProvinceLoc aProvinceLoc=ManagerLocMap.getAProvinceLoc(province);
                if(aProvinceLoc.getCount()>1)//就是在同一个地方
                {
                    List<StrangerBean> list=aProvinceLoc.getDisStrangers(true,userId,0);
                    if(list.size()>0)
                    {
                        System.out.println("周围的人 : ");
                        CSmessage inviteMsg=new CSmessage();
                        inviteMsg.symbol='-';
                        JSONObject inviteJson=new JSONObject();
                        inviteJson.put(JSONKeys.msgType,CSKeys.INVITE_TO_GROUP);
                        inviteJson.put(JSONKeys.groupCreator,gInfo.creator);
                        inviteJson.put(JSONKeys.groupName,gInfo.groupName);
                        inviteJson.put(JSONKeys.groupTopic,gInfo.groupTopic);
                        inviteMsg.msgBytes=FileTools.getInstance().getMultyFileBytes(groupIcon);
                        inviteMsg.msgJson=inviteJson.toJSONString();
                        for(StrangerBean bean:list)
                        {
                            System.out.println("Id = " + bean.strangerId);
                            IoSession ivSession=ManagerClientSession.getSession(bean.strangerId);//表示获得连接
                            ivSession.write(inviteMsg);
                        }
                    }
                }
            }
        }else
        {
            CSmessage moMsg=new CSmessage();
            moMsg.symbol='+';
            JSONObject jsonSend=new JSONObject();
            jsonSend.put(JSONKeys.msgType,CSKeys.CREATE_GROUP_FAILED);
            moMsg.msgJson=jsonSend.toJSONString();
            session.write(moMsg);
        }
    }

    //同意加入群组
    public void handleRecieveToGroup(CSmessage cSmessage)
    {
        JSONObject json=JSON.parseObject(cSmessage.msgJson);
        String userId=json.getString(JSONKeys.userId);
        String groupId=json.getString(JSONKeys.groupId);
        GroupInfo groupInfo=ManageGroups.getGroup(groupId);
        groupInfo.joinGroup(userId);
    }


    //处理发送信息（在线就转发，没在线就缓存）
    public void handleChatMsg(CSmessage cSmessage)
    {
        JSONObject jsonObject1=JSON.parseObject(cSmessage.msgJson);
        int isGroupMsg=0;
        if(jsonObject1.containsKey(JSONKeys.isGroupMsg))
        {
            isGroupMsg=jsonObject1.getInteger(JSONKeys.isGroupMsg);
            System.out.println("isGroupMsg = " + isGroupMsg);
        }
        if(isGroupMsg==0)
        {
            String getterId=jsonObject1.getString(JSONKeys.friendId);
            //判断是否在线。在线就直接转发，不在线就缓存到数据库中
           sendMsgToUser(cSmessage,getterId);
        }else if(isGroupMsg==CSKeys.GROUP_MSG)
        {
            String userId=jsonObject1.getString(JSONKeys.userId);
            String groupId=jsonObject1.getString(JSONKeys.groupId);
            String sendTime=jsonObject1.getString(JSONKeys.sendTime);
            System.out.println("groupId = " + groupId);
            if(ManageGroups.isContainsGroupId(groupId))
            {
                Map<String,String> membersMap=ManageGroups.getGroup(groupId).membersMap;
                for(String key:membersMap.keySet())//获得所有的key
                {
                   String getterId=membersMap.get(key);
                    if(!userId.equals(getterId))
                    {
                        jsonObject1.put(JSONKeys.userId,groupId);//转发群组消息，发送者id要转成群组id
                        jsonObject1.put(JSONKeys.sendTime,userId+'\n'+sendTime);
                        cSmessage.msgJson=jsonObject1.toJSONString();
                        sendMsgToUser(cSmessage,getterId);
                    }
                }
            }else
            {
                System.out.println("群组不存在..");
            }
        }
    }


    //发送消息给单个用户
    private void sendMsgToUser(CSmessage cSmessage,String getterId)
    {
       if(ManagerClientSession.isContainsId(getterId))
       {
           ManagerClientSession.getSession(getterId).write(cSmessage);//每一个会话都有自己的id,根据id进行转发就可以了
       }else
       {
           SqlModel model=new SqlModel();
           if(!model.isTableExists("mc_"+getterId))
           {
               model.createCacheTable(getterId);//创建缓存数据库
           }
           MsgDb msgDb=MsgTranceUtil.getInstance().Trance_Net2Db(cSmessage);
           if(model.insertCacheMsg(msgDb,getterId))
           {
               System.out.println("缓存成功");
           }else
           {
               System.out.println("缓存失败");
           }
       }
    }

    //获取当前时间
    public String getNowTime()
    {
        SimpleDateFormat format=new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        String date=format.format(new Date());
        return date;
    }


    public void handleVitality(CSmessage cSmessage)//关于活力值
    {
        JSONObject jsonObject1=JSON.parseObject(cSmessage.msgJson);
        int type=jsonObject1.getInteger(JSONKeys.msgType);
        String userId=jsonObject1.getString(JSONKeys.userId);
        SqlModel sqlModel=new SqlModel();
        if(type==CSKeys.SIGN)//如果签到了
        {
             sqlModel.UpdateVitality(userId,1);//多增加一天
        }
    }



}
