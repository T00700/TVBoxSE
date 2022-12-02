package com.github.catvod.spider;



import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;

import com.github.catvod.utils.Misc;
import com.github.catvod.utils.okhttp.OKCallBack;
import com.github.catvod.utils.okhttp.OkHttpUtil;
import com.github.catvod.crawler.Spider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;


public class Tkys extends Spider {

    private static final String siteUrl = new String("https://www.tkys.tv");
    private static final String siteHost = new String("www.tkys.tv");

    protected JSONObject playerConfig;
    protected JSONObject filterConfig;

    protected Pattern regexCategory = Pattern.compile("/vodtype/(\\d+).html");
    protected Pattern regexVoddetail = Pattern.compile("/voddetail/(\\d+).html");
    protected Pattern regexPlay = Pattern.compile("/vodplay/(\\S+).html");
    protected Pattern regexPage = Pattern.compile("/vodshow/\\d+--------(\\d+)---.html");
    protected Pattern regexBtToken = Pattern.compile("token = \"(.*?)\"");
    protected Pattern regexEnurl = Pattern.compile("getVideoInfo\\(\"(.*?)\"");


    private final String filterString = "";
    private final String playerString = "{\"mgtv\":{\"show\":\"芒果-官方\",\"des\":\"\",\"ps\":\"1\",\"parse\":\"https://play.tkys.tv/?url=\"},\"qiyi\":{\"show\":\"奇艺-官方\",\"des\":\"\",\"ps\":\"1\",\"parse\":\"https://jx.tiankongyingshi.tv/m3u8222222.php?url=\"},\"qq\":{\"show\":\"QQ-官方\",\"des\":\"\",\"ps\":\"1\",\"parse\":\"https://jx.tiankongyingshi.tv/m3u8222222.php?url=\"},\"sohu\":{\"show\":\"搜狐-官方\",\"des\":\"\",\"ps\":\"1\",\"parse\":\"https://play.tkys.tv/?url=\"},\"xx\":{\"show\":\"蓝光C\",\"des\":\"\",\"ps\":\"1\",\"parse\":\"https://jx.tiankongyingshi.tv/m3u8222222.php?url=\"},\"youku\":{\"show\":\"优酷-官方\",\"des\":\"\",\"ps\":\"1\",\"parse\":\"https://jx.tiankongyingshi.tv/m3u8222222.php?url=\"},\"bilibili\":{\"show\":\"BL-官方\",\"des\":\"\",\"ps\":\"1\",\"parse\":\"https://jx.tiankongyingshi.tv/m3u8222222.php?url=\"},\"dbm3u8\":{\"show\":\"备用1(请勿相信片中广告!!)\",\"des\":\"\",\"ps\":\"1\",\"parse\":\"https://jx.tiankongyingshi.tv/m3u8222222.php?url=\"},\"wjm3u8\":{\"show\":\"备用2(请勿相信片中广告!!)\",\"des\":\"\",\"ps\":\"1\",\"parse\":\"https://jx.tiankongyingshi.tv/m3u8222222.php?url=\"},\"kbm3u8\":{\"show\":\"备用3(有广告,误信)\",\"des\":\"\",\"ps\":\"1\",\"parse\":\"https://jx.tiankongyingshi.tv/m3u8222222.php?url=\"},\"tkqp\":{\"show\":\"app专用\",\"des\":\"天空自切\",\"ps\":\"1\",\"parse\":\"https://jxqd.tkys.tv/dnmdsb2.php?url=\"},\"wuduzy\":{\"show\":\"蓝光B(维护中)\",\"des\":\"五毒\",\"ps\":\"1\",\"parse\":\"https://jx.yyy4080.com/351755?url=\"},\"miaoparty\":{\"show\":\"蓝光MZ\",\"des\":\"\",\"ps\":\"1\",\"parse\":\"https://jxqd.tkys.tv/dnmdsb2.php?url=\"},\"wuduby\":{\"show\":\"蓝光备用\",\"des\":\"五毒备用\",\"ps\":\"0\",\"parse\":\"https://jx.tiankongyingshi.tv/m3u8.php?url=\"},\"iframe\":{\"show\":\"iframe外链数据\",\"des\":\"iframe外链数据\",\"ps\":\"0\",\"parse\":\"\"},\"link\":{\"show\":\"外链数据\",\"des\":\"外部网站播放链接\",\"ps\":\"0\",\"parse\":\"\"}}";

    @Override
    public void init(Context context) {
        super.init(context);
        try {
            playerConfig = new JSONObject(playerString);
            filterConfig = new JSONObject(filterString);
        } catch (JSONException e) {
            SpiderDebug.log(e);
        }
    }

    /**
     * 爬虫headers
     *
     * @param refererUrl
     * @return
     */
    protected HashMap<String, String> getHeaders(String refererUrl) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("method", "GET");
        if (!TextUtils.isEmpty(refererUrl)) {
            headers.put("Referer", refererUrl);
        }
        headers.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36");
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
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
            Document doc = Jsoup.parse(OkHttpUtil.string(url, getHeaders(siteUrl)));
            Elements elements = doc.select("ul.nav-menu>li>a[href*='vodtype']");
            JSONArray classes = new JSONArray();
            for (Element ele : elements) {
                String name = ele.text();
                String href = ele.attr("href");
                Matcher mather = regexCategory.matcher(href);
                if (!mather.find())
                    continue;
                // 把分类的id和名称取出来加到列表里
                String id = mather.group(1).trim();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type_id", id);
                jsonObject.put("type_name", name);
                classes.put(jsonObject);
            }

            JSONObject result = new JSONObject();
            if (filter) {
                result.put("filters", filterConfig);
            }
            result.put("class", classes);
            try {
                // 取首页推荐视频列表
                Elements list = doc.select("ul.myui-vodlist > li > div.myui-vodlist__box > a");
                JSONArray videos = new JSONArray();
                for (int i = 0; i < list.size(); i++) {
                    Element vod = list.get(i);
                    String title = vod.attr("title");
                    String cover = vod.attr("data-original");
                    String remark = "";
                    Element remarkEle = vod.selectFirst("span.pic-text");
                    if (remarkEle != null) {
                        remark = remarkEle.text();
                    }
                    Matcher matcher = regexVoddetail.matcher(vod.attr("href"));
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
            int page = 1;
            TreeMap<String, String> ext = new TreeMap<>();
            if (!TextUtils.isEmpty(pg) && Integer.parseInt(pg.trim()) > 1) {
                page = Integer.parseInt(pg.trim());
                ext.put("page", page + "");
            }
            if (extend != null && extend.size() > 0) {
                ext.putAll(extend);
            }
            ext.put("id", tid);
            List<String> kv = new ArrayList<>();
            for (Map.Entry<String, String> entry : ext.entrySet()) {
                if ("".equals(entry.getValue())) {
                    continue;
                }
                kv.add(entry.getKey() + "/" + entry.getValue());
            }
            String params = TextUtils.join("/", kv);

            String url = siteUrl + "/vodshow/" + params + ".html";

            String html = OkHttpUtil.string(url, getHeaders(siteUrl));
            Document doc = Jsoup.parse(html);
            JSONObject result = new JSONObject();
            int pageCount = 1;

            // 取页码相关信息
            Elements pageInfo = doc.select("ul.myui-page li.visible-xs > a");
            if (pageInfo.size() > 0) {
                String text = pageInfo.text();
                pageCount = Integer.parseInt(text.split("/")[1]);
            }

            JSONArray videos = new JSONArray();
            // 取当前分类页的视频列表
            Elements list = doc.select("ul.myui-vodlist > li > div > a");
            for (int i = 0; i < list.size(); i++) {
                Element vod = list.get(i);
                String title = vod.attr("title");
                String cover = vod.attr("data-original");
                String remark = "";
                Element remarkEle = vod.selectFirst("span.pic-text");
                if (remarkEle != null) {
                    remark = remarkEle.text();
                }

                Matcher matcher = regexVoddetail.matcher(vod.attr("href"));
                if (matcher.find()) {
                    String vodId = matcher.group(1);
                    JSONObject v = new JSONObject();
                    v.put("vod_id", vodId);
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
            String url = siteUrl + "/voddetail/" + ids.get(0) + ".html";
            Document doc = Jsoup.parse(OkHttpUtil.string(url, getHeaders(siteUrl)));
            JSONObject result = new JSONObject();
            JSONObject vodList = new JSONObject();

            // 取基本数据
            String cover = doc.selectFirst("div.myui-content__thumb > a > img").attr("data-original");
            String title = doc.selectFirst("div.myui-content__detail > h1").text();
            String desc = doc.selectFirst("#desc span.data").text();

            vodList.put("vod_id", ids.get(0));
            vodList.put("vod_name", title);
            vodList.put("vod_pic", cover);
            vodList.put("vod_content", desc);

            Map<String, String> vod_play = new LinkedHashMap<>();

            // 取播放列表数据
            Elements sources = doc.select("a[href*='playlist']");
            Elements sourceList = doc.select("div[id*='playlist']");

            for (int i = 0; i < sources.size(); i++) {
                Element source = sources.get(i);
                String sourceName = source.text();
                boolean found = false;
                for (Iterator<String> it = playerConfig.keys(); it.hasNext(); ) {
                    String flag = it.next();
                    if (playerConfig.getJSONObject(flag).getString("show").equals(sourceName)) {
//                        sourceName = flag;
                        found = true;
                        break;
                    }
                }
                if (found) {
                    String playList = "";
                    Elements playListA = sourceList.get(i).select("a");
                    List<String> vodItems = new ArrayList<>();

                    for (int j = 0; j < playListA.size(); j++) {
                        Element vod = playListA.get(j);
                        Matcher matcher = regexPlay.matcher(vod.attr("href"));
                        if (matcher.find()) {
                            String playURL = matcher.group(1);
                            String playUrlName = vod.text();
                            vodItems.add(playUrlName + "$" + playURL);
                        }
                    }
                    if (vodItems.size() > 0) {
                        playList = TextUtils.join("#", vodItems);
                        vod_play.put(sourceName, playList);
                    }
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
            // 播放页 url
            String url = siteUrl + "/vodplay/" + id + ".html";
            Document doc = Jsoup.parse(OkHttpUtil.string(url, getHeaders(siteUrl)));
            Elements allScript = doc.select("script");
            JSONObject result = new JSONObject();
            Map<String, String> playHeader = new HashMap<>();
            playHeader.put("User-Agent", " Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36");
            result.put("header", new JSONObject(playHeader).toString());
            result.put("parse", "0");
            result.put("playUrl", "");
            for (int i = 0; i < allScript.size(); i++) {
                String scContent = allScript.get(i).html().trim();
                if (scContent.startsWith("var player_")) { // 取直链
                    int start = scContent.indexOf('{');
                    int end = scContent.lastIndexOf('}') + 1;
                    String json = scContent.substring(start, end);
                    JSONObject player = new JSONObject(json);
                    String videoUrl = player.optString("url");
                    String from = player.optString("from");
                    if (from.equals("")) {
                        from = "tkqp";
                    }
                    if (Misc.isVideoFormat(videoUrl)) {
                        result.put("url", videoUrl);
                        return result.toString();
                    }

                    JSONObject playJxConfig = playerConfig.optJSONObject(from);
                    String parseUrl = playJxConfig.optString("parse");

                    Map<String, String> paramsMap = new HashMap<>();
                    paramsMap.put("url", videoUrl);
                    String parseHtml = OkHttpUtil.string(parseUrl + videoUrl, getHeaders(siteUrl));

                    Matcher tokenMatcher = regexBtToken.matcher(parseHtml);
                    Matcher enurlMatcher = regexEnurl.matcher(parseHtml);

                    if (tokenMatcher.find() && enurlMatcher.find()) {
                        String token = tokenMatcher.group(1);
                        String enurl = enurlMatcher.group(1);
                        byte[] decode = Base64.decode(enurl, Base64.DEFAULT);
                        String realurl = AES.decryptOfAesCbcPkcs7(decode, "7692AA70EEF92B42".getBytes(), token.getBytes());
                        result.put("url", realurl);
                        return result.toString();
                    }
                    break;
                }
            }
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }


    @Override
    public String searchContent(String key, boolean quick) {
        try {
            long currentTime = System.currentTimeMillis();
            String url = siteUrl + "/index.php/ajax/suggest?mid=1&wd=" + URLEncoder.encode(key) + "&limit=10&timestamp=" + currentTime;
            JSONObject searchResult = new JSONObject(OkHttpUtil.string(url, getHeaders(siteUrl)));
            JSONObject result = new JSONObject();
            JSONArray videos = new JSONArray();
            if (searchResult.getInt("total") > 0) {
                JSONArray lists = new JSONArray(searchResult.getString("list"));
                for (int i = 0; i < lists.length(); i++) {
                    JSONObject vod = lists.getJSONObject(i);
                    String id = vod.getString("id");
                    String title = vod.getString("name");
                    String cover = vod.getString("pic");
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
