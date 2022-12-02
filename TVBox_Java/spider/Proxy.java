package com.github.catvod.spider;

import android.util.Base64;

import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.live.TxtSubscribe;
import com.github.catvod.utils.okhttp.OkHttpUtil;

import java.io.ByteArrayInputStream;
import java.util.Map;

public class Proxy extends Spider {

    public static int localPort = -1;

    static void adjustLocalPort() {
        if (localPort > 0)
            return;
        int port = 9978;
        while (port < 10000) {
            String resp = OkHttpUtil.string("http://127.0.0.1:" + port + "/proxy?do=ck", null);
            if (resp.equals("ok")) {
                SpiderDebug.log("Found local server port " + port);
                localPort = port;
                break;
            }
            port++;
        }
    }

    public static String localProxyUrl() {
        adjustLocalPort();
        return "http://127.0.0.1:" + Proxy.localPort + "/proxy";
    }

    public static Object[] proxy(Map<String, String> params) {
        try {
            String what = params.get("do");
            if (what.equals("live")) {
                String type = params.get("type");
                if (type.equals("txt")) {
                    String ext = params.get("ext");
                    if (!ext.startsWith("http")) {
                        ext = new String(Base64.decode(ext, Base64.DEFAULT | Base64.URL_SAFE | Base64.NO_WRAP), "UTF-8");
                    }
                    return TxtSubscribe.load(ext);
                }
            } else if (what.equals("ck")) {
                Object[] result = new Object[3];
                result[0] = 200;
                result[1] = "text/plain; charset=utf-8";
                ByteArrayInputStream baos = new ByteArrayInputStream("ok".getBytes("UTF-8"));
                result[2] = baos;
                return result;
            } else if (what.equals("ali")) {
                return PushAgent.vod(params);
            } else if (what.equals("czspp")) {
                return Czsapp.loadsub(params.get("url"));
            } else if (what.equals("kmys")) {
                return Kmys.vod(params);
            }

        } catch (Throwable th) {

        }
        return null;
    }

}
