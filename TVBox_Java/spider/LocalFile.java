package com.github.catvod.spider;

import android.content.Context;
import android.os.Environment;

import com.github.catvod.crawler.Spider;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class LocalFile extends Spider {

    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> hashMap) {
        try {

            File file = new File(tid);
            File[] list = file.listFiles();
            JSONArray jSONArray2 = new JSONArray();
            for (File f : list) {
                String filename = f.getName();
                if(filename.indexOf('.') ==0){
                    continue;
                }
                String pic = "https://img.tukuppt.com/png_preview/00/18/23/GBmBU6fHo7.jpg!/fw/260";
                if(!f.isDirectory()){
                    pic = "https://img.tukuppt.com/png_preview/00/42/50/3ySGW7mvyY.jpg!/fw/260";
                }
                JSONObject jSONObject2 = new JSONObject();
                jSONObject2.put("vod_id", f.getAbsolutePath());
                jSONObject2.put("vod_name", f.getName());
                jSONObject2.put("vod_pic", pic);
                // 当vod_tag为folder时会点击该item会把当前vod_id当成新的类型ID重新进
                jSONObject2.put("vod_tag",f.isDirectory() ? "folder" : "file" );
                jSONObject2.put("vod_remarks", fileTime(f.lastModified(), "yyyy/MM/dd aHH:mm:ss"));
                jSONArray2.put(jSONObject2);
            }

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
            JSONObject jSONObject;
            String filename = ids.get(0);
            File f = new File(filename);
            JSONObject vod = new JSONObject();
            vod.put("vod_id", filename);
            vod.put("vod_name", f.getName());
            vod.put("vod_pic", "");
            vod.put("type_name", "");
            vod.put("vod_play_from", "播放");
            vod.put("vod_play_url", f.getName()+"$"+filename);
            JSONObject result = new JSONObject();
            JSONArray list = new JSONArray();
            list.put(vod);
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
            JSONObject filterConfig = new JSONObject();
            JSONArray classes = new JSONArray();
            JSONObject newCls = new JSONObject();
            String root = Environment.getExternalStorageDirectory().getAbsolutePath();
            newCls.put("type_id", root);
            newCls.put("type_name", "本地文件");
            // type_flag 是扩展字段，目前可取值0、1、2，该字段不存在时表示正常模式
            newCls.put("type_flag", "1"); // 1、列表形式的文件夹 2、缩略图 0或者不存在表示正常模式
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

    public void init(Context context) {
        LocalFile.super.init(context);
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