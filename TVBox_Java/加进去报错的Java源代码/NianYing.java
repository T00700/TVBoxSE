package com.github.catvod.spider;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;

import com.github.catvod.utils.okhttp.OkHttpUtil;
import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Nianying extends Spider {

    private static String host = "http://cms111.nbhut.com/api.php/Chengcheng/vod";

    public HashMap<String, String> headers() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 11; M2105K81AC Build/RKQ1.200826.002)");
        return hashMap;
    }


    @Override
    public void init(Context context) {
        super.init(context);
    }

    @Override
    public void init(Context context, String extend) {
        super.init(context, extend);
    }


    @Override
    public String homeContent(boolean filter) {
        try {
            JSONObject result = new JSONObject();
            JSONArray classes = new JSONArray();
            LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
            linkedHashMap.put("movie", "电影");
            linkedHashMap.put("tvplay", "剧集");
            linkedHashMap.put("comic", "动漫");
            linkedHashMap.put("tvshow", "综艺");
            for (String str : linkedHashMap.keySet()) {
                JSONObject item = new JSONObject();
                item.put("type_id", str);
                item.put("type_name", linkedHashMap.get(str));
                classes.put(item);
            }
            result.put("class", classes);

            if (filter) {
                JSONObject filterBox = new JSONObject();
                result.put("filters", filterBox);
                String json = OkHttpUtil.string(host + "/?ac=flitter", headers());
                JSONObject sourceData = new JSONObject(decrypt(json));
                for (String tid : linkedHashMap.keySet()) {
                    JSONArray filterArr = new JSONArray();
                    filterBox.put(tid, filterArr);
                    JSONArray typeJSON = sourceData.optJSONArray(tid);
                    for (int j = 0; j < typeJSON.length(); j++) {
                        JSONObject item = typeJSON.optJSONObject(j);
                        String itemName = item.optString("name");
                        String itemField = item.optString("field");
                        JSONArray itemValues = item.optJSONArray("values");
                        JSONObject jOne = new JSONObject();
                        jOne.put("key", itemField);
                        jOne.put("name", itemName);
                        filterArr.put(jOne);
                        JSONArray values = new JSONArray();
                        jOne.put("value", values);

                        JSONObject all = new JSONObject();
                        all.put("n", "全部");
                        all.put("v", "");
                        values.put(all);
                        for (int k = 0; k < itemValues.length(); k++) {
                            String k1 = itemValues.optString(k);
                            JSONObject kvo = new JSONObject();
                            kvo.put("n", k1);
                            kvo.put("v", k1);
                            values.put(kvo);
                        }
                    }
                }
            }

            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
            return "";
        }
    }

    private String decrypt(String data) {
        return data;
    }

    @Override
    public String homeVideoContent() {
        JSONObject result = new JSONObject();
        try {
            String url = host + "/?ac=list&class=tvplay&page=1";
            String encodeData = OkHttpUtil.string(url, headers());
            JSONObject json = new JSONObject(decrypt(encodeData));
            JSONArray data = json.optJSONArray("data");
            JSONArray list = new JSONArray();
            for (int i = 0; i < data.length(); i++) {
                JSONObject item = data.getJSONObject(i);
                JSONObject vod = new JSONObject();
                String nextlink = item.getString("nextlink");
                String vod_id = nextlink.substring(nextlink.indexOf("ids=") + 4);
                vod.put("vod_id", vod_id);
                vod.put("vod_name", item.getString("title"));
                vod.put("vod_pic", item.getString("pic"));
                vod.put("vod_remarks", item.getString("state"));
                list.put(vod);
            }
            result.put("list", list);
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return result.toString();
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) {
        JSONObject result = new JSONObject();
        try {
            String url = host + "/";
            Map<String, String> params = new HashMap<>();
            if (extend != null) {
                params.putAll(extend);
            }
            params.put("class", tid);
            params.put("page", pg);
            params.put("ac", "list");

            List<String> ps = new LinkedList<>();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                ps.add(entry.getKey() + "=" + entry.getValue());
            }
            url = url + "?" + TextUtils.join("&", ps);
            String encodeData = OkHttpUtil.string(url, headers());
            JSONObject json = new JSONObject(decrypt(encodeData));
            JSONArray list = json.optJSONArray("data");
            JSONArray vods = new JSONArray();
            for (int i = 0; i < list.length(); i++) {
                JSONObject item = list.getJSONObject(i);
                JSONObject vod = new JSONObject();
                String nextlink = item.getString("nextlink");
                String vod_id = nextlink.substring(nextlink.indexOf("ids=") + 4);
                vod.put("vod_id", vod_id);
                vod.put("vod_name", item.getString("title"));
                vod.put("vod_pic", item.getString("pic"));
                vod.put("vod_remarks", item.getString("state"));
                vods.put(vod);
            }
            int parseInt = Integer.parseInt(pg);
            result.put("page", parseInt);
            if (vods.length() == 20) {
                parseInt++;
            }
            result.put("pagecount", parseInt);
            result.put("limit", 20);
            result.put("total", Integer.MAX_VALUE);
            result.put("list", vods);
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return result.toString();
    }

    @Override
    public String detailContent(List<String> ids) {
        JSONObject result = new JSONObject();
        try {
            String url = host + "/?ac=detail&ids=" + ids.get(0);
            String encodeData = OkHttpUtil.string(url, headers());
            JSONObject vodInfo = new JSONObject(decrypt(encodeData));
            JSONObject vod = new JSONObject();
            vod.put("vod_id", vodInfo.getString("id"));
            vod.put("vod_name", vodInfo.getString("title"));
            vod.put("vod_pic", vodInfo.getString("img_url"));
            vod.put("vod_year", vodInfo.optString("pubtime"));
            vod.put("vod_remarks", vodInfo.optString("season_num"));
            vod.put("vod_content", vodInfo.optString("intro").trim());

            JSONArray type = vodInfo.optJSONArray("type");
            if (type != null && type.length() > 0) {
                vod.put("type_name", type.toString().replace("[\"", "").replace("\"]", ""));
            }
            JSONArray area = vodInfo.optJSONArray("area");
            if (area != null && area.length() > 0) {
                vod.put("vod_area", area.toString().replace("[\"", "").replace("\"]", ""));
            }

            JSONArray actor = vodInfo.optJSONArray("actor");
            if (actor != null && actor.length() > 0) {
                vod.put("vod_actor", actor.toString().replace("[\"", "").replace("\"]", ""));
            }

            JSONArray director = vodInfo.optJSONArray("director");
            if (director != null && director.length() > 0) {
                vod.put("vod_director", director.toString().replace("[\"", "").replace("\"]", ""));
            }

            JSONObject videolist = vodInfo.getJSONObject("videolist");
            Iterator<String> keys = videolist.keys();
            Map<String, String> map = new TreeMap<>((o1, o2) -> {
                if (o1.contains("m3u8")) {
                    if (o2.contains("lzm3u8")) {
                        return 1;
                    }
                    return 1;
                }
                if (o1.contains("ddzy")) {
                    return -1;
                }
                return 1;
            });
            while (keys.hasNext()) {
                String source = keys.next();
                JSONArray urls = videolist.optJSONArray(source);
                List<String> sourceValues = new ArrayList<>();
                for (int i = 0; i < urls.length(); i++) {
                    JSONObject item = urls.getJSONObject(i);
                    String title = item.optString("title");
                    String urlv = item.optString("url");
                    if (urlv.contains("url=")) {
                        urlv = urlv.replace("http://ry.nbhut.com/4.7.php?url=", "");
                    }
                    sourceValues.add(title + "$" + urlv);
                }

                map.put(source, TextUtils.join("#", sourceValues));
            }

            String join = TextUtils.join("$$$", map.keySet());
            String join2 = TextUtils.join("$$$", map.values());
            vod.put("vod_play_from", join);
            vod.put("vod_play_url", join2);
            JSONArray vods = new JSONArray();
            vods.put(vod);
            result.put("list", vods);
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return result.toString();
    }


    /**
     * @param flag
     * @param id
     * @param vipFlags
     * @return
     */
    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) {
        JSONObject result = new JSONObject();
        try {
            result.put("parse", "0");
            if (Misc.isVideoFormat(id)) {
                result.put("url", id);
            } else if (Misc.isVip(id)) {
                result.put("url", id);
                result.put("parse", "1");
            }
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return result.toString();
    }

    @Override
    public String searchContent(String key, boolean quick) {
        JSONObject result = new JSONObject();
        try {
            String url = host + "/?ac=list&wd=" + key + "&page=1";
            String encodeData = OkHttpUtil.string(url, headers());
            JSONObject json = new JSONObject(decrypt(encodeData));
            JSONArray list = json.optJSONArray("data");
            JSONArray vods = new JSONArray();
            for (int i = 0; i < list.length(); i++) {
                JSONObject item = list.getJSONObject(i);
                JSONObject vod = new JSONObject();
                String nextlink = item.getString("nextlink");
                String vod_id = nextlink.substring(nextlink.indexOf("ids=") + 4);
                vod.put("vod_id", vod_id);
                vod.put("vod_name", item.getString("title"));
                vod.put("vod_pic", item.getString("pic"));
                vod.put("vod_remarks", item.getString("state"));
                vods.put(vod);
            }
            result.put("list", vods);
        } catch (Exception e) {

        }
        return result.toString();
    }

    @Override
    public boolean isVideoFormat(String url) {
        return super.isVideoFormat(url);
    }

    @Override
    public boolean manualVideoCheck() {
        return super.manualVideoCheck();
    }

}
