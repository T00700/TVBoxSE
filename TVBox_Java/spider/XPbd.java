package com.github.catvod.spider;

import android.text.TextUtils;

import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.utils.Misc;
import com.github.catvod.utils.okhttp.OkHttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XPbd extends XPath {

    @Override
    protected String categoryUrl(String tid, String pg, boolean filter, HashMap<String, String> extend) {
        String cateUrl = rule.getCateUrl();
        if (filter && extend != null && extend.size() > 0) {
            for (Iterator<String> it = extend.keySet().iterator(); it.hasNext(); ) {
                String key = it.next();
                String value = extend.get(key);
                if (value.length() > 0) {
                    cateUrl = cateUrl.replace("{" + key + "}", URLEncoder.encode(value));
                }
            }
        }
        cateUrl = cateUrl.replace("{cateId}", tid).replace("{catePg}", pg);
        Matcher m = Pattern.compile("\\{(.*?)\\}").matcher(cateUrl);
        while (m.find()) {
            String n = m.group(0).replace("{", "").replace("}", "");
            cateUrl = cateUrl.replace(m.group(0), "").replace("/" + n + "/", "");
        }
        return cateUrl;
    }

    @Override
    public String detailContent(List<String> ids) {
        try {
            String webUrl = rule.getDetailUrl().replace("{vid}", ids.get(0));
            String webContent = fetch(webUrl);
            JXDocument doc = JXDocument.create(webContent);
            JXNode vodNode = doc.selNOne(rule.getDetailNode());
            JSONObject vodAtom = new JSONObject();

            String cover = "", title = "", desc = "", category = "", area = "", year = "", remark = "", director = "", actor = "";

            title = vodNode.selOne(rule.getDetailName()).asString().trim();
            title = rule.getDetailNameR(title);

            cover = vodNode.selOne(rule.getDetailImg()).asString().trim();
            cover = rule.getDetailImgR(cover);
            cover = Misc.fixUrl(webUrl, cover);

            if (!rule.getDetailCate().isEmpty()) {
                try {
                    category = vodNode.selOne(rule.getDetailCate()).asString().trim();
                    category = rule.getDetailCateR(category);
                } catch (Exception e) {
                    SpiderDebug.log(e);
                }
            }
            if (!rule.getDetailYear().isEmpty()) {
                try {
                    year = vodNode.selOne(rule.getDetailYear()).asString().trim();
                    year = rule.getDetailYearR(year);
                } catch (Exception e) {
                    SpiderDebug.log(e);
                }
            }
            if (!rule.getDetailArea().isEmpty()) {
                try {
                    area = vodNode.selOne(rule.getDetailArea()).asString().trim();
                    area = rule.getDetailAreaR(area);
                } catch (Exception e) {
                    SpiderDebug.log(e);
                }
            }
            if (!rule.getDetailMark().isEmpty()) {
                try {
                    remark = vodNode.selOne(rule.getDetailMark()).asString().trim();
                    remark = rule.getDetailMarkR(remark);
                } catch (Exception e) {
                    SpiderDebug.log(e);
                }
            }
            if (!rule.getDetailActor().isEmpty()) {
                try {
                    actor = vodNode.selOne(rule.getDetailActor()).asString().trim();
                    actor = rule.getDetailActorR(actor);
                } catch (Exception e) {
                    SpiderDebug.log(e);
                }
            }
            if (!rule.getDetailDirector().isEmpty()) {
                try {
                    director = vodNode.selOne(rule.getDetailDirector()).asString().trim();
                    director = rule.getDetailDirectorR(director);
                } catch (Exception e) {
                    SpiderDebug.log(e);
                }
            }
            if (!rule.getDetailDesc().isEmpty()) {
                try {
                    desc = vodNode.selOne(rule.getDetailDesc()).asString().trim();
                    desc = rule.getDetailDescR(desc);
                } catch (Exception e) {
                    SpiderDebug.log(e);
                }
            }

            vodAtom.put("vod_name", title);
            vodAtom.put("vod_pic", cover);
            vodAtom.put("type_name", category);
            vodAtom.put("vod_year", year);
            vodAtom.put("vod_area", area);
            vodAtom.put("vod_remarks", remark);
            vodAtom.put("vod_actor", actor);
            vodAtom.put("vod_director", director);
            vodAtom.put("vod_content", desc);

            List<JXNode> urlListNodes = doc.selN(rule.getDetailUrlNode());
            for (int i = 0; i < urlListNodes.size(); i++) {
                List<JXNode> urlNodes = urlListNodes.get(i).sel(rule.getDetailUrlSubNode());
                for (int j = 0; j < urlNodes.size(); j++) {
                    String id = urlNodes.get(j).selOne(rule.getDetailUrlId()).asString().trim();
                    id = rule.getDetailUrlIdR(id);
                    JSONObject result = new JSONObject();
                    JSONArray lists = new JSONArray();
                    vodAtom.put("vod_id", id);
                    vodAtom.put("vod_play_from", "磁力测试");
                    vodAtom.put("vod_play_url", "网站没资源$" + id);
                    lists.put(vodAtom);
                    result.put("list", lists);
                    return result.toString();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

}
