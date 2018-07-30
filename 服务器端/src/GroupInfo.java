import java.util.HashMap;
import java.util.Map;

/**
 * Created by L on 2016/10/9.
 */
//团队消息
public class GroupInfo {
    public String groupHeadPath;
    public String creator;//创建者
    public String groupId;
    public String groupName;
    public String groupTopic;
    public long createTime;//创建时间
    public Map<String,String>membersMap;//团队成员情况

    public GroupInfo()
    {
        this.membersMap=new HashMap<String,String>();
    }

    public GroupInfo(String groupHeadPath, String creator, String groupId,
                     String groupName, String groupTopic) {
        this.groupHeadPath = groupHeadPath;
        this.creator = creator;
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupTopic = groupTopic;
        this.membersMap = new HashMap<String, String>();
    }

    public void joinGroup(String userId){
        membersMap.put(userId, userId);
    }

    public void deleteMember(String userId){
        membersMap.remove(userId);
    }

    public String getMember(String userId){
        return membersMap.get(userId).toString();
    }
}
