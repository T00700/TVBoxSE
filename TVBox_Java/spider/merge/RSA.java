//
// Decompiled by Jadx - 632ms
//
package com.github.catvod.spider.merge;

import android.util.Base64;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;

public class RSA {
    public static final Charset qb = Charset.forName("UTF8");

    public static String l(String str, String str2) {
        try {
            byte[] bytes = str.getBytes(qb);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(1, n8(str2));
            int length = bytes.length;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int i = 0;
            int i2 = 0;
            while (true) {
                int i3 = length - i;
                if (i3 > 0) {
                    byte[] doFinal = i3 > 117 ? cipher.doFinal(bytes, i, 117) : cipher.doFinal(bytes, i, i3);
                    byteArrayOutputStream.write(doFinal, 0, doFinal.length);
                    i2++;
                    i = i2 * 117;
                } else {
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    byteArrayOutputStream.close();
                    return new String(Base64.encode(byteArray, 0));
                }
            }
        } catch (Exception e) {
            ug(e);
            return null;
        }
    }

    public static PublicKey n8(String str) {
        try {
            return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.decode(str, 0)));
        } catch (Exception e) {
            ug(e);
            return null;
        }
    }

    public static String qb(String str, String str2) {
        try {
            byte[] decode = Base64.decode(str, 0);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(2, zC(str2));
            int length = decode.length;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int i = 0;
            int i2 = 0;
            while (true) {
                int i3 = length - i;
                if (i3 > 0) {
                    byte[] doFinal = i3 > 128 ? cipher.doFinal(decode, i, 128) : cipher.doFinal(decode, i, i3);
                    byteArrayOutputStream.write(doFinal, 0, doFinal.length);
                    i2++;
                    i = i2 * 128;
                } else {
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    byteArrayOutputStream.close();
                    return new String(byteArray, qb);
                }
            }
        } catch (Exception e) {
            ug(e);
            return null;
        }
    }

    private static void ug(Exception exc) {
        exc.printStackTrace();
    }

    public static PrivateKey zC(String str) {
        try {
            return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(Base64.decode(str, 0)));
        } catch (Exception e) {
            ug(e);
            return null;
        }
    }
}
