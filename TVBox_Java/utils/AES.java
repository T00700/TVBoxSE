package com.github.catvod.utils;

import android.util.Base64;

import com.github.catvod.crawler.SpiderDebug;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {

    public static String CBC(String src, String KEY, String IV) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), "AES");
            AlgorithmParameterSpec paramSpec = new IvParameterSpec(IV.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, keySpec, paramSpec);
            return new String(cipher.doFinal(Base64.decode(src.getBytes(), 0)));
        } catch (Exception exception) {
            SpiderDebug.log(exception);
        }
        return null;
    }


    public static Cipher GetCipher(int opmode, String key) {
        try {
            DESKeySpec dks = new DESKeySpec(key.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            Key secretkey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS7Padding");
            cipher.init(opmode, secretkey);
            return cipher;
        } catch (Exception exception) {
            SpiderDebug.log(exception);
            return null;
        }
    }



    public static String decode(String data, String key) throws Exception {
        if (data == null || data.isEmpty()){
            return null;
        }
        try {
            byte[] b = hex2byte(data);
            Cipher cipher = GetCipher(Cipher.DECRYPT_MODE, key);
            if (cipher != null){
                return new String(cipher.doFinal(b), "UTF-8");
            } else {
                return null;
            }
        } catch (Exception exception) {
            SpiderDebug.log(exception);
            return data;
        }
    }

    public static byte[] hex2byte(String hex) throws IllegalArgumentException {
        if (hex.length() % 2 != 0){
            throw new IllegalArgumentException("invalid hex string");
        }
        char[] arr = hex.toCharArray();
        byte[] b = new byte[hex.length() / 2];
        for (int i = 0, j = 0, l = hex.length(); i < l; i++, j++) {
            String swap ="" + arr[i++] + arr[i];
            int byteint = Integer.parseInt(swap, 16) & 0xFF;
            b[j] = new Integer(byteint).byteValue();
        }
        return b;
    }

    public static String encrypt(String data, String key) {
        try {
            byte[] data2 = data.getBytes("UTF-8");
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] result = cipher.doFinal(data2);
            return bytesToHexStr(result);
        } catch (Exception exception) {
            SpiderDebug.log(exception);
        }
        return null;
    }

    /**
     * byte数组转16进制字符串
     *
     * @param  bytes byte数组
     * @return hex字符串
     */
    public static String bytesToHexStr(byte[] bytes) {
        StringBuilder hexStr = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            hexStr.append(hex);
        }
        return hexStr.toString();
    }

}
