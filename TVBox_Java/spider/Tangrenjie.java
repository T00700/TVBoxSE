package com.github.catvod.spider;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.Switch;

import java.net.URLDecoder;

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

public class Tangrenjie extends Spider {
    private static final String siteUrl = "https://www.tangrenjie.tv";
    private static final String siteHost = "tangrenjie.tv";
    /**
     * 播放源配置
     */
    private JSONObject playerConfig;
    /**
     * 筛选配置
     */
    private JSONObject filterConfig;
    private Pattern regexCategory = Pattern.compile("/vod/type/id/(\\d+).html");
    private Pattern regexVid = Pattern.compile("/vod/detail/id/(\\d+).html");
    private Pattern regexPlay = Pattern.compile("/vod/play/id/(\\d+)/sid/(\\d+)/nid/(\\d+).html");
    private Pattern regexPage = Pattern.compile("\\S+/page/(\\d+)\\S+");

    @Override
    public void init(Context context) {
        super.init(context);
        try {
            playerConfig = new JSONObject("{\"if101\":{\"show\":\"蓝光一\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"\",\"or\":999}}");
            filterConfig = new JSONObject("{\"1\":[{\"key\":\"cateId\",\"name\":\"分类\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"动作片\",\"v\":\"6\"},{\"n\":\"喜剧片\",\"v\":\"7\"},{\"n\":\"爱情片\",\"v\":\"8\"},{\"n\":\"科幻片\",\"v\":\"9\"},{\"n\":\"恐怖片\",\"v\":\"10\"},{\"n\":\"剧情片\",\"v\":\"11\"},{\"n\":\"战争片\",\"v\":\"12\"}]},{\"key\":\"area\",\"name\":\"地区\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"大陆\",\"v\":\"大陆\"},{\"n\":\"香港\",\"v\":\"香港\"},{\"n\":\"台湾\",\"v\":\"台湾\"},{\"n\":\"美国\",\"v\":\"美国\"},{\"n\":\"法国\",\"v\":\"法国\"},{\"n\":\"英国\",\"v\":\"英国\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"韩国\",\"v\":\"韩国\"},{\"n\":\"德国\",\"v\":\"德国\"},{\"n\":\"泰国\",\"v\":\"泰国\"},{\"n\":\"印度\",\"v\":\"印度\"},{\"n\":\"其他\",\"v\":\"其他\"}]},{\"key\":\"year\",\"name\":\"年份\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2008\",\"v\":\"2008\"},{\"n\":\"2000\",\"v\":\"2000\"},{\"n\":\"1997\",\"v\":\"1997\"},{\"n\":\"1980\",\"v\":\"1980\"}]},{\"key\":\"by\",\"name\":\"排序\",\"value\":[{\"n\":\"时间\",\"v\":\"time\"},{\"n\":\"人气\",\"v\":\"hits\"},{\"n\":\"评分\",\"v\":\"score\"}]}],\"2\":[{\"key\":\"cateId\",\"name\":\"分类\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"国产剧\",\"v\":\"13\"},{\"n\":\"港台剧\",\"v\":\"14\"},{\"n\":\"日韩剧\",\"v\":\"15\"},{\"n\":\"欧美剧\",\"v\":\"16\"},{\"n\":\"海外剧\",\"v\":\"27\"}]},{\"key\":\"area\",\"name\":\"地区\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"大陆\",\"v\":\"大陆\"},{\"n\":\"香港\",\"v\":\"香港\"},{\"n\":\"台湾\",\"v\":\"台湾\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"欧美\",\"v\":\"欧美\"},{\"n\":\"韩国\",\"v\":\"韩国\"}]},{\"key\":\"year\",\"name\":\"年份\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2008\",\"v\":\"2008\"},{\"n\":\"2000\",\"v\":\"2000\"},{\"n\":\"1997\",\"v\":\"1997\"},{\"n\":\"1980\",\"v\":\"1980\"}]},{\"key\":\"by\",\"name\":\"排序\",\"value\":[{\"n\":\"时间\",\"v\":\"time\"},{\"n\":\"人气\",\"v\":\"hits\"},{\"n\":\"评分\",\"v\":\"score\"}]}],\"4\":[{\"key\":\"area\",\"name\":\"地区\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"大陆\",\"v\":\"大陆\"},{\"n\":\"香港\",\"v\":\"香港\"},{\"n\":\"台湾\",\"v\":\"台湾\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"欧美\",\"v\":\"欧美\"},{\"n\":\"韩国\",\"v\":\"韩国\"}]},{\"key\":\"year\",\"name\":\"年份\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2008\",\"v\":\"2008\"},{\"n\":\"2000\",\"v\":\"2000\"},{\"n\":\"1997\",\"v\":\"1997\"},{\"n\":\"1980\",\"v\":\"1980\"}]},{\"key\":\"by\",\"name\":\"排序\",\"value\":[{\"n\":\"时间\",\"v\":\"time\"},{\"n\":\"人气\",\"v\":\"hits\"},{\"n\":\"评分\",\"v\":\"score\"}]}],\"3\":[{\"key\":\"cateId\",\"name\":\"分类\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"纪录片\",\"v\":\"28\"}]},{\"key\":\"area\",\"name\":\"地区\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"大陆\",\"v\":\"大陆\"},{\"n\":\"香港\",\"v\":\"香港\"},{\"n\":\"台湾\",\"v\":\"台湾\"},{\"n\":\"美国\",\"v\":\"美国\"},{\"n\":\"法国\",\"v\":\"法国\"},{\"n\":\"英国\",\"v\":\"英国\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"韩国\",\"v\":\"韩国\"},{\"n\":\"德国\",\"v\":\"德国\"},{\"n\":\"泰国\",\"v\":\"泰国\"},{\"n\":\"印度\",\"v\":\"印度\"},{\"n\":\"其他\",\"v\":\"其他\"}]},{\"key\":\"year\",\"name\":\"年份\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2008\",\"v\":\"2008\"},{\"n\":\"2000\",\"v\":\"2000\"},{\"n\":\"1997\",\"v\":\"1997\"},{\"n\":\"1980\",\"v\":\"1980\"}]},{\"key\":\"by\",\"name\":\"排序\",\"value\":[{\"n\":\"时间\",\"v\":\"time\"},{\"n\":\"人气\",\"v\":\"hits\"},{\"n\":\"评分\",\"v\":\"score\"}]}]}");
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
                Elements b = ele.select("b");
                String name = b.text();
                boolean show = name.equals("电影") ||
                        name.equals("电视剧") ||
                        name.equals("动漫") ||
                        name.equals("综艺") ;

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
                Element homeList = doc.select("div.cbox1").get(1);
                Elements list = homeList.select("ul.vodlist li.vodlist_item");
                JSONArray videos = new JSONArray();
                for (int i = 0; i < list.size(); i++) {
                    Element vod = list.get(i);
                    String title = vod.selectFirst("a.vodlist_thumb").attr("title");
                    String cover = siteUrl + vod.selectFirst("a.vodlist_thumb").attr("data-original");
                    String remark = vod.select("span.pic_text b").text();

                    Matcher matcher = regexVid.matcher(vod.selectFirst("a.vodlist_thumb").attr("href"));
                    if (!matcher.find())
                        continue;
                    String id = "1="+ matcher.group(1);
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
            String url = siteUrl + "/vod/show/id/";
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
                    String remark = vod.select("span.pic_text b").text();
                    Matcher matcher = regexVid.matcher(vod.selectFirst("a.vodlist_thumb").attr("href"));
                    if (!matcher.find())
                        continue;
                    String id = tid + "=" + matcher.group(1);
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
            String[] idd = ids.get(0).split("=");
            String tid =idd[0];
            String detail = idd[1];
            String url = siteUrl + "/vod/detail/id/" + detail + ".html";
            String htmltemp = OkHttpUtil.string(url, getHeaders(url));
            String htmlsrc ="";
            if(tid.equals("1")){
                htmlsrc = htmltemp.substring(0,25000);
            }else{
                htmlsrc = htmltemp;
            }
            Document doc = Jsoup.parse(htmlsrc);
            JSONObject result = new JSONObject();
            JSONObject vodList = new JSONObject();
            // 取基本数据
            String vid = doc.selectFirst("div.content_thumb a").attr("href");
            String cover = siteUrl + doc.selectFirst("div.content_thumb a").attr("data-original");
            String title = doc.selectFirst("div.content_thumb a").attr("title");
            String desc = doc.selectFirst("div.content > section > font").text();
            String category = "", area = "", year = "", director = "", actor = "", remark = "";
            remark = doc.select("span.data_style b").text();
            Elements span_text_muted = doc.select("li.data span");
            for (int i = 0; i < span_text_muted.size() - 2; i++) {
                Element text = span_text_muted.get(i);
                String info = text.text();
                if (info.contains("类型")) {
                    try {
                        String cate = text.parent().text();
                        category =cate.substring(3);
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
                        area = text.parent().select("a").get(0).text();
                    } catch (Exception e) {
                        area = "";
                    }
                }
            }
            List<String> actors = new ArrayList<>();
            Elements aa = doc.select("li.data").get(0).select("a");
            for (int j = 0; j < aa.size(); j++) {
                actors.add(aa.get(j).text());
            }
            actor = TextUtils.join(",", actors);
            List<String> directors = new ArrayList<>();
            Elements bb = doc.select("li.data").get(1).select("a");
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
                    if (playerConfig.getJSONObject(flag).getString("show").equals(sourceName)) {
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
                    vodItems.add( vod.text() + "$" + playURL + "="+ tid);
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
            String[] idd = id.split("=");
            String tid =idd[1];
            String detail = idd[0];
            String url = siteUrl + "/vod/play/id/" + detail + ".html";
            String htmltemp = OkHttpUtil.string(url, getHeaders(url));
            String htmlsrc ="";
            if(tid.equals("1")){
                htmlsrc = htmltemp.substring(0,25000);
            }else{
                htmlsrc = htmltemp;
            }
            Document doc = Jsoup.parse(htmlsrc);
            Elements allScript = doc.select("script");
            JSONObject result = new JSONObject();
            for (int i = 0; i < allScript.size(); i++) {
                String scContent = allScript.get(i).toString();
                if (scContent.contains("var player_")) { // 取直链
                    int start = scContent.indexOf('{');
                    int end = scContent.lastIndexOf('}') + 1;
                    String json = scContent.substring(start, end);
                    JSONObject player = new JSONObject(json);
                    String urlsrc = player.getString("url");
                    String urlb64 = new String(Base64.decode(urlsrc.getBytes(), Base64.DEFAULT));
                    String urldec = URLDecoder.decode(urlb64,"UTF-8");
                    result.put("parse", 0);
                    result.put("playUrl", "");
                    result.put("url", urldec);
                    result.put("header", "");
                    break;
                    }

                }
            //无直链则嗅探
            String uu = result.getString("url");
            if (!(uu.length() >0)){
               result.put("parse", 1);
               result.put("url", url);
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
            String url = siteUrl + "/vod/search.html?wd=" + URLEncoder.encode(key) + "&submit=";
            String html = OkHttpUtil.string(url, getHeaders(url));
            Document doc = Jsoup.parse(html);
            JSONObject result = new JSONObject();
            JSONArray videos = new JSONArray();
            if (!html.contains("没有找到您想要的结果哦")) {
                Elements list = doc.select("li.searchlist_item");
                for (int i = 0; i < list.size(); i++) {
                    Element vod = list.get(i);
                    String classes = vod.selectFirst("h4.vodlist_title > a >span").text();
                    String tid="";
                    switch (classes) {
                        case "电影":
                            tid = "1";
                            break;
                        case "电视剧":
                            tid = "2";
                            break;
                        case "综艺":
                            tid = "3";
                            break;
                        case "动漫":
                            tid = "4";
                            break;
                    }
                    String title = vod.selectFirst("a.vodlist_thumb").attr("title");
                    String cover = siteUrl + vod.selectFirst("a.vodlist_thumb").attr("data-original");
                    String remark = vod.select("span.pic_text").text();
                    Matcher matcher = regexVid.matcher(vod.selectFirst("a.vodlist_thumb").attr("href"));
                    if (!matcher.find())
                        continue;
                    String id = tid + "=" + matcher.group(1);
                    JSONObject v = new JSONObject();
                    v.put("vod_id", id);
                    v.put("vod_name", title);
                    v.put("vod_pic", cover);
                    v.put("vod_remarks", remark);
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

    @Override
    public boolean manualVideoCheck() {
        return true;
    }

    private String[] videoFormatList = new String[]{".m3u8", ".mp4", ".mpeg", ".flv"};

    @Override
    public boolean isVideoFormat(String url) {
        url = url.toLowerCase();
        if (url.contains("=http") || url.contains("=https") || url.contains("=https%3a%2f") || url.contains("=http%3a%2f")) {
            return false;
        }
        for (String format : videoFormatList) {
            if (url.contains(format)) {
                return true;
            }
        }
        return false;
    }

}
