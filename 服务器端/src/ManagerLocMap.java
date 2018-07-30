import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by L on 2016/10/10.
 */
//按照省份来管理用户位置
public class ManagerLocMap {
    public static Map<String,ManagerAProvinceLoc> map=new HashMap<String,ManagerAProvinceLoc>();

    //得到一个省的位置管理类
    public static boolean isContainsProvince(String province)
    {
        return map.containsKey(province);
    }

    public static ManagerAProvinceLoc getAProvinceLoc(String province)
    {
        if(isContainsProvince(province))
        {
            return (ManagerAProvinceLoc)map.get(province);
        }
        return null;
    }

    public static void deleteAProvinceLoc(String province)
    {
        map.remove(province);
    }

    public static void addAProvinceLoc(String province,
                                       ManagerAProvinceLoc AProvinceLoc) {
//		System.out.println("添加省：--" + province);
        map.put(province, AProvinceLoc);
    }

    //用户下线，及时删掉位置信息
    public static void deleteOneUser(String userId)
    {
        Iterator<ManagerAProvinceLoc>iterator=(Iterator<ManagerAProvinceLoc>)map.values().iterator();
        while (iterator.hasNext())
        {
            ManagerAProvinceLoc aProvinceLoc=iterator.next();
            if(aProvinceLoc.isContainsId(userId))
            {
                aProvinceLoc.deleteOneUser(userId);
                if(aProvinceLoc.getCount()==0)
                {
                    deleteAProvinceLoc(aProvinceLoc.province);
                }
            }
        }
    }


}
