package com.github.catvod.spider;

import android.content.Context;
import android.net.UrlQuerySanitizer;
import android.text.TextUtils;

import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.utils.Misc;
import com.github.catvod.utils.okhttp.OKCallBack;
import com.github.catvod.utils.okhttp.OkHttpUtil;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class PushAgent extends Spider {

    private static String j = "";
    private static long StopRefresh;
    private static final Map<String, String> l = new HashMap();
    private static final Map<String, Long> q = new HashMap();
    private static final Map<String, Map<String, String>> dizz = new HashMap();
    private static final ReentrantLock ReentrantLock = new ReentrantLock();
    private static final long n = 0;
    private static final String SiteUrl = "https://api.aliyundrive.com";
    private static final Pattern AliPLink = Pattern.compile("(https://www.aliyundrive.com/s/[^\"]+)");
    public static Pattern Folder = Pattern.compile("www.aliyundrive.com/s/([^/]+)(/folder/([^/]+))?");
    public static String Token;

    @Override
    public void init(Context context, String extend) {
        super.init(context, extend);
        if (extend.startsWith("http")) {
            Token = OkHttpUtil.string(extend, null);
        } else {
            Token = extend;
        }
    }

    protected static long Time() {
        return (System.currentTimeMillis() / 1000) + n;
    }

    public static Object[] ProxyMedia(Map<String, String> map) {
        try {
            String ShareId = map.get("share_id");
            String FileId = map.get("file_id");
            String MediaId = map.get("media_id");
            String Token = getShareTk(ShareId, "");
            ReentrantLock.lock();
            String str4 = dizz.get(FileId).get(MediaId);
            UrlQuerySanitizer urlQuerySanitizer = new UrlQuerySanitizer(str4);
            Long l2 = new Long(urlQuerySanitizer.getValue("x-oss-expires"));
            long longValue = l2.longValue();
            if (longValue - Time() <= 60) {
                VideoDetail(ShareId, Token, FileId);
                Map<String, Map<String, String>> map3 = dizz;
                str4 = map3.get(FileId).get(MediaId);
            }
            ReentrantLock.unlock();
            new HashMap();
            OKCallBack.OKCallBackDefault abVar = new OKCallBack.OKCallBackDefault() {
                public void onResponse(Response response) {
                }

                @Override
                protected void onFailure(Call call, Exception exc) {
                }
            };
            OkHttpClient YM = OkHttpUtil.defaultClient();
            HashMap<String, String> t = Headers();
            OkHttpUtil.get(YM, str4, null, t, abVar);
            ResponseBody body = abVar.getResult().body();
            return new Object[]{200, "video/MP2T", body.byteStream()};
        } catch (Exception e) {
            SpiderDebug.log(e);
            return null;
        }
    }

    public static Object[] vod(Map<String, String> map) {
        String str = map.get("type");
        if (str.equals("m3u8")) {
            return getFile(map);
        }
        if (str.equals("media")) {
            return ProxyMedia(map);
        }
        return null;
    }


    public static Object[] getFile(Map<String, String> map) {
        try {
            String ShareId = map.get("share_id");
            String ShareTK = getShareTk(ShareId, "");
            return new Object[]{200, "application/octet-stream", new ByteArrayInputStream(VideoDetail(ShareId, ShareTK, map.get("file_id")).getBytes())};
        } catch (Exception e) {
            SpiderDebug.log(e);
            return null;
        }
    }

    private static String Post(String str, String str2, Map<String, String> map) {
        OKCallBack.OKCallBackString acVar = new OKCallBack.OKCallBackString() {

            public void onResponse(String r) {
            }

            @Override
            protected void onFailure(Call call, Exception exc) {
            }
        };
        OkHttpClient YM = OkHttpUtil.defaultClient();
        OkHttpUtil.postJson(YM, str, str2, map, acVar);
        return acVar.getResult();
    }

    private static synchronized String getShareTk(String str, String str2) {

        try {
            long b = Time();
            Map<String, String> map = l;
            String str3 = map.get(str);
            Long l2 = q.get(str);
            if (!TextUtils.isEmpty(str3)) {
                if (l2 - b > 600) {
                    return str3;
                }
            }
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("share_id", str);
            jSONObject.put("share_pwd", str2);
            String jSONObject2 = jSONObject.toString();
            JSONObject jSONObject3 = new JSONObject(Post("https://api.aliyundrive.com/v2/share_link/get_share_token", jSONObject2, Headers()));
            String string = jSONObject3.getString("share_token");
            Long valueOf = Long.valueOf(b + jSONObject3.getLong("expires_in"));
            Map<String, Long> map2 = q;
            map2.put(str, valueOf);
            Map<String, String> map3 = l;
            map3.put(str, string);
            return string;
        } catch (Exception e) {
            SpiderDebug.log(e);
            return "";
        }

    }


    private static HashMap<String, String> Headers() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.54 Safari/537.36");
        headers.put("Referer", "https://www.aliyundrive.com/");
        return headers;
    }

    private static HashMap<String, String> sHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.54 Safari/537.36");
        return headers;
    }


    private static void refreshTk() {
        long b = Time();
        String str = j;
        if (str.isEmpty() || StopRefresh - b <= 600) {
            try {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("refresh_token", Token);
                String jSONObject2 = jSONObject.toString();
                HashMap<String, String> t = Headers();
                JSONObject jSONObject3 = new JSONObject(Post("https://api.aliyundrive.com/token/refresh", jSONObject2, t));
                StringBuilder sb = new StringBuilder();
                String string = jSONObject3.getString("token_type");
                sb.append(string);
                sb.append(" ");
                String string2 = jSONObject3.getString("access_token");
                sb.append(string2);
                j = sb.toString();
                long j2 = jSONObject3.getLong("expires_in");
                StopRefresh = b + j2;
            } catch (Exception e) {
                SpiderDebug.log(e);
            }
        }
    }


    private static String VideoDetail(String shareid, String token, String fileid) {
        int i;
        try {
            String url = SiteUrl + "/v2/file/get_share_link_video_preview_play_info";
            JSONObject Data = new JSONObject();
            Data.put("share_id", shareid);
            Data.put("category", "live_transcoding");
            Data.put("file_id", fileid);
            Data.put("template_id", "");
            HashMap<String, String> headers = Headers();
            headers.put("x-share-token", token);
            String str5 = j;
            headers.put("authorization", str5);
            String jSONObject2 = Data.toString();
            JSONObject jSONObject3 = new JSONObject(Post(url, jSONObject2, headers));
            ArrayList arrayList = new ArrayList();
            arrayList.add("FHD");
            arrayList.add("HD");
            arrayList.add("SD");
            JSONObject jSONObject4 = jSONObject3.getJSONObject("video_preview_play_info");
            JSONArray jSONArray = jSONObject4.getJSONArray("live_transcoding_task_list");
            Iterator it = arrayList.iterator();
            String objectUrl = "";
            while (true) {
                boolean hasNext = it.hasNext();
                i = 0;
                if (!hasNext) {
                    break;
                }
                String str7 = (String) it.next();
                if (!objectUrl.isEmpty()) {
                    break;
                }
                while (true) {
                    if (i < jSONArray.length()) {
                        JSONObject jSONObject5 = jSONArray.getJSONObject(i);
                        String string = jSONObject5.getString("template_id");
                        if (string.equals(str7)) {
                            objectUrl = jSONObject5.getString("url");
                            break;
                        }
                        i++;
                    }
                }
            }
            if (TextUtils.isEmpty(objectUrl)) {
                JSONObject jSONObject6 = jSONArray.getJSONObject(0);
                objectUrl = jSONObject6.getString("url");
            }
            HashMap hashMap = new HashMap();
            HashMap<String, String> header = Headers();
            OkHttpUtil.stringNoRedirect(objectUrl, header, hashMap);
            String d = OkHttpUtil.getRedirectLocation(hashMap);
            String i2 = OkHttpUtil.string(d, Headers());
            String substring = d.substring(0, d.lastIndexOf("/")) + "/";
            ArrayList arrayList2 = new ArrayList();
            HashMap hashMap2 = new HashMap();
            String[] split = i2.split("\n");
            int length = split.length;
            int i3 = 0;
            while (i < length) {
                String str8 = split[i];
                if (str8.contains("x-oss-expires")) {
                    i3++;
                    String sb6 = substring + str8;
                    String sb8 = "" + i3;
                    hashMap2.put(sb8, sb6);
                    str8 = Proxy.localProxyUrl() + "?do=push&type=media&share_id=" + shareid + "&file_id=" + fileid + "&media_id=" + i3;
                }
                arrayList2.add(str8);
                i++;
            }
            dizz.put(fileid, hashMap2);
            return TextUtils.join("\n", arrayList2);
        } catch (Exception e) {
            SpiderDebug.log(e);
            return "";
        }
    }

    public void listFiles(Map<String, String> map, String str, String str2, String str3) {
        String str4;
        try {
            String str5 = "https://api.aliyundrive.com/adrive/v3/file/list";
            HashMap<String, String> Y2 = Headers();
            Y2.put("x-share-token", str2);
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("image_thumbnail_process", "image/resize,w_160/format,jpeg");
            jSONObject.put("image_url_process", "image/resize,w_1920/format,jpeg");
            jSONObject.put("limit", 200);
            jSONObject.put("order_by", "updated_at");
            jSONObject.put("order_direction", "DESC");
            jSONObject.put("parent_file_id", str3);
            jSONObject.put("share_id", str);
            jSONObject.put("video_thumbnail_process", "video/snapshot,t_1000,f_jpg,ar_auto,w_300");
            String str6 = "";
            ArrayList<String> arrayList = new ArrayList();
            for (int i = 1; i <= 50 && (i < 2 || !TextUtils.isEmpty(str6)); i++) {
                jSONObject.put("marker", str6);
                JSONObject jSONObject2 = new JSONObject(Post(str5, jSONObject.toString(), Y2));
                JSONArray jSONArray = jSONObject2.getJSONArray("items");
                int i2 = 0;
                while (i2 < jSONArray.length()) {
                    JSONObject jSONObject3 = jSONArray.getJSONObject(i2);
                    if (jSONObject3.getString("type").equals("folder")) {
                        arrayList.add(jSONObject3.getString("file_id"));
                        str4 = str5;
                    } else {
                        str4 = str5;
                        if (jSONObject3.getString("mime_type").contains("video")) {
                            String replace = jSONObject3.getString("name").replace("#", "_").replace("$", "_");
                            if (replace.length() > 20) {
                                replace = replace.substring(0, 10) + "..." + replace.substring(replace.length() - 10);
                            }
                            String fileIds = jSONObject3.getString("file_id");
                            map.put(replace, str + "+" + str2 + "+" + fileIds);
                        }
                    }
                    i2++;
                    str5 = str4;
                }
                str6 = jSONObject2.getString("next_marker");
            }
            for (String str7 : arrayList) {
                try {
                    listFiles(map, str, str2, str7);
                } catch (Exception e) {
                    SpiderDebug.log(e);
                    return;
                }
            }
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
    }

    public String getAliContent(List<String> list) {
        String str;
        try {
            String url = list.get(0).trim();
            Pattern pattern = Folder;
            Matcher matcher = pattern.matcher(url);
            if (!matcher.find()) {
                return "";
            }
            String group = matcher.group(1);
            if (matcher.groupCount() == 3) {
                str = matcher.group(3);
            } else {
                str = "";
            }
            String AnonymousUrl = SiteUrl + "/adrive/v3/share_link/get_share_by_anonymous";
            JSONObject Data = new JSONObject();
            Data.put("share_id", group);
            HashMap<String, String> headers = Headers();
            JSONObject jSONObject3 = new JSONObject(Post(AnonymousUrl, Data.toString(), headers));
            JSONArray jSONArray = jSONObject3.getJSONArray("file_infos");
            if (jSONArray.length() == 0) {
                return "";
            }
            JSONObject jSONObject4 = null;
            boolean isEmpty = TextUtils.isEmpty(str);
            if (!isEmpty) {
                int i = 0;
                while (true) {
                    if (i >= jSONArray.length()) {
                        break;
                    }
                    JSONObject jSONObject5 = jSONArray.getJSONObject(i);
                    String string = jSONObject5.getString("file_id");
                    String string2 = jSONObject5.getString("file_id");
                    if (string.equals(string2)) {
                        jSONObject4 = jSONObject5;
                        break;
                    }
                    i++;
                }
                if (jSONObject4 == null) {
                    return "";
                }
            } else {
                jSONObject4 = jSONArray.getJSONObject(0);
                str = jSONObject4.getString("file_id");
            }
            JSONObject jSONObject6 = new JSONObject();
            jSONObject6.put("vod_id", url);
            String string3 = jSONObject3.getString("share_name");
            jSONObject6.put("vod_name", string3);
            jSONObject6.put("vod_pic", jSONObject3.getString("avatar"));
            jSONObject6.put("vod_content", url);
            jSONObject6.put("vod_play_from", "AliYun");
            ArrayList arrayList = new ArrayList();
            String string4 = jSONObject4.getString("type");
            if (!string4.equals("folder")) {
                String string5 = jSONObject4.getString("type");
                if (string5.equals("file")) {
                    String string6 = jSONObject4.getString("category");
                    if (string6.equals("video")) {
                        str = "root";
                    }
                }
                return "";
            }

            String s = getShareTk(group, "");
            Map<String, String> hashMap = new HashMap<>();
            listFiles(hashMap, group, s, str);
            ArrayList arrayList2 = new ArrayList(hashMap.keySet());
            Collections.sort(arrayList2);
            Iterator it = arrayList2.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                String str3 = (String) it.next();
                String sb4 = str3 + "$" + hashMap.get(str3);
                arrayList.add(sb4);
            }
            ArrayList arrayList3 = new ArrayList();
            for (int i2 = 0; i2 < 4; i2++) {
                String join = TextUtils.join("#", arrayList);
                arrayList3.add(join);
            }
            String join2 = TextUtils.join("$$$", arrayList3);
            jSONObject6.put("vod_play_url", join2);
            JSONObject result = new JSONObject();
            JSONArray jSONArray2 = new JSONArray();
            jSONArray2.put(jSONObject6);
            result.put("list", jSONArray2);
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
            return "";
        }
    }

    @Override
    public String detailContent(List<String> list) {
        try {
            String url = list.get(0);
            Pattern pattern = Folder;
            Pattern pattern2 = AliPLink;
            Matcher matcher2 = pattern2.matcher(url);
            Matcher matcher = pattern.matcher(url);
            if (Misc.isVip(url) && !url.contains("qq.com") && !url.contains("mgtv.com")) {
                Document doc = Jsoup.parse(OkHttpUtil.string(url, null));
                String VodName = doc.select("head > title").text();
                JSONObject result = new JSONObject();
                JSONArray lists = new JSONArray();
                JSONObject vodAtom = new JSONObject();
                vodAtom.put("vod_id", url);
                vodAtom.put("vod_name", VodName);
                vodAtom.put("vod_pic", "https://img.zcool.cn/community/0123545c74c5aea801213f261297df.png");
                vodAtom.put("type_name", "官源");
                vodAtom.put("vod_year", "");
                vodAtom.put("vod_area", "");
                vodAtom.put("vod_remarks", "");
                vodAtom.put("vod_actor", "");
                vodAtom.put("vod_director", "");
                vodAtom.put("vod_content", "");
                vodAtom.put("vod_play_from", "jx");
                vodAtom.put("vod_play_url", "立即播放$" + url);
                lists.put(vodAtom);
                result.put("list", lists);
                return result.toString();
            } else if (Misc.isVip(url) && url.contains("qq.com")) {
                List<String> vodItems = new ArrayList<>();
                JSONObject result = new JSONObject();
                JSONArray lists = new JSONArray();
                JSONObject vodAtom = new JSONObject();
                Document doc = Jsoup.parse(OkHttpUtil.string(url, sHeaders()));
                String VodName = doc.select("head > title").text();
                Elements playListA = doc.select("div.episode-list-rect__item");
                if (!playListA.isEmpty()) {
                    for (int j = 0; j < playListA.size(); j++) {
                        Element vod = playListA.get(j);
                        String a = vod.select("div").attr("data-vid");
                        String b = vod.select("div").attr("data-cid");
                        String id = "https://v.qq.com/x/cover/" + b + "/" + a;
                        String name = vod.select("div span").text();
                        vodItems.add(name + "$" + id);
                    }
                    String playList = TextUtils.join("#", vodItems);
                    vodAtom.put("vod_play_url", playList);
                } else {
                    vodAtom.put("vod_play_url", "立即播放$" + url);
                }
                vodAtom.put("vod_id", url);
                vodAtom.put("vod_name", VodName);
                vodAtom.put("vod_pic", "https://img2.baidu.com/it/u=2655029475,2190949369&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=593");
                vodAtom.put("type_name", "腾讯TV");
                vodAtom.put("vod_year", "");
                vodAtom.put("vod_area", "");
                vodAtom.put("vod_remarks", "");
                vodAtom.put("vod_actor", "");
                vodAtom.put("vod_director", "");
                vodAtom.put("vod_content", url);
                vodAtom.put("vod_play_from", "jx");
                lists.put(vodAtom);
                result.put("list", lists);
                return result.toString();
            } else if (Misc.isVip(url) && url.contains("mgtv.com")) {
                List<String> vodItems = new ArrayList<>();
                JSONObject result = new JSONObject();
                JSONArray lists = new JSONArray();
                JSONObject vodAtom = new JSONObject();
                Pattern mgtv = Pattern.compile("https://\\S+mgtv.com/b/(\\d+)/(\\d+).html.*");
                Matcher mgtv1 = mgtv.matcher(url);
                String VodNames = "";
                if (mgtv1.find()) {
                    String Ep = "https://pcweb.api.mgtv.com/episode/list?video_id=" + mgtv1.group(2);
                    JSONObject Data = new JSONObject(OkHttpUtil.string(Ep, Headers()));
                    VodNames = Data.getJSONObject("data").getJSONObject("info").getString("title");
                    JSONArray a = new JSONArray(Data.getJSONObject("data").getString("list"));
                    if (a.length() > 0) {
                        for (int i = 0; i < a.length(); i++) {
                            JSONObject jObj = a.getJSONObject(i);
                            if (jObj.getString("isIntact").equals("1")) {
                                String VodName = jObj.getString("t4");
                                String id = jObj.getString("video_id");
                                String VodId = "https://www.mgtv.com/b/" + mgtv1.group(1) + "/" + id + ".html";
                                vodItems.add(VodName + "$" + VodId);
                            }
                        }
                        String playList = TextUtils.join("#", vodItems);
                        vodAtom.put("vod_play_url", playList);
                    } else {
                        vodAtom.put("vod_play_url", "立即播放$" + url);
                    }
                }
                vodAtom.put("vod_id", url);
                vodAtom.put("vod_name", VodNames);
                vodAtom.put("vod_pic", "https://img2.baidu.com/it/u=2562822927,704100654&fm=253&fmt=auto&app=138&f=JPEG?w=600&h=380");
                vodAtom.put("type_name", "芒果TV");
                vodAtom.put("vod_year", "");
                vodAtom.put("vod_area", "");
                vodAtom.put("vod_remarks", "");
                vodAtom.put("vod_actor", "");
                vodAtom.put("vod_director", "");
                vodAtom.put("vod_content", url);
                vodAtom.put("vod_play_from", "jx");
                lists.put(vodAtom);
                result.put("list", lists);
                return result.toString();
            } else if (Misc.isVideoFormat(url)) {
                JSONObject result = new JSONObject();
                JSONArray lists = new JSONArray();
                JSONObject vodAtom = new JSONObject();
                vodAtom.put("vod_id", url);
                vodAtom.put("vod_name", url);
                vodAtom.put("vod_pic", "https://img.zcool.cn/community/0123545c74c5aea801213f261297df.png");
                vodAtom.put("type_name", "直连");
                vodAtom.put("vod_play_from", "player");
                vodAtom.put("vod_play_url", "立即播放$" + url);
                lists.put(vodAtom);
                result.put("list", lists);
                return result.toString();
            } else if (url.startsWith("magnet")) {
                String VodName = url;
                if (url.length() > 100) {
                    VodName = url.substring(0, 30) + "..." + url.substring(url.length() - 10);
                }
                JSONObject result = new JSONObject();
                JSONArray lists = new JSONArray();
                JSONObject vodAtom = new JSONObject();
                vodAtom.put("vod_id", url);
                vodAtom.put("vod_name", VodName);
                vodAtom.put("vod_pic", "https://img2.baidu.com/it/u=1609185522,4130752057&fm=253&f=JPEG");
                vodAtom.put("type_name", "磁力");
                vodAtom.put("vod_play_from", "磁力测试");
                vodAtom.put("vod_play_url", "立即播放$" + url);
                lists.put(vodAtom);
                result.put("list", lists);
                return result.toString();
            } else if (url.startsWith("http") && (matcher2.find())) {
                return getAliContent(list);
            } else if (url.startsWith("http") && (!matcher.find()) && (!matcher2.find())) {
                Document doc = Jsoup.parse(OkHttpUtil.string(url, null));
                String VodName = doc.select("head > title").text();
                JSONObject result = new JSONObject();
                JSONArray lists = new JSONArray();
                JSONObject vodAtom = new JSONObject();
                vodAtom.put("vod_id", url);
                vodAtom.put("vod_name", VodName);
                vodAtom.put("vod_pic", "https://pic.rmb.bdstatic.com/bjh/1d0b02d0f57f0a42201f92caba5107ed.jpeg");
                vodAtom.put("type_name", "嗅探");
                vodAtom.put("vod_content", url);
                vodAtom.put("vod_play_from", "嗅探");
                vodAtom.put("vod_play_url", "立即播放嗅探$" + url);
                lists.put(vodAtom);
                result.put("list", lists);
                return result.toString();
            }
        } catch (Throwable throwable) {

        }
        return "";
    }


    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) {
        try {
            switch (flag) {
                case "jx": {
                    JSONObject result = new JSONObject();
                    result.put("parse", 1);
                    result.put("jx", "1");
                    result.put("url", id);
                    return result.toString();
                }
                case "player": {
                    JSONObject result = new JSONObject();
                    result.put("parse", 0);
                    result.put("playUrl", "");
                    result.put("url", id);
                    return result.toString();
                }
                case "嗅探": {
                    JSONObject result = new JSONObject();
                    result.put("parse", 1);
                    result.put("playUrl", "");
                    result.put("url", id);
                    return result.toString();
                }
                case "AliYun":
                    refreshTk();
                    String[] split = id.split("\\+");
                    String str3 = split[0];
                    String str5 = split[2];
                    String url = Proxy.localProxyUrl() + "?do=push&type=m3u8&share_id=" + str3 + "&file_id=" + str5;
                    JSONObject jSONObject = new JSONObject();
                    jSONObject.put("parse", "0");
                    jSONObject.put("playUrl", "");
                    jSONObject.put("url", url);
                    jSONObject.put("header", "");
                    return jSONObject.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            SpiderDebug.log(e);
        }
        return "";
    }

}
