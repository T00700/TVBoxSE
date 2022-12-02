package com.github.catvod.spider;

import android.content.Context;
import android.text.TextUtils;

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

public class If101 extends Spider {
    private static final String siteUrl = "https://oulevod.tv";
    private static final String siteHost = "oulevod.tv";

    /**
     * 播放源配置
     */
    private JSONObject playerConfig;
    /**
     * 筛选配置
     */
    private JSONObject filterConfig;
    private Pattern regexCategory = Pattern.compile("/index.php/vod/type/id/(\\d+).html");
    private Pattern regexVid = Pattern.compile("/index.php/vod/detail/id/(\\d+).html");
    private Pattern regexPlay = Pattern.compile("/index.php/vod/play/id/(\\d+)/sid/(\\d+)/nid/(\\d+).html");
    private Pattern regexPage = Pattern.compile("\\S+/page/(\\d+)\\S+");


    @Override
    public void init(Context context) {
        super.init(context);
        try {
            playerConfig = new JSONObject("{\"if101\":{\"sh\":\"高清蓝光\",\"sn\":0,\"pu\":\"\",\"or\":999}}");
            filterConfig = new JSONObject("{\"1\":[{\"key\":\"tid\",\"name\":\"分类\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"冒险\",\"v\":\"6\"},{\"n\":\"剧情\",\"v\":\"7\"},{\"n\":\"动作\",\"v\":\"8\"},{\"n\":\"动画电影\",\"v\":\"9\"},{\"n\":\"同性\",\"v\":\"10\"},{\"n\":\"喜剧\",\"v\":\"11\"},{\"n\":\"奇幻\",\"v\":\"12\"},{\"n\":\"恐怖\",\"v\":\"20\"},{\"n\":\"悬疑\",\"v\":\"21\"},{\"n\":\"惊悚\",\"v\":\"22\"},{\"n\":\"战争\",\"v\":\"23\"},{\"n\":\"歌舞\",\"v\":\"24\"},{\"n\":\"灾难\",\"v\":\"25\"},{\"n\":\"爱情\",\"v\":\"26\"},{\"n\":\"犯罪\",\"v\":\"27\"},{\"n\":\"科幻\",\"v\":\"28\"},{\"n\":\"经典\",\"v\":\"29\"},{\"n\":\"网络电影\",\"v\":\"30\"}]},{\"key\":\"class\",\"name\":\"类型\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"喜剧\",\"v\":\"喜剧\"},{\"n\":\"爱情\",\"v\":\"爱情\"},{\"n\":\"恐怖\",\"v\":\"恐怖\"},{\"n\":\"动作\",\"v\":\"动作\"},{\"n\":\"科幻\",\"v\":\"科幻\"},{\"n\":\"剧情\",\"v\":\"剧情\"},{\"n\":\"战争\",\"v\":\"战争\"},{\"n\":\"警匪\",\"v\":\"警匪\"},{\"n\":\"犯罪\",\"v\":\"犯罪\"},{\"n\":\"动画\",\"v\":\"动画\"},{\"n\":\"奇幻\",\"v\":\"奇幻\"},{\"n\":\"武侠\",\"v\":\"武侠\"},{\"n\":\"冒险\",\"v\":\"冒险\"},{\"n\":\"枪战\",\"v\":\"枪战\"},{\"n\":\"恐怖\",\"v\":\"恐怖\"},{\"n\":\"悬疑\",\"v\":\"悬疑\"},{\"n\":\"惊悚\",\"v\":\"惊悚\"},{\"n\":\"经典\",\"v\":\"经典\"},{\"n\":\"青春\",\"v\":\"青春\"},{\"n\":\"文艺\",\"v\":\"文艺\"},{\"n\":\"微电影\",\"v\":\"微电影\"},{\"n\":\"古装\",\"v\":\"古装\"},{\"n\":\"历史\",\"v\":\"历史\"},{\"n\":\"运动\",\"v\":\"运动\"},{\"n\":\"农村\",\"v\":\"农村\"},{\"n\":\"儿童\",\"v\":\"儿童\"},{\"n\":\"网络电影\",\"v\":\"网络电影\"}]},{\"key\":\"area\",\"name\":\"地区\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"大陆\",\"v\":\"大陆\"},{\"n\":\"香港\",\"v\":\"香港\"},{\"n\":\"台湾\",\"v\":\"台湾\"},{\"n\":\"美国\",\"v\":\"美国\"},{\"n\":\"法国\",\"v\":\"法国\"},{\"n\":\"英国\",\"v\":\"英国\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"韩国\",\"v\":\"韩国\"},{\"n\":\"德国\",\"v\":\"德国\"},{\"n\":\"泰国\",\"v\":\"泰国\"},{\"n\":\"印度\",\"v\":\"印度\"},{\"n\":\"意大利\",\"v\":\"意大利\"},{\"n\":\"西班牙\",\"v\":\"西班牙\"},{\"n\":\"加拿大\",\"v\":\"加拿大\"},{\"n\":\"其他\",\"v\":\"其他\"}]},{\"key\":\"year\",\"name\":\"年份\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2015\",\"v\":\"2015\"},{\"n\":\"2014\",\"v\":\"2014\"},{\"n\":\"2013\",\"v\":\"2013\"},{\"n\":\"2012\",\"v\":\"2012\"},{\"n\":\"2011\",\"v\":\"2011\"},{\"n\":\"2010\",\"v\":\"2010\"},{\"n\":\"2009\",\"v\":\"2009\"},{\"n\":\"2008\",\"v\":\"2008\"},{\"n\":\"2007\",\"v\":\"2007\"},{\"n\":\"2006\",\"v\":\"2006\"},{\"n\":\"2005\",\"v\":\"2005\"},{\"n\":\"2004\",\"v\":\"2004\"},{\"n\":\"2003\",\"v\":\"2003\"},{\"n\":\"2002\",\"v\":\"2002\"},{\"n\":\"2001\",\"v\":\"2001\"},{\"n\":\"2000\",\"v\":\"2000\"}]},{\"key\":\"lang\",\"name\":\"语言\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"国语\",\"v\":\"国语\"},{\"n\":\"英语\",\"v\":\"英语\"},{\"n\":\"粤语\",\"v\":\"粤语\"},{\"n\":\"闽南语\",\"v\":\"闽南语\"},{\"n\":\"韩语\",\"v\":\"韩语\"},{\"n\":\"日语\",\"v\":\"日语\"},{\"n\":\"法语\",\"v\":\"法语\"},{\"n\":\"德语\",\"v\":\"德语\"},{\"n\":\"其它\",\"v\":\"其它\"}]},{\"key\":\"version\",\"name\":\"版本\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"高清版\",\"v\":\"高清版\"},{\"n\":\"剧场版\",\"v\":\"剧场版\"},{\"n\":\"抢先版\",\"v\":\"抢先版\"},{\"n\":\"OVA\",\"v\":\"OVA\"},{\"n\":\"TV\",\"v\":\"TV\"},{\"n\":\"影院版\",\"v\":\"影院版\"}]},{\"key\":\"state\",\"name\":\"资源\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"正片\",\"v\":\"正片\"},{\"n\":\"预告片\",\"v\":\"预告片\"},{\"n\":\"花絮\",\"v\":\"花絮\"}]},{\"key\":\"letter\",\"name\":\"字母\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"A\",\"v\":\"A\"},{\"n\":\"B\",\"v\":\"B\"},{\"n\":\"C\",\"v\":\"C\"},{\"n\":\"D\",\"v\":\"D\"},{\"n\":\"E\",\"v\":\"E\"},{\"n\":\"F\",\"v\":\"F\"},{\"n\":\"G\",\"v\":\"G\"},{\"n\":\"H\",\"v\":\"H\"},{\"n\":\"I\",\"v\":\"I\"},{\"n\":\"J\",\"v\":\"J\"},{\"n\":\"K\",\"v\":\"K\"},{\"n\":\"L\",\"v\":\"L\"},{\"n\":\"M\",\"v\":\"M\"},{\"n\":\"N\",\"v\":\"N\"},{\"n\":\"O\",\"v\":\"O\"},{\"n\":\"P\",\"v\":\"P\"},{\"n\":\"Q\",\"v\":\"Q\"},{\"n\":\"R\",\"v\":\"R\"},{\"n\":\"S\",\"v\":\"S\"},{\"n\":\"T\",\"v\":\"T\"},{\"n\":\"U\",\"v\":\"U\"},{\"n\":\"V\",\"v\":\"V\"},{\"n\":\"W\",\"v\":\"W\"},{\"n\":\"X\",\"v\":\"X\"},{\"n\":\"Y\",\"v\":\"Y\"},{\"n\":\"Z\",\"v\":\"Z\"}]},{\"key\":\"by\",\"name\":\"By\",\"value\":[{\"n\":\"按最新\",\"v\":\"time\"},{\"n\":\"按最热\",\"v\":\"hits\"},{\"n\":\"按评分\",\"v\":\"score\"}]}],\"2\":[{\"key\":\"tid\",\"name\":\"分类\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"欧美剧\",\"v\":\"13\"},{\"n\":\"日剧\",\"v\":\"14\"},{\"n\":\"韩剧\",\"v\":\"15\"},{\"n\":\"国产剧\",\"v\":\"16\"},{\"n\":\"泰剧\",\"v\":\"31\"},{\"n\":\"港剧\",\"v\":\"32\"},{\"n\":\"台剧\",\"v\":\"33\"},{\"n\":\"新马剧\",\"v\":\"34\"},{\"n\":\"其它剧\",\"v\":\"35\"}]},{\"key\":\"class\",\"name\":\"类型\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"古装\",\"v\":\"古装\"},{\"n\":\"战争\",\"v\":\"战争\"},{\"n\":\"青春偶像\",\"v\":\"青春偶像\"},{\"n\":\"喜剧\",\"v\":\"喜剧\"},{\"n\":\"家庭\",\"v\":\"家庭\"},{\"n\":\"犯罪\",\"v\":\"犯罪\"},{\"n\":\"动作\",\"v\":\"动作\"},{\"n\":\"奇幻\",\"v\":\"奇幻\"},{\"n\":\"剧情\",\"v\":\"剧情\"},{\"n\":\"历史\",\"v\":\"历史\"},{\"n\":\"经典\",\"v\":\"经典\"},{\"n\":\"乡村\",\"v\":\"乡村\"},{\"n\":\"情景\",\"v\":\"情景\"},{\"n\":\"商战\",\"v\":\"商战\"},{\"n\":\"网剧\",\"v\":\"网剧\"},{\"n\":\"其他\",\"v\":\"其他\"}]},{\"key\":\"area\",\"name\":\"地区\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"大陆\",\"v\":\"大陆\"},{\"n\":\"韩国\",\"v\":\"韩国\"},{\"n\":\"香港\",\"v\":\"香港\"},{\"n\":\"台湾\",\"v\":\"台湾\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"美国\",\"v\":\"美国\"},{\"n\":\"泰国\",\"v\":\"泰国\"},{\"n\":\"英国\",\"v\":\"英国\"},{\"n\":\"新加坡\",\"v\":\"新加坡\"},{\"n\":\"其他\",\"v\":\"其他\"}]},{\"key\":\"year\",\"name\":\"年份\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2015\",\"v\":\"2015\"},{\"n\":\"2014\",\"v\":\"2014\"},{\"n\":\"2013\",\"v\":\"2013\"},{\"n\":\"2012\",\"v\":\"2012\"},{\"n\":\"2011\",\"v\":\"2011\"},{\"n\":\"2010\",\"v\":\"2010\"},{\"n\":\"2009\",\"v\":\"2009\"},{\"n\":\"2008\",\"v\":\"2008\"},{\"n\":\"2007\",\"v\":\"2007\"},{\"n\":\"2006\",\"v\":\"2006\"},{\"n\":\"2005\",\"v\":\"2005\"},{\"n\":\"2004\",\"v\":\"2004\"},{\"n\":\"2003\",\"v\":\"2003\"},{\"n\":\"2002\",\"v\":\"2002\"},{\"n\":\"2001\",\"v\":\"2001\"},{\"n\":\"2000\",\"v\":\"2000\"}]},{\"key\":\"lang\",\"name\":\"语言\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"国语\",\"v\":\"国语\"},{\"n\":\"英语\",\"v\":\"英语\"},{\"n\":\"粤语\",\"v\":\"粤语\"},{\"n\":\"闽南语\",\"v\":\"闽南语\"},{\"n\":\"韩语\",\"v\":\"韩语\"},{\"n\":\"日语\",\"v\":\"日语\"},{\"n\":\"其它\",\"v\":\"其它\"}]},{\"key\":\"version\",\"name\":\"版本\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"高清版\",\"v\":\"高清版\"},{\"n\":\"剧场版\",\"v\":\"剧场版\"},{\"n\":\"抢先版\",\"v\":\"抢先版\"},{\"n\":\"OVA\",\"v\":\"OVA\"},{\"n\":\"TV\",\"v\":\"TV\"},{\"n\":\"影院版\",\"v\":\"影院版\"}]},{\"key\":\"state\",\"name\":\"资源\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"正片\",\"v\":\"正片\"},{\"n\":\"预告片\",\"v\":\"预告片\"},{\"n\":\"花絮\",\"v\":\"花絮\"}]},{\"key\":\"letter\",\"name\":\"字母\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"A\",\"v\":\"A\"},{\"n\":\"B\",\"v\":\"B\"},{\"n\":\"C\",\"v\":\"C\"},{\"n\":\"D\",\"v\":\"D\"},{\"n\":\"E\",\"v\":\"E\"},{\"n\":\"F\",\"v\":\"F\"},{\"n\":\"G\",\"v\":\"G\"},{\"n\":\"H\",\"v\":\"H\"},{\"n\":\"I\",\"v\":\"I\"},{\"n\":\"J\",\"v\":\"J\"},{\"n\":\"K\",\"v\":\"K\"},{\"n\":\"L\",\"v\":\"L\"},{\"n\":\"M\",\"v\":\"M\"},{\"n\":\"N\",\"v\":\"N\"},{\"n\":\"O\",\"v\":\"O\"},{\"n\":\"P\",\"v\":\"P\"},{\"n\":\"Q\",\"v\":\"Q\"},{\"n\":\"R\",\"v\":\"R\"},{\"n\":\"S\",\"v\":\"S\"},{\"n\":\"T\",\"v\":\"T\"},{\"n\":\"U\",\"v\":\"U\"},{\"n\":\"V\",\"v\":\"V\"},{\"n\":\"W\",\"v\":\"W\"},{\"n\":\"X\",\"v\":\"X\"},{\"n\":\"Y\",\"v\":\"Y\"},{\"n\":\"Z\",\"v\":\"Z\"}]},{\"key\":\"by\",\"name\":\"By\",\"value\":[{\"n\":\"按最新\",\"v\":\"time\"},{\"n\":\"按最热\",\"v\":\"hits\"},{\"n\":\"按评分\",\"v\":\"score\"}]}],\"3\":[{\"key\":\"tid\",\"name\":\"分类\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"欧美动漫\",\"v\":\"36\"},{\"n\":\"日本动漫\",\"v\":\"37\"},{\"n\":\"韩国动漫\",\"v\":\"38\"},{\"n\":\"国产动漫\",\"v\":\"39\"},{\"n\":\"新马泰动漫\",\"v\":\"40\"},{\"n\":\"港台动漫\",\"v\":\"41\"},{\"n\":\"其他动漫\",\"v\":\"42\"}]},{\"key\":\"class\",\"name\":\"类型\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"选秀\",\"v\":\"选秀\"},{\"n\":\"情感\",\"v\":\"情感\"},{\"n\":\"访谈\",\"v\":\"访谈\"},{\"n\":\"播报\",\"v\":\"播报\"},{\"n\":\"旅游\",\"v\":\"旅游\"},{\"n\":\"音乐\",\"v\":\"音乐\"},{\"n\":\"美食\",\"v\":\"美食\"},{\"n\":\"纪实\",\"v\":\"纪实\"},{\"n\":\"曲艺\",\"v\":\"曲艺\"},{\"n\":\"生活\",\"v\":\"生活\"},{\"n\":\"游戏互动\",\"v\":\"游戏互动\"},{\"n\":\"财经\",\"v\":\"财经\"},{\"n\":\"求职\",\"v\":\"求职\"}]},{\"key\":\"area\",\"name\":\"地区\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"内地\",\"v\":\"内地\"},{\"n\":\"港台\",\"v\":\"港台\"},{\"n\":\"日韩\",\"v\":\"日韩\"},{\"n\":\"欧美\",\"v\":\"欧美\"}]},{\"key\":\"year\",\"name\":\"年份\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2015\",\"v\":\"2015\"},{\"n\":\"2014\",\"v\":\"2014\"},{\"n\":\"2013\",\"v\":\"2013\"},{\"n\":\"2012\",\"v\":\"2012\"},{\"n\":\"2011\",\"v\":\"2011\"},{\"n\":\"2010\",\"v\":\"2010\"},{\"n\":\"2009\",\"v\":\"2009\"},{\"n\":\"2008\",\"v\":\"2008\"},{\"n\":\"2007\",\"v\":\"2007\"},{\"n\":\"2006\",\"v\":\"2006\"},{\"n\":\"2005\",\"v\":\"2005\"},{\"n\":\"2004\",\"v\":\"2004\"},{\"n\":\"2003\",\"v\":\"2003\"},{\"n\":\"2002\",\"v\":\"2002\"},{\"n\":\"2001\",\"v\":\"2001\"},{\"n\":\"2000\",\"v\":\"2000\"}]},{\"key\":\"lang\",\"name\":\"语言\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"国语\",\"v\":\"国语\"},{\"n\":\"英语\",\"v\":\"英语\"},{\"n\":\"粤语\",\"v\":\"粤语\"},{\"n\":\"闽南语\",\"v\":\"闽南语\"},{\"n\":\"韩语\",\"v\":\"韩语\"},{\"n\":\"日语\",\"v\":\"日语\"},{\"n\":\"其它\",\"v\":\"其它\"}]},{\"key\":\"letter\",\"name\":\"字母\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"A\",\"v\":\"A\"},{\"n\":\"B\",\"v\":\"B\"},{\"n\":\"C\",\"v\":\"C\"},{\"n\":\"D\",\"v\":\"D\"},{\"n\":\"E\",\"v\":\"E\"},{\"n\":\"F\",\"v\":\"F\"},{\"n\":\"G\",\"v\":\"G\"},{\"n\":\"H\",\"v\":\"H\"},{\"n\":\"I\",\"v\":\"I\"},{\"n\":\"J\",\"v\":\"J\"},{\"n\":\"K\",\"v\":\"K\"},{\"n\":\"L\",\"v\":\"L\"},{\"n\":\"M\",\"v\":\"M\"},{\"n\":\"N\",\"v\":\"N\"},{\"n\":\"O\",\"v\":\"O\"},{\"n\":\"P\",\"v\":\"P\"},{\"n\":\"Q\",\"v\":\"Q\"},{\"n\":\"R\",\"v\":\"R\"},{\"n\":\"S\",\"v\":\"S\"},{\"n\":\"T\",\"v\":\"T\"},{\"n\":\"U\",\"v\":\"U\"},{\"n\":\"V\",\"v\":\"V\"},{\"n\":\"W\",\"v\":\"W\"},{\"n\":\"X\",\"v\":\"X\"},{\"n\":\"Y\",\"v\":\"Y\"},{\"n\":\"Z\",\"v\":\"Z\"}]},{\"key\":\"by\",\"name\":\"By\",\"value\":[{\"n\":\"按最新\",\"v\":\"time\"},{\"n\":\"按最热\",\"v\":\"hits\"},{\"n\":\"按评分\",\"v\":\"score\"}]}],\"4\":[{\"key\":\"tid\",\"name\":\"分类\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"欧美综艺\",\"v\":\"43\"},{\"n\":\"日本综艺\",\"v\":\"44\"},{\"n\":\"韩国综艺\",\"v\":\"45\"},{\"n\":\"国产综艺\",\"v\":\"46\"},{\"n\":\"新马泰综艺\",\"v\":\"47\"},{\"n\":\"港台综艺\",\"v\":\"48\"},{\"n\":\"其他综艺\",\"v\":\"49\"}]},{\"key\":\"class\",\"name\":\"类型\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"情感\",\"v\":\"情感\"},{\"n\":\"科幻\",\"v\":\"科幻\"},{\"n\":\"热血\",\"v\":\"热血\"},{\"n\":\"推理\",\"v\":\"推理\"},{\"n\":\"搞笑\",\"v\":\"搞笑\"},{\"n\":\"冒险\",\"v\":\"冒险\"},{\"n\":\"萝莉\",\"v\":\"萝莉\"},{\"n\":\"校园\",\"v\":\"校园\"},{\"n\":\"动作\",\"v\":\"动作\"},{\"n\":\"机战\",\"v\":\"机战\"},{\"n\":\"运动\",\"v\":\"运动\"},{\"n\":\"战争\",\"v\":\"战争\"},{\"n\":\"少年\",\"v\":\"少年\"},{\"n\":\"少女\",\"v\":\"少女\"},{\"n\":\"社会\",\"v\":\"社会\"},{\"n\":\"原创\",\"v\":\"原创\"},{\"n\":\"亲子\",\"v\":\"亲子\"},{\"n\":\"益智\",\"v\":\"益智\"},{\"n\":\"励志\",\"v\":\"励志\"},{\"n\":\"其他\",\"v\":\"其他\"}]},{\"key\":\"area\",\"name\":\"地区\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"大陆\",\"v\":\"大陆\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"欧美\",\"v\":\"欧美\"},{\"n\":\"其他\",\"v\":\"其他\"}]},{\"key\":\"year\",\"name\":\"年份\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2015\",\"v\":\"2015\"},{\"n\":\"2014\",\"v\":\"2014\"},{\"n\":\"2013\",\"v\":\"2013\"},{\"n\":\"2012\",\"v\":\"2012\"},{\"n\":\"2011\",\"v\":\"2011\"},{\"n\":\"2010\",\"v\":\"2010\"},{\"n\":\"2009\",\"v\":\"2009\"},{\"n\":\"2008\",\"v\":\"2008\"},{\"n\":\"2007\",\"v\":\"2007\"},{\"n\":\"2006\",\"v\":\"2006\"},{\"n\":\"2005\",\"v\":\"2005\"},{\"n\":\"2004\",\"v\":\"2004\"},{\"n\":\"2003\",\"v\":\"2003\"},{\"n\":\"2002\",\"v\":\"2002\"},{\"n\":\"2001\",\"v\":\"2001\"},{\"n\":\"2000\",\"v\":\"2000\"}]},{\"key\":\"lang\",\"name\":\"语言\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"国语\",\"v\":\"国语\"},{\"n\":\"英语\",\"v\":\"英语\"},{\"n\":\"粤语\",\"v\":\"粤语\"},{\"n\":\"闽南语\",\"v\":\"闽南语\"},{\"n\":\"韩语\",\"v\":\"韩语\"},{\"n\":\"日语\",\"v\":\"日语\"},{\"n\":\"其它\",\"v\":\"其它\"}]},{\"key\":\"version\",\"name\":\"版本\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"TV版\",\"v\":\"TV版\"},{\"n\":\"电影版\",\"v\":\"电影版\"},{\"n\":\"OVA版\",\"v\":\"OVA版\"},{\"n\":\"真人版\",\"v\":\"真人版\"}]},{\"key\":\"letter\",\"name\":\"字母\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"A\",\"v\":\"A\"},{\"n\":\"B\",\"v\":\"B\"},{\"n\":\"C\",\"v\":\"C\"},{\"n\":\"D\",\"v\":\"D\"},{\"n\":\"E\",\"v\":\"E\"},{\"n\":\"F\",\"v\":\"F\"},{\"n\":\"G\",\"v\":\"G\"},{\"n\":\"H\",\"v\":\"H\"},{\"n\":\"I\",\"v\":\"I\"},{\"n\":\"J\",\"v\":\"J\"},{\"n\":\"K\",\"v\":\"K\"},{\"n\":\"L\",\"v\":\"L\"},{\"n\":\"M\",\"v\":\"M\"},{\"n\":\"N\",\"v\":\"N\"},{\"n\":\"O\",\"v\":\"O\"},{\"n\":\"P\",\"v\":\"P\"},{\"n\":\"Q\",\"v\":\"Q\"},{\"n\":\"R\",\"v\":\"R\"},{\"n\":\"S\",\"v\":\"S\"},{\"n\":\"T\",\"v\":\"T\"},{\"n\":\"U\",\"v\":\"U\"},{\"n\":\"V\",\"v\":\"V\"},{\"n\":\"W\",\"v\":\"W\"},{\"n\":\"X\",\"v\":\"X\"},{\"n\":\"Y\",\"v\":\"Y\"},{\"n\":\"Z\",\"v\":\"Z\"}]},{\"key\":\"by\",\"name\":\"By\",\"value\":[{\"n\":\"按最新\",\"v\":\"time\"},{\"n\":\"按最热\",\"v\":\"hits\"},{\"n\":\"按评分\",\"v\":\"score\"}]}],\"50\":[{\"key\":\"letter\",\"name\":\"字母\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"A\",\"v\":\"A\"},{\"n\":\"B\",\"v\":\"B\"},{\"n\":\"C\",\"v\":\"C\"},{\"n\":\"D\",\"v\":\"D\"},{\"n\":\"E\",\"v\":\"E\"},{\"n\":\"F\",\"v\":\"F\"},{\"n\":\"G\",\"v\":\"G\"},{\"n\":\"H\",\"v\":\"H\"},{\"n\":\"I\",\"v\":\"I\"},{\"n\":\"J\",\"v\":\"J\"},{\"n\":\"K\",\"v\":\"K\"},{\"n\":\"L\",\"v\":\"L\"},{\"n\":\"M\",\"v\":\"M\"},{\"n\":\"N\",\"v\":\"N\"},{\"n\":\"O\",\"v\":\"O\"},{\"n\":\"P\",\"v\":\"P\"},{\"n\":\"Q\",\"v\":\"Q\"},{\"n\":\"R\",\"v\":\"R\"},{\"n\":\"S\",\"v\":\"S\"},{\"n\":\"T\",\"v\":\"T\"},{\"n\":\"U\",\"v\":\"U\"},{\"n\":\"V\",\"v\":\"V\"},{\"n\":\"W\",\"v\":\"W\"},{\"n\":\"X\",\"v\":\"X\"},{\"n\":\"Y\",\"v\":\"Y\"},{\"n\":\"Z\",\"v\":\"Z\"}]},{\"key\":\"by\",\"name\":\"By\",\"value\":[{\"n\":\"按最新\",\"v\":\"time\"},{\"n\":\"按最热\",\"v\":\"hits\"},{\"n\":\"按评分\",\"v\":\"score\"}]}],\"51\":[{\"key\":\"letter\",\"name\":\"字母\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"A\",\"v\":\"A\"},{\"n\":\"B\",\"v\":\"B\"},{\"n\":\"C\",\"v\":\"C\"},{\"n\":\"D\",\"v\":\"D\"},{\"n\":\"E\",\"v\":\"E\"},{\"n\":\"F\",\"v\":\"F\"},{\"n\":\"G\",\"v\":\"G\"},{\"n\":\"H\",\"v\":\"H\"},{\"n\":\"I\",\"v\":\"I\"},{\"n\":\"J\",\"v\":\"J\"},{\"n\":\"K\",\"v\":\"K\"},{\"n\":\"L\",\"v\":\"L\"},{\"n\":\"M\",\"v\":\"M\"},{\"n\":\"N\",\"v\":\"N\"},{\"n\":\"O\",\"v\":\"O\"},{\"n\":\"P\",\"v\":\"P\"},{\"n\":\"Q\",\"v\":\"Q\"},{\"n\":\"R\",\"v\":\"R\"},{\"n\":\"S\",\"v\":\"S\"},{\"n\":\"T\",\"v\":\"T\"},{\"n\":\"U\",\"v\":\"U\"},{\"n\":\"V\",\"v\":\"V\"},{\"n\":\"W\",\"v\":\"W\"},{\"n\":\"X\",\"v\":\"X\"},{\"n\":\"Y\",\"v\":\"Y\"},{\"n\":\"Z\",\"v\":\"Z\"}]},{\"key\":\"by\",\"name\":\"By\",\"value\":[{\"n\":\"按最新\",\"v\":\"time\"},{\"n\":\"按最热\",\"v\":\"hits\"},{\"n\":\"按评分\",\"v\":\"score\"}]}]}");
        } catch (JSONException e) {
            SpiderDebug.log(e);
        }
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
            Elements elements = doc.select("div.conch-nav div ul li a");
            JSONArray classes = new JSONArray();
            for (Element ele : elements) {
                String name = ele.text();
                boolean show = name.equals("电影") ||
                        name.equals("电视剧") ||
                        name.equals("动漫") ||
                        name.equals("综艺") ||
                        name.equals("纪录片") ||
                        name.equals("体育");
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
                Element homeList = doc.select("ul.hl-vod-list").get(0);
                Elements list = homeList.select("li.hl-list-item");
                JSONArray videos = new JSONArray();
                for (int i = 0; i < list.size(); i++) {
                    Element vod = list.get(i);
                    String title = vod.selectFirst("div.hl-item-title a").text();
                    String cover = vod.selectFirst(".hl-item-thumb").attr("data-original");
                    String remark = vod.selectFirst(".hl-pic-text span").text();
                    Matcher matcher = regexVid.matcher(vod.selectFirst("div.hl-item-title a").attr("href"));
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
            String url = siteUrl + "/index.php/vod/show/id/";
            if (extend != null && extend.size() > 0 && extend.containsKey("tid") && extend.get("tid").length() > 0) {
                url += extend.get("tid");
            } else {
                url += tid;
            }
            if (extend != null && extend.size() > 0) {
                for (Iterator<String> it = extend.keySet().iterator(); it.hasNext(); ) {
                    String key = it.next();
                    String value = extend.get(key);
                    if (value.length() > 0) {
                        url += "/" + key + "/" + URLEncoder.encode(value);
                    }
                }
            }
            url += "/page/" + pg + ".html";
            String html = OkHttpUtil.string(url, getHeaders(url));
            Document doc = Jsoup.parse(html);
            JSONObject result = new JSONObject();
            int pageCount = 0;
            int page = -1;

            // 取页码相关信息
            Elements pageInfo = doc.select("ul.hl-page-wrap");
            if (pageInfo.size() == 0) {
                page = Integer.parseInt(pg);
                pageCount = page;
            } else {
                for (int i = 0; i < pageInfo.size(); i++) {
                    Element li = pageInfo.get(i);
                    Element a = li.selectFirst("li a");
                    if (a == null)
                        continue;
                    String span = pageInfo.select("li a.active").text();
                    String wy = doc.select("ul.hl-page-wrap li a").last().attr("href");
                    if (page == -1) {
                        page = Integer.parseInt(span);
                    } else {
                        page = 0;
                    }
                    Matcher matcher = regexPage.matcher(wy);
                    if (matcher.find()) {
                        pageCount = Integer.parseInt(matcher.group(1).trim());
                    } else {
                        pageCount = 0;
                    }
                    break;

                }
            }

            JSONArray videos = new JSONArray();
            if (!html.contains("没有找到您想要的结果哦")) {
                // 取当前分类页的视频列表
                Elements list = doc.select("li.hl-list-item");
                for (int i = 0; i < list.size(); i++) {
                    Element vod = list.get(i);
                    String title = vod.selectFirst("div.hl-item-title a").text();
                    String cover = vod.selectFirst(".hl-item-thumb").attr("data-original");
                    String remark = vod.selectFirst(".hl-pic-text span").text();
                    Matcher matcher = regexVid.matcher(vod.selectFirst("div.hl-item-title a").attr("href"));
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
            String url = siteUrl + "/index.php/vod/detail/id/" + ids.get(0) + ".html";
            Document doc = Jsoup.parse(OkHttpUtil.string(url, getHeaders(url)));
            JSONObject result = new JSONObject();
            JSONObject vodList = new JSONObject();

            // 取基本数据
            String vid = doc.selectFirst("a.digg_link").attr("data-id");

            String cover = doc.selectFirst("div.hl-dc-pic span").attr("data-original");
            String title = doc.selectFirst("h2.hl-dc-title").text();
            String desc = doc.selectFirst("span.hl-content-text em").text();
            String category = "", area = "", year = "", director = "", actor = "", remark = "";
            Elements span_text_muted = doc.select("li.hl-col-xs-12 em");
            for (int i = 0; i < span_text_muted.size() - 2; i++) {
                Element text = span_text_muted.get(i);
                String info = text.text();
                if (info.contains("类型")) {
                    try {
                        category = text.parent().select("a").text();
                    } catch (Exception e) {
                        category = "";
                    }
                } else if (info.contains("年份")) {
                    try {
                        year = text.parent().text().split("：")[1];
                    } catch (Exception e) {
                        year = "";
                    }
                } else if (info.contains("地区")) {
                    try {
                        area = text.parent().text().split("：")[1];
                    } catch (Exception e) {
                        area = "";
                    }
                } else if (info.contains("上映")) {
                    try {
                        remark = text.text();
                    } catch (Exception e) {
                        remark = "";
                    }
                } else if (info.contains("导演")) {
                    try {
                        director = text.parent().select("a").text();
                    } catch (Exception e) {
                        director = "";
                    }
                } else if (info.contains("主演")) {
                    try {
                        actor = text.parent().select("a").text();
                    } catch (Exception e) {
                        actor = "";
                    }
                }
            }

            vodList.put("vod_id", vid);
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
            Elements sources = doc.select("div.hl-plays-from a");
            Elements sourceList = doc.select("div.hl-list-wrap");

            for (int i = 0; i < sources.size(); i++) {
                Element source = sources.get(i);
                String sourceName = source.text();
                sourceName = sourceName.replaceAll("[^\u4E00-\u9FA5]", "");
                boolean found = false;
                for (Iterator<String> it = playerConfig.keys(); it.hasNext(); ) {
                    String flag = it.next();
                    if (playerConfig.getJSONObject(flag).getString("sh").equals(sourceName)) {
                        sourceName = flag;
                        found = true;
                        break;
                    }
                }
                if (!found)
                    continue;
                String playList = "";
                Elements playListA = sourceList.get(i).select("ul>li>a");
                List<String> vodItems = new ArrayList<>();

                for (int j = 0; j < playListA.size(); j++) {
                    Element vod = playListA.get(j);
                    Matcher matcher = regexPlay.matcher(vod.attr("href"));
                    if (!matcher.find())
                        continue;
                    String playURL = matcher.group(1) + "/sid/" + matcher.group(2) + "/nid/" + matcher.group(3);
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
    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) {
        try {
            // 播放页 url
            String url = siteUrl + "/index.php/vod/play/id/" + id + ".html";
            Document doc = Jsoup.parse(OkHttpUtil.string(url, getHeaders(url)));
            Elements allScript = doc.select("script");
            JSONObject result = new JSONObject();
            for (int i = 0; i < allScript.size(); i++) {
                String scContent = allScript.get(i).html().trim();
                if (scContent.startsWith("var player_")) { // 取直链
                    int start = scContent.indexOf('{');
                    int end = scContent.lastIndexOf('}') + 1;
                    String json = scContent.substring(start, end);
                    JSONObject player = new JSONObject(json);
                    if (playerConfig.has(player.getString("from"))) {
                        JSONObject pCfg = playerConfig.getJSONObject(player.getString("from"));
                        String videoUrl = player.getString("url");
                        String playUrl = pCfg.getString("pu");
                        result.put("parse", pCfg.getInt("sn"));
                        result.put("playUrl", playUrl);
                        result.put("url", videoUrl);
                        result.put("header", "");
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
                    if (title.contains(key)) {
                        JSONObject v = new JSONObject();
                        v.put("vod_id", id);
                        v.put("vod_name", title);
                        v.put("vod_pic", cover);
                        v.put("vod_remarks", "");
                        videos.put(v);
                    }
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
