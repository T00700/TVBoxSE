package com.github.catvod.spider;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.utils.okhttp.OKCallBack;
import com.github.catvod.utils.okhttp.OkHttpUtil;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Juhuang extends Spider {
    private static final String siteUrl = "https://juhuang.tv";
    private static final String siteHost = "juhuang.tv";

    /**
     * 播放源配置
     */
    private JSONObject playerConfig;
    /**
     * 筛选配置
     */
    private JSONObject filterConfig;

    private Pattern regexCategory = Pattern.compile("/type/(\\d+)_type.html");
    private Pattern regexVid = Pattern.compile("/play/(\\d+)_play_\\d+_\\d+.html");
    private Pattern regexPlay = Pattern.compile("/play/(\\d+)_play_(\\d+)_(\\d+).html");
    private Pattern regexPage = Pattern.compile("/type/\\d+_type_(\\d+).html");

    public void init(Context context) {
        Juhuang.super.init(context);
        try {
            playerConfig = new JSONObject("{\"xg\":{\"sh\":\"xg播放器\",\"or\":999,\"sn\":0,\"pu\":\"\"},\"dplayer\":{\"sh\":\"dplayer播放器\",\"or\":999,\"sn\":0,\"pu\":\"\"},\"videojs\":{\"sh\":\"videojs-H5播放器\",\"or\":999,\"sn\":0,\"pu\":\"\"},\"iva\":{\"sh\":\"iva-H5播放器\",\"or\":999,\"sn\":0,\"pu\":\"\"},\"iframe\":{\"sh\":\"iframe外链数据\",\"or\":999,\"sn\":0,\"pu\":\"\"},\"link\":{\"sh\":\"外链数据\",\"or\":999,\"sn\":0,\"pu\":\"\"},\"swf\":{\"sh\":\"Flash文件\",\"or\":999,\"sn\":0,\"pu\":\"\"},\"flv\":{\"sh\":\"Flv文件\",\"or\":999,\"sn\":0,\"pu\":\"\"},\"plyr\":{\"sh\":\"plyr\",\"or\":999,\"sn\":0,\"pu\":\"\"},\"H5player\":{\"sh\":\"H5player\",\"or\":999,\"sn\":0,\"pu\":\"\"},\"playerjs\":{\"sh\":\"playerjs\",\"or\":999,\"sn\":0,\"pu\":\"\"},\"aliplayer\":{\"sh\":\"阿里播放器\",\"or\":999,\"sn\":0,\"pu\":\"\"}}");
            filterConfig = new JSONObject("{}");
        } catch (JSONException e) {
            SpiderDebug.log(e);
        }
    }

    protected HashMap<String, String> getHeaders(String url) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("method", "GET");
        headers.put("Host", "juhuang.tv");
        headers.put("Upgrade-Insecure-Requests", "1");
        headers.put("DNT", "1");
        headers.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36");
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        headers.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
        return headers;
    }

    public String homeContent(boolean filter) {
        try{
            Document doc = Jsoup.parse(OkHttpUtil.string(siteUrl, getHeaders(siteUrl)));
            // 分类节点
            Elements elements = doc.select("ul.nav-menu-items > li > a");
            JSONArray classes = new JSONArray();
            for (Element ele : elements) {
                //分类名
                String name = ele.text();
                boolean show = name.equals("Youtube精选") ||
                        name.equals("剧集") ||
                        name.equals("电影") ||
                        name.equals("综艺") ||
                        name.equals("动漫") ||
                        name.equals("纪录片");
                if (show) {
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
                Element homeList = doc.select("div[class='module-items']").get(1);
                Elements list = homeList.select("div.module-item");
                JSONArray videos = new JSONArray();
                for (int i = 0; i < list.size(); i++) {
                    Element vod = list.get(i);
                    String title = vod.selectFirst("div.module-item-cover > div.module-item-pic > a").attr("title");
                    String cover = vod.selectFirst("div.module-item-cover > div.module-item-pic > img").attr("data-src");
                    String remark = vod.selectFirst("div.module-item-text").text();
                    Matcher matcher = regexVid.matcher(vod.selectFirst("div.module-item-cover > div.module-item-pic > a").attr("href"));
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
        try {
            int pageCount = 0;
            int page = -1;
            // 获取分类数据的url
            String url = siteUrl + "/type/" + tid + "_type.html";

            if (TextUtils.isEmpty(pg) || Integer.parseInt(pg.trim()) <= 1) {
                page = 1;
            } else {
                url = siteUrl + String.format("/type/%s_type_%s.html", tid, pg);
                page = Integer.parseInt(pg.trim());
            }
            String html = OkHttpUtil.string(url, getHeaders(url));
            Document doc = Jsoup.parse(html);
            JSONObject result = new JSONObject();
            // 取页码相关信息
            Elements pageInfo = doc.select("div.module-footer >div[id=page] > a");
            if (pageInfo.size() == 0) {
                page = Integer.parseInt(pg);
                pageCount = page;
            } else {
                for (int i = 0; i < pageInfo.size(); i++) {
                    Element li = pageInfo.get(i);
                    Element a = li.selectFirst("a");
                    if (a == null)
                    continue;
                    String name = a.text();
                    if (name.equals("尾页")) {
                        Matcher matcher = regexPage.matcher(a.attr("href"));
                        if (matcher.find()) {
                            pageCount = Integer.parseInt(matcher.group(1));
                        } else {
                            pageCount = 0;
                        }
                        break;
                    }
                }
            }
            JSONArray videos = new JSONArray();
            if (!html.contains("没有找到您想要的结果哦")) {
                Elements list = doc.select("div.module-items>div");
                for (int i = 0; i < list.size(); i++) {
                    Element vod = list.get(i);
                    String title = vod.selectFirst("div.module-item-cover > div.module-item-pic > a").attr("title");
                    String cover = vod.selectFirst("div.module-item-cover > div.module-item-pic > img").attr("data-src");
                    String remark = "";
                    Element remarkElement = vod.selectFirst("div.module-item-text");
                    if (remarkElement != null) {
                        remark = remarkElement.text();
                    }
                    Matcher matcher = regexVid.matcher(vod.selectFirst("div.module-item-cover > div.module-item-pic > a").attr("href"));
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
            result.put("page", page);
            result.put("pagecount", pageCount);
            result.put("limit", 48);
            result.put("total", pageCount <= 1 ? videos.length() : pageCount * 48);
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
            String url = siteUrl + "/vod/" + ids.get(0) + "_vod.html";
            Document doc = Jsoup.parse(OkHttpUtil.string(url, getHeaders(url)));
            JSONObject result = new JSONObject();
            JSONObject vodList = new JSONObject();
            // 取基本数据
            String cover = doc.selectFirst("div.module-item-pic > img").attr("data-src");
            String title = doc.selectFirst("div.video-info-header > h1.page-title").text();
            String desc = doc.select("p.zkjj_a").text();
            String category = "", area = "", year = "", remark = "", director = "", actor = "";

            vodList.put("vod_id", ids.get(0));
            vodList.put("vod_name", title);
            vodList.put("vod_pic", cover);
            vodList.put("type_name", category);
            vodList.put("vod_year", year);
            vodList.put("vod_area", area);
            vodList.put("vod_remarks", remark);
            vodList.put("vod_actor", actor);
            vodList.put("vod_director", director);
            vodList.put("vod_content", desc);
            Map<String, String> vod_play = new TreeMap<>(new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    try {
                        int sort1 = playerConfig.getJSONObject(o1).getInt("or");
                        int sort2 = playerConfig.getJSONObject(o2).getInt("or");

                        if (sort1 == sort2) {
                            return 1;
                        }
                        return sort1 - sort2 > 0 ? 1 : -1;
                    } catch (JSONException e) {
                        SpiderDebug.log(e);
                    }
                    return 1;
                }
            });
            // 取播放列表数据
            Elements sources = doc.select("div.module");
            for (int i = 0; i < sources.size(); i++) {
                Element source = sources.get(i);
                String sourceName = source.select("div.module-heading > h2.module-title").text();

                String playList = "";
                Elements playListA = source.select("div.module-list > div.module-blocklist > div.scroll-content > a");
                List<String> vodItems = new ArrayList<>();

                for (int j = 0; j < playListA.size(); j++) {
                    Element vod = playListA.get(j);
                    String href = vod.attr("href");
                    String playURL = href.substring(6, href.indexOf(".html"));
                    vodItems.add(vod.select("span").text() + "$" + playURL);
                }
                if (vodItems.size() > 0)
                    playList = TextUtils.join("#", vodItems);

                if (playList.length() == 0)
                    continue;

                vod_play.put(sourceName, playList);
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
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
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
            JSONObject headers = new JSONObject();
            //headers.put("Origin", " https://juhuang.tv");
            headers.put("User-Agent", " Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36");
            //headers.put("Accept", " */*");
            //headers.put("Accept-Language", " zh-CN,zh;q=0.9,en-US;q=0.3,en;q=0.7");
            //headers.put("Accept-Encoding", " gzip, deflate");
            //headers.put("Referer", " https://juhuang.tv/");

            // 播放页 url
            String url = siteUrl + "/play/" + id + ".html";
            Document doc = Jsoup.parse(OkHttpUtil.string(url, getHeaders(url)));
            Elements allScript = doc.select("script");

            JSONObject result = new JSONObject();
            for (int i = 0; i < allScript.size(); i++) {
                String scContent = allScript.get(i).html().trim();
                if (scContent.startsWith("var player_aaaa")) {
                    int start = scContent.indexOf('{');
                    int end = scContent.lastIndexOf('}') + 1;
                    String json = scContent.substring(start, end);
                    JSONObject player = new JSONObject(json);
                    if (playerConfig.has(player.getString("from"))) {
                        String jxurl = player.getString("url");
                        if (player.has("encrypt")) {
                            int encrypt = player.getInt("encrypt");
                            if (encrypt == 1) {
                                jxurl = URLDecoder.decode(jxurl);
                            } else if (encrypt == 2) {
                                jxurl = URLEncoder.encode(new String(Base64.decode(URLDecoder.decode(new String(Base64.decode(jxurl, 0))),0)));
                            }
                        }
                        String directUrl = "https://web-webapi-tsjqsvyzyx.cn-shenzhen.fcapp.run/?url=" + jxurl;
                        HashMap hashMap = new HashMap();
                        hashMap.put("Referer", " https://juhuang.tv/");
                        hashMap.put("Origin", " https://juhuang.tv");
                        hashMap.put("User-Agent", " Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36");
                        result.put("url", new JSONObject(OkHttpUtil.string(directUrl, hashMap)).getString("play_url"));
                        result.put("parse", "0");
                        result.put("playUrl", "");
                        result.put("header", headers.toString());
                        //result.put("header", "{\"User-Agent\", \" Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36\"}");
                    }
                }
            }
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }

    /**
     * 搜索
     *
     * @param key
     * @param quick 是否播放页的快捷搜索
     * @return
     */
    @Override
    public String searchContent(String key, boolean quick) {
        try {
            if (quick)
                return "";
            String url = "https://so.juhuang.tv/soapi.php?&wd=" + URLEncoder.encode(key);
            JSONObject searchResult = new JSONObject(OkHttpUtil.string(url, getHeaders(url)));
            JSONObject result = new JSONObject();
            JSONArray videos = new JSONArray();
            if (searchResult.getInt("count") > 0) {
                JSONArray lists = new JSONArray(searchResult.getString("list"));
                for (int i = 0; i < lists.length(); i++) {
                    JSONObject vod = lists.getJSONObject(i);
                    String id = vod.getString("vod_id");
                    String title = vod.getString("vod_name");
                    String cover = vod.getString("vod_pic");
                    JSONObject v = new JSONObject();
                    v.put("vod_id", id);
                    v.put("vod_name", title);
                    v.put("vod_pic", cover);
                    v.put("vod_remarks", "");
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
}
