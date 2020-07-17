package com.hellostranger.client.util;

import android.util.Base64;

import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static com.hellostranger.client.core.MessageConstants.*;

/*
* 양방향 암호화 알고리즘인 AES256 암호화를 지원하는 서비스.
* */

public class AES256Util {
    public static byte[] ivBytes = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

    // 키 사이즈가 32바이트.
    public static String encryption(String str)  {
        try{
            byte[] textBytes = str.getBytes("UTF-8");
            AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
            SecretKeySpec newKey = new SecretKeySpec(MSG_BUSY.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, newKey, ivSpec);
            return Base64.encodeToString(cipher.doFinal(textBytes), 0);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static String decryption(String str)  {
        try{
            byte[] textBytes = Base64.decode(str, 0);
            AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
            SecretKeySpec newKey = new SecretKeySpec(MSG_BUSY.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, newKey, ivSpec);
            return new String(cipher.doFinal(textBytes), "UTF-8");
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}