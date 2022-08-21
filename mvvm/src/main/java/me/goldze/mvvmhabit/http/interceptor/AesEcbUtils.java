package me.goldze.mvvmhabit.http.interceptor;

import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class AesEcbUtils {
    //密钥 (需要前端和后端保持一致)
    private static final String KEY = "wxtdefgabcdawn12";
    //算法
    private static final String ALGORITHMSTR = "AES/ECB/PKCS5Padding";

    // 加密
    public static String encrypt(String sSrc) throws Exception {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
        byte[] raw = KEY.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes());
        return new BASE64Encoder().encode(encrypted).replaceAll("\r|\n", "");//此处使用BASE64做转码。
    }

    // 解密
    public static String decrypt(String sSrc){
        try {
            byte[] raw = KEY.getBytes();
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] encrypted1 =new BASE64Decoder().decodeBuffer(sSrc);//先用base64解密
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original);
            return originalString;
        } catch (Exception ex) {
            return sSrc;
        }
    }

}
