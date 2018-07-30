package com.example.administrator.finalprocject.Comparator;

import com.example.administrator.finalprocject.Info.FriendInfo;

import java.util.Comparator;

/**
 * Created by Administrator on 2016/9/1 0001.
 */
//根据拼音对朋友列表进行排序
public class PinyinComparator implements Comparator<FriendInfo> {
    @Override
    public int compare(FriendInfo o1, FriendInfo o2) {
        if (o1.getSortLetters().equals("@") || o2.getSortLetters().equals("#")) {
            return -1;
        } else if (o1.getSortLetters().equals("#")
                || o2.getSortLetters().equals("@")) {
            return 1;
        } else {
            return o1.getSortLetters().compareTo(o2.getSortLetters());
        }
    }
}