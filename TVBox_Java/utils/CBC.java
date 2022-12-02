package com.github.catvod.utils;

import com.github.catvod.crawler.SpiderDebug;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CBC {
    public static byte[] CBC(byte[] data, byte[] key, byte[] iv) {
        try {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(2, secretKeySpec, ivParameterSpec);
            return cipher.doFinal(data);
        } catch (Exception e) {
            SpiderDebug.log(e);
            return null;
        }
    }
}