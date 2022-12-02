package com.github.catvod.spider;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.utils.Misc;
import com.github.catvod.utils.okhttp.OKCallBack;
import com.github.catvod.utils.okhttp.OkHttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class IKan extends AppYsV2 {
    private final OkHttpClient y;
    private boolean K = false;
    private Context cccc = null;

    public IKan() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        TimeUnit timeUnit = TimeUnit.SECONDS;
        y = builder.readTimeout(15L, timeUnit).writeTimeout(15L, timeUnit).connectTimeout(15L, timeUnit).cookieJar(new CookieJar() {
            final List<Cookie> Mf = new ArrayList();

            public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                ArrayList arrayList = new ArrayList();
                ArrayList arrayList2 = new ArrayList();
                for (Cookie cookie : Mf) {
                    if (cookie.expiresAt() < System.currentTimeMillis()) {
                        arrayList.add(cookie);
                    } else if (cookie.matches(httpUrl)) {
                        arrayList2.add(cookie);
                    }
                }
                Mf.removeAll(arrayList);
                return arrayList2;
            }

            public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
                Mf.addAll(list);
            }
        }).retryOnConnectionFailure(true).build();
    }

    private void S(final String str, final String str2) {
        HashMap hashMap = new HashMap();
        hashMap.put("user_name", str);
        hashMap.put("user_pwd", str2);
        hashMap.put("user_pwd2", str2);
        try {
            HashMap hashMap2 = new HashMap();
            hashMap2.put("Host", "tvcms.ikan6.vip");
            hashMap2.put("User-Agent", "okhttp/3.11.0");
            OkHttpUtil.post(this.y, "http://tvcms.ikan6.vip/api.php/gctvapi.auth/register", hashMap, hashMap2, new OKCallBack.OKCallBackString() {
                @Override
                public void onFailure(Call call, Exception exc) {
                }

                public void onResponse(String str3) {
                    try {
                        if (new JSONObject(str3).optInt("code", 0) == 200) {
                            cccc.getSharedPreferences("spider_IKan", 0).edit().putString("user", str).putString("pwd", str2).commit();
                            login(str, str2);
                        }
                    } catch (JSONException unused) {
                    }
                }
            });
        } catch (Throwable unused) {
        }
    }

    private void iq() {
        SharedPreferences sharedPreferences = cccc.getSharedPreferences("spider_IKan", 0);
        try {
            String string = sharedPreferences.getString("user", null);
            String string2 = sharedPreferences.getString("pwd", null);
            if (!(string == null || string2 == null)) {
                login(string, string2);
                return;
            }
        } catch (Throwable unused) {
        }
        S(random(18), random(8));
    }

    public static String random(int i) {
        Random random = new Random();
        StringBuffer stringBuffer = new StringBuffer();
        for (int i2 = 0; i2 < i; i2++) {
            stringBuffer.append("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456".charAt(random.nextInt(59)));
        }
        return stringBuffer.toString();
    }


    public void init(Context context) {
        cccc = context;
    }

    public void login(String str, String str2) {
        HashMap headers = new HashMap();
        headers.put("user_name", str);
        headers.put("user_pwd", str2);
        try {
            HashMap hashMap2 = new HashMap();
            hashMap2.put("Host", "tvcms.ikan6.vip");
            hashMap2.put("User-Agent", "okhttp/3.11.0");
            OkHttpUtil.post(y, "http://tvcms.ikan6.vip/api.php/gctvapi.auth/login", headers, hashMap2, new OKCallBack.OKCallBackString() {
                @Override
                public void onFailure(Call call, Exception exc) {
                }

                public void onResponse(String str3) {
                    try {
                        if (new JSONObject(str3).optInt("code", 0) == 200) {
                            K = true;
                        }
                    } catch (JSONException unused) {
                    }
                }
            });
        } catch (Throwable unused) {
        }
    }

    private HashMap<String, String> getHeaders(String URL) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Mobile Safari/537.36");
        return headers;
    }

    @Override
    public String homeVideoContent() {
        try {
            String url = "http://tvcms.ikan6.vip/api.php/gctvapi.vod";
            String json = OkHttpUtil.string(url, getHeaders(url));
            JSONObject obj = new JSONObject(json);
            JSONArray videos = new JSONArray();
            JSONArray jsonArray = obj.getJSONObject("data").getJSONArray("list");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject vObj = jsonArray.getJSONObject(i);
                String vid = vObj.getString("vod_id");
                JSONObject v = new JSONObject();
                v.put("vod_id", vid);
                v.put("vod_name", vObj.getString("vod_name"));
                v.put("vod_pic", vObj.getString("vod_pic"));
                v.put("vod_remarks", vObj.getString("vod_remarks"));
                videos.put(v);
            }
            JSONObject result = new JSONObject();
            result.put("list", videos);
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }

    @Override
    public String playerContent(String str, String str2, List<String> list) {
        try {
            if (str.equals("lekanzyw")) {
                if (!K) {
                    iq();
                }

                JSONObject headers = new JSONObject();
                headers.put("User-Agent", " Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36");
                headers.put("Accept", " */*");
                headers.put("Accept-Language", " zh-CN,zh;q=0.9,en-US;q=0.3,en;q=0.7");
                headers.put("Accept-Encoding", " gzip, deflate");
                headers.put("Connection", " Keep-Alive");

                String parseUrl = "https://player.4kan.top/?url=" + str2;
                Document doc = Jsoup.parse(OkHttpUtil.string(parseUrl, null));
                Pattern pattern = Pattern.compile("(?<=getrandom\\S\").*?(?=\")");
                Elements allScripts = doc.select("body script");
                for (int j = 0; j < allScripts.size(); j++) {
                    String scContents = allScripts.get(j).html().trim();
                    Matcher matcher = pattern.matcher(scContents);
                    if (!matcher.find())
                        continue;
                    String players = new String(Base64.decode(matcher.group(0), Base64.DEFAULT));
                    int start = players.indexOf("http");
                    int end = players.lastIndexOf("key=") + 36;
                    String url = players.substring(start, end);
                    Map<String, List<String>> respHeaders = new TreeMap<>();
                    OkHttpUtil.stringNoRedirect(url, null, respHeaders);
                    String redLoc = OkHttpUtil.getRedirectLocation(respHeaders);


                    JSONObject result = new JSONObject();
                    result.put("parse", 0);
                    result.put("playUrl", "");
                    result.put("url", redLoc);
                    result.put("header", headers.toString());
                    return result.toString();

                }
            }
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }

    @Override
    public void init(Context context, String str) {
        super.init(context, "http://tvcms.ikan6.vip/api.php/gctvapi.vod");
    }

}