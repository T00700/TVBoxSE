package com.github.catvod.spider;

import android.text.TextUtils;

import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.utils.AES;
import com.github.catvod.utils.CBC;
import com.github.catvod.utils.gZip;
import com.github.catvod.utils.okhttp.OKCallBack;
import com.github.catvod.utils.okhttp.OkHttpUtil;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Demo for self study
 * <p>
 * Source from Author: CatVod
 */

public class Ddys extends Spider {

    private static final String siteUrl = "https://ddys.tv";
    private static final String siteHost = "ddys.tv";
    private String cookie="";

    protected JSONObject filterConfig;

    protected Pattern regexCategory = Pattern.compile("/category/(\\S+)/");
    protected Pattern regexVid = Pattern.compile("https://ddys.tv/(\\S+)/");

    protected Pattern regexPage = Pattern.compile("\\S+/page/(\\S+)\\S+");
    protected Pattern m = Pattern.compile("\\S+(http\\S+g)");
    protected Pattern mark = Pattern.compile("\\S+(.*)");

    //   protected Pattern t = Pattern.compile("(\\S+)");


    /**
     * 爬虫headers
     *
     * @param url
     * @return
     */
    protected HashMap<String, String> getHeaders(String url) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.5005.62 Safari/537.36");
        headers.put("Referer", "https://ddys.tv/");
        if(cookie.length()>0 && !url.contains(("google"))){
            headers.put("Cookie", cookie);
        }
        return headers;
    }

    protected static HashMap<String, String> Headers() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36");
        headers.put("Referer", "https://ddys.tv/");
        return headers;
    }

    /**
     * 获取分类数据 + 首页最近更新视频列表数据
     *
     * @param filter 是否开启筛选 关联的是 软件设置中 首页数据源里的筛选开关
     * @return
     */
    @Override
    public String homeContent(boolean filter) {
        try {
            String url = siteUrl + '/';
            cookie="";
            Map<String, List<String>> cookies = new HashMap<>();
            Document doc = Jsoup.parse(OkHttpUtil.string(url, getHeaders(url),cookies));
            for( Map.Entry<String, List<String>> entry : cookies.entrySet() ){
                if(entry.getKey().equals("set-cookie")){
                    cookie = TextUtils.join(";",entry.getValue());
                    break;
                }
            }
            Elements elements = doc.select("li.menu-item a");
            JSONArray classes = new JSONArray();
            ArrayList<String> allClass = new ArrayList<>();
            for (Element ele : elements) {
                String name = ele.attr("title");
                boolean show = !filter || (name.equals("热映中")
                        || name.equals("欧美剧")
                        || name.equals("日剧")
                        || name.equals("韩剧")
                        || name.equals("华语剧")
                        || name.equals("其他地区")
                        || name.equals("全部")
                        || name.equals("欧美电影")
                        || name.equals("日韩电影")
                        || name.equals("华语电影")
                        || name.equals("新番")
                        || name.equals("动画")
                        || name.equals("纪录片")
                        || name.equals("综艺"));
                if (allClass.contains(name))
                    show = false;
                if (show) {
                    allClass.add(name);
                    Matcher mather = regexCategory.matcher(ele.attr("href"));
                    if (!mather.find())
                        continue;
                    // 把分类的id和名称取出来加到列表里
                    String id = mather.group(1).trim();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("type_id", id);
                    jsonObject.put("type_name", name);
                    classes.put(jsonObject);
                }
            }
            JSONObject result = new JSONObject();
            if (filter) {
                result.put("filters", filterConfig);
            }
            result.put("class", classes);
            try {
                // 取首页推荐视频列表
                Elements list = doc.select("div.post-box-container");
                JSONArray videos = new JSONArray();
                for (int i = 0; i < list.size(); i++) {
                    Element vod = list.get(i);
                    String a = vod.selectFirst(".post-box-title a").text();
                    if (a.contains("(")) {
                        String[] item = a.split("\\(");
                        String title = item[0];
                        String remark = item[1].replace(")", "");
                        String cover = doReplaceRegex(m, vod.selectFirst(".post-box-image").attr("style"));
                        Matcher matcher = regexVid.matcher(vod.selectFirst(".post-box-title a").attr("href"));
                        if (!matcher.find())
                            continue;
                        String id = matcher.group(1);
                        JSONObject v = new JSONObject();
                        v.put("vod_id", id);
                        v.put("vod_name", title);
                        v.put("vod_pic", cover);
                        v.put("vod_remarks", remark);
                        videos.put(v);
                    } else {
                        String title = a;
                        String cover = doReplaceRegex(m, vod.selectFirst(".post-box-image").attr("style"));
                        String remark = doReplaceRegex(mark, vod.selectFirst(".post-box-title a").text());
                        Matcher matcher = regexVid.matcher(vod.selectFirst(".post-box-title a").attr("href"));
                        if (!matcher.find())
                            continue;
                        String id = matcher.group(1);
                        JSONObject v = new JSONObject();
                        v.put("vod_id", id);
                        v.put("vod_name", title);
                        v.put("vod_pic", cover);
                        v.put("vod_remarks", remark);
                        videos.put(v);
                    }
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


    /**
     * 获取分类信息数据
     *
     * @param tid    分类id
     * @param pg     页数
     * @param filter 同homeContent方法中的filter
     * @param extend 筛选参数{k:v, k1:v1}
     * @return
     */
    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) {
        String url = "";
        try {
            if (extend != null && extend.size() > 0) {
                for (Iterator<String> it = extend.keySet().iterator(); it.hasNext(); ) {
                    String key = it.next();
                    String value = extend.get(key);
                    if (value != null && value.length() != 0 && value != " ") {
                        url = siteUrl + "/category/" + tid + "/" + value;
                    } else {
                        url = siteUrl + "/category/" + tid;
                    }
                    ;
                }
            } else {
                url = siteUrl + "/category/" + tid;
            }
            ;
            if (pg.equals("1")) {
                url = url + "/";
            } else {
                url = url + "/page/" + pg + "/";
            }
            //System.out.println(url);
            String html = OkHttpUtil.string(url, getHeaders(url));
            Document doc = Jsoup.parse(html);
            JSONObject result = new JSONObject();
            int pageCount = 0;
            int page = -1;

            // 取页码相关信息
            Elements pageInfo = doc.select("div.nav-links");
            if (pageInfo.size() == 0) {
                page = Integer.parseInt(pg);
                pageCount = page;
            } else {
                for (int i = 0; i < pageInfo.size(); i++) {
                    Element li = pageInfo.get(i);
                    Element a = li.selectFirst("a");
                    if (a == null)
                        continue;
                    String wy = doc.select("div.nav-links a").last().attr("href");
                    String span = doc.select("span.current").text().trim();
                    if (page == -1) {
                        page = Integer.parseInt(span);
                    } else {

                        page = 0;
                    }
                    Matcher matcher = regexPage.matcher(wy);
                    if (matcher.find()) {
                        //System.out.println("尾页" + matcher.group(1));
                        pageCount = Integer.parseInt(matcher.group(1));
                    } else {
                        pageCount = 0;
                    }
                    break;
                }
            }

            JSONArray videos = new JSONArray();
            if (!html.contains("没有找到您想要的结果哦")) {
                // 取当前分类页的视频列表
                Elements list = doc.select("div.post-box-container");
                for (int i = 0; i < list.size(); i++) {
                    Element vod = list.get(i);
                    String a = vod.selectFirst(".post-box-title a").text();
                    if (a.contains("(")) {
                        String[] item = a.split("\\(");
                        String title = item[0];
                        String remark = item[1].replace(")", "");
                        String cover = doReplaceRegex(m, vod.selectFirst(".post-box-image").attr("style"));
                        Matcher matcher = regexVid.matcher(vod.selectFirst(".post-box-title a").attr("href"));
                        if (!matcher.find())
                            continue;
                        String id = matcher.group(1);
                        JSONObject v = new JSONObject();
                        v.put("vod_id", id);
                        v.put("vod_name", title);
                        v.put("vod_pic", cover);
                        v.put("vod_remarks", remark);
                        videos.put(v);
                    } else {
                        String title = a;
                        String cover = doReplaceRegex(m, vod.selectFirst(".post-box-image").attr("style"));
                        String remark = doReplaceRegex(mark, vod.selectFirst(".post-box-title a").text());
                        Matcher matcher = regexVid.matcher(vod.selectFirst(".post-box-title a").attr("href"));
                        if (!matcher.find())
                            continue;
                        String id = matcher.group(1);
                        JSONObject v = new JSONObject();
                        v.put("vod_id", id);
                        v.put("vod_name", title);
                        v.put("vod_pic", cover);
                        v.put("vod_remarks", remark);
                        videos.put(v);
                    }
                }
            }
            result.put("page", page);
            result.put("pagecount", pageCount);
            result.put("limit", 24);
            result.put("total", pageCount <= 1 ? videos.length() : pageCount * 24);

            result.put("list", videos);
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }

    /**
     * 视频详情信息
     *
     * @param ids 视频id
     * @return
     */
    @Override
    public String detailContent(List<String> ids) {
        try {
            // 视频详情url
            String url = siteUrl + "/" + ids.get(0) + "/";
            Document doc = Jsoup.parse(OkHttpUtil.string(url, getHeaders(url)));
            JSONObject result = new JSONObject();
            JSONObject vodList = new JSONObject();

            // 取基本数据
            String cover = doc.select("div.post img").attr("src");
            String ab = doc.select("h1.post-title").text();
            if (ab.contains("(")) {
                String[] b = ab.split("\\(");
                String title = b[0];
                String remark = b[1].replace("(", "");
                vodList.put("vod_name", title);
                vodList.put("vod_remarks", remark);
            } else {
                vodList.put("vod_name", ab);
                String remark = doc.select("time").text().trim();
                vodList.put("vod_remarks", "全");
            }
            String str2 = doc.select("div.abstract").text().replace(" ", "");
            String replace = str2.replace("<br>", "");
            String text = replace.replace("<p></p>", "");
            Pattern categorys = Pattern.compile("类型:(.*)制");
            String category = doReplaceRegex(categorys, text);
            Pattern a = Pattern.compile("年份:(.*)简");
            String year = doReplaceRegex(a, text);
            Pattern b = Pattern.compile("地区:(.*)年份");
            String area = doReplaceRegex(b, text);
            Pattern c = Pattern.compile("演员:(.*)类");
            String actor = doReplaceRegex(c, text);
            Pattern d = Pattern.compile("导演:(.*)演");
            String director = doReplaceRegex(d, text);
            Pattern e = Pattern.compile("简介:(.*)");
            String desc = doReplaceRegex(e, text);


            vodList.put("vod_id", ids.get(0));
            vodList.put("vod_pic", cover);
            vodList.put("type_name", category);
            vodList.put("vod_year", year);
            vodList.put("vod_area", area);
            vodList.put("vod_actor", actor);
            vodList.put("vod_director", director);
            vodList.put("vod_content", desc);

            List<String> vodItems = new ArrayList<>();
            List<String> vodItems2 = new ArrayList<>();
            List<String> vodItemsos = new ArrayList<>();
            List<String> vodItems2os = new ArrayList<>();
            Map<String, String> vod_play = new LinkedHashMap<>();

            Elements allScript = doc.select(".wp-playlist-script");
            String sourceName = "第1季";
            for (int j = 0; j < allScript.size(); j++) {
                String scContent = allScript.get(j).html().trim();
                int start = scContent.indexOf('{');
                int end = scContent.lastIndexOf('}') + 1;
                String json = scContent.substring(start, end);
                JSONObject UJson = new JSONObject(json);
                JSONArray Track = UJson.getJSONArray("tracks");
                for (int k = 0; k < Track.length(); k++) {
                    JSONObject src = Track.getJSONObject(k);
                    String adk = src.getString("src1");
                    String vodName = src.getString("caption");
                    String playURL = "https://ddys.tv/getvddr/video?id=" + adk +"&dim=1080P+&type=mix";
                    String zm = "https://ddys.tv/subddr/" + src.getString("subsrc");
                    String pzm = playURL + "|" + zm;
                    vodItems.add(vodName + "$" + pzm);
                }
                for (int k = 0; k < Track.length(); k++) {
                    JSONObject src = Track.getJSONObject(k);
                    String adksrc0 = src.getString("src0");
                    String adksrc2 = src.getString("src2");
                    String vodName = src.getString("caption");
                    String playURL = "https://w.ddys.tv" + adksrc0 +"?ddrkey=" + adksrc2;
                    String zm = "https://ddys.tv/subddr/" + src.getString("subsrc");
                    String pzm = playURL + "|" + zm;
                    vodItemsos.add(vodName + "$" + pzm);
                }
                vod_play.put(sourceName, TextUtils.join("#", vodItems));
                vod_play.put(sourceName+":海外", TextUtils.join("#", vodItemsos));
            }


            Elements sources = doc.select(".post-page-numbers");
            if (!sources.isEmpty())
                for (int i = 0; i < sources.size(); i++) {
                    Element source = sources.get(i);
                    sourceName = "第" + source.text() + "季";
                    String Purl = siteUrl + "/" + ids.get(0) + "/" + source.text() + "/";
                    Document docs = Jsoup.parse(OkHttpUtil.string(Purl, getHeaders(Purl)));
                    Elements allScripts = docs.select(".wp-playlist-script");
                    for (int j = 0; j < allScripts.size(); j++) {
                        String scContent = allScripts.get(j).html().trim();
                        int start = scContent.indexOf('{');
                        int end = scContent.lastIndexOf('}') + 1;
                        String json = scContent.substring(start, end);
                        JSONObject UJson = new JSONObject(json);
                        JSONArray Track = UJson.getJSONArray("tracks");
                        for (int k = 0; k < Track.length(); k++) {
                            JSONObject src = Track.getJSONObject(k);
                            String adk = src.getString("src1");
                            String vodName = src.getString("caption");
                            String playURL = "https://ddys.tv/getvddr/video?id=" + adk +"&dim=1080P+&type=mix";
                            String zm = "https://ddys.tv/subddr/" + src.getString("subsrc");
                            String pzm = playURL + "|" + zm;
                            vodItems2.add(vodName + "$" + pzm);
                        }
                        for (int k = 0; k < Track.length(); k++) {
                            JSONObject src = Track.getJSONObject(k);
                            String adksrc0 = src.getString("src0");
                            String adksrc2 = src.getString("src2");
                            String vodName = src.getString("caption");
                            String playURL = "https://w.ddys.tv" + adksrc0 +"?ddrkey=" + adksrc2;
                            String zm = "https://ddys.tv/subddr/" + src.getString("subsrc");
                            String pzm = playURL + "|" + zm;
                            vodItems2os.add(vodName + "$" + pzm);
                        }
                        vod_play.put(sourceName, TextUtils.join("#", vodItems2));
                        vod_play.put(sourceName+":海外", TextUtils.join("#", vodItems2os));
                    }
                    vodItems2.removeAll(vodItems2);
                    vodItems2os.removeAll(vodItems2os);
                }

            String vod_play_from = TextUtils.join("$$$", vod_play.keySet());
            String vod_play_url = TextUtils.join("$$$", vod_play.values());
            vodList.put("vod_play_from", vod_play_from);
            vodList.put("vod_play_url", vod_play_url);

            JSONArray list = new JSONArray();
            list.put(vodList);
            result.put("list", list);
            return result.toString();

        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }

    public static Object[] loadsub(String sub) {
        try {
            OKCallBack.OKCallBackDefault callBack = new OKCallBack.OKCallBackDefault() {
                @Override
                protected void onFailure(Call call, Exception e) {

                }

                @Override
                protected void onResponse(Response response) {

                }
            };
            OkHttpClient YM = OkHttpUtil.defaultClient();
            OkHttpUtil.get(YM, sub, null, Headers(), callBack);
            Response result = callBack.getResult();
            int code = result.code();
            if (code == 404) {
                return new Object[]{200, "application/octet-stream", new ByteArrayInputStream("WEBVTT".getBytes())};
            }
            ResponseBody body = result.body();
            byte[] bytes = body.bytes();
            byte[] tokenkey = Arrays.copyOfRange(bytes, 0, 16);
            byte[] data = Arrays.copyOfRange(bytes, 16, bytes.length);
            byte[] KS = CBC.CBC(data, tokenkey, tokenkey);
            String vtt = gZip.KS(KS);


            vtt = vtt.replaceAll("(\\d{2}:\\d{2}:\\d{2}.\\d{3}.+\\d{2}:\\d{2}:\\d{2}.\\d{3}).*", "$1");
            vtt = vtt.replaceAll("(\\d{2}:\\d{2}.\\d{3}).*?( --> )(\\d{2}:\\d{2}.\\d{3}).*", "00:$1$200:$3");
            BufferedReader br = new BufferedReader(new StringReader(vtt));
            ArrayList<String> lines = new ArrayList<>();
            int captionNumber = 1;
            String line = br.readLine();
            while (line != null) {
                if (line.matches("\\d{2}:\\d{2}:\\d{2}.\\d{3}.+\\d{2}:\\d{2}:\\d{2}.\\d{3}")) {
                    if (lines.get(lines.size() - 1).trim().isEmpty()) {
                        lines.add(captionNumber + "");
                        captionNumber++;
                    }
                }
                lines.add(line);
                line = br.readLine();
            }
            String join = TextUtils.join("\n", lines);

            return new Object[]{200, "application/octet-stream", new ByteArrayInputStream(join.getBytes())};
        } catch (Exception e) {
            e.printStackTrace();
            SpiderDebug.log(e);
            return null;
        }
    }


    /**
     * 获取视频播放信息
     *
     * @param flag     播放源
     * @param id       视频id
     * @param vipFlags 所有可能需要vip解析的源
     * @return
     */
    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) {
        try {
            JSONObject result = new JSONObject();
            String[] item = id.split("\\|");
            String playUrl = item[0];
            String ZiMu = item[1];
            String RealUrl="";
            if(playUrl.contains("ddrkey")){
                Map<String, List<String>> respHeaders = new HashMap<>();
                OkHttpUtil.stringNoRedirect(playUrl, getHeaders(playUrl),respHeaders);
                RealUrl = OkHttpUtil.getRedirectLocation(respHeaders);
            } else {
                String content = OkHttpUtil.string(playUrl, getHeaders(playUrl));
                JSONObject obj = new JSONObject(content);
                RealUrl = obj.optString("url");
            }

            result.put("parse", 0);
            result.put("playUrl", "");
            result.put("url", RealUrl);
            result.put("header", "");
            //    if (!TextUtils.isEmpty(str4)) {
            result.put("subf", "/vtt/utf-8");
            result.put("subt", Proxy.localProxyUrl() + "?do=ddys&url=" + URLEncoder.encode(ZiMu));
            //       result.put("subt", ZiMu);
            //    }
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }


    @Override
    public String searchContent(String key, boolean quick) {
        try {
            String url = "https://www.google.com/search?q=site%3Addys.tv+" + URLEncoder.encode(key);
            Document doc = Jsoup.parse(OkHttpUtil.string(url, getHeaders(url)));
            JSONObject result = new JSONObject();
            JSONArray videos = new JSONArray();
            String sourceName = doc.select("div.yuRUbf a h3").text();
            if (sourceName.contains(key)) {
                String list1 = doc.select("div.yuRUbf a").attr("href");
                Document ddrklink = Jsoup.parse(OkHttpUtil.string(list1, getHeaders(list1)));
                String DDlink = ddrklink.select("link[rel=canonical]").attr("href");
                Matcher matcher = regexVid.matcher(DDlink);
                if (matcher.find()) {
                    JSONObject v = new JSONObject();
                    String group = matcher.group(1);
                    String ab = ddrklink.select("h1.post-title").text();
                    String cover = ddrklink.select("div.post img").attr("src");
                    if (ab.contains("(")) {
                        String[] b = ab.split("\\(");
                        String title = b[0];
                        String remark = b[1].replace("(", "");
                        v.put("vod_name", title);
                        v.put("vod_remarks", remark);
                    } else {
                        v.put("vod_name", ab);
                        String remark = "全";
                        v.put("vod_remarks", remark);
                    }
                    v.put("vod_id", group);
                    v.put("vod_pic", cover);
                    videos.put(v);
                }
            }
            result.put("list", videos);
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }

    private static String doReplaceRegex(Pattern pattern, String src) {
        if (pattern == null)
            return src;
        try {
            Matcher matcher = pattern.matcher(src);
            if (matcher.find()) {
                return matcher.group(1).trim();
            }
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return src;
    }


}
