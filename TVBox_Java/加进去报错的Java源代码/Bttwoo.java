package com.github.catvod.spider;

import android.text.TextUtils;
import android.util.Base64;
import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.utils.CBC;
import com.github.catvod.utils.okhttp.OkHttpUtil;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/* loaded from: classes.dElements */
public class Bttwoo extends Spider {
    private final String O = "{}";
    private final Pattern G = Pattern.compile("https://www.bttwoo.com/(\\S+)");
    private final Pattern n = Pattern.compile("https://www.bttwoo.com/movie/(\\d+).html");
    private final Pattern Ur = Pattern.compile("https://www.bttwoo.com/v_play/(\\S+).html");
    private final Pattern eA = Pattern.compile("https://www.bttwoo.com/\\S+/page/(\\d+)");
    private final Pattern o = Pattern.compile("=\"(.*?)\";var");
    private final Pattern F = Pattern.compile("parse\\(\"(.*?)\"\\);var iv");
    private final Pattern Cp = Pattern.compile("iv=md5\\.enc\\.Utf8\\.parse\\((.*?)\\);var decrypted");
    private final Pattern JC = Pattern.compile("url: \"(.*?)\"");

    protected HashMap<String, String> header(String str) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("method", "GET");
        if (!TextUtils.isEmpty(str)) {
            hashMap.put("Referer", str);
        }
        hashMap.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36");
        return hashMap;
    }

    public String categoryContent(String str, String str2, boolean z, HashMap<String, String> hashMap) {
        int i;
        try {
            String str3 = "https://www.bttwoo.com/" + str + "/page/" + str2;
            String h = OkHttpUtil.string(str3, header(str3));
            Document ue = Jsoup.parse(h);
            JSONObject jSONObject = new JSONObject();
            Matcher matcher = this.eA.matcher(ue.select("div.pagenavi_txt > a.extend").attr("href"));
            int i2 = 1;
            int parseInt = matcher.find() ? Integer.parseInt(matcher.group(1).trim()) : 1;

            Elements B1 = ue.select("div.pagenavi_txt > a ");
            int i3 = 0;
            if (B1.size() > 0) {
                i = Integer.parseInt(str2);
                for (int i4 = 0; i4 < B1.size(); i4++) {
                    Element O = B1.get(i4).selectFirst("a");
                    if (O != null) {
                        O.text().trim();
                        if (O.hasClass("current")) {
                            Matcher matcher2 = this.eA.matcher(O.attr("href"));
                            if (matcher2.find()) {
                                i = Integer.parseInt(matcher2.group(1).trim());
                            }
                        }
                    }
                }
            } else {
                i = 1;
//                i = Integer.parseInt(str2);
            }
            JSONArray jSONArray = new JSONArray();
            if (!h.contains("没有找到您想要的结果哦")) {
                Elements B12 = ue.select("div.bt_img > ul > li");
                while (i3 < B12.size()) {
                    Element hq = B12.get(i3);
                    String pE = hq.selectFirst("h3.dytit > a").text();
                    String Pd = hq.selectFirst("a > img ").attr("data-original");
                    Element O2 = hq.selectFirst("div.jidi > span");
                    String pE2 = O2 != null ? O2.text() : "";
                    Matcher matcher3 = this.n.matcher(hq.selectFirst("h3.dytit > a").attr("href"));
                    if (matcher3.find()) {
                        String group = matcher3.group(i2);
                        JSONObject jSONObject2 = new JSONObject();
                        jSONObject2.put("vod_id", group);
                        jSONObject2.put("vod_name", pE);
                        jSONObject2.put("vod_pic", Pd);
                        jSONObject2.put("vod_remarks", pE2);
                        jSONArray.put(jSONObject2);
                    }
                    i3++;
                    i2 = 1;
                }
            }
            System.out.println("page:  "+i);
            jSONObject.put("page", i);
            jSONObject.put("pagecount", parseInt);
            jSONObject.put("limit", 25);
            jSONObject.put("total", parseInt <= 1 ? jSONArray.length() : parseInt * 25);
            jSONObject.put("list", jSONArray);
            return jSONObject.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
            return "";
        }
    }

    public String detailContent(List<String> list) {
        String str ="";
        Exception e;
        Document ue = null;
        JSONObject jSONObject = null;
        String Pd = "";
        String pE = "";
        String pE2 ="";
        Elements B1 = null;
        String str2 =" ";
        String str3 = "";
        String str4 = "";
        String str5 = "";
        String str6 = "";
        String str7 = "";
        int i = 0;
        Element hq = null;
        String str8;
        String str9 = "$$$";
        String str10 = "";
        try {
            String str11 = "https://www.bttwoo.com/movie/" + list.get(0) + ".html";
            ue = Jsoup.parse(OkHttpUtil.string(str11, header(str11)));
            jSONObject = new JSONObject();
            Pd = ue.selectFirst("div.dyimg > img").attr("src");
            pE = ue.selectFirst("div.moviedteail_tt > h1").text();
            pE2 = Jsoup.parse(ue.selectFirst("meta[name=description]").attr("content")).text();
            B1 = ue.select("ul.moviedteail_list > li");
            str2 = str10;
            str3 = str2;
            str4 = str3;
            str5 = str4;
            str6 = str5;
            str7 = str6;
//            i = 0;
        } catch (Exception e2) {
            e = e2;
            str = str10;
            return "1111";
        }
        while (true) {
            str = str10;
            if (i >= B1.size()) {
                break;
            }
            try {
                Element hq2 = B1.get(i);
                Elements exVar = B1;
                String pE3 = hq2.text();
                JSONObject jSONObject3 = jSONObject;
                if (pE3.contains("类型：")) {
                    str2 = hq2.text();
                } else if (pE3.contains("年份：")) {
                    str3 = hq2.text();
                } else if (pE3.contains("地区：")) {
                    str4 = hq2.text();
                } else if (pE3.contains("上映：")) {
                    str5 = hq2.text();
                } else {
                    str8 = str9;
                    if (pE3.contains("导演：")) {
                        ArrayList arrayList = new ArrayList();
                        Elements B12 = hq2.select("a");
                        for (int i2 = 0; i2 < B12.size(); i2++) {
                            arrayList.add(B12.get(i2).text());
                        }
                        str7 = TextUtils.join(",", arrayList);
                    } else if (pE3.contains("主演：")) {
                        ArrayList arrayList2 = new ArrayList();
                        Elements B13 = hq2.select("a");
                        for (int i3 = 0; i3 < B13.size(); i3++) {
                            arrayList2.add(B13.get(i3).text());
                        }
                        str6 = TextUtils.join(",", arrayList2);
                    }
                }
                str8 = str9;
                i++;
                str10 = str;
                B1 = exVar;
                jSONObject = jSONObject3;
                str9 = str8;
            } catch (Exception e3) {
                e = e3;
                SpiderDebug.log(e);
                return str;
            }
        }
        String str12 = str9;
        JSONObject jSONObject4 = jSONObject;
        JSONObject jSONObject2 = new JSONObject();
        try{
            jSONObject2.put("vod_id", list.get(0));
            jSONObject2.put("vod_name", pE);
            jSONObject2.put("vod_pic", Pd);
            jSONObject2.put("type_name", str2);
            jSONObject2.put("vod_year", str3);
            jSONObject2.put("vod_area", str4);
            jSONObject2.put("vod_remarks", str5);
            jSONObject2.put("vod_actor", str6);
            jSONObject2.put("vod_director", str7);
            jSONObject2.put("vod_content", pE2);
            TreeMap treeMap = new TreeMap();
            Elements B14 = ue.select("div.mi_paly_box > div > div.ypxingq_t");
            Elements B15 = ue.select("div.paly_list_btn");

            for (int i4 = 0; i4 < B14.size(); i4++) {
                String pE4 = B14.get(i4).text();
                Elements B16 = B15.get(i4).select("a");
                ArrayList arrayList3 = new ArrayList();
                for (int i5 = 0; i5 < B16.size(); i5++) {
                    String name = B16.get(i5).text();
                    Matcher matcher = this.Ur.matcher(B16.get(i5).attr("href"));
                    if (matcher.find()) {
                        arrayList3.add(name + "$" + matcher.group(1));
                    }
                }
                String join = arrayList3.size() > 0 ? TextUtils.join("#", arrayList3) : str;
                if (join.length() != 0) {
                    treeMap.put(pE4, join);
                }
            }
            if (treeMap.size() > 0) {
                String join2 = TextUtils.join(str12, treeMap.keySet());
                String join3 = TextUtils.join(str12, treeMap.values());
                jSONObject2.put("vod_play_from", join2);
                jSONObject2.put("vod_play_url", join3);
            }
            JSONArray jSONArray = new JSONArray();
            jSONArray.put(jSONObject2);
            jSONObject4.put("list", jSONArray);

        }catch (Exception e4){
            SpiderDebug.log(e4);
        }

        return jSONObject4.toString();
    }

    public String homeContent(boolean z) {
        int i;
        try {
            Document ue = Jsoup.parse(OkHttpUtil.string("https://www.bttwoo.com/", header("https://www.bttwoo.com")));
            Elements B1 = ue.select("ul.navlist > li > a");
            JSONArray jSONArray = new JSONArray();
            Iterator<Element> it = B1.iterator();
            while (true) {
                i = 0;
                if (!it.hasNext()) {
                    break;
                }
                Element next = it.next();
                String pE = next.text();
                if (pE.equals("最新电影")) {
                    pE = "最新";
                }
                if (pE.equals("热门下载")) {
                    pE = "热门放映";
                }
                if (pE.equals("最新") || pE.equals("本月热门")  || pE.equals("热门放映") || pE.equals("国产剧") || pE.equals("日韩剧") || pE.equals("美剧")) {
                    i = 1;
                }
                if (i != 0) {
                    Matcher matcher = this.G.matcher(next.attr("href"));
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
                jSONObject2.put("filters", new JSONObject("{}"));
            }
            jSONObject2.put("class", jSONArray);
            try {
                Elements B12 = ue.select("div.leibox > ul > li ");
                JSONArray jSONArray2 = new JSONArray();
                while (i < B12.size()) {
                    Element hq = B12.get(i);
                    String pE2 = hq.selectFirst("h3.dytit > a").text();
                    String Pd = hq.selectFirst("a > img").attr("data-original");
                    Element O = hq.selectFirst("div.jidi > span ");
                    String pE3 = O != null ? O.text() : "";
                    Matcher matcher2 = this.n.matcher(hq.selectFirst("h3.dytit > a").attr("href"));
                    if (matcher2.find()) {
                        String group = matcher2.group(1);
                        JSONObject jSONObject3 = new JSONObject();
                        jSONObject3.put("vod_id", group);
                        jSONObject3.put("vod_name", pE2);
                        jSONObject3.put("vod_pic", Pd);
                        jSONObject3.put("vod_remarks", pE3);
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

    public String playerContent(String str, String str2, List<String> list) {
        try {
            String str3 = "https://www.bttwoo.com/v_play/" + str2 + ".html";
            Document ue = Jsoup.parse(OkHttpUtil.string(str3, header(str3)));
            JSONObject jSONObject = new JSONObject();
            Matcher matcher = this.o.matcher(ue.toString());
            Matcher matcher2 = this.F.matcher(ue.toString());
            Matcher matcher3 = this.Cp.matcher(ue.toString());
            if (matcher.find() && matcher2.find() && matcher3.find()) {
               // Matcher matcher4 = this.JC.matcher(AES.decryptToString(Base64.decode(matcher.group(1), 0), matcher2.group(1).getBytes(), matcher3.group(1).getBytes(),AES.AES_CBC_PKCS7Padding));
                Matcher matcher4 = this.JC.matcher(CBC.DECRYPT(Base64.decode(matcher.group(1), 0), matcher2.group(1).getBytes(), matcher3.group(1).getBytes()));
                if (matcher4.find()) {
                    String group = matcher4.group(1);
                    HashMap hashMap = new HashMap();
                    hashMap.put("User-Agent", " Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36");
                    jSONObject.put("url", group);
                    jSONObject.put("header", new JSONObject(hashMap).toString());
                    jSONObject.put("parse", "0");
                    jSONObject.put("palyUrl", "");
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
            String str2 = "https://www.bttwoo.com/xssearch?q=" + URLEncoder.encode(str) + "&f=_all&p=1";
            Document ue = Jsoup.parse(OkHttpUtil.string(str2, header(str2)));
            JSONObject jSONObject = new JSONObject();
            JSONArray jSONArray = new JSONArray();
            Elements B1 = ue.select("div.mi_ne_kd > ul > li");
            for (int i = 0; i < B1.size(); i++) {
                Element hq = B1.get(i);
                String trim = hq.selectFirst("img").attr("alt").trim();
                String Pd = hq.selectFirst("img ").attr("data-original");
                Element O = hq.selectFirst("div.jidi > span");
                String pE = O != null ? O.text() : "";
                Matcher matcher = this.n.matcher(hq.selectFirst("a").attr("href"));
                if (matcher.find()) {
                    String group = matcher.group(1);
                    JSONObject jSONObject2 = new JSONObject();
                    jSONObject2.put("vod_id", group);
                    jSONObject2.put("vod_name", trim);
                    jSONObject2.put("vod_pic", Pd);
                    jSONObject2.put("vod_remarks", pE);
                    jSONArray.put(jSONObject2);
                }
            }
            jSONObject.put("list", jSONArray);
            return jSONObject.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
            return "";
        }
    }
}
