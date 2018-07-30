import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by L on 2016/10/10.
 */
public class ManageGroups {
    public static Map<String, GroupInfo> groupMap = new HashMap<String, GroupInfo>();
    public static boolean isHaveGroup = false;
    static long FIVE_MINUTES = 300000;//5分钟
    static long ONE_DAY = 86400000;//一天

    public static GroupInfo getGroup(String groupId) {
        if (isContainsGroupId(groupId))
        {
            return (GroupInfo)(groupMap.get(groupId));
        }
        return null;
    }

    public static boolean addGroup(String groupId, GroupInfo groupInfo)
    {
        groupInfo.membersMap=new HashMap<String,String>();
        groupMap.put(groupId,groupInfo);
        return true;
    }

    public static void deleteGroup(String groupId)
    {
        if(isContainsGroupId(groupId))
        {
            groupMap.remove(groupId);
        }
    }

    public static boolean isContainsGroupId(String groupId)
    {
        return groupMap.containsKey(groupId);
    }

    public static void keepWatching() {
        isHaveGroup = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (isHaveGroup) {
                        Thread.sleep(FIVE_MINUTES);//每五分钟查看一次
                        Iterator<GroupInfo> iter = groupMap.values().iterator();
                        while (iter.hasNext()) {
                            GroupInfo group = (GroupInfo) iter.next();
                            //一天之后消除群组,就是临时陌生人群组
                            if (System.currentTimeMillis() - group.createTime > ONE_DAY) {
                                deleteGroup(group.groupId);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


}
