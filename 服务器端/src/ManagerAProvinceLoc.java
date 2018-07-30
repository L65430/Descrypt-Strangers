import server_utils.DistanceUtil;

import java.util.*;

/**
 * Created by L on 2016/10/9.
 */
//管理用户位置
public class ManagerAProvinceLoc {
    Map<String, myLocationBean> locationBeanMap;
    public String province;

    public ManagerAProvinceLoc(String province) {
        this.province = province;
        locationBeanMap = new TreeMap<String, myLocationBean>();
    }

    public void addLocation(String userId,myLocationBean locationBean)
    {
        locationBeanMap.put(userId,locationBean);
    }


    public void deleteOneUser(String userId)
    {
        locationBeanMap.remove(userId);
    }

    public myLocationBean getLocation(String userId) {
        if (isContainsId(userId))
        {
          return (myLocationBean)locationBeanMap.get(userId);
        }
        return null;
    }

    public int getCount() {
        return locationBeanMap.size();
    }

    public boolean isContainsId(String userId) {
        return locationBeanMap.containsKey(userId);//判断有没有
    }

    //得到指定距离内的陌生人信息
    public List<StrangerBean> getDisStrangers(boolean isOnekm, String userId, int range) {
        int NUM = 0;
        int MAX = 100;
        List<StrangerBean> list = new ArrayList<StrangerBean>();
        myLocationBean loc1 = this.getLocation(userId);
        Iterator<String> iter = locationBeanMap.keySet().iterator();
        while (iter.hasNext()) {
            String strangerId = iter.next();
            if (!strangerId.equals(userId)) {
                myLocationBean loc2 = this.getLocation(strangerId);
                double dist = DistanceUtil.getDistance(loc1.longitude, loc1.latitude, loc2.longitude, loc2.latitude);
                SqlModel model = new SqlModel();
                if (isOnekm) {
                    if (dist > 0 && dist <= 1) {
                        StrangerBean strangerBean = new StrangerBean();
                        strangerBean.strangerId = strangerId;
                        strangerBean.strangerName = model.getUserName(strangerId, false);
                        strangerBean.Longitude = loc2.longitude;
                        strangerBean.Latitude = loc2.latitude;
                        strangerBean.strangerLoc = 1 + "公里以内";
                        list.add(strangerBean);
                        NUM++;
                        if (NUM == MAX) {
                            break;
                        }
                    } else {
                        if (dist > range - 1 && dist <= range) {
                            StrangerBean bean = new StrangerBean();
                            bean.strangerId = strangerId;
                            bean.strangerName = model.getUserName(strangerId, false);
                            bean.Longitude = loc2.longitude;
                            bean.Latitude = loc2.latitude;
                            bean.strangerLoc = range + "公里以内";
                            list.add(bean);
                            if (NUM == MAX) {
                                break;
                            }
                        }
                    }
                }
            }
        }
        return list;
    }

}
