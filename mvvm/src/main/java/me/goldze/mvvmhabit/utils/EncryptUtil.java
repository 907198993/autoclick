package me.goldze.mvvmhabit.utils;

import java.security.MessageDigest;

public class EncryptUtil {
    private static final char hexDigits[] = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};

    public static String md5hex(String data) {
        try {
            MessageDigest mdInst = MessageDigest.getInstance("md5");
            mdInst.update(data.getBytes("UTF-8"));
            byte[] md = mdInst.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
