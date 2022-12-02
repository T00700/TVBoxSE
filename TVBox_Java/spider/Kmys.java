package com.github.catvod.spider;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;

import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.utils.Misc;
import com.github.catvod.utils.okhttp.OKCallBack;
import com.github.catvod.utils.okhttp.OkHttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.Response;

/**
 * Author: @SDL
 */
public class Kmys extends Spider {
    private String apiDomain = "";
    private String staticDomain = "";

    private String appId = "4"; // 飞瓜 1 酷猫 5 暗影 4

    private String device = "1a7348ae45f8f6de4d798614fc07e7271657392501986";

    @Override
    public void init(Context context) {
        super.init(context);
        SharedPreferences sharedPreferences = context.getSharedPreferences("sp_Kmys", Context.MODE_PRIVATE);
        try {
            device = sharedPreferences.getString("device", null);
        } catch (Throwable th) {
        } finally {
            if (device == null) {
                device = Misc.MD5(UUID.randomUUID().toString(), Misc.CharsetUTF8).toLowerCase();
                sharedPreferences.edit().putString("device", device).commit();
            }
        }
    }

    private HashMap<String, String> getHeaders(String url) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("versionNumber", "360");
        headers.put("versionName", "3.6.0");
        headers.put("device", device);
        headers.put("ed", device);
        headers.put("appId", appId);
        headers.put("platformId", "7");
        headers.put("User-Agent", "okhttp/3.14.7");
        return headers;
    }

    private void checkDomain() {
        if (staticDomain.isEmpty()) {
            String url = "http://feigua2021.oss-cn-beijing.aliyuncs.com/static/config/video/" + appId + ".json";
            HashMap<String, String> headers = new HashMap<>();
            headers.put("User-Agent", "okhttp/3.14.7");
            headers.put("ed", device);
            String json = OkHttpUtil.string(url, headers);
            try {
                JSONObject obj = new JSONObject(json);
                apiDomain = obj.getString("apiDomain");
                staticDomain = obj.getString("staticDomain");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String homeContent(boolean filter) {
        try {
            checkDomain();
            String url = staticDomain + "/static/" + appId + "/config/lib-new.json";
            String content = OkHttpUtil.string(url, getHeaders(url));
            JSONObject jsonObject = new JSONObject(content).getJSONObject("data");

            JSONArray years = jsonObject.getJSONArray("year");
            JSONArray types = jsonObject.getJSONArray("types");

            JSONArray classes = new JSONArray();
            JSONObject filterConfig = new JSONObject();

            for (int i = 0; i < types.length(); i++) {
                JSONObject item = types.getJSONObject(i);
                String typeId = item.getString("typeId").trim();
                if (typeId.isEmpty())
                    continue;
                String typeName = item.getString("typeName");
                JSONObject newCls = new JSONObject();
                newCls.put("type_id", typeId);
                newCls.put("type_name", typeName);
                classes.put(newCls);

                JSONArray tags = item.getJSONArray("tags");
                JSONArray extendsAll = new JSONArray();
                // 类型
                JSONObject newTypeExtend = new JSONObject();
                newTypeExtend.put("key", "type");
                newTypeExtend.put("name", "类型");
                JSONArray newTypeExtendKV = new JSONArray();
                JSONObject kv = new JSONObject();
                kv.put("n", "全部");
                kv.put("v", "-1");
                newTypeExtendKV.put(kv);
                for (int j = 0; j < tags.length(); j++) {
                    JSONObject child = tags.getJSONObject(j);
                    kv = new JSONObject();
                    kv.put("n", child.getString("typeName"));
                    kv.put("v", child.getString("typeId"));
                    newTypeExtendKV.put(kv);
                }
                newTypeExtend.put("value", newTypeExtendKV);
                extendsAll.put(newTypeExtend);
                // 排序
                newTypeExtend = new JSONObject();
                newTypeExtend.put("key", "sort");
                newTypeExtend.put("name", "排序");
                newTypeExtendKV = new JSONArray();
                kv = new JSONObject();
                kv.put("n", "最热");
                kv.put("v", "2");
                newTypeExtendKV.put(kv);
                kv = new JSONObject();
                kv.put("n", "最新");
                kv.put("v", "1");
                newTypeExtendKV.put(kv);
                kv = new JSONObject();
                kv.put("n", "好评");
                kv.put("v", "3");
                newTypeExtendKV.put(kv);
                newTypeExtend.put("value", newTypeExtendKV);
                extendsAll.put(newTypeExtend);
                // 地区
                JSONArray regions = item.getJSONArray("children");
                newTypeExtend = new JSONObject();
                newTypeExtend.put("key", "area");
                newTypeExtend.put("name", "地区");
                newTypeExtendKV = new JSONArray();
                kv = new JSONObject();
                kv.put("n", "全部");
                kv.put("v", "-1");
                newTypeExtendKV.put(kv);
                for (int j = 0; j < regions.length(); j++) {
                    JSONObject child = regions.getJSONObject(j);
                    kv = new JSONObject();
                    kv.put("n", child.getString("typeName"));
                    kv.put("v", child.getString("typeId"));
                    newTypeExtendKV.put(kv);
                }
                newTypeExtend.put("value", newTypeExtendKV);
                extendsAll.put(newTypeExtend);

                // 年份
                newTypeExtend = new JSONObject();
                newTypeExtend.put("key", "year");
                newTypeExtend.put("name", "年份");
                newTypeExtendKV = new JSONArray();
                kv = new JSONObject();
                kv.put("n", "全部");
                kv.put("v", "-1");
                newTypeExtendKV.put(kv);
                for (int j = 0; j < years.length(); j++) {
                    String year = years.getString(j).trim();
                    if (year.isEmpty())
                        continue;
                    kv = new JSONObject();
                    kv.put("n", year);
                    kv.put("v", year);
                    newTypeExtendKV.put(kv);
                }
                newTypeExtend.put("value", newTypeExtendKV);
                extendsAll.put(newTypeExtend);
                filterConfig.put(typeId, extendsAll);
            }
            JSONObject result = new JSONObject();
            result.put("class", classes);
            if (filter) {
                result.put("filters", filterConfig);
            }
            return result.toString();
        } catch (Throwable th) {

        }
        return "";
    }

    @Override
    public String homeVideoContent() {
        try {
            checkDomain();
            String url = staticDomain + "/static/" + appId + "/index/cloumn/1.json";
            String content = OkHttpUtil.string(url, getHeaders(url));
            JSONObject jsonObject = new JSONObject(content).getJSONObject("data");
            JSONArray list = jsonObject.getJSONArray("list");
            JSONArray videos = new JSONArray();
            for (int i = 0; i < list.length(); i++) {
                JSONArray videoList = list.getJSONObject(i).getJSONArray("videoList");
                for (int j = 0; j < videoList.length(); j++) {
                    JSONObject vObj = videoList.getJSONObject(j);
                    JSONObject v = new JSONObject();
                    v.put("vod_id", vObj.getString("id"));
                    v.put("vod_name", vObj.getString("vodName"));
                    v.put("vod_pic", fixUrl(vObj.getString("vodPicThumb")));
                    v.put("vod_remarks", vObj.getString("lastName"));
                    videos.put(v);
                }
            }
            JSONObject result = new JSONObject();
            result.put("list", videos);
            return result.toString();
        } catch (Throwable th) {

        }
        return "";
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) {
        try {
            checkDomain();
            JSONObject result = new JSONObject();

            String url = apiDomain + "/videolibrary/v2/" + appId + "/" + tid;
            extend = extend == null ? new HashMap<>() : extend;
            if (!extend.containsKey("area")) {
                extend.put("area", "-1");
            }
            if (!extend.containsKey("type")) {
                extend.put("type", "-1");
            }
            if (!extend.containsKey("sort")) {
                extend.put("sort", "2");
            }
            if (!extend.containsKey("year")) {
                extend.put("year", "-1");
            }
            url += "/" + extend.get("area");
            url += "/" + extend.get("type");
            url += "/" + extend.get("sort");
            url += "/-1";
            url += "/" + extend.get("year");
            url += "/-1/" + pg + ".json";

            String content = OkHttpUtil.string(url, getHeaders(url));
            JSONObject jsonObject = new JSONObject(content).getJSONObject("data");
            JSONArray jsonArray = jsonObject.getJSONArray("list");
            JSONArray videos = new JSONArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject vObj = jsonArray.getJSONObject(i);
                JSONObject v = new JSONObject();
                v.put("vod_id", vObj.getString("id"));
                v.put("vod_name", vObj.getString("vodName"));
                v.put("vod_pic", fixUrl(vObj.getString("vodPicThumb")));
                v.put("vod_remarks", vObj.getString("lastName"));
                videos.put(v);
            }

            int total = jsonObject.getInt("count");
            int limit = jsonObject.getInt("pageSize");
            int totalPg = total % limit == 0 ? (total / limit) : (total / limit + 1);
            result.put("page", jsonObject.getInt("pageIndex"));
            result.put("pagecount", totalPg);
            result.put("limit", limit);
            result.put("total", total);
            result.put("list", videos);
            return result.toString();
        } catch (Throwable th) {

        }
        return "";
    }

    @Override
    public String detailContent(List<String> ids) {
        try {
            checkDomain();
            JSONObject result = new JSONObject();
            String url = staticDomain + "/static/video/detail/" + ids.get(0) + ".json";
            String content = OkHttpUtil.string(url, getHeaders(url));
            JSONObject jsonObject = new JSONObject(content).getJSONObject("data");
            JSONObject vodList = new JSONObject();
            vodList.put("vod_id", jsonObject.getString("id"));
            vodList.put("vod_name", jsonObject.getString("vodName"));
            vodList.put("vod_pic", fixUrl(jsonObject.getString("vodPic")));
            vodList.put("type_name", jsonObject.getString("typeName"));
            vodList.put("vod_year", jsonObject.getString("vodYear"));
            vodList.put("vod_area", jsonObject.getString("vodArea"));
            vodList.put("vod_remarks", "");
            vodList.put("vod_actor", jsonObject.getString("vodActor"));
            vodList.put("vod_director", "");
            vodList.put("vod_content", jsonObject.getString("vodBlurb"));

            String vodPlayUrl = jsonObject.getString("vodPlayUrl");
            vodPlayUrl = rsa(vodPlayUrl.substring(0, 10) + vodPlayUrl.substring(16));

            ArrayList<String> playFrom = new ArrayList<>();
            ArrayList<String> playList = new ArrayList<>();

            JSONArray playSource = new JSONArray(vodPlayUrl);
            for (int i = 0; i < playSource.length(); i++) {
                JSONObject item = playSource.getJSONObject(i);
                String srcName = item.getString("name");
                boolean userProxy = srcName.equals("高速线路");
                ArrayList<String> urls = new ArrayList<>();
                JSONArray playUrls = item.getJSONArray("list");
                for (int j = 0; j < playUrls.length(); j++) {
                    JSONObject urlObj = playUrls.getJSONObject(j);
                    if (userProxy)
                        urls.add(urlObj.getString("name") + "$" + Proxy.localProxyUrl() + "?do=kmys&type=m3u8&url=" + Base64.encodeToString(urlObj.getString("url").getBytes(Misc.CharsetUTF8), Base64.DEFAULT | Base64.URL_SAFE | Base64.NO_WRAP) + ".." + urlObj.getInt("isParse"));
                    else
                        urls.add(urlObj.getString("name") + "$" + urlObj.getString("url") + ".." + urlObj.getInt("isParse"));
                }
                if (urls.isEmpty())
                    continue;
                playFrom.add(srcName);
                playList.add(TextUtils.join("#", urls));
            }

            String vod_play_from = TextUtils.join("$$$", playFrom);
            String vod_play_url = TextUtils.join("$$$", playList);
            vodList.put("vod_play_from", vod_play_from);
            vodList.put("vod_play_url", vod_play_url);

            JSONArray list = new JSONArray();
            list.put(vodList);
            result.put("list", list);
            return result.toString();
        } catch (Throwable th) {

        }
        return "";
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) {
        try {
            JSONObject result = new JSONObject();
            String[] info = id.split("\\.\\.");
            String url = info[0];
            int isParse = Integer.parseInt(info[1]);
            if (isParse == 2) {
                result.put("parse", 0);
                result.put("playUrl", "");
                result.put("url", url);
            } else {
                result.put("parse", 0);
                result.put("playUrl", "");
                result.put("url", url);
            }
            return result.toString();
        } catch (Throwable th) {

        }
        return "";
    }

    @Override
    public String searchContent(String key, boolean quick) {
        try {
            checkDomain();
            JSONObject result = new JSONObject();
            String url = apiDomain + "/api/v2/index/search?pageIndex=1&wd=" + URLEncoder.encode(key) + "&limit=10&type=1";
            String content = OkHttpUtil.string(url, getHeaders(url));
            JSONArray videos = new JSONArray();
            JSONObject jsonObject = new JSONObject(content).getJSONObject("data");
            JSONArray jsonArray = jsonObject.getJSONArray("list");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject vObj = jsonArray.getJSONObject(i);
                JSONObject v = new JSONObject();
                String title = vObj.getString("vodName");
                if (!title.contains(key))
                    continue;
                v.put("vod_id", vObj.getString("id"));
                v.put("vod_name", title);
                v.put("vod_pic", fixUrl(vObj.getString("vodPicThumb")));
                v.put("vod_remarks", vObj.getString("lastName"));
                videos.put(v);
            }
            result.put("list", videos);
            return result.toString();
        } catch (Throwable th) {

        }
        return "";
    }

    protected String fixUrl(String src) {
        try {
            if (src.startsWith("//")) {
                Uri parse = Uri.parse(staticDomain);
                src = parse.getScheme() + ":" + src;
            } else if (!src.contains("://")) {
                Uri parse = Uri.parse(staticDomain);
                if (!src.startsWith("/"))
                    src = "/" + src;
                src = parse.getScheme() + "://" + parse.getHost() + src;
            }
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return src;
    }

    String rsa(String in) {
        try {
            RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.decode("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA3VLHgbkFN0ebMaR4e0D\n" +
                    "z6Z2mFexPBFKGqK0tuRhzu7XOrG92nKWfnublf2p1i22UN81whBLINjMttOuqW6\n" +
                    "fM9DCnAPTelud1zCXWYWIsv5Z19inJSG8vytJ7xg1dnfuRSRUkx11IE7bm0T/sM\n" +
                    "0sI4GgcktQJNSizyirHtuJjUUxxQabEhFkFeqQ5r+A69KjB5QkotCc4pG5lENyT\n" +
                    "ARHGSsfaiJthaiH0yJ/8tUlyMgJ9H6/jbQg0wlLcEUzdfe2KuCPrTRzIzx4Cjm1\n" +
                    "JogT6JV2byvXpzAMC3O48LDiekJdVztg2Cj7E0cGrOsGs+IK6F7TWsKD/cIELTF\n" +
                    "hLz6dExQIDAQAB", Base64.DEFAULT)));
            Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, pubKey);
            byte[] inData = Base64.decode(in, Base64.DEFAULT);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            if (inData.length <= 256) {
                outputStream.write(cipher.doFinal(inData));
            } else {
                for (int i = 0; i < inData.length; i += 256) {
                    outputStream.write(cipher.doFinal(inData, i, 256));
                }
            }
            outputStream.flush();
            String result = new String(outputStream.toByteArray(), Misc.CharsetUTF8);
            outputStream.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static HashMap<String, String> kmysPlayerHeaders = null;
    private static String signPlayerStr = "700a4cce35";
    private static final Pattern tsRex = Pattern.compile("(\\S+.ts)|(#EXT-X-KEY:\\S+\")(\\S+)(\")");

    static String subUrl(String url) {
        if (url.startsWith("http://")) {
            return url.substring(0, url.substring(7).indexOf(47) + 7);
        }
        String tmp = url.startsWith("https://") ? url.substring(0, url.substring(8).indexOf(47) + 8) : "";
        return url.substring(tmp.length());
    }

    static Object[] getVodContent(String m3u8) {
        Uri uri = Uri.parse(m3u8);
        String time = String.valueOf(System.currentTimeMillis());
        String key = Misc.MD5(subUrl(m3u8) + signPlayerStr + time, Misc.CharsetUTF8).toLowerCase();
        String realUrl = m3u8 + "?key=" + key + "&v=360&i=5&p=7&ed=1a7348ae45f8f6de4d798614fc07e7271657392501986&time=" + time;
        String m3u8Content = OkHttpUtil.string(realUrl, kmysPlayerHeaders);
        String tsUrl = m3u8.substring(0, m3u8.indexOf(uri.getLastPathSegment()));
        StringBuilder sb = new StringBuilder();
        int start = 0;
        Matcher matcher = tsRex.matcher(m3u8Content);
        while (matcher.find()) {
            sb.append(m3u8Content, start, matcher.start());
            if (matcher.group().contains("EXT-X-KEY")) {
                String keyRealUrl = Proxy.localProxyUrl() + "?do=kmys&type=key&url=" + Base64.encodeToString((tsUrl + matcher.group(3)).getBytes(Misc.CharsetUTF8), Base64.DEFAULT | Base64.URL_SAFE | Base64.NO_WRAP);
                sb.append(matcher.group(2) + keyRealUrl + matcher.group(4));
            } else {
                String tsRealUrl = Proxy.localProxyUrl() + "?do=kmys&type=ts&url=" + Base64.encodeToString((tsUrl + matcher.group(1)).getBytes(Misc.CharsetUTF8), Base64.DEFAULT | Base64.URL_SAFE | Base64.NO_WRAP);
                sb.append(tsRealUrl);
            }
            start = matcher.end();
        }
        sb.append(m3u8Content, start, m3u8Content.length());
        String type = "application/octet-stream";
        Object[] result = new Object[3];
        result[0] = 200;
        result[1] = type;
        ByteArrayInputStream baos = new ByteArrayInputStream(sb.toString().getBytes(Misc.CharsetUTF8));
        result[2] = baos;
        return result;
    }

    static Object[] getKeyContent(String key) {
        String time = String.valueOf(System.currentTimeMillis());
        String md5 = Misc.MD5(subUrl(key) + signPlayerStr + time, Misc.CharsetUTF8).toLowerCase();
        String realUrl = key + "?key=" + md5 + "&v=360&i=5&p=7&ed=1a7348ae45f8f6de4d798614fc07e7271657392501986&time=" + time;
        String keyContent = OkHttpUtil.string(realUrl, kmysPlayerHeaders);
        String type = "application/octet-stream";
        Object[] result = new Object[3];
        result[0] = 200;
        result[1] = type;
        ByteArrayInputStream baos = new ByteArrayInputStream(keyContent.getBytes(Misc.CharsetUTF8));
        result[2] = baos;
        return result;
    }

    static Object[] getTsContent(String ts) {
        String time = String.valueOf(System.currentTimeMillis());
        String key = Misc.MD5(subUrl(ts) + signPlayerStr + time, Misc.CharsetUTF8).toLowerCase();
        String realUrl = ts + "?key=" + key + "&v=360&i=5&p=7&ed=1a7348ae45f8f6de4d798614fc07e7271657392501986&time=" + time;
        OKCallBack.OKCallBackDefault callBack = new OKCallBack.OKCallBackDefault() {


            @Override
            public void onFailure(Call call, Exception e) {

            }

            @Override
            public void onResponse(Response response) {
            }
        };
        OkHttpUtil.get(OkHttpUtil.defaultClient(), realUrl, null, kmysPlayerHeaders, callBack);
        if (callBack.getResult().code() == 200) {
            Headers headers = callBack.getResult().headers();
            String type = headers.get("Content-Type");
            if (type == null) {
                type = "application/octet-stream";
            }
            Object[] result = new Object[3];
            result[0] = 200;
            result[1] = type;
            result[2] = callBack.getResult().body().byteStream();
            return result;
        }
        return null;
    }

    public static Object[] vod(Map<String, String> params) {
        String type = params.get("type");
        String url = params.get("url");
        url = new String(Base64.decode(url, Base64.DEFAULT | Base64.URL_SAFE | Base64.NO_WRAP), Misc.CharsetUTF8);
        if (kmysPlayerHeaders == null) {
            kmysPlayerHeaders = new HashMap<>();
            kmysPlayerHeaders.put("User-Agent", "okhttp/3.14.7");
            kmysPlayerHeaders.put("ed", "1a7348ae45f8f6de4d798614fc07e7271657392501986");
            kmysPlayerHeaders.put("Connection", "close");
        }
        if (type.equals("m3u8")) {
            return getVodContent(url);
        } else if (type.equals("key")) {
            return getKeyContent(url);
        } else if (type.equals("ts")) {
            return getTsContent(url);
        }
        return null;
    }
}