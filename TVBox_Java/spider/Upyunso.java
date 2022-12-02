package com.github.catvod.spider;

import android.content.Context;
import android.util.Base64;

import com.github.catvod.crawler.Spider;
import com.github.catvod.utils.okhttp.OkHttpUtil;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Upyunso extends Spider {
    private PushAgent pushAgent;
    private static String siteUrl = "https://www.upyunso.com/";
    private static String apiUrl = "https://api.upyunso2.com/";
    private static Pattern aliyun = Pattern.compile("(https:\\/\\/www.aliyundrive.com\\/s\\/[^\\\"]+)");

    @Override
    public void init(Context context, String extend) {
        super.init(context, extend);
        pushAgent = new PushAgent();
        pushAgent.init(context, extend);
    }

    @Override
    public String detailContent(List<String> list) {
        try {
            Pattern pattern = aliyun;
            if (pattern.matcher(list.get(0)).find()) {
                return pushAgent.detailContent(list);
            }
            String jqurl=(apiUrl+list.get(0)).replace(".html","");
            String jxUrl=new String(Base64.decode(OkHttpUtil.string(jqurl, sHeaders()), Base64.DEFAULT));
            String realUrl= new JSONObject(jxUrl).getJSONObject("result").getString("res_url");
            Matcher matcher = pattern.matcher(realUrl);
            if (!matcher.find()) {
                return "";
            }
            list.set(0, matcher.group(1).replaceAll("\\\\", ""));
            return pushAgent.detailContent(list);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public String playerContent(String str, String str2, List<String> list) {
        return pushAgent.playerContent(str, str2, list);
    }

//    protected static HashMap<String, String> Headers() {
//        HashMap<String, String> headers = new HashMap<>();
//        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.5005.62 Safari/537.36");
//        headers.put("origin", "https://www.upyunso.com");
//        return headers;
//    }

    protected static HashMap<String, String> sHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.5005.62 Safari/537.36");
        return headers;
    }


    @Override
    public String searchContent(String key, boolean quick) {
        try {
            JSONObject result = new JSONObject();
            JSONArray videos = new JSONArray();
            String cover = "https://inews.gtimg.com/newsapp_bt/0/13263837859/1000";

            String searchurl = apiUrl + "search?keyword=" + URLEncoder.encode(key) + "&page=1&s_type=2";
            String content = new String(Base64.decode(OkHttpUtil.string(searchurl, sHeaders()), Base64.DEFAULT));
            JSONArray playList = new JSONObject(content).getJSONObject("result").getJSONArray("items");


            for (int i = 0; i < playList.length(); i++) {
                JSONArray source = playList.getJSONObject(i).getJSONArray("content");
                for (int j = 0; j < source.length(); j++) {
                    JSONObject vid = source.getJSONObject(j);
                    String sourceName = vid.getString("title");
                    String sourceUrl = vid.getString("size");
                    if (sourceUrl.contains("download.html")) {
                        JSONObject v = new JSONObject();
                        v.put("vod_name", sourceName);
                        v.put("vod_id", sourceUrl);
                        v.put("vod_pic", cover);
                        videos.put(v);
                    } else
                        continue;

                }

            }
            result.put("list", videos);
            return result.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
