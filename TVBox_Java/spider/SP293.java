package com.github.catvod.spider;

import android.content.Context;
import android.text.TextUtils;
import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.spider.merge.RSA;
import com.github.catvod.utils.okhttp.OkHttpUtil;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;

public class SP293 extends Spider {
    public static String l = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAL4cSBLgAtKxA3brZ73KA6/uagCFNBqh3+GVWZGyKlrChUrrqcBHmV/fZcH/oVIwZHCxv0rnOesTE7WZhzgbbUEsBDK8W8+paIwQI3Pvpp3JzEvduOfS/4G4EPYpKiGE3+vK9q7h+9xVv6FS8uzKAe6uS98qDgHad46cMJVkYR0DAgMBAAECgYAZMBAQiCN3nMJTwC63g4tnhNQAi6Vynd3Wun3qgst/yOP2IDEWa6YTgLqvsFnEVOsJz1MD7ozK63UiC1xR/7hCvqTziaV7g0lCB9Gk9ZMRIpS8pPtfnDSMjeYzMCQWvdRMOf8BkFp7SISCtlQRm9VKKRKMZ27zH3BvIvTed4tYoQJBAOw7Gda7L2CXBdKfpRK6KgUVkki/d9qEcm1kS9TFAxrQcEWdijF7l9w8LnaBPC0uaRKmEB4/uiOMXXN89Vo6CnECQQDOBR57KgMAAKDzFIRDyegl1Y6xTpEIi0/YlTlPyZmYpG2vS6czG+OQfJoT/w/vBw7mMGTR3IiQwF2qqYACUNCzAkEA60suwGgvl3i1jwX+iLmu8uN6kkVL3vZ/dyAoO+SD5ChrO5vgMstVBkNXUCgHRUVt2OpZMZfuEkxUJJz5UQZwsQJAUCLFnHrW9+VGtcbBO+0Jk83h1y4MVBp8UG6bAGIWkL1EvO7cdpDej5EoDack94DzVq50SP1TUZrB1GRiGoR86QJAfy9NP2vDtk8MyjIUWjpYTTUhjMk/6WurIBtwEzE56QLFLshm1KwEFh25qrdI4vXChQQKXAgu17b+I7qfYh2IMA==";
    public static String qb = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDWHmmu0aFU2m1ZnQb09+/QQRnuy3GmcwnTnFwPLR/CsfbsxiNQLULpdwhC1h1U/3yEyc2kZ549+X3iYFed3tHa0NLntcSw6w6IcEAgiaeHasRlzh98IgEai9XChbhLAy7a/s4HnhFXJCsg5/FqrjZ/FuEOhbCN3gWcc6Aly+OZhwIDAQAB";
    public static String zC = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAMNlbdQeVJIW9baQ7qAUYL6gbMfKcfxuwTlt+eh8hf9oC2lcztpjwm1s/MWqHk7/yFpps/vup6p+1inh5gzlKov9Ay5hHa3feYtKsb07jgKyfUJUhAqRIrqAQz+XL96dtDyaGXd2mT2Vo7OLcN6NeDcGR8c5tVG34o3LEDEd9s8dAgMBAAECgYEAvxaGnhc7ZdOGYRDEDVFge4ywVYMqjlBSLzPaXv7InMrHvjbBJNrC3fjkCvsfwOpsqd8N4ae32QlJsJP3Q00qmR9I8beLjUe50zOSk1ExSdKTAHV8iHFlxxQhfP0y9T7pTYuDFMRC8Qcc2J7d6OUVF3Au/GBM6P8yFrKeDx2hXQECQQDjM4V75SmQ+dtPjajzBZt9OctVwAfEVePUX84dioMOS1W7KxaHYYpZMPMC9SkAsCYuGhw2bXIiCRQM+6PDm9aRAkEA3CncsOQTLRgpGOo/Lr5UXybq8EVB5s1ItTd+dCqAH8i220kw5OXH3siMwLJBdv8zUVrAwqocaNRzgjYY1WytzQJBAKAjAEKDEvkMIne/8QTZPu7UfWzDHLZVk29s12oa5qR8lMCHbimdplWXGrFK+stxXHfCD4CNQ5SZsXg1JQgKHwECQFprxvvPcCDBH3/gFPr5qGU78pmjmCFWhloG5KSJH/3tzUvaj34y8ReKfmRV0ZUx0r3C7BAHFYvoVVhRJ3ngHLECQQCC5iTBfTdd51Lb5kwxffk/iw+BRAsZ5SAzUlmwRyB0cvX/9yvton8PPK590yYQqkc6BMmNePzRypVlYNXzhYfe";
    public static String zcC = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAM6T6V5lfKE5miEMR88NalNn6CvzQC+PR8bH0ipUege8D/tFgjh22J3Yh1Ibgl1zsoifz3eF8/IKWZ6Hui7iUeOvbZteCuz7IBLFvnbPsNCP7SbLGPYRasVp3zqwt2NV72aZ/bI9pLczK2+b0R+KJXZ5V6fmVFx/QWWAo+NmkerVAgMBAAECgYA0yBQjm1iptRcNhJ7AZ5QYNX9b424t+LUFND8ds7HuUf3fXNY16R/VzOJed0rq58nhILwYtPAskrptSA6pNghn56dike27vANR6mTaYv+0o1FS/+lGQ1UohSAnzVw30L/tJzrobvvU4joXYjb95typAOiIvcOh8WAuwQRU7I5MAQJBAPh/Ji6mKbllYHAn+sHdE0cGm8QISZz+7dvZQQ8L7MzJG1eUYZXCXQ9Fiqrk3kLNwq6IW+eruBsH4HJa9LNyjwECQQDU0Lvy/EnnQNITryXQ20gxR+x4e9Nak+4GJbL6JFf9kqCUhei2ni8t/RO7wp+jrKUy2kdzGlRL+cv5dgxzv+/VAkBQua2DtgMT8TT0+mfhlpnultz/P9n6IG7Q3rDd3Hfexu4U82UIK43jqimz/omdlg5KeI2yovw5+8MUIywfJ3YBAkBanIVp8AGHdRH9T5XKV5NlaDpHEnHrHxE5jNOnrdHJJaU5l8p99twfuKGuUC+ogNnVzRqe55b8wl8W2Cx1HEQBAkAzWKejO1OWmdf+VwgUwShiRMvlNvzO7iPoyE6B4DrLM5dAMk8BN4Cyk1T/4pDfcj9FCydDk3fCmtPA0DWngU6A";
    private String n8 = "";
    private Map<String, Set<String>> ug = new HashMap();
    private String v = "";
    private String in = "";
    private String I4 = "";

    private String l(String str) {
        return RSA.l(str, qb);
    }

    private HashMap<String, String> n8() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 10; PBEM00 Build/QKQ1.190918.001)");
        return hashMap;
    }

    private String qb(String str, String str2) {
        return RSA.qb(str, str2);
    }

    private String zC() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }

    public String categoryContent(String str, String str2, boolean z, HashMap<String, String> hashMap) {
        JSONObject jSONObject = new JSONObject();
        try {
            String str3 = this.n8 + "api.php/Videolast/video?class=";
            if (hashMap.containsKey("class")) {
                str3 = str3 + hashMap.get("class");
            }
            String str4 = str3 + "&area=";
            if (hashMap.containsKey("area")) {
                str4 = str4 + hashMap.get("area");
            }
            String str5 = str4 + "&year=";
            if (hashMap.containsKey("year")) {
                str5 = str5 + hashMap.get("year");
            }
            JSONArray jSONArray = new JSONObject(qb(OkHttpUtil.string(str5 + "&tid=" + str + "&pg=" + str2 + "&limit=20&csrf=" + URLEncoder.encode(l(zC())), n8()), l)).getJSONArray("data");
            JSONArray jSONArray2 = new JSONArray();
            for (int i = 0; i < jSONArray.length(); i++) {
                JSONObject jSONObject2 = jSONArray.getJSONObject(i);
                JSONObject jSONObject3 = new JSONObject();
                jSONObject3.put("vod_id", jSONObject2.getString("vod_id"));
                jSONObject3.put("vod_name", jSONObject2.getString("vod_name"));
                jSONObject3.put("vod_pic", jSONObject2.getString("vod_pic"));
                jSONObject3.put("vod_remarks", jSONObject2.getString("vod_remarks"));
                jSONArray2.put(jSONObject3);
            }
            int parseInt = Integer.parseInt(str2);
            jSONObject.put("page", parseInt);
            if (jSONArray2.length() == 12) {
                parseInt++;
            }
            jSONObject.put("pagecount", parseInt);
            jSONObject.put("limit", 12);
            jSONObject.put("total", Integer.MAX_VALUE);
            jSONObject.put("list", jSONArray2);
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return jSONObject.toString();
    }

    public String detailContent(List<String> list) {
        JSONObject jSONObject = new JSONObject();
        try {
            JSONObject jSONObject2 = new JSONObject();
            String zC2 = zC();
            String l2 = l(zC2);
            jSONObject2.put("time", zC2);
            String str = new String(RSA.l(l2, qb));
            JSONObject aa = new JSONObject(OkHttpUtil.string(this.n8 + "api.php/Videolast/videoDetail?id=" + list.get(0) + "&ck=" + URLEncoder.encode(str) + "&csrf=" + URLEncoder.encode(l2), n8()));
            //String ba = aa.optJSONObject("data").toString();
            JSONObject optJSONObject = new JSONObject(qb(aa.optString("data"), l)).optJSONObject("data").optJSONObject("vod_info");
            //JSONObject optJSONObjects = new JSONObject(qb(OkHttpUtil.string(this.n8 + "api.php/Videolast/videoDetail?id=" + list.get(0) + "&ck=" + URLEncoder.encode(str) + "&csrf=" + URLEncoder.encode(l2), n8()), l)).optJSONObject("data").optJSONObject("vod_info");
            JSONObject jSONObject3 = new JSONObject();
            jSONObject3.put("vod_id", optJSONObject.getString("vod_id"));
            jSONObject3.put("vod_name", optJSONObject.getString("vod_name"));
            jSONObject3.put("vod_pic", optJSONObject.getString("vod_pic"));
            jSONObject3.put("type_name", optJSONObject.optString("vod_class"));
            jSONObject3.put("vod_year", optJSONObject.optString("vod_year"));
            jSONObject3.put("vod_area", optJSONObject.optString("vod_area"));
            jSONObject3.put("vod_remarks", optJSONObject.optString("vod_remarks"));
            jSONObject3.put("vod_actor", optJSONObject.optString("vod_actor"));
            jSONObject3.put("vod_content", optJSONObject.optString("vod_content").trim());
            JSONArray jSONArray = aa.getJSONArray("video_list");
            LinkedHashMap linkedHashMap = new LinkedHashMap();
            for (int i = 0; i < jSONArray.length(); i++) {
                JSONObject jSONObject4 = jSONArray.getJSONObject(i);
                String string = jSONObject4.getString("code");
                linkedHashMap.put(string, jSONObject4.optString("url"));
                JSONArray optJSONArray = jSONObject4.optJSONArray("parse_api");
                this.I4 = jSONObject4.optString("user_agent");
                if (!this.ug.containsKey(string)) {
                    HashSet hashSet = new HashSet();
                    for (int i2 = 0; i2 < optJSONArray.length(); i2++) {
                        hashSet.add(optJSONArray.optString(i2));
                    }
                    this.ug.put(string, hashSet);
                    SpiderDebug.log(this.ug.toString());
                } else {
                    Set<String> set = this.ug.get(string);
                    for (int i3 = 0; i3 < optJSONArray.length(); i3++) {
                        set.add(optJSONArray.optString(i3));
                    }
                }
            }
            String join = TextUtils.join("$$$", linkedHashMap.keySet());
            String join2 = TextUtils.join("$$$", linkedHashMap.values());
            jSONObject3.put("vod_play_from", join);
            jSONObject3.put("vod_play_url", join2);
            JSONArray jSONArray2 = new JSONArray();
            jSONArray2.put(jSONObject3);
            jSONObject.put("list", jSONArray2);
        } catch (Exception unused) {
        }
        return jSONObject.toString();
    }

    public String homeContent(boolean z) {
        String str = "type_id";
        String str2 = "type_name";
        JSONObject jSONObject = new JSONObject();
        try {
            JSONArray jSONArray = new JSONObject(qb(OkHttpUtil.string(this.n8 + "api.php/Videolast/nav?csrf=" + URLEncoder.encode(l(zC())), (Map) null), l)).getJSONArray("data");
            JSONArray jSONArray2 = new JSONArray();
            for (int i = 0; i < jSONArray.length(); i++) {
                JSONObject jSONObject2 = jSONArray.getJSONObject(i);
                String string = jSONObject2.getString(str2);
                String string2 = jSONObject2.getString(str);
                JSONObject jSONObject3 = new JSONObject();
                jSONObject3.put(str, string2);
                jSONObject3.put(str2, string);
                JSONObject optJSONObject = jSONObject2.optJSONObject("type_extend");
                if (!z || optJSONObject == null) {
                    str = str;
                    str2 = str2;
                    jSONArray = jSONArray;
                } else {
                    JSONArray jSONArray3 = new JSONArray();
                    LinkedHashMap linkedHashMap = new LinkedHashMap();
                    linkedHashMap.put("class", "剧情");
                    linkedHashMap.put("area", "地区");
                    linkedHashMap.put("lang", "语言");
                    linkedHashMap.put("year", "年份");
                    Iterator it = linkedHashMap.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry entry = (Map.Entry) it.next();
                        String string3 = optJSONObject.getString((String) entry.getKey());
                        if (!TextUtils.isEmpty(string3)) {
                            JSONObject jSONObject4 = new JSONObject();
                            jSONObject4.put("key", entry.getKey());
                            jSONObject4.put("name", entry.getValue());
                            jSONArray3.put(jSONObject4);
                            String[] split = string3.split(",");
                            JSONArray jSONArray4 = new JSONArray();
                            JSONObject jSONObject5 = new JSONObject();
                            jSONObject5.put("n", "全部");
                            jSONObject5.put("v", "");
                            jSONArray4.put(jSONObject5);
                            int length = split.length;
                            int i2 = 0;
                            while (i2 < length) {
                                String str3 = split[i2];
                                JSONObject jSONObject6 = new JSONObject();
                                jSONObject6.put("n", str3);
                                jSONObject6.put("v", str3);
                                jSONArray4.put(jSONObject6);
                                i2++;
                                jSONArray = jSONArray;
                                split = split;
                            }
                            jSONObject4.put("value", jSONArray4);
                            it = it;
                            str = str;
                            str2 = str2;
                            jSONArray = jSONArray;
                        }
                    }
                    str = str;
                    str2 = str2;
                    jSONArray = jSONArray;
                    if (!jSONObject.has("filters")) {
                        jSONObject.put("filters", new JSONObject());
                    }
                    jSONObject.getJSONObject("filters").put(string2, jSONArray3);
                }
                jSONArray2.put(jSONObject3);
            }
            jSONObject.put("class", jSONArray2);
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return jSONObject.toString();
    }

    public String homeVideoContent() {
        JSONObject jSONObject = new JSONObject();
        try {
            JSONArray jSONArray = new JSONArray();
            JSONArray jSONArray2 = new JSONObject(qb(OkHttpUtil.string(this.n8 + "api.php/Videolast/indexVideo?csrf=" + URLEncoder.encode(l(zC())), n8()), l)).getJSONArray("data");
            for (int i = 0; i < jSONArray2.length(); i++) {
                JSONArray jSONArray3 = jSONArray2.getJSONObject(i).getJSONArray("vlist");
                for (int i2 = 0; i2 < jSONArray3.length(); i2++) {
                    JSONObject jSONObject2 = jSONArray3.getJSONObject(i2);
                    JSONObject jSONObject3 = new JSONObject();
                    jSONObject3.put("vod_id", jSONObject2.getString("vod_id"));
                    jSONObject3.put("vod_name", jSONObject2.getString("vod_name"));
                    jSONObject3.put("vod_pic", jSONObject2.getString("vod_pic"));
                    jSONObject3.put("vod_remarks", jSONObject2.getString("vod_remarks"));
                    jSONArray.put(jSONObject3);
                }
            }
            jSONObject.put("list", jSONArray);
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return jSONObject.toString();
    }

    public void init(Context context) {
        SP293.super.init(context);
    }

    public boolean isVideoFormat(String str) {
        return SP293.super.isVideoFormat(str);
    }

    public boolean manualVideoCheck() {
        return SP293.super.manualVideoCheck();
    }

    public String playerContent(String str, String str2, List<String> list) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("parse", "0");
            jSONObject.put("playUrl", "");
            SpiderDebug.log(new JSONObject(this.ug).toString());
            if (this.ug.containsKey(str)) {
                Iterator<String> it = this.ug.get(str).iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    HashMap hashMap = new HashMap();
                    hashMap.put("User-Agent", "hahaha");
                    //hashMap.put("Host", "123.56.222.84:2025");
                    JSONObject jSONObject2 = new JSONObject(OkHttpUtil.string(it.next() + str2 + "&appId=1000&version=1.3.3&device_id=b08b45ee1124f23b", hashMap));
                    String optString = jSONObject2.optString("data");

                    Integer valueOf = Integer.valueOf(jSONObject2.optInt("encryption"));
                    if (Integer.valueOf(jSONObject2.optInt("code")).intValue() == 1 || (!optString.equals("") && valueOf.intValue() >= 0)) {
                        if (valueOf.intValue() == 1) {
                            JSONObject jSONObject3 = new JSONObject(qb(jSONObject2.optString("data"), zcC));
                            String optString2 = jSONObject3.optString("url");
                            String optString3 = jSONObject3.optString("header");
                            if (!TextUtils.isEmpty(optString2)) {
                                jSONObject.put("url", optString2);
                                if (!TextUtils.isEmpty(optString3) && optString2.contains("weiyun")) {
                                    jSONObject.put("header", optString3);
                                }
                            }
                        } else if (valueOf.intValue() == 0) {
                            String optString4 = new JSONObject(optString).optString("url");
                            if (!TextUtils.isEmpty(optString4)) {
                                jSONObject.put("url", optString4);
                                break;
                            }
                        } else {
                            continue;
                        }
                    }
                }
            }
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return jSONObject.toString();
    }

    public String searchContent(String str, boolean z) {
        JSONObject jSONObject = new JSONObject();
        try {
            JSONArray optJSONArray = new JSONObject(qb(OkHttpUtil.string(this.n8 + "api.php/Videolast/search?pg=1&text=" + URLEncoder.encode(str) + "&pg=1&csrf=" + URLEncoder.encode(l(zC())), n8()), l)).optJSONArray("data");
            JSONArray jSONArray = new JSONArray();
            for (int i = 0; i < optJSONArray.length(); i++) {
                JSONObject jSONObject2 = optJSONArray.getJSONObject(i);
                JSONObject jSONObject3 = new JSONObject();
                jSONObject3.put("vod_id", jSONObject2.getString("vod_id"));
                jSONObject3.put("vod_name", jSONObject2.getString("vod_name"));
                jSONObject3.put("vod_pic", jSONObject2.getString("vod_pic"));
                jSONObject3.put("vod_remarks", jSONObject2.getString("vod_remarks"));
                jSONArray.put(jSONObject3);
            }
            jSONObject.put("list", jSONArray);
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return jSONObject.toString();
    }

    public void init(Context context, String str) {
        try {
            this.n8 = new JSONObject(qb(OkHttpUtil.string("https://alogin.oss-cn-beijing.aliyuncs.com/iphones.json", n8()), zC)).optJSONObject("data").optString("url");
            JSONObject jSONObject = new JSONObject(OkHttpUtil.string("https://agit.ai/fangzimo/TVBox/raw/branch/main/293.txt", (Map) null));
            this.in = jSONObject.optString("appId");
            this.v = jSONObject.optString("version");
           
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
    }
}
