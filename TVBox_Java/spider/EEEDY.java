package com.github.catvod.spider;

import android.content.Context;
import android.text.TextUtils;
import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.utils.okhttp.OkHttpUtil;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class EEEDY extends Spider {

    private Pattern B = Pattern.compile("/vodtype/(\\d+).html");
    private Pattern Pd = Pattern.compile("/vodplay/(\\d+)-(\\d+)-(\\d+).html");
    private Pattern W = Pattern.compile("/voddetail/(\\d+).html");
    private JSONObject u;
    private Pattern u6 = Pattern.compile("/vodshow/(\\S+).html");
    private JSONObject ue;

    /* renamed from: com.github.catvod.spider.EEEDY$1  reason: invalid class name */
    class AnonymousClass1 implements Comparator<String> {
        public int compare(String str, String str2) {
            String $2 = "or";
            try {
                int i = EEEDY.this.ue.getJSONObject(str).getInt($2);
                int i2 = EEEDY.this.ue.getJSONObject(str2).getInt($2);
                if (i != i2 && i - i2 <= 0) {
                    return -1;
                }
                return 1;
            } catch (JSONException e) {
                SpiderDebug.log(e);
                return 1;
            }
        }
    }

    public String categoryContent(String str, String str2, boolean z, HashMap<String, String> hashMap) {
        int i;
        int i2;
        String $2 = "h4 > a";
        String $3 = "-";
        try {
            int i3 = 1;
            String[] strArr = {"", "", "", "", "", "", "", "", "", "", "", ""};
            strArr[0] = str;
            strArr[8] = str2;
            if (hashMap != null && hashMap.size() > 0) {
                for (String str3 : hashMap.keySet()) {
                    strArr[Integer.parseInt(str3)] = URLEncoder.encode(hashMap.get(str3));
                }
            }
            String str4 = "https://www.3edy.com/vodshow/" + TextUtils.join($3, strArr) + ".html";
            String h = OkHttpUtil.string(str4, u(str4));
            Document ue2 = Jsoup.parse(h);
            JSONObject jSONObject = new JSONObject();
            Elements B1 = ue2.select("ul.pagination-lg>li");
            int size = B1.size();
            String $4 = "href";
            if (size == 0) {
                i2 = Integer.parseInt(str2);
                i = i2;
            } else {
                int i4 = -1;
                int i5 = -1;
                int i6 = 0;
                while (true) {
                    if (i6 >= B1.size()) {
                        i = i5;
                        break;
                    }
                    Element hq = (Element) B1.get(i6);
                    Element O = hq.selectFirst("a");
                    if (O != null) {
                        String pE = O.text();
                        if (i5 != i4 || !hq.hasClass("disabled")) {
                            i = i5;
                        } else {
                            Matcher matcher = this.u6.matcher(O.attr($4));
                            i = matcher.find() ? Integer.parseInt(matcher.group(1).split($3)[8]) : 0;
                        }
                        if (pE.contains("...")) {
                            Matcher matcher2 = this.u6.matcher(O.attr($4));
                            if (matcher2.find()) {
                                i2 = Integer.parseInt(matcher2.group(1).split($3)[8]);
                            }
                        } else {
                            i5 = i;
                        }
                    }
                    i6++;
                    i4 = -1;
                }
                i2 = 0;
            }
            JSONArray jSONArray = new JSONArray();
            if (!h.contains("没有找到您想要的结果哦")) {
                Elements B12 = ue2.select("ul.list-unstyled>li");
                int i7 = 0;
                while (i7 < B12.size()) {
                    Element hq2 = (Element) B12.get(i7);
                    String ue3 = hq2.select($2).attr("title");
                    String Pd2 = hq2.selectFirst("img").attr("data-original");
                    String jw = hq2.select("em.littplus").text();
                    Matcher matcher3 = this.W.matcher(hq2.select($2).attr($4));
                    if (matcher3.find()) {
                        String group = matcher3.group(i3);
                        JSONObject jSONObject2 = new JSONObject();
                        jSONObject2.put("vod_id", group);
                        jSONObject2.put("vod_name", ue3);
                        jSONObject2.put("vod_pic", Pd2);
                        jSONObject2.put("vod_remarks", jw);
                        jSONArray.put(jSONObject2);
                    }
                    i7++;
                    i3 = 1;
                }
            }
            jSONObject.put("page", i);
            jSONObject.put("pagecount", i2);
            jSONObject.put("limit", 24);
            jSONObject.put("total", i2 <= 1 ? jSONArray.length() : i2 * 24);
            jSONObject.put("list", jSONArray);
            return jSONObject.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
            return "";
        }
    }

    public String detailContent(List<String> list) {
        String str;
        Exception e;
        Elements exVar;
        String str2;
        String $2 = "-";
        String $3 = ",";
        String $4 = "a";
        try {
            String str3 = "https://www.3edy.com/voddetail/" + list.get(0) + ".html";
            Document ue2 = Jsoup.parse(OkHttpUtil.string(str3, u(str3)));
            JSONObject jSONObject = new JSONObject();
            JSONObject jSONObject2 = new JSONObject();
            Elements B1 = ue2.select("ul.vod-infos>li");
            String ue3 = ((Element) B1.get(0)).select("img").attr("data-original");
            String jw = ((Element) B1.get(1)).select("h4 > a").text();
            Elements B12 = ue2.select("dl.dl-horizontal dd");
            String jw2 = ((Element) B12.get(1)).select($4).text();
            String pE = ((Element) B12.get(3)).text();
            String pE2 = ((Element) B12.get(4)).text();
            String pE3 = ((Element) B12.get(4)).text();
            str = "";
            try {
                ArrayList arrayList = new ArrayList();
                Elements B13 = ((Element) B12.get(0)).select($4);
                String str4 = $2;
                for (int i = 0; i < B13.size(); i++) {
                    arrayList.add(((Element) B13.get(i)).text());
                }
                String join = TextUtils.join($3, arrayList);
                ArrayList arrayList2 = new ArrayList();
                Elements B14 = ((Element) B12.get(2)).select($4);
                String str5 = $4;
                for (int i2 = 0; i2 < B14.size(); i2++) {
                    arrayList2.add(((Element) B14.get(i2)).text());
                }
                String join2 = TextUtils.join($3, arrayList2);
                String pE4 = ((Element) B12.get(5)).text();
                jSONObject2.put("vod_id", list.get(0));
                jSONObject2.put("vod_name", jw);
                jSONObject2.put("vod_pic", ue3);
                jSONObject2.put("type_name", jw2);
                jSONObject2.put("vod_year", pE2);
                jSONObject2.put("vod_area", pE);
                jSONObject2.put("vod_remarks", pE3);
                jSONObject2.put("vod_actor", join);
                jSONObject2.put("vod_director", join2);
                jSONObject2.put("vod_content", pE4);
                new TreeMap(new AnonymousClass1());
                Elements u62 = ue2.select("ul#ff-tab").select("li");
                String str6 = str;
                String str7 = str6;
                int i3 = 0;
                while (i3 < u62.size()) {
                    str6 = ((Element) u62.get(i3)).selectFirst(str5).text().trim();
                    Elements B15 = ue2.select("ul.play-list > li > a");
                    ArrayList arrayList3 = new ArrayList();
                    int i4 = 0;
                    while (i4 < B15.size()) {
                        Element hq = (Element) B15.get(i4);
                        Matcher matcher = this.Pd.matcher(hq.attr("href"));
                        if (!matcher.find()) {
                            exVar = u62;
                            str2 = str4;
                        } else {
                            StringBuilder sb = new StringBuilder();
                            sb.append(matcher.group(1));
                            str2 = str4;
                            sb.append(str2);
                            exVar = u62;
                            sb.append(matcher.group(2));
                            sb.append(str2);
                            sb.append(matcher.group(3));
                            arrayList3.add(hq.text() + "$" + sb.toString());
                        }
                        i4++;
                        u62 = exVar;
                        str4 = str2;
                    }
                    if (arrayList3.size() > 0) {
                        str7 = TextUtils.join("#", arrayList3);
                    }
                    str7.length();
                    i3++;
                    u62 = u62;
                    str5 = str5;
                    str4 = str4;
                }
                if(str6.isEmpty() || str7.isEmpty())str6="暂无播放源";
                jSONObject2.put("vod_play_from", str6);
                jSONObject2.put("vod_play_url", str7);
                JSONArray jSONArray = new JSONArray();
                jSONArray.put(jSONObject2);
                jSONObject.put("list", jSONArray);
                return jSONObject.toString();
            } catch (Exception e2) {
                e = e2;
                SpiderDebug.log(e);
                return str;
            }
        } catch (Exception e3) {
            e = e3;
            str = "";
            SpiderDebug.log(e);
            return str;
        }
    }

    public String homeContent(boolean z) {
        int i;
        String $2;
        String $3 = "h4 > a";
        String $4 = "https://www.3edy.com";
        try {
            Document ue2 = Jsoup.parse(OkHttpUtil.string($4, u($4)));
            Elements B1 = ue2.select("ul.navbar-left>li a");
            JSONArray jSONArray = new JSONArray();
            Iterator it = B1.iterator();
            while (true) {
                boolean hasNext = it.hasNext();
                i = 0;
                $2 = "href";
                if (!hasNext) {
                    break;
                }
                Element hq = (Element) it.next();
                String pE = hq.text();
                if (pE.equals("电影") || pE.equals("美剧") || pE.equals("韩剧") || pE.equals("国产剧") || pE.equals("日剧") || pE.equals("泰剧") || pE.equals("动漫") || pE.equals("综艺")) {
                    i = 1;
                }
                if (i != 0) {
                    Matcher matcher = this.B.matcher(hq.attr($2));
                    if (matcher.find()) {
                        String trim = matcher.group(1).trim();
                        JSONObject jSONObject = new JSONObject();
                        jSONObject.put("type_id", trim);
                        jSONObject.put("type_name", pE);
                        jSONArray.put(jSONObject);
                    }
                }
            }
            JSONObject jSONObject2 = new JSONObject();
            if (z) {
                jSONObject2.put("filters", this.u);
            }
            jSONObject2.put("class", jSONArray);
            try {
                Elements B12 = ((Element) ue2.select("ul.list-unstyled-index").get(3)).select("li");
                JSONArray jSONArray2 = new JSONArray();
                while (i < B12.size()) {
                    Element hq2 = (Element) B12.get(i);
                    String ue3 = hq2.select($3).attr("title");
                    String Pd2 = hq2.selectFirst("img").attr("src");
                    String jw = hq2.select("em.littplus").text();
                    Matcher matcher2 = this.W.matcher(hq2.select($3).attr($2));
                    if (matcher2.find()) {
                        String group = matcher2.group(1);
                        JSONObject jSONObject3 = new JSONObject();
                        jSONObject3.put("vod_id", group);
                        jSONObject3.put("vod_name", ue3);
                        jSONObject3.put("vod_pic", Pd2);
                        jSONObject3.put("vod_remarks", jw);
                        jSONArray2.put(jSONObject3);
                    }
                    i++;
                }
                jSONObject2.put("list", jSONArray2);
            } catch (Exception e) {
                SpiderDebug.log(e);
            }
            return jSONObject2.toString();
        } catch (Exception e2) {
            SpiderDebug.log(e2);
            return "";
        }
    }

    public void init(Context context) {
        EEEDY.super.init(context);
        try {
            this.ue = new JSONObject("{\"789pan\":{\"sh\":\" 飘花速播\",\"pu\":\"https://dp.jiexi.work/jsonno.php?url=\",\"sn\":1,\"or\":999}}");
            this.u = new JSONObject("");
        } catch (JSONException e) {
            SpiderDebug.log(e);
        }
    }

    public String playerContent(String str, String str2, List<String> list) {
        String $2 = "url";
        try {
            String str3 = "https://www.3edy.com/vodplay/" + str2 + ".html";
            Elements B1 = Jsoup.parse(OkHttpUtil.string(str3, u(str3))).select("div.ff-player script");
            JSONObject jSONObject = new JSONObject();
            System.out.println("scriptnum:  "+B1.size());
            for (int i = 0; i < B1.size(); i++) {
                String trim = B1.get(i).html().trim();
//                System.out.println("trim:  "+trim);
                if (trim.startsWith("var player_")) {
                    JSONObject jSONObject2 = new JSONObject(trim.substring(trim.indexOf(123), trim.lastIndexOf(125) + 1));
                    String str4 = "https://dp.jiexi.work/jsonno.php?url=" + jSONObject2.getString($2);
//                    System.out.println(str4);
                    String string = new JSONObject(OkHttpUtil.string(str4, u(str3))).getString($2);
                    jSONObject.put("parse", 0);
                    jSONObject.put("playUrl", "");
                    jSONObject.put($2, string);
                }
            }
            return jSONObject.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
            return "";
        }
    }

    public String searchContent(String str, boolean z) {
        if (z) {
            return "";
        }
        try {
            String str2 = "https://www.3edy.com/index.php/ajax/suggest?mid=1&wd=" + URLEncoder.encode(str) + "&limit=10&timestamp=" + System.currentTimeMillis();
            JSONObject jSONObject = new JSONObject(OkHttpUtil.string(str2, u(str2)));
            JSONObject jSONObject2 = new JSONObject();
            JSONArray jSONArray = new JSONArray();
            int i = jSONObject.getInt("total");
            String $2 = "list";
            if (i > 0) {
                JSONArray jSONArray2 = new JSONArray(jSONObject.getString($2));
                for (int i2 = 0; i2 < jSONArray2.length(); i2++) {
                    JSONObject jSONObject3 = jSONArray2.getJSONObject(i2);
                    String string = jSONObject3.getString("id");
                    String string2 = jSONObject3.getString("name");
                    String string3 = jSONObject3.getString("pic");
                    JSONObject jSONObject4 = new JSONObject();
                    jSONObject4.put("vod_id", string);
                    jSONObject4.put("vod_name", string2);
                    jSONObject4.put("vod_pic", string3);
                    jSONObject4.put("vod_remarks", "");
                    jSONArray.put(jSONObject4);
                }
            }
            jSONObject2.put($2, jSONArray);
            return jSONObject2.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
            return "";
        }
    }

    /* access modifiers changed from: protected */
    public HashMap<String, String> u(String str) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("method", "GET");
        String $2 = "Upgrade-Insecure-Requests";
        String $3 = "1";
        hashMap.put($2, $3);
        hashMap.put("DNT", $3);
        hashMap.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36");
        hashMap.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        hashMap.put("Accept-Language", "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2");
        return hashMap;
    }
}
