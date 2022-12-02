package com.github.catvod.spider;

import android.annotation.TargetApi;
import android.content.Context;
import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.utils.CBC;
import com.github.catvod.utils.okhttp.OKCallBack;
import com.github.catvod.utils.okhttp.OkHttpUtil;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import okhttp3.Call;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


public class Jianying extends Spider {
    public static String token;


    public void init(Context context, String str) {
        super.init(context, str);
        token = str;
    }

    private HashMap<String, String> getHeaders(String url) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("accept", "application/json, text/plain, */*");
        headers.put("accept-encoding", "gzip, deflate, br");
        headers.put("connection", "keep-alive");
        headers.put("Authorization", token);
        headers.put("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.54 Safari/537.36");
        return headers;
    }

    public String homeContent(boolean filter) {
        try {
            String url = "https://admin.syrme.top/v1/api/video/index";
            JSONObject jSONObject = new JSONObject();
            JSONArray classes = new JSONArray();
            OkHttpUtil.postJson(OkHttpUtil.defaultClient(), url, jSONObject.toString(), getHeaders(url), new OKCallBack.OKCallBackString() {

                @Override
                public void onFailure(Call call, Exception exc) {
                }

                public void onResponse(String str) {
                    try {
                        JSONArray list = new JSONObject(str).getJSONArray("data");
                        for (int i = 0; i < list.length(); i++) {
                            JSONObject vObj = list.getJSONObject(i);
                            String name = vObj.getString("title");
                            if (!name.equals("轮播")) {
                                String id = vObj.getString("title");
                                JSONObject newCls = new JSONObject();
                                newCls.put("type_id", id);
                                newCls.put("type_name", name);
                                classes.put(newCls);
                            }
                        }
                    } catch (JSONException unused) {
                    }
                }
            });
            JSONObject result = new JSONObject();
            result.put("class", classes);
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }

    public String homeVideoContent() {
        try {
            JSONArray videos = new JSONArray();
            JSONObject jSONObject = new JSONObject();
            String url = "https://admin.syrme.top/v1/api/video/index";
            OkHttpUtil.postJson(OkHttpUtil.defaultClient(), url, jSONObject.toString(), getHeaders(url), new OKCallBack.OKCallBackString() {
                @Override
                public void onFailure(Call call, Exception exc) {
                }

                public void onResponse(String str) {
                    try {
                        JSONArray jsonArray = new JSONObject(str).getJSONArray("data");
                        JSONArray list = jsonArray.getJSONObject(1).getJSONArray("video_list");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject vObj = list.getJSONObject(i);
                            String id = vObj.getString("ID");
                            String title = vObj.getString("title");
                            String cover = vObj.getString("image");
                            String mark = vObj.getString("score");
                            JSONObject v = new JSONObject();
                            v.put("vod_id", id);
                            v.put("vod_name", title);
                            v.put("vod_pic", cover);
                            v.put("vod_remarks", mark);
                            videos.put(v);
                        }
                    } catch (JSONException unused) {
                    }
                }
            });
            JSONObject result = new JSONObject();
            result.put("list", videos);
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }

    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) {
        try {
            int parseInt = Integer.parseInt(pg);
            if (parseInt == 0) {
                parseInt = 1;
            }
            String url = "https://admin.syrme.top/v1/api/video/search?q=" + tid + "&page=" + pg + "&size=" + 24;
            JSONObject jSONObject = new JSONObject();
            JSONArray list = new JSONArray();
            OkHttpUtil.postJson(OkHttpUtil.defaultClient(), url, jSONObject.toString(), getHeaders(url), new OKCallBack.OKCallBackString() {

                @Override
                public void onFailure(Call call, Exception exc) {
                }

                public void onResponse(String str4) {
                    try {
                        JSONArray data = new JSONObject(str4).getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject vObj = data.getJSONObject(i);
                            JSONObject vodAtom = new JSONObject();
                            vodAtom.put("vod_id", vObj.getString("ID"));
                            vodAtom.put("vod_name", vObj.getString("title"));
                            vodAtom.put("vod_pic", vObj.optString("image"));
                            String mark = vObj.optString("score");
                            if (mark.equals("null")) {
                                mark = "";
                            }
                            vodAtom.put("vod_remarks", mark);
                            list.put(vodAtom);
                        }
                    } catch (JSONException unused) {
                    }
                }
            });
            JSONObject result = new JSONObject();
            result.put("page", parseInt);
            if (list.length() == 24) {
                parseInt++;
            }
            result.put("pagecount", parseInt);
            result.put("limit", 24);
            result.put("total", Integer.MAX_VALUE);
            result.put("list", list);
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }

    public String detailContent(List<String> ids) {
        try {
            String url = "https://admin.syrme.top/v1/api/video/id?id=" + ids.get(0);
            JSONObject jSONObject = new JSONObject();
            JSONObject vodAtom = new JSONObject();
            OkHttpUtil.postJson(OkHttpUtil.defaultClient(), url, jSONObject.toString(), getHeaders(url), new OKCallBack.OKCallBackString() {
                @Override
                public void onFailure(Call call, Exception exc) {
                }

                public void onResponse(String str) {
                    try {
                        JSONObject vObj = new JSONObject(str).getJSONObject("data");
                        vodAtom.put("vod_id", vObj.getString("ID"));
                        vodAtom.put("vod_name", vObj.getString("title"));
                        vodAtom.put("vod_pic", vObj.getString("image"));
                        vodAtom.put("type_name", vObj.getString("video_type"));
                        vodAtom.put("vod_year", vObj.getString("year"));
                        vodAtom.put("vod_area", vObj.getString("video_tags"));
                        String optString = vObj.optString("score");
                        String des = "";
                        if (optString.equals("null")) {
                            optString = des;
                        }
                        vodAtom.put("vod_remarks", optString);
                        vodAtom.put("vod_actor", vObj.getString("authors"));
                        vodAtom.put("vod_director", vObj.getString("director"));
                        String optString2 = vObj.optString("content");
                        if (!optString2.equals("null")) {
                            des = optString2;
                        }
                        vodAtom.put("vod_content", des);
                        vodAtom.put("vod_play_from", "简影");
                        vodAtom.put("vod_play_url", vObj.optString("url_content").replace("\n", "#"));
                    } catch (JSONException unused) {
                    }
                }
            });
            JSONObject result = new JSONObject();
            JSONArray list = new JSONArray();
            list.put(vodAtom);
            result.put("list", list);
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }

    @TargetApi(26)
    public String playerContent(String flag, String id, List<String> vipFlags) {
        try {
            JSONObject result = new JSONObject();
            String time = LocalDateTime.now(ZoneId.of("Asia/Shanghai")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")).replace("-", "丨");
            String key = "ba0-1024-11eb-ad";
            String enData = CBC.hexs(time, key, key);
            String basic = "";
            int i = 0;
            while (i < enData.length()) {
                int i2 = i + 8;
                basic = basic + enData.substring(i, i2) + "-";
                i = i2;
            }
            String sid = basic.substring(0, basic.length() - 1);
            JSONObject headers = new JSONObject();
            headers.put("User-Agent", " Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36");
            headers.put("Accept", " */*");
            headers.put("Accept-Language", " zh-CN,zh;q=0.9,en-US;q=0.3,en;q=0.7");
            headers.put("Accept-Encoding", " gzip, deflate");

            result.put("parse", 0);
            result.put("url", id + "?sid=" + sid);
            result.put("playUrl", "");
            result.put("header", headers.toString());
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }

    public String searchContent(String str, boolean quick) {
        if (quick) {
            return "";
        }
        try {
            JSONArray videos = new JSONArray();
            try {
                String url = "https://admin.syrme.top/v1/api/video/search?q=" + str + "&page=1&size=24";
                OkHttpUtil.postJson(OkHttpUtil.defaultClient(), url, new JSONObject().toString(), getHeaders(url), new OKCallBack.OKCallBackString() {
                    @Override
                    public void onFailure(Call call, Exception exc) {
                    }

                    public void onResponse(String str3) {
                        try {
                            JSONArray jsonArray = new JSONObject(str3).getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject vObj = jsonArray.getJSONObject(i);
                                JSONObject v = new JSONObject();
                                v.put("vod_id", vObj.getString("ID"));
                                v.put("vod_name", vObj.getString("title"));
                                v.put("vod_pic", vObj.optString("image"));
                                String mark = vObj.optString("score");
                                if (mark.equals("null")) {
                                    mark = "";
                                }
                                v.put("vod_remarks", mark);
                                videos.put(v);
                            }
                        } catch (JSONException unused) {
                        }
                    }
                });
            } catch (Exception unused) {
            }
            JSONObject result = new JSONObject();
            result.put("list", videos);
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }
}