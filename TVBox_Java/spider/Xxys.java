package com.github.catvod.spider;

import android.content.Context;


import com.github.catvod.utils.Misc;
import com.github.catvod.utils.okhttp.OkHttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.regex.Pattern;

public class Xxys extends AppYsV2 {

    @Override
    protected JSONObject getFinalVideo(String flag, ArrayList<String> parseUrls, String url) throws JSONException {
        String htmlPlayUrl = "";
        for (String parseUrl : parseUrls) {
            if (parseUrl.isEmpty() || parseUrl.equals("null"))
                continue;
            String playUrl = parseUrl + url;
            String content = desc(OkHttpUtil.string(playUrl, null), (byte) 4);
            try {
                HashMap hashMap = new HashMap();
                OkHttpUtil.stringNoRedirect(playUrl, null, hashMap);
                String d = OkHttpUtil.getRedirectLocation(hashMap);
                JSONObject result = new JSONObject();
                result.put("parse", 0);
                result.put("playUrl", "");
                result.put("url", d);
                return result;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONObject tryJson = null;
            try {
                tryJson = Misc.jsonParse(url, content);
            } catch (Throwable th) {

            }
            if (tryJson != null && tryJson.has("url") && tryJson.has("header")) {
                tryJson.put("header", tryJson.getJSONObject("header").toString());
                return tryJson;
            }
            if (content.contains("<html")) {
                boolean sniffer = false;
                for (Pattern p : htmlVideoKeyMatch) {
                    if (p.matcher(content).find()) {
                        sniffer = true;
                        break;
                    }
                }
                if (sniffer) {
                    htmlPlayUrl = parseUrl;
                }
            }
        }
        if (!htmlPlayUrl.isEmpty()) {
            JSONObject result = new JSONObject();
            result.put("parse", 1);
            result.put("playUrl", htmlPlayUrl);
            result.put("url", url);
            return result;
        }
        return null;
    }

    @Override
    public void init(Context context, String str) {
        super.init(context, "http://tvht.lanrentk.cc/api.php/iptv/vod");
    }

}
