package com.example.administrator.finalprocject.Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2016/10/3 0003.
 */
public class PasswordUtil {
    private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

    private PasswordUtil()
    {
    }

    private static String byteToHexString(byte b)
    {
        int n = b;
        if (n < 0)
            n = 256 + n;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    private static String byteArrayToHexString(byte[]b)
    {
        StringBuffer resultSb=new StringBuffer();
        for(int i=0;i<b.length;i++)
        {
            resultSb.append(byteToHexString(b[i]));
        }
        return resultSb.toString();
    }

    public static String toMD5(String pwd)//md5算法
    {
        byte[]results_byte=null;
        try
        {
            MessageDigest md5=MessageDigest.getInstance("MD5");//后面的是算法的名字,具体算法怎么写自然和我们没关系
            results_byte=md5.digest(pwd.getBytes());
        }catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
      return byteArrayToHexString(results_byte);
    }
}
