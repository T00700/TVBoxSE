package com.github.catvod.spider;

import android.content.Context;
import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.utils.okhttp.OkHttpUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Yisou extends Spider {
    private static final Pattern aliyun = Pattern.compile("(https://www.aliyundrive.com/s/[^\"]+)");
    private PushAgent yisou;


    public String detailContent(List<String> list) {
        try {
            return yisou.detailContent(list);
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }


    public void init(Context context, String str) {
        super.init(context, str);
        yisou = new PushAgent();
        yisou.init(context,str);
    }


    public String playerContent(String str, String str2, List<String> list) {
        return yisou.playerContent(str, str2, list);
    }

    private Pattern regexVid = Pattern.compile("(\\S+)");
    public String searchContent(String key, boolean quick) {
        try {
            if (quick)
                return "";
            String url = "https://yiso.fun/info?searchKey=" + URLEncoder.encode(key)+"&from=ali";
            Document doc = Jsoup.parse(OkHttpUtil.string(url, null));
            JSONObject result = new JSONObject();

            JSONArray videos = new JSONArray();
            // Elements list = doc.select("ul.newsList>li>div.li_con");
            Elements list = doc.select("div.col-md-8>div.row-cards");
            for (int i = 0; i < list.size(); i++) {
                Element vod = list.get(i);
                String title = "";
                Elements titlelist = vod.select("h3.card-title>a>span");
                for(int k = 0; k < titlelist.size(); k++){
                    title+=titlelist.get(k).text()
                }
                String cover = "";
                String remark = vod.selectFirst("div.align-items-center>p.text-muted").text();
                Matcher matcher = regexVid.matcher(vod.select("h3.card-title>a").attr("href"));
                if (!matcher.find())
                    continue;
                String id = matcher.group(1);
                JSONObject v = new JSONObject();
                //v.put("vod_id", vod.select("h3.card-title>a").attr("href"));
                v.put("vod_id", id);
                v.put("vod_name", title);
                v.put("vod_pic", "https://inews.gtimg.com/newsapp_bt/0/13263837859/1000");
                v.put("vod_remarks", remark);
                videos.put(v);
            }

            result.put("list", videos);
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }
}