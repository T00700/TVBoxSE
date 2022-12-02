package com.github.catvod.spider;

import android.content.Context;
import android.text.TextUtils;

import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.utils.Misc;
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

public class Olevod extends Spider {
    private static final String siteUrl = "https://olevod.com";
    private static final String siteHost = "olevod.com";


    boolean pron = false;
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
    public void init(Context context, String extend) {
        super.init(context, extend);
        if (extend != null && extend.trim().equals("ojbkporn")) {
            pron = true;
        }
    }

    @Override
    public void init(Context context) {
        super.init(context);
        try {
            playerConfig = new JSONObject("{\"plyr\":{\"sh\":\"欧乐官方播放器\",\"sn\":0,\"pu\":\"\",\"or\":999},\"openplayer\":{\"sh\":\"欧乐影院\",\"sn\":0,\"pu\":\"\",\"or\":999},\"videojs\":{\"sh\":\"欧乐直播播放\",\"sn\":0,\"pu\":\"\",\"or\":999}}");
            filterConfig = new JSONObject("{\"1\":[{\"key\":\"tid\",\"name\":\"分类\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"动作片\",\"v\":\"101\"},{\"n\":\"喜剧片\",\"v\":\"102\"},{\"n\":\"爱情片\",\"v\":\"103\"},{\"n\":\"科幻片\",\"v\":\"104\"},{\"n\":\"恐怖片\",\"v\":\"105\"},{\"n\":\"剧情片\",\"v\":\"106\"},{\"n\":\"战争片\",\"v\":\"107\"},{\"n\":\"动画片\",\"v\":\"108\"},{\"n\":\"悬疑片\",\"v\":\"109\"},{\"n\":\"惊悚片\",\"v\":\"110\"},{\"n\":\"纪录片\",\"v\":\"111\"},{\"n\":\"奇幻片\",\"v\":\"112\"},{\"n\":\"犯罪片\",\"v\":\"113\"}]},{\"key\":\"area\",\"name\":\"地区\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"大陆\",\"v\":\"大陆\"},{\"n\":\"香港\",\"v\":\"香港\"},{\"n\":\"台湾\",\"v\":\"台湾\"},{\"n\":\"美国\",\"v\":\"美国\"},{\"n\":\"韩国\",\"v\":\"韩国\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"印度\",\"v\":\"印度\"},{\"n\":\"英国\",\"v\":\"英国\"},{\"n\":\"法国\",\"v\":\"法国\"},{\"n\":\"加拿大\",\"v\":\"加拿大\"},{\"n\":\"西班牙\",\"v\":\"西班牙\"},{\"n\":\"德国\",\"v\":\"德国\"},{\"n\":\"俄罗斯\",\"v\":\"俄罗斯\"},{\"n\":\"意大利\",\"v\":\"意大利\"},{\"n\":\"泰国\",\"v\":\"泰国\"},{\"n\":\"新加坡\",\"v\":\"新加坡\"},{\"n\":\"马来西亚\",\"v\":\"马来西亚\"},{\"n\":\"其它\",\"v\":\"其它\"}]},{\"key\":\"year\",\"name\":\"年份\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2015\",\"v\":\"2015\"},{\"n\":\"2014\",\"v\":\"2014\"},{\"n\":\"2013\",\"v\":\"2013\"},{\"n\":\"2012\",\"v\":\"2012\"},{\"n\":\"2011\",\"v\":\"2011\"},{\"n\":\"2010\",\"v\":\"2010\"},{\"n\":\"2009\",\"v\":\"2009\"},{\"n\":\"2008\",\"v\":\"2008\"},{\"n\":\"2007\",\"v\":\"2007\"},{\"n\":\"2006\",\"v\":\"2006\"},{\"n\":\"2005\",\"v\":\"2005\"},{\"n\":\"2004\",\"v\":\"2004\"},{\"n\":\"2003\",\"v\":\"2003\"},{\"n\":\"2002\",\"v\":\"2002\"},{\"n\":\"2001\",\"v\":\"2001\"},{\"n\":\"2000\",\"v\":\"2000\"}]},{\"key\":\"lang\",\"name\":\"语言\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"国语\",\"v\":\"国语\"},{\"n\":\"英语\",\"v\":\"英语\"},{\"n\":\"粤语\",\"v\":\"粤语\"},{\"n\":\"西语\",\"v\":\"西语\"},{\"n\":\"韩语\",\"v\":\"韩语\"},{\"n\":\"日语\",\"v\":\"日语\"},{\"n\":\"法语\",\"v\":\"法语\"},{\"n\":\"俄语\",\"v\":\"俄语\"},{\"n\":\"意大利语\",\"v\":\"意大利语\"},{\"n\":\"德语\",\"v\":\"德语\"},{\"n\":\"印地语\",\"v\":\"印地语\"},{\"n\":\"其它\",\"v\":\"其它\"}]},{\"key\":\"letter\",\"name\":\"字母\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"A\",\"v\":\"A\"},{\"n\":\"B\",\"v\":\"B\"},{\"n\":\"C\",\"v\":\"C\"},{\"n\":\"D\",\"v\":\"D\"},{\"n\":\"E\",\"v\":\"E\"},{\"n\":\"F\",\"v\":\"F\"},{\"n\":\"G\",\"v\":\"G\"},{\"n\":\"H\",\"v\":\"H\"},{\"n\":\"I\",\"v\":\"I\"},{\"n\":\"J\",\"v\":\"J\"},{\"n\":\"K\",\"v\":\"K\"},{\"n\":\"L\",\"v\":\"L\"},{\"n\":\"M\",\"v\":\"M\"},{\"n\":\"N\",\"v\":\"N\"},{\"n\":\"O\",\"v\":\"O\"},{\"n\":\"P\",\"v\":\"P\"},{\"n\":\"Q\",\"v\":\"Q\"},{\"n\":\"R\",\"v\":\"R\"},{\"n\":\"S\",\"v\":\"S\"},{\"n\":\"T\",\"v\":\"T\"},{\"n\":\"U\",\"v\":\"U\"},{\"n\":\"V\",\"v\":\"V\"},{\"n\":\"W\",\"v\":\"W\"},{\"n\":\"X\",\"v\":\"X\"},{\"n\":\"Y\",\"v\":\"Y\"},{\"n\":\"Z\",\"v\":\"Z\"}]},{\"key\":\"ifvip\",\"name\":\"权限\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"免费\",\"v\":\"2\"},{\"n\":\"会员\",\"v\":\"3\"}]}],\"2\":[{\"key\":\"tid\",\"name\":\"分类\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"国产剧\",\"v\":\"202\"},{\"n\":\"欧美剧\",\"v\":\"201\"},{\"n\":\"港台剧\",\"v\":\"203\"},{\"n\":\"日韩剧\",\"v\":\"204\"}]},{\"key\":\"area\",\"name\":\"地区\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"大陆\",\"v\":\"大陆\"},{\"n\":\"香港\",\"v\":\"香港\"},{\"n\":\"台湾\",\"v\":\"台湾\"},{\"n\":\"美国\",\"v\":\"美国\"},{\"n\":\"韩国\",\"v\":\"韩国\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"印度\",\"v\":\"印度\"},{\"n\":\"英国\",\"v\":\"英国\"},{\"n\":\"法国\",\"v\":\"法国\"},{\"n\":\"加拿大\",\"v\":\"加拿大\"},{\"n\":\"西班牙\",\"v\":\"西班牙\"},{\"n\":\"俄罗斯\",\"v\":\"俄罗斯\"},{\"n\":\"意大利\",\"v\":\"意大利\"},{\"n\":\"泰国\",\"v\":\"泰国\"},{\"n\":\"新加坡\",\"v\":\"新加坡\"},{\"n\":\"马来西亚\",\"v\":\"马来西亚\"},{\"n\":\"其它\",\"v\":\"其它\"}]},{\"key\":\"year\",\"name\":\"年份\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2015\",\"v\":\"2015\"},{\"n\":\"2014\",\"v\":\"2014\"},{\"n\":\"2013\",\"v\":\"2013\"},{\"n\":\"2012\",\"v\":\"2012\"},{\"n\":\"2011\",\"v\":\"2011\"},{\"n\":\"2010\",\"v\":\"2010\"},{\"n\":\"2009\",\"v\":\"2009\"},{\"n\":\"2008\",\"v\":\"2008\"},{\"n\":\"2007\",\"v\":\"2007\"},{\"n\":\"2006\",\"v\":\"2006\"},{\"n\":\"2005\",\"v\":\"2005\"},{\"n\":\"2004\",\"v\":\"2004\"},{\"n\":\"2003\",\"v\":\"2003\"},{\"n\":\"2002\",\"v\":\"2002\"},{\"n\":\"2001\",\"v\":\"2001\"},{\"n\":\"2000\",\"v\":\"2000\"}]},{\"key\":\"lang\",\"name\":\"语言\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"国语\",\"v\":\"国语\"},{\"n\":\"英语\",\"v\":\"英语\"},{\"n\":\"粤语\",\"v\":\"粤语\"},{\"n\":\"西语\",\"v\":\"西语\"},{\"n\":\"韩语\",\"v\":\"韩语\"},{\"n\":\"日语\",\"v\":\"日语\"},{\"n\":\"法语\",\"v\":\"法语\"},{\"n\":\"俄语\",\"v\":\"俄语\"},{\"n\":\"意大利语\",\"v\":\"意大利语\"},{\"n\":\"德语\",\"v\":\"德语\"},{\"n\":\"印地语\",\"v\":\"印地语\"},{\"n\":\"其它\",\"v\":\"其它\"}]},{\"key\":\"letter\",\"name\":\"字母\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"A\",\"v\":\"A\"},{\"n\":\"B\",\"v\":\"B\"},{\"n\":\"C\",\"v\":\"C\"},{\"n\":\"D\",\"v\":\"D\"},{\"n\":\"E\",\"v\":\"E\"},{\"n\":\"F\",\"v\":\"F\"},{\"n\":\"G\",\"v\":\"G\"},{\"n\":\"H\",\"v\":\"H\"},{\"n\":\"I\",\"v\":\"I\"},{\"n\":\"J\",\"v\":\"J\"},{\"n\":\"K\",\"v\":\"K\"},{\"n\":\"L\",\"v\":\"L\"},{\"n\":\"M\",\"v\":\"M\"},{\"n\":\"N\",\"v\":\"N\"},{\"n\":\"O\",\"v\":\"O\"},{\"n\":\"P\",\"v\":\"P\"},{\"n\":\"Q\",\"v\":\"Q\"},{\"n\":\"R\",\"v\":\"R\"},{\"n\":\"S\",\"v\":\"S\"},{\"n\":\"T\",\"v\":\"T\"},{\"n\":\"U\",\"v\":\"U\"},{\"n\":\"V\",\"v\":\"V\"},{\"n\":\"W\",\"v\":\"W\"},{\"n\":\"X\",\"v\":\"X\"},{\"n\":\"Y\",\"v\":\"Y\"},{\"n\":\"Z\",\"v\":\"Z\"}]},{\"key\":\"ifvip\",\"name\":\"权限\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"免费\",\"v\":\"2\"},{\"n\":\"会员\",\"v\":\"3\"}]}],\"3\":[{\"key\":\"tid\",\"name\":\"分类\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"真人秀\",\"v\":\"305\"},{\"n\":\"音乐\",\"v\":\"302\"},{\"n\":\"搞笑\",\"v\":\"304\"},{\"n\":\"家庭\",\"v\":\"301\"},{\"n\":\"曲艺\",\"v\":\"303\"}]},{\"key\":\"area\",\"name\":\"地区\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"大陆\",\"v\":\"大陆\"},{\"n\":\"香港\",\"v\":\"香港\"},{\"n\":\"台湾\",\"v\":\"台湾\"},{\"n\":\"美国\",\"v\":\"美国\"},{\"n\":\"韩国\",\"v\":\"韩国\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"印度\",\"v\":\"印度\"},{\"n\":\"英国\",\"v\":\"英国\"},{\"n\":\"法国\",\"v\":\"法国\"},{\"n\":\"加拿大\",\"v\":\"加拿大\"},{\"n\":\"西班牙\",\"v\":\"西班牙\"},{\"n\":\"德国\",\"v\":\"德国\"},{\"n\":\"俄罗斯\",\"v\":\"俄罗斯\"},{\"n\":\"意大利\",\"v\":\"意大利\"},{\"n\":\"泰国\",\"v\":\"泰国\"},{\"n\":\"新加坡\",\"v\":\"新加坡\"},{\"n\":\"马来西亚\",\"v\":\"马来西亚\"},{\"n\":\"其它\",\"v\":\"其它\"}]},{\"key\":\"year\",\"name\":\"年份\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2015\",\"v\":\"2015\"},{\"n\":\"2014\",\"v\":\"2014\"},{\"n\":\"2013\",\"v\":\"2013\"},{\"n\":\"2012\",\"v\":\"2012\"},{\"n\":\"2011\",\"v\":\"2011\"},{\"n\":\"2010\",\"v\":\"2010\"},{\"n\":\"2009\",\"v\":\"2009\"},{\"n\":\"2008\",\"v\":\"2008\"},{\"n\":\"2007\",\"v\":\"2007\"},{\"n\":\"2006\",\"v\":\"2006\"},{\"n\":\"2005\",\"v\":\"2005\"},{\"n\":\"2004\",\"v\":\"2004\"},{\"n\":\"2003\",\"v\":\"2003\"},{\"n\":\"2002\",\"v\":\"2002\"},{\"n\":\"2001\",\"v\":\"2001\"},{\"n\":\"2000\",\"v\":\"2000\"}]},{\"key\":\"lang\",\"name\":\"语言\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"国语\",\"v\":\"国语\"},{\"n\":\"英语\",\"v\":\"英语\"},{\"n\":\"粤语\",\"v\":\"粤语\"},{\"n\":\"西语\",\"v\":\"西语\"},{\"n\":\"韩语\",\"v\":\"韩语\"},{\"n\":\"日语\",\"v\":\"日语\"},{\"n\":\"法语\",\"v\":\"法语\"},{\"n\":\"俄语\",\"v\":\"俄语\"},{\"n\":\"意大利语\",\"v\":\"意大利语\"},{\"n\":\"德语\",\"v\":\"德语\"},{\"n\":\"印地语\",\"v\":\"印地语\"},{\"n\":\"其它\",\"v\":\"其它\"}]},{\"key\":\"letter\",\"name\":\"字母\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"A\",\"v\":\"A\"},{\"n\":\"B\",\"v\":\"B\"},{\"n\":\"C\",\"v\":\"C\"},{\"n\":\"D\",\"v\":\"D\"},{\"n\":\"E\",\"v\":\"E\"},{\"n\":\"F\",\"v\":\"F\"},{\"n\":\"G\",\"v\":\"G\"},{\"n\":\"H\",\"v\":\"H\"},{\"n\":\"I\",\"v\":\"I\"},{\"n\":\"J\",\"v\":\"J\"},{\"n\":\"K\",\"v\":\"K\"},{\"n\":\"L\",\"v\":\"L\"},{\"n\":\"M\",\"v\":\"M\"},{\"n\":\"N\",\"v\":\"N\"},{\"n\":\"O\",\"v\":\"O\"},{\"n\":\"P\",\"v\":\"P\"},{\"n\":\"Q\",\"v\":\"Q\"},{\"n\":\"R\",\"v\":\"R\"},{\"n\":\"S\",\"v\":\"S\"},{\"n\":\"T\",\"v\":\"T\"},{\"n\":\"U\",\"v\":\"U\"},{\"n\":\"V\",\"v\":\"V\"},{\"n\":\"W\",\"v\":\"W\"},{\"n\":\"X\",\"v\":\"X\"},{\"n\":\"Y\",\"v\":\"Y\"},{\"n\":\"Z\",\"v\":\"Z\"}]},{\"key\":\"ifvip\",\"name\":\"权限\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"免费\",\"v\":\"2\"},{\"n\":\"会员\",\"v\":\"3\"}]}],\"4\":[{\"key\":\"tid\",\"name\":\"分类\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"日本\",\"v\":\"401\"},{\"n\":\"国产\",\"v\":\"402\"},{\"n\":\"欧美\",\"v\":\"403\"}]},{\"key\":\"area\",\"name\":\"地区\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"大陆\",\"v\":\"大陆\"},{\"n\":\"香港\",\"v\":\"香港\"},{\"n\":\"台湾\",\"v\":\"台湾\"},{\"n\":\"美国\",\"v\":\"美国\"},{\"n\":\"韩国\",\"v\":\"韩国\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"印度\",\"v\":\"印度\"},{\"n\":\"英国\",\"v\":\"英国\"},{\"n\":\"法国\",\"v\":\"法国\"},{\"n\":\"加拿大\",\"v\":\"加拿大\"},{\"n\":\"西班牙\",\"v\":\"西班牙\"},{\"n\":\"德国\",\"v\":\"德国\"},{\"n\":\"俄罗斯\",\"v\":\"俄罗斯\"},{\"n\":\"意大利\",\"v\":\"意大利\"},{\"n\":\"泰国\",\"v\":\"泰国\"},{\"n\":\"新加坡\",\"v\":\"新加坡\"},{\"n\":\"马来西亚\",\"v\":\"马来西亚\"},{\"n\":\"其它\",\"v\":\"其它\"}]},{\"key\":\"year\",\"name\":\"年份\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2015\",\"v\":\"2015\"},{\"n\":\"2014\",\"v\":\"2014\"},{\"n\":\"2013\",\"v\":\"2013\"},{\"n\":\"2012\",\"v\":\"2012\"},{\"n\":\"2011\",\"v\":\"2011\"},{\"n\":\"2010\",\"v\":\"2010\"},{\"n\":\"2009\",\"v\":\"2009\"},{\"n\":\"2008\",\"v\":\"2008\"},{\"n\":\"2007\",\"v\":\"2007\"},{\"n\":\"2006\",\"v\":\"2006\"},{\"n\":\"2005\",\"v\":\"2005\"},{\"n\":\"2004\",\"v\":\"2004\"},{\"n\":\"2003\",\"v\":\"2003\"},{\"n\":\"2002\",\"v\":\"2002\"},{\"n\":\"2001\",\"v\":\"2001\"},{\"n\":\"2000\",\"v\":\"2000\"}]},{\"key\":\"lang\",\"name\":\"语言\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"国语\",\"v\":\"国语\"},{\"n\":\"英语\",\"v\":\"英语\"},{\"n\":\"粤语\",\"v\":\"粤语\"},{\"n\":\"西语\",\"v\":\"西语\"},{\"n\":\"韩语\",\"v\":\"韩语\"},{\"n\":\"日语\",\"v\":\"日语\"},{\"n\":\"法语\",\"v\":\"法语\"},{\"n\":\"俄语\",\"v\":\"俄语\"},{\"n\":\"意大利语\",\"v\":\"意大利语\"},{\"n\":\"德语\",\"v\":\"德语\"},{\"n\":\"印地语\",\"v\":\"印地语\"},{\"n\":\"其它\",\"v\":\"其它\"}]},{\"key\":\"letter\",\"name\":\"字母\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"A\",\"v\":\"A\"},{\"n\":\"B\",\"v\":\"B\"},{\"n\":\"C\",\"v\":\"C\"},{\"n\":\"D\",\"v\":\"D\"},{\"n\":\"E\",\"v\":\"E\"},{\"n\":\"F\",\"v\":\"F\"},{\"n\":\"G\",\"v\":\"G\"},{\"n\":\"H\",\"v\":\"H\"},{\"n\":\"I\",\"v\":\"I\"},{\"n\":\"J\",\"v\":\"J\"},{\"n\":\"K\",\"v\":\"K\"},{\"n\":\"L\",\"v\":\"L\"},{\"n\":\"M\",\"v\":\"M\"},{\"n\":\"N\",\"v\":\"N\"},{\"n\":\"O\",\"v\":\"O\"},{\"n\":\"P\",\"v\":\"P\"},{\"n\":\"Q\",\"v\":\"Q\"},{\"n\":\"R\",\"v\":\"R\"},{\"n\":\"S\",\"v\":\"S\"},{\"n\":\"T\",\"v\":\"T\"},{\"n\":\"U\",\"v\":\"U\"},{\"n\":\"V\",\"v\":\"V\"},{\"n\":\"W\",\"v\":\"W\"},{\"n\":\"X\",\"v\":\"X\"},{\"n\":\"Y\",\"v\":\"Y\"},{\"n\":\"Z\",\"v\":\"Z\"}]},{\"key\":\"ifvip\",\"name\":\"权限\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"免费\",\"v\":\"2\"},{\"n\":\"会员\",\"v\":\"3\"}]}],\"5\":[{\"key\":\"tid\",\"name\":\"分类\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"AV女优\",\"v\":\"505\"},{\"n\":\"日韩\",\"v\":\"502\"},{\"n\":\"欧美\",\"v\":\"501\"},{\"n\":\"动漫\",\"v\":\"503\"},{\"n\":\"国产\",\"v\":\"504\"}]},{\"key\":\"year\",\"name\":\"年份\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2015\",\"v\":\"2015\"},{\"n\":\"2014\",\"v\":\"2014\"},{\"n\":\"2013\",\"v\":\"2013\"},{\"n\":\"2012\",\"v\":\"2012\"},{\"n\":\"2011\",\"v\":\"2011\"},{\"n\":\"2010\",\"v\":\"2010\"},{\"n\":\"2009\",\"v\":\"2009\"},{\"n\":\"2008\",\"v\":\"2008\"},{\"n\":\"2007\",\"v\":\"2007\"},{\"n\":\"2006\",\"v\":\"2006\"},{\"n\":\"2005\",\"v\":\"2005\"},{\"n\":\"2004\",\"v\":\"2004\"},{\"n\":\"2003\",\"v\":\"2003\"},{\"n\":\"2002\",\"v\":\"2002\"},{\"n\":\"2001\",\"v\":\"2001\"},{\"n\":\"2000\",\"v\":\"2000\"}]},{\"key\":\"letter\",\"name\":\"字母\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"A\",\"v\":\"A\"},{\"n\":\"B\",\"v\":\"B\"},{\"n\":\"C\",\"v\":\"C\"},{\"n\":\"D\",\"v\":\"D\"},{\"n\":\"E\",\"v\":\"E\"},{\"n\":\"F\",\"v\":\"F\"},{\"n\":\"G\",\"v\":\"G\"},{\"n\":\"H\",\"v\":\"H\"},{\"n\":\"I\",\"v\":\"I\"},{\"n\":\"J\",\"v\":\"J\"},{\"n\":\"K\",\"v\":\"K\"},{\"n\":\"L\",\"v\":\"L\"},{\"n\":\"M\",\"v\":\"M\"},{\"n\":\"N\",\"v\":\"N\"},{\"n\":\"O\",\"v\":\"O\"},{\"n\":\"P\",\"v\":\"P\"},{\"n\":\"Q\",\"v\":\"Q\"},{\"n\":\"R\",\"v\":\"R\"},{\"n\":\"S\",\"v\":\"S\"},{\"n\":\"T\",\"v\":\"T\"},{\"n\":\"U\",\"v\":\"U\"},{\"n\":\"V\",\"v\":\"V\"},{\"n\":\"W\",\"v\":\"W\"},{\"n\":\"X\",\"v\":\"X\"},{\"n\":\"Y\",\"v\":\"Y\"},{\"n\":\"Z\",\"v\":\"Z\"}]},{\"key\":\"ifvip\",\"name\":\"权限\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"免费\",\"v\":\"2\"},{\"n\":\"会员\",\"v\":\"3\"}]}]}");
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
            Elements elements = doc.select("ul.top_nav li a");
            JSONArray classes = new JSONArray();
            for (Element ele : elements) {
                String name = ele.text();
                boolean show = name.equals("电影") ||
                        name.equals("连续剧") ||
                        name.equals("动漫") ||
                        name.equals("综艺") ||
                        name.equals("午夜影院");

                if (name.equals("午夜影院") && !pron) {
                    continue;
                }
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
                Element homeList = doc.select("div.cbox1").get(0);
                Elements list = homeList.select("ul.vodlist li.vodlist_item");
                JSONArray videos = new JSONArray();
                for (int i = 0; i < list.size(); i++) {
                    Element vod = list.get(i);
                    String title = vod.selectFirst("a.vodlist_thumb").attr("title");
                    String cover = siteUrl + vod.selectFirst("a.vodlist_thumb").attr("data-original");
                    String remark = "";
                    try {
                        remark = vod.select("span.pic_text em").text();
                    } catch (Exception e) {
                        remark = vod.select("span.text_dy").get(0).text();
                    }
                    Matcher matcher = regexVid.matcher(vod.selectFirst("a.vodlist_thumb").attr("href"));
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
            Elements pageInfo = doc.select("ul.page");
            if (pageInfo.size() == 0) {
                page = Integer.parseInt(pg);
                pageCount = page;
            } else {
                for (int i = 0; i < pageInfo.size(); i++) {
                    Element li = pageInfo.get(i);
                    Element a = li.selectFirst("li a");
                    if (a == null)
                        continue;
                    String span = pageInfo.select("li.active a").text();
                    String wy = doc.select("ul.page li a").last().attr("href");
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
                Elements list = doc.select("ul.vodlist li.vodlist_item");
                for (int i = 0; i < list.size(); i++) {
                    Element vod = list.get(i);
                    String title = vod.selectFirst("a.vodlist_thumb").attr("title");
                    String cover = siteUrl + vod.selectFirst("a.vodlist_thumb").attr("data-original");
                    String remark = "";
                    try {
                        remark = vod.select("span.pic_text em").text();
                    } catch (Exception e) {
                        remark = vod.select("span.text_dy").get(0).text();
                    }
                    Matcher matcher = regexVid.matcher(vod.selectFirst("a.vodlist_thumb").attr("href"));
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
            result.put("limit", 20);
            result.put("total", pageCount <= 1 ? videos.length() : pageCount * 20);

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

            String cover = siteUrl + doc.selectFirst("div.content_thumb a").attr("data-original");
            String title = doc.selectFirst("div.content_thumb a").attr("title");
            String desc = doc.selectFirst("div.context span").text();
            String category = "", area = "", year = "", director = "", actor = "", remark = "";
            remark = doc.select("span.data_style").text();
            Elements span_text_muted = doc.select("li.data span");
            for (int i = 0; i < span_text_muted.size() - 2; i++) {
                Element text = span_text_muted.get(i);
                String info = text.text();
                if (info.contains("类型")) {
                    try {
                        category = text.parent().select("a").get(2).text();
                    } catch (Exception e) {
                        category = "";
                    }
                } else if (info.contains("年份")) {
                    try {
                        year = text.parent().select("a").get(0).text();
                    } catch (Exception e) {
                        year = "";
                    }
                } else if (info.contains("地区")) {
                    try {
                        area = text.parent().select("a").get(1).text();
                    } catch (Exception e) {
                        area = "";
                    }
                }
            }
            List<String> actors = new ArrayList<>();
            Elements aa = doc.select("li.data").get(3).select("a");
            for (int j = 0; j < aa.size(); j++) {
                actors.add(aa.get(j).text());
            }
            actor = TextUtils.join(",", actors);
            List<String> directors = new ArrayList<>();
            Elements bb = doc.select("li.data").get(2).select("a");
            for (int j = 0; j < bb.size(); j++) {
                directors.add(bb.get(j).text());
            }
            director = TextUtils.join(",", directors);


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
            Elements sources = doc.select("div.play_source_tab a");
            Elements sourceList = doc.select("div.playlist_full");

            for (int i = 0; i < sources.size(); i++) {
                Element source = sources.get(i);
                String sourceName = source.attr("alt");
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
