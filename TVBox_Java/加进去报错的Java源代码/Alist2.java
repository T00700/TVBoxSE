package com.github.catvod.spider;

import android.content.Context;

import com.github.catvod.crawler.Spider;
import com.github.catvod.utils.okhttp.OKCallBack;
import com.github.catvod.utils.okhttp.OkHttpUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.Call;

public class Alist extends Spider {
    private String ext;

    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> hashMap) {
        try {
            String url = this.ext + "/api/public/path";
            JSONObject params = new JSONObject();
            params.put("path", tid);

            JSONArray jSONArray2 = new JSONArray();
            OkHttpUtil.postJson(OkHttpUtil.defaultClient(), url, params.toString(), new OKCallBack.OKCallBackString() {
                @Override
                protected void onFailure(Call call, Exception e) {

                }

                @Override
                protected void onResponse(String response) {
                    try {
                        JSONObject retval = new JSONObject(response);
                        JSONArray list = retval.getJSONObject("data").getJSONArray("files");
                        for (int i =0; i < list.length(); ++i){
                            JSONObject o = list.getJSONObject(i);
                            String pic = o.getString("thumbnail");
                            if(pic.isEmpty()){
                                pic = "http://img1.3png.com/281e284a670865a71d91515866552b5f172b.png";
                            }
                            JSONObject jSONObject2 = new JSONObject();
                            jSONObject2.put("vod_id", tid + (tid.charAt(tid.length()-1) == '/' ? "" : "/") + o.getString("name"));
                            jSONObject2.put("vod_name", o.getString("name"));
                            jSONObject2.put("vod_pic", pic);
                            jSONObject2.put("vod_tag",o.getInt("type")==1 ? "folder" : "file" );
                            jSONObject2.put("vod_remarks", o.getInt("type")==1 ? "文件夹" : "文件");
                            jSONArray2.put(jSONObject2);

                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

            JSONObject jSONObject3 = new JSONObject();
            jSONObject3.put("page", 1);
            jSONObject3.put("pagecount", 1);
            jSONObject3.put("limit", jSONArray2.length());
            jSONObject3.put("total", jSONArray2.length());
            jSONObject3.put("list", jSONArray2);
            return jSONObject3.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String detailContent(List<String> ids) {
        try
        {
            String filename = ids.get(0);
            String url = this.ext + "/api/public/path";
            JSONObject params = new JSONObject();
            params.put("path", filename);

            JSONObject result = new JSONObject();
            JSONArray list = new JSONArray();

            OkHttpUtil.postJson(OkHttpUtil.defaultClient(), url, params.toString(), new OKCallBack.OKCallBackString() {
                @Override
                protected void onFailure(Call call, Exception e) {

                }

                @Override
                protected void onResponse(String response) {
                    try {
                        JSONObject retval = new JSONObject(response);
                        JSONArray files = retval.getJSONObject("data").getJSONArray("files");
                        for (int i =0; i < files.length(); ++i){
                            JSONObject o = files.getJSONObject(i);
                            String url = o.getString("url");
                            if(url.indexOf("//") ==0){
                                url = "http:"+url;
                            }
                            JSONObject jSONObject2 = new JSONObject();
                            jSONObject2.put("vod_id", filename + "/" + o.getString("name"));
                            jSONObject2.put("vod_name", o.getString("name"));
                            jSONObject2.put("vod_pic", o.getString("thumbnail"));
                            jSONObject2.put("vod_tag",o.getInt("type")==1 ? "folder" : "file" );
                            jSONObject2.put("vod_play_from", "播放");
                            jSONObject2.put("vod_play_url", o.getString("name")+"$"+url);
                            list.put(jSONObject2);

                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

            result.put("list", list);
            return result.toString();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return  "";

    }


    String fileTime(long time, String fmt) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        Date date = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat(fmt);
        return sdf.format(date);
    }

    public String homeContent(boolean z) {
        try {
            JSONArray classes = new JSONArray();
            JSONObject newCls = new JSONObject();
            newCls.put("type_id", "/");
            newCls.put("type_name", "Alist");
            newCls.put("type_flag", "1"); // 1 列表形式的文件夹 2 缩略图 0 或者不存在 表示正常
            classes.put(newCls);

            JSONArray jSONArray3 = new JSONArray();

            JSONObject jSONObject4 = new JSONObject();
            jSONObject4.put("class", classes);
            if(z) {
                jSONObject4.put("filters", new JSONObject("{}"));
            }
            return jSONObject4.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void init(Context context, String ext){
        Alist.super.init(context);
        this.ext = ext;
    }

    public String playerContent(String str, String str2, List<String> list) {
        try{
            JSONObject result = new JSONObject();
            result.put("parse", 0);
            result.put("playUrl", "");
            result.put("url", str2);
            return result.toString();
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    public String searchContent(String str, boolean z) {
        try {
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}
