    package com.github.catvod.spider;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;

import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.utils.okhttp.OKCallBack;
import com.github.catvod.utils.okhttp.OkHttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Headers;

public class Voflix extends Spider {
    private static final String siteUrl = "https://www.voflix.com";
    private static final String siteHost = "www.voflix.com";

    /**
     * 播放源配置
     */
    private JSONObject playerConfig;
    /**
     * 筛选配置
     */
    private JSONObject filterConfig;
    private Pattern regexCategory = Pattern.compile("/type/(\\d+).html");
    private Pattern regexVid = Pattern.compile("/detail/(\\d+).html");
    private Pattern regexPlay = Pattern.compile("/play/(\\d+)-(\\d+)-(\\d+).html");
    private Pattern regexPage = Pattern.compile("/show/(\\S+).html");

    @Override
    public void init(Context context) {
        super.init(context);
        try {
            playerConfig = new JSONObject("{\"duoduozy\":{\"show\":\"VOFLIX\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"\"},\"LINE406\":{\"show\":\"LINE406\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"\"},\"LINE407\":{\"show\":\"LINE407\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"\"},\"LINE408\":{\"show\":\"LINE408\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"\"},\"p300\":{\"show\":\"LINE300\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"\"},\"p301\":{\"show\":\"LINE301\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"\"},\"line402-日语\":{\"show\":\"LINE402\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"\"},\"LINE400\":{\"show\":\"LINE400\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"\"},\"line401\":{\"show\":\"LINE401\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"\"},\"iframe268\":{\"show\":\"LINE268\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"\"},\"iframe290\":{\"show\":\"LINE290\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"\"},\"iframe291\":{\"show\":\"LINE291\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"\"},\"iframe296\":{\"show\":\"LINE296\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"\"},\"iframe297\":{\"show\":\"LINE297\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"\"},\"iframe307\":{\"show\":\"LINE307\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"\"},\"iframe308\":{\"show\":\"LINE308\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"\"},\"iframe309\":{\"show\":\"LINE309\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"\"},\"line301\":{\"show\":\"LINE333\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"\"},\"line302\":{\"show\":\"LINE302\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"\"},\"LINE405\":{\"show\":\"LINE405\",\"des\":\"更多极速线路请访问APP\",\"ps\":\"0\",\"parse\":\"\"},\"LINE409\":{\"show\":\"LINE409\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"\"},\"banquan\":{\"show\":\"已下架\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"\"},\"iframe261\":{\"show\":\"LINE261\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"\"},\"iframe265\":{\"show\":\"LINE265\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"\"},\"iframe278\":{\"show\":\"LINE278\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"\"},\"iframe306\":{\"show\":\"LINE306\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"\"},\"iframe317\":{\"show\":\"LINE317\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"\"},\"xg_app_player\":{\"show\":\"app全局解析\",\"des\":\"\",\"ps\":\"1\",\"parse\":\"\"},\"iframe257\":{\"show\":\"LINE257\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"\"},\"iframe263\":{\"show\":\"LINE263\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"\"},\"iframe258\":{\"show\":\"LINE258\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"\"},\"iframe267\":{\"show\":\"LINE267\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"\"},\"iframe\":{\"show\":\"LINE200\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"\"},\"iframe262\":{\"show\":\"LINE262\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"\"},\"iframe266\":{\"show\":\"LINE266\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"\"},\"dplayer3\":{\"show\":\"播放线路3\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"\"},\"dplayer2\":{\"show\":\"播放线路2\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"\"},\"dplayer\":{\"show\":\"播放线路1\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"\"},\"xunlei1\":{\"show\":\"百度云盘\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"\"},\"if101国外\":{\"show\":\"VOFLIX_海外\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"https://demo.if101.tv/player/?url=\"},\"aliyun\":{\"show\":\"阿里云盘\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"\"},\"xunlei2\":{\"show\":\"迅雷云盘\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"\"},\"\":{\"show\":\"\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"\"},\"LINE1080\":{\"show\":\"LINE1080\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"\"},\"app\":{\"show\":\"LINEAPP\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"\"},\"kuake\":{\"show\":\"夸克网盘\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"\"}}");
            filterConfig = new JSONObject("{\"1\":[{\"key\":0,\"name\":\"分类\",\"value\":[{\"n\":\"全部\",\"v\":\"4\"},{\"n\":\"动作片\",\"v\":\"6\"},{\"n\":\"喜剧\",\"v\":\"7\"},{\"n\":\"爱情\",\"v\":\"8\"},{\"n\":\"科幻\",\"v\":\"9\"},{\"n\":\"恐怖\",\"v\":\"10\"},{\"n\":\"剧情\",\"v\":\"11\"},{\"n\":\"战争\",\"v\":\"12\"},{\"n\":\"动画\",\"v\":\"23\"}]},{\"key\":3,\"name\":\"剧情\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"警匪\",\"v\":\"警匪\"},{\"n\":\"犯罪\",\"v\":\"犯罪\"},{\"n\":\"动画\",\"v\":\"动画\"},{\"n\":\"奇幻\",\"v\":\"奇幻\"},{\"n\":\"武侠\",\"v\":\"武侠\"},{\"n\":\"冒险\",\"v\":\"冒险\"},{\"n\":\"枪战\",\"v\":\"枪战\"},{\"n\":\"恐怖\",\"v\":\"恐怖\"},{\"n\":\"悬疑\",\"v\":\"悬疑\"},{\"n\":\"惊悚\",\"v\":\"惊悚\"},{\"n\":\"经典\",\"v\":\"经典\"},{\"n\":\"青春\",\"v\":\"青春\"},{\"n\":\"文艺\",\"v\":\"文艺\"},{\"n\":\"微电影\",\"v\":\"微电影\"},{\"n\":\"古装\",\"v\":\"古装\"},{\"n\":\"历史\",\"v\":\"历史\"},{\"n\":\"运动\",\"v\":\"运动\"},{\"n\":\"农村\",\"v\":\"农村\"},{\"n\":\"儿童\",\"v\":\"儿童\"},{\"n\":\"网络电影\",\"v\":\"网络电影\"}]},{\"key\":1,\"name\":\"地区\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"中国大陆\",\"v\":\"中国大陆\"},{\"n\":\"中国香港\",\"v\":\"中国香港\"},{\"n\":\"中国台湾\",\"v\":\"中国台湾\"},{\"n\":\"韩国\",\"v\":\"韩国\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"美国\",\"v\":\"美国\"},{\"n\":\"法国\",\"v\":\"法国\"},{\"n\":\"英国\",\"v\":\"英国\"},{\"n\":\"德国\",\"v\":\"德国\"},{\"n\":\"泰国\",\"v\":\"泰国\"},{\"n\":\"新加坡\",\"v\":\"新加坡\"},{\"n\":\"马来西亚\",\"v\":\"马来西亚\"},{\"n\":\"印度\",\"v\":\"印度\"},{\"n\":\"法国\",\"v\":\"法国\"},{\"n\":\"英国\",\"v\":\"英国\"},{\"n\":\"德国\",\"v\":\"德国\"},{\"n\":\"意大利\",\"v\":\"意大利\"},{\"n\":\"西班牙\",\"v\":\"西班牙\"},{\"n\":\"加拿大\",\"v\":\"加拿大\"},{\"n\":\"其他\",\"v\":\"其他\"}]},{\"key\":11,\"name\":\"年份\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2015\",\"v\":\"2015\"},{\"n\":\"2014\",\"v\":\"2014\"},{\"n\":\"2013\",\"v\":\"2013\"},{\"n\":\"2012\",\"v\":\"2012\"},{\"n\":\"2011\",\"v\":\"2011\"},{\"n\":\"2010\",\"v\":\"2010\"}]},{\"key\":4,\"name\":\"语言\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"国语\",\"v\":\"国语\"},{\"n\":\"英语\",\"v\":\"英语\"},{\"n\":\"粤语\",\"v\":\"粤语\"},{\"n\":\"闽南语\",\"v\":\"闽南语\"},{\"n\":\"韩语\",\"v\":\"韩语\"},{\"n\":\"日语\",\"v\":\"日语\"},{\"n\":\"其它\",\"v\":\"其它\"}]},{\"key\":5,\"name\":\"字母\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"A\",\"v\":\"A\"},{\"n\":\"B\",\"v\":\"B\"},{\"n\":\"C\",\"v\":\"C\"},{\"n\":\"D\",\"v\":\"D\"},{\"n\":\"E\",\"v\":\"E\"},{\"n\":\"F\",\"v\":\"F\"},{\"n\":\"G\",\"v\":\"G\"},{\"n\":\"H\",\"v\":\"H\"},{\"n\":\"I\",\"v\":\"I\"},{\"n\":\"J\",\"v\":\"J\"},{\"n\":\"K\",\"v\":\"K\"},{\"n\":\"L\",\"v\":\"L\"},{\"n\":\"M\",\"v\":\"M\"},{\"n\":\"N\",\"v\":\"N\"},{\"n\":\"O\",\"v\":\"O\"},{\"n\":\"P\",\"v\":\"P\"},{\"n\":\"Q\",\"v\":\"Q\"},{\"n\":\"R\",\"v\":\"R\"},{\"n\":\"S\",\"v\":\"S\"},{\"n\":\"T\",\"v\":\"T\"},{\"n\":\"U\",\"v\":\"U\"},{\"n\":\"V\",\"v\":\"V\"},{\"n\":\"W\",\"v\":\"W\"},{\"n\":\"X\",\"v\":\"X\"},{\"n\":\"Y\",\"v\":\"Y\"},{\"n\":\"Z\",\"v\":\"Z\"},{\"n\":\"0-9\",\"v\":\"0-9\"}]},{\"key\":2,\"name\":\"排序\",\"value\":[{\"n\":\"时间\",\"v\":\"time\"},{\"n\":\"人气\",\"v\":\"hits\"},{\"n\":\"评分\",\"v\":\"score\"}]}],\"2\":[{\"key\":0,\"name\":\"类型\",\"value\":[{\"n\":\"全部\",\"v\":\"2\"},{\"n\":\"港台剧\",\"v\":\"\"},{\"n\":\"国产剧\",\"v\":\"13\"},{\"n\":\"港台剧\",\"v\":\"14\"},{\"n\":\"日韩剧\",\"v\":\"15\"},{\"n\":\"欧美剧\",\"v\":\"16\"},{\"n\":\"纪录片\",\"v\":\"21\"},{\"n\":\"泰国剧\",\"v\":\"24\"}]},{\"key\":3,\"name\":\"剧情\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"古装\",\"v\":\"古装\"},{\"n\":\"战争\",\"v\":\"战争\"},{\"n\":\"青春偶像\",\"v\":\"青春偶像\"},{\"n\":\"喜剧\",\"v\":\"喜剧\"},{\"n\":\"家庭\",\"v\":\"家庭\"},{\"n\":\"犯罪\",\"v\":\"犯罪\"},{\"n\":\"动作\",\"v\":\"动作\"},{\"n\":\"奇幻\",\"v\":\"奇幻\"},{\"n\":\"剧情\",\"v\":\"剧情\"},{\"n\":\"历史\",\"v\":\"历史\"},{\"n\":\"经典\",\"v\":\"经典\"},{\"n\":\"乡村\",\"v\":\"乡村\"},{\"n\":\"情景\",\"v\":\"情景\"},{\"n\":\"商战\",\"v\":\"商战\"},{\"n\":\"网剧\",\"v\":\"网剧\"},{\"n\":\"其他\",\"v\":\"其他\"}]},{\"key\":1,\"name\":\"地区\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"中国大陆\",\"v\":\"中国大陆\"},{\"n\":\"中国香港\",\"v\":\"中国香港\"},{\"n\":\"中国台湾\",\"v\":\"中国台湾\"},{\"n\":\"韩国\",\"v\":\"韩国\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"美国\",\"v\":\"美国\"},{\"n\":\"法国\",\"v\":\"法国\"},{\"n\":\"英国\",\"v\":\"英国\"},{\"n\":\"德国\",\"v\":\"德国\"},{\"n\":\"泰国\",\"v\":\"泰国\"},{\"n\":\"新加坡\",\"v\":\"新加坡\"},{\"n\":\"马来西亚\",\"v\":\"马来西亚\"},{\"n\":\"印度\",\"v\":\"印度\"},{\"n\":\"法国\",\"v\":\"法国\"},{\"n\":\"英国\",\"v\":\"英国\"},{\"n\":\"德国\",\"v\":\"德国\"},{\"n\":\"意大利\",\"v\":\"意大利\"},{\"n\":\"西班牙\",\"v\":\"西班牙\"},{\"n\":\"加拿大\",\"v\":\"加拿大\"},{\"n\":\"其他\",\"v\":\"其他\"}]},{\"key\":11,\"name\":\"年份\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2015\",\"v\":\"2015\"},{\"n\":\"2014\",\"v\":\"2014\"},{\"n\":\"2013\",\"v\":\"2013\"},{\"n\":\"2012\",\"v\":\"2012\"},{\"n\":\"2011\",\"v\":\"2011\"},{\"n\":\"2010\",\"v\":\"2010\"},{\"n\":\"2009\",\"v\":\"2009\"},{\"n\":\"2008\",\"v\":\"2008\"},{\"n\":\"2007\",\"v\":\"2007\"},{\"n\":\"2006\",\"v\":\"2006\"},{\"n\":\"2005\",\"v\":\"2005\"},{\"n\":\"2004\",\"v\":\"2004\"}]},{\"key\":4,\"name\":\"语言\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"国语\",\"v\":\"国语\"},{\"n\":\"英语\",\"v\":\"英语\"},{\"n\":\"粤语\",\"v\":\"粤语\"},{\"n\":\"闽南语\",\"v\":\"闽南语\"},{\"n\":\"韩语\",\"v\":\"韩语\"},{\"n\":\"日语\",\"v\":\"日语\"},{\"n\":\"其它\",\"v\":\"其它\"}]},{\"key\":5,\"name\":\"字母\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"A\",\"v\":\"A\"},{\"n\":\"B\",\"v\":\"B\"},{\"n\":\"C\",\"v\":\"C\"},{\"n\":\"D\",\"v\":\"D\"},{\"n\":\"E\",\"v\":\"E\"},{\"n\":\"F\",\"v\":\"F\"},{\"n\":\"G\",\"v\":\"G\"},{\"n\":\"H\",\"v\":\"H\"},{\"n\":\"I\",\"v\":\"I\"},{\"n\":\"J\",\"v\":\"J\"},{\"n\":\"K\",\"v\":\"K\"},{\"n\":\"L\",\"v\":\"L\"},{\"n\":\"M\",\"v\":\"M\"},{\"n\":\"N\",\"v\":\"N\"},{\"n\":\"O\",\"v\":\"O\"},{\"n\":\"P\",\"v\":\"P\"},{\"n\":\"Q\",\"v\":\"Q\"},{\"n\":\"R\",\"v\":\"R\"},{\"n\":\"S\",\"v\":\"S\"},{\"n\":\"T\",\"v\":\"T\"},{\"n\":\"U\",\"v\":\"U\"},{\"n\":\"V\",\"v\":\"V\"},{\"n\":\"W\",\"v\":\"W\"},{\"n\":\"X\",\"v\":\"X\"},{\"n\":\"Y\",\"v\":\"Y\"},{\"n\":\"Z\",\"v\":\"Z\"},{\"n\":\"0-9\",\"v\":\"0-9\"}]},{\"key\":2,\"name\":\"排序\",\"value\":[{\"n\":\"时间\",\"v\":\"time\"},{\"n\":\"人气\",\"v\":\"hits\"},{\"n\":\"评分\",\"v\":\"score\"}]}],\"4\":[{\"key\":3,\"name\":\"剧情\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"情感\",\"v\":\"情感\"},{\"n\":\"科幻\",\"v\":\"科幻\"},{\"n\":\"热血\",\"v\":\"热血\"},{\"n\":\"推理\",\"v\":\"推理\"},{\"n\":\"搞笑\",\"v\":\"搞笑\"},{\"n\":\"冒险\",\"v\":\"冒险\"},{\"n\":\"萝莉\",\"v\":\"萝莉\"},{\"n\":\"校园\",\"v\":\"校园\"},{\"n\":\"动作\",\"v\":\"动作\"},{\"n\":\"机战\",\"v\":\"机战\"},{\"n\":\"运动\",\"v\":\"运动\"},{\"n\":\"战争\",\"v\":\"战争\"},{\"n\":\"少年\",\"v\":\"少年\"},{\"n\":\"少女\",\"v\":\"少女\"},{\"n\":\"社会\",\"v\":\"社会\"},{\"n\":\"原创\",\"v\":\"原创\"},{\"n\":\"亲子\",\"v\":\"亲子\"},{\"n\":\"益智\",\"v\":\"益智\"},{\"n\":\"其他\",\"v\":\"其他\"}]},{\"key\":1,\"name\":\"地区\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"中国\",\"v\":\"中国\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"欧美\",\"v\":\"欧美\"},{\"n\":\"其他\",\"v\":\"其他\"}]},{\"key\":11,\"name\":\"年份\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2015\",\"v\":\"2015\"},{\"n\":\"2014\",\"v\":\"2014\"},{\"n\":\"2013\",\"v\":\"2013\"},{\"n\":\"2012\",\"v\":\"2012\"},{\"n\":\"2011\",\"v\":\"2011\"},{\"n\":\"2010\",\"v\":\"2010\"},{\"n\":\"2009\",\"v\":\"2009\"},{\"n\":\"2008\",\"v\":\"2008\"},{\"n\":\"2007\",\"v\":\"2007\"},{\"n\":\"2006\",\"v\":\"2006\"},{\"n\":\"2005\",\"v\":\"2005\"},{\"n\":\"2004\",\"v\":\"2004\"},{\"n\":\"2003\",\"v\":\"2003\"},{\"n\":\"2002\",\"v\":\"2002\"},{\"n\":\"2001\",\"v\":\"2001\"},{\"n\":\"2000\",\"v\":\"2000\"}]},{\"key\":2,\"name\":\"排序\",\"value\":[{\"n\":\"时间\",\"v\":\"time\"},{\"n\":\"人气\",\"v\":\"hits\"},{\"n\":\"评分\",\"v\":\"score\"}]}]}");
        } catch (JSONException e) {
            SpiderDebug.log(e);
        }
    }

    protected static HashMap<String, String> Headers() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.5005.62 Safari/537.36");
        headers.put("Referer", siteUrl);
        return headers;
    }
    protected static HashMap<String, String> postHeaders() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("User-Agent", " Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36");
        hashMap.put("Origin", " siteUrl");
        return hashMap;
    }
    /**
     * 爬虫headers
     *
     * @param url
     * @return
     */
    protected HashMap<String, String> getHeaders(String url) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("method", "GET");
        headers.put("Host", siteHost);
        headers.put("Upgrade-Insecure-Requests", "1");
        headers.put("DNT", "1");
        headers.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36");
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        headers.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
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
            Document doc = Jsoup.parse(OkHttpUtil.string(siteUrl, getHeaders(siteUrl)));
            // 分类节点
            Elements elements = doc.select("ul.navbar-items>li.navbar-item>a");
            JSONArray classes = new JSONArray();
            for (Element ele : elements) {
                String name = ele.text();
                boolean show = name.equals("电影") ||
                        name.equals("剧集") ||
                        name.equals("动漫") ||
                        name.equals("综艺") ||
                        name.equals("国产剧") ||
                        name.equals("日韩剧") ||
                        name.equals("欧美剧");
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
                Element homeList = doc.select("div.module-main.scroll-box").get(0);
                Elements list = homeList.select("div.module-items.module-poster-items-small.scroll-content a");
                JSONArray videos = new JSONArray();
                for (int i = 0; i < list.size(); i++) {
                    Element vod = list.get(i);
                    String title = vod.select("a").attr("title");
                    String cover = vod.select("img.lazy.lazyload").attr("data-original");
                    String remark = vod.select("div.module-item-note").text();
                    Matcher matcher = regexVid.matcher(vod.select("a").attr("href"));
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
            String[] urlParams = new String[]{"", "", "", "", "", "", "", "", "", "", "", ""};
            urlParams[0] = tid;
            urlParams[8] = pg;
            if (extend != null && extend.size() > 0) {
                for (Iterator<String> it = extend.keySet().iterator(); it.hasNext(); ) {
                    String key = it.next();
                    String value = extend.get(key);
                    urlParams[Integer.parseInt(key)] = URLEncoder.encode(value);
                }
            }
            // 获取分类数据的url
            String url = siteUrl + "/show/" + TextUtils.join("-", urlParams) + ".html";
            String html = OkHttpUtil.string(url, getHeaders(url));
            Document doc = Jsoup.parse(html);
            JSONObject result = new JSONObject();
            int pageCount = 0;
            int page = -1;

            // 取页码相关信息
            Elements pageInfo = doc.select("div[id=page]");
            if (pageInfo.size() == 0) {
                page = Integer.parseInt(pg);
                pageCount = page;
            } else {
                for (int i = 0; i < pageInfo.size(); i++) {
                    Element li = pageInfo.get(i);
                    Element a = li.selectFirst("a");
                    if (a == null)
                        continue;
                    String span = doc.select("span.page-current").text();
                    String wy = doc.select("div[id=page] a").last().attr("href");
                    if (page == -1) {
                        page = Integer.parseInt(span);
                    } else {
                        page = 0;
                    }
                    Matcher matcher = regexPage.matcher(wy);
                    if (matcher.find()) {
                        pageCount = Integer.parseInt(matcher.group(1).split("-")[8]);
                    } else {
                        pageCount = 0;
                    }
                    break;

                }
            }
            JSONArray videos = new JSONArray();
            if (!html.contains("没有找到您想要的结果哦")) {
                // 取当前分类页的视频列表
                Elements list = doc.select("div.module-items.module-poster-items-base a ");
                for (int i = 0; i < list.size(); i++) {
                    Element vod = list.get(i);
                    String title = vod.select("a").attr("title");
                    String cover = vod.select("img.lazy.lazyload").attr("data-original");
                    String remark = vod.select("div.module-item-note").text();
                    Matcher matcher = regexVid.matcher(vod.select("a").attr("href"));
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
            String url = siteUrl + "/video/" + ids.get(0) + ".html";
            Document doc = Jsoup.parse(OkHttpUtil.string(url, getHeaders(url)));
            JSONObject result = new JSONObject();
            JSONObject vodList = new JSONObject();
            // 取基本数据
            String cover = doc.selectFirst("div.module-item-pic > img").attr("data-src");
            String title = doc.selectFirst("div.video-info-header > h1.page-title").text();
            String desc = doc.select("div.video-info-content").text();
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
            Elements sources = doc.select("div[id='y-playList'] span");

            Elements sourceList = doc.select("div.module-play-list");

            for (int i = 0; i < sources.size(); i++) {
                Element source = sources.get(i);
                String sourceName = source.text();
                boolean found = false;
                for (Iterator<String> it = playerConfig.keys(); it.hasNext(); ) {
                    String flag = it.next();
                    if (playerConfig.getJSONObject(flag).getString("show").equals(sourceName)) {
                        sourceName = flag;
                        found = true;
                        break;
                    }
                }
                if (!found)
                    continue;
                String playList = "";
                Elements playListA = sourceList.get(i).select("a.module-play-list-link ");
                List<String> vodItems = new ArrayList<>();

                for (int j = 0; j < playListA.size(); j++) {
                    Element vod = playListA.get(j);
                    Matcher matcher = regexPlay.matcher(vod.attr("href"));

                    if (!matcher.find())
                        continue;
                    String playURL = matcher.group(1) + "-" + matcher.group(2) + "-" + matcher.group(3);
                    vodItems.add(vod.text() + "$" + playURL);
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
     private final Pattern urlt = Pattern.compile("\"url\": *\"([^\"]*)\",");
    private final Pattern token = Pattern.compile("\"token\": *\"([^\"]*)\"");
    private final Pattern vkey = Pattern.compile("\"vkey\": *\"([^\"]*)\",");
//    private final Pattern tm = Pattern.compile("\"tm\": *\"([^\"]*)\",");
    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) {
        try {
            //定义播放用的headers

            String url = siteUrl + "/play/" + id + ".html";
           // JSONObject headers = new JSONObject();
           // headers.put("origin", " https://www.jubaibai.me/");
           // headers.put("User-Agent", " Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36");
          //  headers.put("Accept", " */*");
        //     headers.put("Accept-Language", " zh-CN,zh;q=0.9,en-US;q=0.3,en;q=0.7");
         //   headers.put("Accept-Encoding", " gzip, deflate");

            // 播放页 url
            Elements allScript = Jsoup.parse(OkHttpUtil.string(url, getHeaders(url))).select("script");
            JSONObject result = new JSONObject();
            for (int i = 0; i < allScript.size(); i++) {
                String scContent = allScript.get(i).html().trim();
                if (scContent.startsWith("var player_aaaa")) { // 取直链
                    int start = scContent.indexOf('{');
                    int end = scContent.lastIndexOf('}') + 1;
                    String json = scContent.substring(start, end);
                    JSONObject player = new JSONObject(json);
                    System.out.println("pla" + player);
                    if (playerConfig.has(player.getString("from"))) {
                        JSONObject pCfg = playerConfig.getJSONObject(player.getString("from"));
                        System.out.println("pc" + pCfg);
                        String videoUrl = player.getString("url");
                        String playUrl = pCfg.getString("parse");
                        String show = pCfg.getString("show");
                        if (show.contains("VOFLIX")) {
                       String jxurl = "https://play.shcpin.com/xplay/?url=" + videoUrl;
                       System.out.println("jx" + jxurl);
                       HashMap<String, String> headers = new HashMap<>();
                            headers.put("referer", siteUrl);
                       Document doc = Jsoup.parse(OkHttpUtil.string(jxurl,headers));
                       System.out.println("zh" + doc);
                        Elements script = doc.select("body>script");
                        for (int j = 0; j < script.size(); j++) {
                          String content = script.get(j).html().trim();
                        if (content.contains("var config =")){
                            Matcher matcher1 = urlt.matcher(content);
                            if (!matcher1.find())
                                continue;
                            Matcher matcher2 = token.matcher(content);
                            if (!matcher2.find())
                                continue;
                            Matcher matcher3 = vkey.matcher(content);
                            if (!matcher3.find())
                                continue;
                            String video_url = matcher1.group(1);
                            String video_token = matcher2.group(1);
                            String video_key = matcher3.group(1);                          
                            String video_sign= "F4penExTGogdt6U8" ;
                            String video_tm = String.valueOf(System.currentTimeMillis()/ 1000);
                            HashMap hashMap = new HashMap();
                            hashMap.put("token", video_token);
                            hashMap.put("tm", video_tm);
                            hashMap.put("url", video_url);
                            hashMap.put("vkey", video_key);
                             hashMap.put("sign", video_sign);
                            OkHttpUtil.get(OkHttpUtil.defaultClient(), "https://play.shcpin.com/xplay/555tZ4pvzHE3BpiO838.php", hashMap, new OKCallBack.OKCallBackString() {
                                @Override
                                protected void onFailure(Call call, Exception exc) {
                                }

                            public void onResponse(String str) {
                            try {
                                        String url = new String(Base64.decode(new JSONObject(str).getString("url").substring(8).getBytes(), Base64.DEFAULT));
                                        result.put("url", url.substring(8, url.length() - 8));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                          }
                          }
                        //    result.put("header", headers.toString());
                            result.put("parse", 0);
                            result.put("playUrl", "");
                        } else {
                            if (videoUrl.contains(".m3u8")) {
                                result.put("parse", 0);
                                result.put("playUrl", "");
                                result.put("url", videoUrl);
                             //   result.put("header", headers.toString());
                    }    
                  }
                 } 
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
            String url = siteUrl + "/index.php/ajax/suggest?mid=1&wd=" + URLEncoder.encode(key) + "&limit=35&timestamp=" + currentTime;
            JSONObject searchResult = new JSONObject(OkHttpUtil.string(url, getHeaders(url)));
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
