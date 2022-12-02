package com.github.catvod.spider;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;

import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.utils.AES;
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

import static com.github.catvod.utils.AES.AES_CBC_PKCS7Padding;
import static com.github.catvod.utils.AES.decryptToString;

/**
 * 双十电影
 * <p>
 * Author: 忍一忍我很大
 */
public class Dy1010 extends Spider {
    private static final String siteUrl = "http://www.1010dy1.com";
    private static final String siteHost = "www.1010dy1.com";
    /**
     * 播放源配置
     */
    private JSONObject playerConfig;
    /**
     * 筛选配置
     */
    private JSONObject filterConfig;

    private Pattern regexCategory = Pattern.compile("/type/(\\w+)/");
    private Pattern regexVid = Pattern.compile("/detail/(\\w+)/");
    private Pattern regexPlay = Pattern.compile("/play/(\\w+)-(\\d+)-(\\d+)/");
    private Pattern regexPage = Pattern.compile("/show/\\d+/page/(\\d+)/");

    @Override
    public void init(Context context) {
        super.init(context);
        try {
            playerConfig = new JSONObject("{\"xg_app_player\":{\"show\":\"app全局解析\",\"or\":999,\"ps\":\"1\",\"parse\":\"\"},\"duoduozy\":{\"show\":\"多多云\",\"or\":999,\"ps\":\"0\",\"parse\":\"\"},\"renrenmi\":{\"show\":\"快速云\",\"or\":999,\"ps\":\"0\",\"parse\":\"\"},\"xigua\":{\"show\":\"西瓜云\",\"or\":999,\"ps\":\"0\",\"parse\":\"\"},\"mgtv\":{\"show\":\"芒果云\",\"or\":999,\"ps\":\"0\",\"parse\":\"\"},\"qq\":{\"show\":\"腾讯云\",\"or\":999,\"ps\":\"0\",\"parse\":\"\"},\"youku\":{\"show\":\"优酷云\",\"or\":999,\"ps\":\"0\",\"parse\":\"\"},\"qiyi\":{\"show\":\"爱奇艺\",\"or\":999,\"ps\":\"0\",\"parse\":\"\"},\"pptv\":{\"show\":\"PPTV\",\"or\":999,\"ps\":\"0\",\"parse\":\"\"},\"letv\":{\"show\":\"乐视云\",\"or\":999,\"ps\":\"0\",\"parse\":\"\"},\"sohu\":{\"show\":\"搜狐云\",\"or\":999,\"ps\":\"0\",\"parse\":\"\"},\"bilibili\":{\"show\":\"哔哩云\",\"or\":999,\"ps\":\"0\",\"parse\":\"\"},\"dbm3u8\":{\"show\":\"百度云\",\"des\":\"在线播放\",\"ps\":\"0\",\"parse\":\"\"},\"tkm3u8\":{\"show\":\"天空云\",\"des\":\"天空播放器\",\"ps\":\"0\",\"parse\":\"\"},\"dplayer\":{\"show\":\"DPlayer-H5播放器\",\"des\":\"dplayer.js.org\",\"ps\":\"0\",\"parse\":\"\"},\"videojs\":{\"show\":\"videojs-H5播放器\",\"des\":\"videojs.com\",\"ps\":\"0\",\"parse\":\"\"},\"iva\":{\"show\":\"iva-H5播放器\",\"des\":\"videojj.com\",\"ps\":\"0\",\"parse\":\"\"},\"iframe\":{\"show\":\"iframe外链数据\",\"des\":\"iframe外链数据\",\"ps\":\"0\",\"parse\":\"\"},\"link\":{\"show\":\"外链数据\",\"des\":\"外部网站播放链接\",\"ps\":\"0\",\"parse\":\"\"},\"swf\":{\"show\":\"Flash文件\",\"des\":\"swf\",\"ps\":\"0\",\"parse\":\"\"},\"flv\":{\"show\":\"Flv文件\",\"des\":\"flv\",\"ps\":\"0\",\"parse\":\"\"},\"bdxm3u8\":{\"show\":\"北斗云\",\"des\":\"在线播放\",\"ps\":\"0\",\"parse\":\"\"},\"lym3u8\":{\"show\":\"老鸭资源\",\"des\":\"在线播放\",\"ps\":\"0\",\"parse\":\"\"},\"aliplayer\":{\"show\":\"阿里云播放器\",\"or\":999,\"ps\":\"0\",\"parse\":\"\"},\"hnm3u8\":{\"show\":\"红牛云\",\"or\":999,\"ps\":\"0\",\"parse\":\"\"},\"kbm3u8\":{\"show\":\"快播云\",\"or\":999,\"ps\":\"0\",\"parse\":\"\"},\"wjm3u8\":{\"show\":\"无尽备用\",\"or\":999,\"ps\":\"0\",\"parse\":\"\"}}");
            filterConfig = new JSONObject("{\"1\":[{\"key\":\"class\",\"name\":\"类型\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"喜剧\",\"v\":\"喜剧\"},{\"n\":\"爱情\",\"v\":\"爱情\"},{\"n\":\"恐怖\",\"v\":\"恐怖\"},{\"n\":\"动作\",\"v\":\"动作\"},{\"n\":\"科幻\",\"v\":\"科幻\"},{\"n\":\"剧情\",\"v\":\"剧情\"},{\"n\":\"战争\",\"v\":\"战争\"},{\"n\":\"犯罪\",\"v\":\"犯罪\"},{\"n\":\"灾难\",\"v\":\"灾难\"},{\"n\":\"奇幻\",\"v\":\"奇幻\"},{\"n\":\"悬疑\",\"v\":\"悬疑\"},{\"n\":\"惊悚\",\"v\":\"惊悚\"},{\"n\":\"冒险\",\"v\":\"冒险\"}]},{\"key\":\"area\",\"name\":\"地区\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"大陆\",\"v\":\"大陆\"},{\"n\":\"韩国\",\"v\":\"韩国\"},{\"n\":\"香港\",\"v\":\"香港\"},{\"n\":\"台湾\",\"v\":\"台湾\"},{\"n\":\"英国\",\"v\":\"英国\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"美国\",\"v\":\"美国\"},{\"n\":\"泰国\",\"v\":\"泰国\"},{\"n\":\"法国\",\"v\":\"法国\"},{\"n\":\"德国\",\"v\":\"德国\"},{\"n\":\"印度\",\"v\":\"印度\"},{\"n\":\"其他\",\"v\":\"其他\"}]},{\"key\":\"year\",\"name\":\"年份\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2015\",\"v\":\"2015\"},{\"n\":\"2014\",\"v\":\"2014\"},{\"n\":\"2013\",\"v\":\"2013\"},{\"n\":\"2012\",\"v\":\"2012\"},{\"n\":\"2011\",\"v\":\"2011\"},{\"n\":\"2010\",\"v\":\"2010\"},{\"n\":\"2009\",\"v\":\"2009\"},{\"n\":\"2008\",\"v\":\"2008\"},{\"n\":\"2007\",\"v\":\"2007\"},{\"n\":\"2006\",\"v\":\"2006\"},{\"n\":\"2000\",\"v\":\"2000\"},{\"n\":\"1997\",\"v\":\"1997\"}]},{\"key\":\"by\",\"name\":\"排序\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"时间\",\"v\":\"time\"},{\"n\":\"人气\",\"v\":\"hits\"},{\"n\":\"评分\",\"v\":\"score\"}]}],\"2\":[{\"key\":\"class\",\"name\":\"类型\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"国产\",\"v\":\"国产\"},{\"n\":\"港台\",\"v\":\"港台\"},{\"n\":\"日韩\",\"v\":\"日韩\"},{\"n\":\"欧美\",\"v\":\"欧美\"},{\"n\":\"海外\",\"v\":\"海外\"},{\"n\":\"古装\",\"v\":\"古装\"},{\"n\":\"战争\",\"v\":\"战争\"},{\"n\":\"偶像\",\"v\":\"偶像\"},{\"n\":\"犯罪\",\"v\":\"犯罪\"},{\"n\":\"奇幻\",\"v\":\"奇幻\"},{\"n\":\"剧情\",\"v\":\"剧情\"},{\"n\":\"历史\",\"v\":\"历史\"},{\"n\":\"网剧\",\"v\":\"网剧\"}]},{\"key\":\"area\",\"name\":\"地区\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"大陆\",\"v\":\"大陆\"},{\"n\":\"韩国\",\"v\":\"韩国\"},{\"n\":\"香港\",\"v\":\"香港\"},{\"n\":\"台湾\",\"v\":\"台湾\"},{\"n\":\"英国\",\"v\":\"英国\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"美国\",\"v\":\"美国\"},{\"n\":\"泰国\",\"v\":\"泰国\"},{\"n\":\"新加坡\",\"v\":\"新加坡\"},{\"n\":\"其他\",\"v\":\"其他\"}]},{\"key\":\"year\",\"name\":\"年份\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2015\",\"v\":\"2015\"},{\"n\":\"2014\",\"v\":\"2014\"},{\"n\":\"2013\",\"v\":\"2013\"},{\"n\":\"2012\",\"v\":\"2012\"},{\"n\":\"2011\",\"v\":\"2011\"},{\"n\":\"2010\",\"v\":\"2010\"},{\"n\":\"2009\",\"v\":\"2009\"},{\"n\":\"2008\",\"v\":\"2008\"},{\"n\":\"2007\",\"v\":\"2007\"},{\"n\":\"2006\",\"v\":\"2006\"},{\"n\":\"1997\",\"v\":\"1997\"},{\"n\":\"1980\",\"v\":\"1980\"}]},{\"key\":\"by\",\"name\":\"排序\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"时间\",\"v\":\"time\"},{\"n\":\"人气\",\"v\":\"hits\"},{\"n\":\"评分\",\"v\":\"score\"}]}]}");
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
        headers.put("Referer", "http://www.1010dy1.com/");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.71 Safari/537.36 Core/1.94.169.400 QQBrowser/11.0.5130.400");
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
            Elements elements = doc.select("ul.stui-header__menu > li > a");
            JSONArray classes = new JSONArray();
            for (Element ele : elements) {
                String name = ele.text();
                Matcher mather = regexCategory.matcher(ele.attr("href"));
                if (!mather.find())
                    continue;
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
                Elements list = doc.select("div.stui-pannel_bd:nth-child(1) ul.stui-vodlist li div.stui-vodlist__box");
                JSONArray videos = new JSONArray();
                for (int i = 0; i < list.size(); i++) {
                    Element vod = list.get(i);
                    Matcher matcher = regexVid.matcher(vod.selectFirst(".stui-vodlist__thumb").attr("href"));
                    if (!matcher.find())
                        continue;
                    String title = vod.selectFirst(".stui-vodlist__thumb").attr("title");
                    String cover = vod.selectFirst(".stui-vodlist__thumb").attr("data-original");
                    String remark = vod.selectFirst("span.pic-text").text();
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
            String url = siteUrl + "/show/";
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
            url += "/page/" + pg + "/";
            // 获取分类数据的url
            //String url = siteUrl + "/show/" + tid + "/page/" + pg + "/";
            String html = OkHttpUtil.string(url, getHeaders(url));
            Document doc = Jsoup.parse(html);
            JSONObject result = new JSONObject();
            int pageCount = 0;
            int page = -1;
            // 取页码相关信息
            Elements pageInfo = doc.select(".stui-page li");
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
                    if (page == -1 && li.hasClass("active")) {
                        Matcher matcher = regexPage.matcher(a.attr("href"));
                        if (matcher.find()) {
                            page = Integer.parseInt(matcher.group(1));
                        } else {
                            page = 0;
                        }
                    }
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
                Elements list = doc.select("ul.stui-vodlist li div.stui-vodlist__box");
                for (int i = 0; i < list.size(); i++) {
                    Element vod = list.get(i);
                    String title = vod.selectFirst(".title").text();
                    String cover = vod.selectFirst(".stui-vodlist__thumb").attr("data-original");
                    String remark = vod.selectFirst("span.pic-text").text();
                    Matcher matcher = regexVid.matcher(vod.selectFirst(".stui-vodlist__thumb").attr("href"));
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
            result.put("limit", 30);
            result.put("total", pageCount <= 1 ? videos.length() : pageCount * 30);

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
            String url = siteUrl + "/detail/" + ids.get(0) + "/";
            Document doc = Jsoup.parse(OkHttpUtil.string(url, getHeaders(url)));
            JSONObject result = new JSONObject();
            JSONObject vodList = new JSONObject();

            // 取基本数据
            String cover = doc.selectFirst("a.stui-vodlist__thumb img").attr("data-original");
            String title = doc.selectFirst("a.stui-vodlist__thumb").attr("title");
            String desc = Jsoup.parse(doc.selectFirst("meta[name=description]").attr("content")).text();
            String category = "", area = "", year = "", remark = "", director = "", actor = "";
            Elements span_text_muted = doc.select("div.stui-content__detail span.text-muted");
            for (int i = 0; i < span_text_muted.size(); i++) {
                Element text = span_text_muted.get(i);
                String info = text.text();
                if (info.equals("类型：")) {
                    category = text.nextElementSibling().text();
                } else if (info.equals("年份：")) {
                    year = text.nextElementSibling().text();
                } else if (info.equals("地区：")) {
                    area = text.nextElementSibling().text();
                } else if (info.equals("更新：")) {
                    remark = text.nextSibling().outerHtml().trim();
                } else if (info.equals("导演：")) {
                    List<String> directors = new ArrayList<>();
                    Elements aa = text.parent().select("a");
                    for (int j = 0; j < aa.size(); j++) {
                        directors.add(aa.get(j).text());
                    }
                    director = TextUtils.join(",", directors);
                } else if (info.equals("主演：")) {
                    List<String> actors = new ArrayList<>();
                    Elements aa = text.parent().select("a");
                    for (int j = 0; j < aa.size(); j++) {
                        actors.add(aa.get(j).text());
                    }
                    actor = TextUtils.join(",", actors);
                }
            }

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

            Elements sources = doc.select("div.playlist");

            for (int i = 0; i < sources.size(); i++) {
                Element source = sources.get(i);
                String sourceName = source.selectFirst("h3.title").text().trim();
                String tabHref = source.attr("href");
                boolean found = false;
                for (Iterator<String> it = playerConfig.keys(); it.hasNext(); ) {
                    String flag = it.next();
                    if (playerConfig.getJSONObject(flag).getString("show").equals(sourceName)) {
                        sourceName = playerConfig.getJSONObject(flag).getString("show");
                        found = true;
                        break;
                    }
                }
                if (!found)
                    continue;
                String playList = "";
                Elements playListA = source.select("ul.stui-content__playlist > li > a");
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

    private final Pattern regexUrl = Pattern.compile("(?<=urls = \").+?(?=\";)");
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
            headers.put("Origin", " https://dp.1010dy.cc");
            //headers.put("Referer", " http://www.1010dy1.com/");
            //headers.put("Icy-MetaData", "1");
            //headers.put("scheme", " https");
            headers.put("User-Agent", " Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36");
            headers.put("Accept", " */*");
            //headers.put("Accept-Language", " zh-CN,zh;q=0.9,en-GB;q=0.8,en-US;q=0.7,en;q=0.6");
            //headers.put("Accept-Encoding", " gzip, deflate");
            headers.put("Connection", " close");
            // 播放页 url
            String url = siteUrl + "/play/" + id + "/";
            Document doc = Jsoup.parse(OkHttpUtil.string(url, getHeaders(url)));
            Elements allScript = doc.select("script");
            JSONObject result = new JSONObject();
            for (int i = 0; i < allScript.size(); i++) {
                String scContent = allScript.get(i).html().trim();
                if (scContent.startsWith("var player_")) {
                    int start = scContent.indexOf('{');
                    int end = scContent.lastIndexOf('}') + 1;
                    String json = scContent.substring(start, end);
                    JSONObject player = new JSONObject(json);
                    if (playerConfig.has(player.getString("from"))) {
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("User-Agent", " Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36");
                        hashMap.put("Referer", url);
                        String jxurl = "https://dp.1010dy.cc/?url=" + player.getString("url")+"&next="+player.getString("link_next")+"&id="+player.getString("id")+"&nid="+player.getString("nid")+"&from="+player.getString("from");
                        String doc1 = OkHttpUtil.string(jxurl, hashMap);
                        Matcher matcher = regexUrl.matcher(doc1);
                        if (!matcher.find()) {
                            return "";
                        }
                        String data = matcher.group(0);
                        byte[] decode = Base64.decode(data, Base64.DEFAULT);
                        String iv = "c487ebl2e38a0faO";
                        String key = "Of84ff0clf252cba";
                        String directUrl = decryptToString(decode, key.getBytes(), iv.getBytes(),AES_CBC_PKCS7Padding);
                        if (player.getString("from").equals("duoduozy")) {
                            result.put("parse", 0);
                            result.put("playUrl", "");
                            result.put("url", directUrl);
                            result.put("header", headers.toString());
                        } else {
                            result.put("parse", 0);
                            result.put("playUrl", "");
                            result.put("url", directUrl);
                            result.put("header", "");
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
