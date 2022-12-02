package com.github.catvod.spider;


import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;

import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.utils.okhttp.OkHttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Anfuns extends Spider {
    private static final String siteUrl = "https://www.anfuns.cc";
    private static final String siteHost = "www.anfuns.cc";
    private JSONObject filterConfig;
    private String cookie="";
    private String referer="";



    @Override
    public void init(Context context) {
        super.init(context);
        try {
            filterConfig = new JSONObject("{\"1\":[{\"key\":\"year\",\"name\":\"年份\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2015\",\"v\":\"2015\"},{\"n\":\"2014\",\"v\":\"2014\"},{\"n\":\"2013\",\"v\":\"2013\"},{\"n\":\"2012\",\"v\":\"2012\"},{\"n\":\"2011\",\"v\":\"2011\"},{\"n\":\"2010\",\"v\":\"2010\"},{\"n\":\"2009\",\"v\":\"2009\"},{\"n\":\"2008\",\"v\":\"2008\"},{\"n\":\"2007\",\"v\":\"2007\"},{\"n\":\"2006\",\"v\":\"2006\"},{\"n\":\"2005\",\"v\":\"2005\"},{\"n\":\"2004\",\"v\":\"2004\"},{\"n\":\"2003\",\"v\":\"2003\"},{\"n\":\"2002\",\"v\":\"2002\"},{\"n\":\"2001\",\"v\":\"2001\"},{\"n\":\"2000\",\"v\":\"2000\"}]},{\"key\":\"order\",\"name\":\"排序\",\"value\":[{\"n\":\"最新\",\"v\":\"time\"},{\"n\":\"最热\",\"v\":\"hits\"},{\"n\":\"评分\",\"v\":\"score\"}]}],\"2\":[{\"key\":\"year\",\"name\":\"年份\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2015\",\"v\":\"2015\"},{\"n\":\"2014\",\"v\":\"2014\"},{\"n\":\"2013\",\"v\":\"2013\"},{\"n\":\"2012\",\"v\":\"2012\"},{\"n\":\"2011\",\"v\":\"2011\"},{\"n\":\"2010\",\"v\":\"2010\"},{\"n\":\"2009\",\"v\":\"2009\"},{\"n\":\"2008\",\"v\":\"2008\"},{\"n\":\"2007\",\"v\":\"2007\"},{\"n\":\"2006\",\"v\":\"2006\"},{\"n\":\"2005\",\"v\":\"2005\"},{\"n\":\"2004\",\"v\":\"2004\"},{\"n\":\"2003\",\"v\":\"2003\"},{\"n\":\"2002\",\"v\":\"2002\"},{\"n\":\"2001\",\"v\":\"2001\"},{\"n\":\"2000\",\"v\":\"2000\"}]},{\"key\":\"order\",\"name\":\"排序\",\"value\":[{\"n\":\"最新\",\"v\":\"time\"},{\"n\":\"最热\",\"v\":\"hits\"},{\"n\":\"评分\",\"v\":\"score\"}]}],\"3\":[{\"key\":\"year\",\"name\":\"年份\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2015\",\"v\":\"2015\"},{\"n\":\"2014\",\"v\":\"2014\"},{\"n\":\"2013\",\"v\":\"2013\"},{\"n\":\"2012\",\"v\":\"2012\"},{\"n\":\"2011\",\"v\":\"2011\"},{\"n\":\"2010\",\"v\":\"2010\"},{\"n\":\"2009\",\"v\":\"2009\"},{\"n\":\"2008\",\"v\":\"2008\"},{\"n\":\"2007\",\"v\":\"2007\"},{\"n\":\"2006\",\"v\":\"2006\"},{\"n\":\"2005\",\"v\":\"2005\"},{\"n\":\"2004\",\"v\":\"2004\"},{\"n\":\"2003\",\"v\":\"2003\"},{\"n\":\"2002\",\"v\":\"2002\"},{\"n\":\"2001\",\"v\":\"2001\"},{\"n\":\"2000\",\"v\":\"2000\"}]},{\"key\":\"order\",\"name\":\"排序\",\"value\":[{\"n\":\"最新\",\"v\":\"time\"},{\"n\":\"最热\",\"v\":\"hits\"},{\"n\":\"评分\",\"v\":\"score\"}]}],\"4\":[{\"key\":\"year\",\"name\":\"年份\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2015\",\"v\":\"2015\"},{\"n\":\"2014\",\"v\":\"2014\"},{\"n\":\"2013\",\"v\":\"2013\"},{\"n\":\"2012\",\"v\":\"2012\"},{\"n\":\"2011\",\"v\":\"2011\"},{\"n\":\"2010\",\"v\":\"2010\"},{\"n\":\"2009\",\"v\":\"2009\"},{\"n\":\"2008\",\"v\":\"2008\"},{\"n\":\"2007\",\"v\":\"2007\"},{\"n\":\"2006\",\"v\":\"2006\"},{\"n\":\"2005\",\"v\":\"2005\"},{\"n\":\"2004\",\"v\":\"2004\"},{\"n\":\"2003\",\"v\":\"2003\"},{\"n\":\"2002\",\"v\":\"2002\"},{\"n\":\"2001\",\"v\":\"2001\"},{\"n\":\"2000\",\"v\":\"2000\"}]},{\"key\":\"order\",\"name\":\"排序\",\"value\":[{\"n\":\"最新\",\"v\":\"time\"},{\"n\":\"最热\",\"v\":\"hits\"},{\"n\":\"评分\",\"v\":\"score\"}]}]}");
        } catch (JSONException e) {
            SpiderDebug.log(e);
        }
    }




    protected HashMap<String, String> getHeaders(String url, String ref) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36");
        if(ref.length()>0){
            headers.put("Referer", ref);
        }
        if(cookie.length()>0){
        headers.put("Cookie", cookie);
        }
        headers.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
        return headers;
    }


    protected HashMap<String, String> getHeaders2(String url,String ref) {
        HashMap<String, String> headers = new HashMap<>();
        String ss = url.replace("https://","").split("/")[0];
        headers.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36");
        headers.put("Authority", ss);
        headers.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
        if (ref.contains("ref")){
            headers.put("Referer", ref.replace("ref:",""));
        } else if (ref.contains("origin")){
            headers.put("Origin", ref.replace("origin:",""));
        }
        return headers;
    }


    protected void getCookie(){
        cookie="";
        String cookieurl="https://www.anfuns.cc/verify/index.html";
        Map<String, List<String>> cookies = new HashMap<>();
        OkHttpUtil.string(cookieurl,getHeaders(cookieurl,""),cookies);
        for( Map.Entry<String, List<String>> entry : cookies.entrySet() ){
            if(entry.getKey().equals("set-cookie")){
                cookie = TextUtils.join(";",entry.getValue());
                break;
            }
        }
    }

    @Override
    public String homeContent(boolean filter) {
        try {
            Document doc = Jsoup.parse(OkHttpUtil.string(siteUrl, getHeaders(siteUrl,referer)));
            referer=siteUrl+"/";
            JSONObject result = new JSONObject();
            JSONArray classes = new JSONArray();
            String catestr ="{\"新旧番剧\": \"1\",\"蓝光无修\": \"2\",\"动漫剧场\": \"3\",\"欧美动漫\": \"4\"}";
            JSONObject catedef = new JSONObject(catestr);
            Iterator it = catedef.keys();
            while(it.hasNext()){
                JSONObject jsonObject = new JSONObject();
                String key =(String) it.next();
                jsonObject.put("type_name", key);
                jsonObject.put("type_id", catedef.getString(key));
                classes.put(jsonObject);
            }
            result.put("class", classes);
            if (filter) {
                result.put("filters", filterConfig);
            }
            try {
                Element homeList = doc.select("ul.hl-vod-list.swiper-wrapper").get(0);
                Elements list = homeList.select("li");
                JSONArray videos = new JSONArray();
                for (int i = 0; i < list.size(); i++) {
                    Element vod = list.get(i);
                    String title = vod.selectFirst("a").attr("title");
                    String cover = vod.selectFirst("a" ).attr("data-original");
                    String remark = vod.selectFirst(" a > div.hl-pic-text > span").text();
                    String id =vod.selectFirst("a").attr("href");
                    JSONObject v = new JSONObject();
                    v.put("vod_id", id);
                    v.put("vod_name", title);
                    v.put("vod_pic", cover);
                    v.put("vod_remarks", remark);
                    videos.put(v);
                }
                result.put("list", videos);
            } catch (Exception e) {
                SpiderDebug.log(e);
            }
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) {
        try {
            String url = siteUrl + "/show/"+tid+"---{year}/by/{order}/page/"+pg+".html" ;
            if (extend != null && extend.size() > 0 ) {
                for (Iterator<String> it = extend.keySet().iterator(); it.hasNext(); ) {
                    String key = it.next();
                    String value = extend.get(key);
                    if (value.length() > 0) {
                        url=url.replace("{"+key+"}", value);
                    }
                }
            }
            for (int i=0 ;i<2;i++) {
                if (url.contains("{year}")) {
                    url = url.replace("{year}", "");
                } else if (url.contains("{order}")) {
                    url = url.replace("/by/{order}", "");
                }
            }
            String html = OkHttpUtil.string(url, getHeaders(url,referer));
            referer = url;
            Document doc = Jsoup.parse(html);
            JSONObject result = new JSONObject();
            JSONArray videos = new JSONArray();
            if (!html.contains("没有找到您想要的结果哦")) {
                Elements list = doc.select("li.hl-list-item");
                for (int i = 0; i < list.size(); i++) {
                    Element vod = list.get(i);
                    String title = vod.selectFirst("a").attr("title");
                    String cover = vod.selectFirst("a" ).attr("data-original");
                    String remark = vod.selectFirst(" a > div.hl-pic-text > span").text();
                    String id = vod.selectFirst("a").attr("href");
                    JSONObject v = new JSONObject();
                    v.put("vod_id", id);
                    v.put("vod_name", title);
                    v.put("vod_pic", cover);
                    v.put("vod_remarks", remark);
                    videos.put(v);
                }
            }
            int page =Integer.parseInt(pg);
            result.put("page", page);
            result.put("pagecount", videos.length()== 36 ? page + 1 : page);
            result.put("limit", 36);
            result.put("total",Integer.MAX_VALUE );
            result.put("list", videos);
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }

    @Override
    public String detailContent(List<String> ids) {
        try {
            String url = siteUrl + ids.get(0);
            Document doc = Jsoup.parse(OkHttpUtil.string(url, getHeaders(url,referer)));
            referer=url;
            JSONObject result = new JSONObject();
            JSONObject vodList = new JSONObject();
            String cover = doc.selectFirst("span.hl-item-thumb").attr("data-original");
            String title = doc.selectFirst("div.hl-dc-sub").text();
            String desc = doc.selectFirst("a.hl-data-menu").text();
            String vid = ids.get(0);
            vodList.put("vod_id", vid);
            vodList.put("vod_name", title);
            vodList.put("vod_pic", cover);
            vodList.put("vod_content", desc);
            Map<String, String> vod_play = new LinkedHashMap<>();
            Elements sources = doc.select("div.hl-plays-wrap > div > a");
            Elements sourceList = doc.select("div.hl-tabs-box.hl-fadeIn");
            for (int i = 0; i < sources.size(); i++) {
                Element source = sources.get(i);
                String sourceName = source.attr("alt");
                String playList = "";
                Element playListB = sourceList.get(i);
                Elements playListA = playListB.select("li.hl-col-xs-4");
                List<String> vodItems = new ArrayList<>();
                for (int j = 0; j < playListA.size(); j++) {
                     Element vod = playListA.get(j);
                     String playURL = vod.selectFirst("a").attr("href");
                     String playUrlName = vod.selectFirst("a").text();
                     vodItems.add(playUrlName + "$" + playURL);
                }
                if (vodItems.size() > 0) {
                    playList = TextUtils.join("#", vodItems);
                    vod_play.put(sourceName, playList);
                }
            }
            if (vod_play.size() > 0) {
                String vod_play_from = TextUtils.join("$$$", vod_play.keySet());
                String vod_play_url = TextUtils.join("$$$", vod_play.values());
                vodList.put("vod_play_from", vod_play_from);
                vodList.put("vod_play_url", vod_play_url);
            }
            JSONArray list = new JSONArray();
            list.put(vodList);
            result.put("list", list);
            getCookie();
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) {
        try {
            String url = siteUrl + id ;
            String htmlplay = OkHttpUtil.string(url, getHeaders(url,referer));
            Document doc = Jsoup.parse(htmlplay);
            Elements allScript = doc.select("script");
            JSONObject result = new JSONObject();
            String urlreq="";
            for (int i = 0; i < allScript.size(); i++) {
                String scContent = allScript.get(i).toString();
                if (scContent.contains("var player_aaaa=")) {
                    int start = scContent.indexOf("{");
                    int end = scContent.lastIndexOf("}") + 1;
                    String urltmp = scContent.substring(start, end);
                    JSONObject urltmpobj = new JSONObject(urltmp);
                    String urlraw = urltmpobj.getString("url");
                    String urlnext= urltmpobj.optString("link_next");
                    urlreq = new String(Base64.decode(urlraw.getBytes(), Base64.DEFAULT));
                    urlreq = urlreq.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
                    urlreq = URLDecoder.decode(urlreq);
                    if(urlreq.contains("yanm")){
                        urlreq = "https://api.peizq.online/play/anfuns.php?url="+urlreq+"&next=//www.anfuns.cc"+urlnext;
                    }else {
                        if(urlreq.contains("%u")){
                            start = urlreq.indexOf("%u");
                            end = urlreq.lastIndexOf("%u")+6;
                            String urlreqtmp =urlreq.substring(start,end);
                            String urlreqtmps = urlreq.substring(0,start);
                            String urlreqtmpe = urlreq.substring(end,urlreq.length());
                            urlreqtmp = urlreqtmp.replace("%","\\");
                            Pattern pattern = Pattern.compile("(\\\\u(\\w{4}))");
                            Matcher matcher = pattern.matcher(urlreqtmp);
                            while (matcher.find()) {
                                String unicodeFull = matcher.group(1);
                                String unicodeNum = matcher.group(2);
                                char singleChar = (char) Integer.parseInt(unicodeNum, 16);
                                urlreqtmp = urlreqtmp.replace(unicodeFull, singleChar + "");
                            }
                            urlreq = urlreqtmps+urlreqtmp+urlreqtmpe;
                        }
                        urlreq = urlreq+"&next=//www.anfuns.cc"+urlnext;
                    }
                    break;
                }
            }

            if(urlreq.contains("yanm")){
                String htmlyanm = OkHttpUtil.string(urlreq, getHeaders2(urlreq,"ref:https://www.anfuns.cc/"));
                Document docyanm = Jsoup.parse(htmlyanm);
                Elements allScriptyanm = docyanm.select("script");
                String urlreqyanm="";
                for (int i = 0; i < allScriptyanm.size(); i++) {
                    String scContent = allScriptyanm.get(i).toString();
                    if (scContent.contains("var config =")) {
                        int start = scContent.indexOf("getrandom('")+11;
                        int end = scContent.indexOf("'),") ;
                        String getrandom = scContent.substring(start,end);
                        String getrandomb64 = new String(Base64.decode(getrandom.substring(8,getrandom.length()).getBytes(), Base64.DEFAULT));
                        String urlreqyanmtmp = URLDecoder.decode(getrandomb64.substring(8,getrandomb64.length()-8));
                        Map<String, List<String>> respHeaders = new HashMap<>();
                        OkHttpUtil.stringNoRedirect(urlreqyanmtmp, getHeaders2(urlreqyanmtmp,"origin:https://api.peizq.online"), respHeaders);
                        String redirect = OkHttpUtil.getRedirectLocation(respHeaders);
                        String m3u8raw = OkHttpUtil.string(redirect, getHeaders2(redirect,"origin:https://api.peizq.online"));
                        start = m3u8raw.indexOf("/hls/");
                        String m3ubrawf1tmp =m3u8raw.substring(start,start+200);
                        end = m3ubrawf1tmp.indexOf("#EXTINF");
                        String m3ubrawf1req="https://fata.peizq.online"+m3ubrawf1tmp.substring(0,end);
                        OkHttpUtil.stringNoRedirect(m3ubrawf1req, getHeaders2(m3ubrawf1req,"origin:https://api.peizq.online"), respHeaders);
                        String m3ubrawf1 = OkHttpUtil.getRedirectLocation(respHeaders);
                        m3u8raw = m3u8raw.replace(m3u8raw.substring(start,start+end),m3ubrawf1);
                        String realm3u8b64=Base64.encodeToString(m3u8raw.getBytes(), 2);
                        result.put("url", "data:application/vnd.apple.mpegurl;base64,"+realm3u8b64);
                        result.put("header", new JSONObject(getHeaders2(m3ubrawf1,"origin:https://api.peizq.online")).toString());
                        break;
                    }
                }
           }else {
                Map<String, List<String>> respHeaders = new HashMap<>();
                OkHttpUtil.stringNoRedirect(urlreq, getHeaders2(urlreq,""), respHeaders);
                String redirect = OkHttpUtil.getRedirectLocation(respHeaders);
                result.put("url", redirect);
                result.put("header", getHeaders2(redirect,"").toString());
            }
            result.put("parse", 0);
            result.put("playUrl", "");
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }

    @Override
    public String searchContent(String key, boolean quick) {
        try {
            String url = "https://www.anfuns.cc/search.html?wd="+URLEncoder.encode(key)+"&submit=";
            Document doc = Jsoup.parse(OkHttpUtil.string(url,getHeaders(url,referer)));
            referer=url;
            JSONObject result = new JSONObject();
            JSONArray videos = new JSONArray();
            Elements sourceList = doc.select("ul.hl-one-list");
            if(sourceList.size()>0){
                for (int i = 0; i < sourceList.size(); i++) {
                    Element sources = sourceList.get(i);
                    JSONObject v = new JSONObject();
                    String cover = sources.selectFirst("a").attr("data-original");
                    String title = sources.selectFirst("a").attr("title");
                    String id =sources.selectFirst("a").attr("href");
                    String remarks =sources.selectFirst("span.hl-lc-1.remarks ").text();
                    v.put("vod_name", title);
                    v.put("vod_remarks", remarks);
                    v.put("vod_id", id);
                    v.put("vod_pic", cover);
                    videos.put(v);
                }
                result.put("list", videos);
                return result.toString();
            }
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }
}
