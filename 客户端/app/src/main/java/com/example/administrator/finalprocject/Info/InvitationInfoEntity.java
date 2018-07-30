package com.example.administrator.finalprocject.Info;

/**
 * Created by Administrator on 2016/8/29 0029.
 */
public class InvitationInfoEntity {

    String invitorName;
    String groupId;
    String groupName;
    String topic;
    String groupIconPath;

    // public int isLooked;// 消息是否被查看 (0 = false , 1 = true) 处理后就删除，所以不需要考虑是否查看

    public InvitationInfoEntity() {
    }

    public InvitationInfoEntity(String invitorName, String groupId,
                                String groupName, String topic, String groupIconPath) {
        this.invitorName = invitorName;//发起邀请人的名字
        this.groupId = groupId;
        this.groupName = groupName;
        this.topic = topic;
        this.groupIconPath = groupIconPath;//群组图标
    }

    public String getInvitorName() {
        return invitorName;
    }

    public void setInvitorName(String invitorName) {
        this.invitorName = invitorName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getGroupIconPath() {
        return groupIconPath;
    }

    public void setGroupIconPath(String groupIconPath) {
        this.groupIconPath = groupIconPath;
    }

    @Override
    public String toString() {
        return "InvitationInfoEntity [invitorName=" + invitorName
                + ", groupId=" + groupId + ", groupName=" + groupName
                + ", topic=" + topic + ", groupIconPath=" + groupIconPath + "]";
    }

}

