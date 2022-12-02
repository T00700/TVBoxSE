package com.github.catvod.spider;

import android.content.Context;
import android.util.TimeUtils;

import com.github.catvod.crawler.Spider;
import com.github.catvod.utils.okhttp.OkHttpUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

public class Gitcafe extends Spider {
    private Map<String, JSONObject> a;
    private List<String> b;
    private PushAgent pushAgent;
    private JSONObject d;
    private JSONObject Home;


    @Override
    public void init(Context context, String extend) {
        super.init(context, extend);
        pushAgent = new PushAgent();
        pushAgent.init(context, extend);
        try {
            d = new JSONObject("{\"hyds\":\"华语电视\",\"rhds\":\"日韩电视\",\"omds\":\"欧美电视\",\"qtds\":\"其他电视\",\"hydy\":\"华语电影\",\"rhdy\":\"日韩电影\",\"omdy\":\"欧美电影\",\"qtdy\":\"其他电影\",\"hydm\":\"华语动漫\",\"rhdm\":\"日韩动漫\",\"omdm\":\"欧美动漫\",\"jlp\":\"纪录片\",\"zyp\":\"综艺片\",\"jypx\":\"教育培训\",\"qtsp\":\"其他视频\",\"hyyy\":\"华语音乐\",\"rhyy\":\"日韩音乐\",\"omyy\":\"欧美音乐\",\"qtyy\":\"其他音乐\",\"kfrj\":\"娱乐软件\",\"xtrj\":\"系统软件\",\"wlrj\":\"网络软件\",\"bgrj\":\"办公软件\",\"qtrj\":\"其他软件\",\"mh\":\"漫画\",\"xs\":\"小说\",\"cbs\":\"出版书\",\"zspx\":\"知识培训\",\"qtwd\":\"其他文档\",\"bz\":\"壁纸\",\"rw\":\"人物\",\"fj\":\"风景\",\"qttp\":\"其他图片\",\"qt\":\"其他\"}");
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList arrayList = new ArrayList();
        this.b = arrayList;
        arrayList.add("hydm");
        List<String> list = this.b;
        list.add("hyds");
        List<String> list2 = this.b;
        list2.add("hydy");
        List<String> list3 = this.b;
        list3.add("omdm");
        List<String> list4 = this.b;
        list4.add("omds");
        List<String> list5 = this.b;
        list5.add("omdy");
        List<String> list6 = this.b;
        list6.add("rhdm");
        List<String> list7 = this.b;
        list7.add("rhds");
        List<String> list8 = this.b;
        list8.add("rhdy");
        List<String> list9 = this.b;
        list9.add("qtds");
        List<String> list10 = this.b;
        list10.add("qtdy");
        List<String> list11 = this.b;
        list11.add("qtsp");
        List<String> list12 = this.b;
        list12.add("jlp");
        List<String> list13 = this.b;
        list13.add("zyp");
    }

    protected HashMap<String, String> LT() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.54 Safari/537.36");
        return hashMap;
    }

    @Override
    public String categoryContent(String str, String str2, boolean z, HashMap<String, String> hashMap) {
        try {
            Map allData = getAllData(this, this, this);
            JSONArray jSONArray = new JSONArray();
            Set<String> keySet = allData.keySet();
            for (String str3 : keySet) {
                JSONObject jSONObject = (JSONObject) allData.get(str3);
                if (jSONObject.has("cat")) {
                    String string = jSONObject.getString("cat");
                    if (string.equals(str)) {
                        JSONObject jSONObject2 = new JSONObject();
                        String sb2 = "https://www.aliyundrive.com/s/" + jSONObject.getString("key");
                        jSONObject2.put("vod_id", sb2);
                        String string2 = jSONObject.getString("title");
                        jSONObject2.put("vod_name", string2);
                        jSONObject2.put("vod_pic", "https://www.lgstatic.com/i/image2/M01/15/7E/CgoB5lysLXCADg6ZAABapAHUnQM321.jpg");
                        jSONArray.put(jSONObject2);
                    }
                }
            }
            JSONObject jSONObject3 = new JSONObject();
            jSONObject3.put("page", 1);
            jSONObject3.put("pagecount", 1);
            int length = jSONArray.length();
            jSONObject3.put("limit", length);
            TimeUtils.getTimeZoneDatabaseVersion();
            int length2 = jSONArray.length();
            jSONObject3.put("total", length2);
            jSONObject3.put("list", jSONArray);
            return jSONObject3.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public String detailContent(List<String> list) {
        try {
            Map allData = getAllData(this, this, this);
            JSONObject jSONObject = new JSONObject(pushAgent.detailContent(list));
            JSONArray jSONArray = jSONObject.getJSONArray("list");
            int i = 0;
            while (true) {
                if (i >= list.size()) {
                    break;
                }
                if (i >= jSONArray.length()) {
                    break;
                }
                JSONObject jSONObject2 = jSONArray.getJSONObject(i);
                Pattern pattern = PushAgent.Folder;
                Matcher matcher = pattern.matcher(list.get(i));
                if (matcher.find()) {
                    String group = matcher.group(1);
                    if (allData.containsKey(group)) {
                        JSONObject jSONObject3 = (JSONObject) allData.get(group);
                        jSONObject2.put("vod_pic", "https://www.lgstatic.com/i/image2/M01/15/7E/CgoB5lysLXCADg6ZAABapAHUnQM321.jpg");

                    }
                }
                i++;
            }
            return jSONObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public synchronized Map getAllData(Object obj, Object obj2, Object obj3) {
        Map<String, JSONObject> map;
        obj.getClass();
        obj2.getClass();
        obj3.getClass();
        synchronized (this) {
            try {
                if (a == null) {
                    HashMap hashMap = new HashMap();
                    HashMap<String, String> LT = LT();
                    for (Iterator<String> iter = b.iterator(); iter.hasNext(); ) {
                        String element = iter.next();
                        String content = OkHttpUtil.string("https://gitcafe.net/alipaper/data/" + element + ".json", LT);
//                        JSONObject data = new JSONObject(content);
                        JSONArray jSONArray = new JSONArray(content);
 //      data.getJSONArray("data");
                        for (int i = 0; i < jSONArray.length(); i++) {
                            JSONObject jSONObject = jSONArray.getJSONObject(i);
                            hashMap.put(jSONObject.getString("key"), jSONObject);
                        }
                    }
                    a = hashMap;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            map = a;
        }
        return map;
    }

    public JSONObject getHomeData(Object obj) {
        obj.getClass();
        try {
            if (Home == null) {
                HashMap<String, String> LT = LT();
                Home = new JSONObject(OkHttpUtil.string("https://gitcafe.net/alipaper/home.json", LT));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Home;
    }

    @Override
    public String homeContent(boolean z) {
        String str = "https://www.lgstatic.com/i/image2/M01/15/7E/CgoB5lysLXCADg6ZAABapAHUnQM321.jpg";

        try {
            JSONObject homeData = getHomeData(this);
            JSONObject jSONObject = homeData.getJSONObject("info");
            JSONArray jSONArray = jSONObject.getJSONArray("new");
            JSONArray jSONArray2 = new JSONArray();
            List<String> list = b;
            Iterator<String> it = list.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                String next = it.next();
                JSONObject jSONObject2 = new JSONObject();
                jSONObject2.put("type_id", next);
                JSONObject jSONObject3 = d;
                String string = jSONObject3.getString(next);
                jSONObject2.put("type_name", string);
                jSONArray2.put(jSONObject2);
            }
            Map allData = getAllData(this, this, this);
            JSONArray jSONArray3 = new JSONArray();
            for (int i = 0; i < jSONArray.length(); i++) {
                JSONObject jSONObject4 = jSONArray.getJSONObject(i);
                String string2 = jSONObject4.getString("cat");
                List<String> list2 = b;
                if (list2.contains(string2)) {
                    String string3 = jSONObject4.getString("key");
                    if (allData.containsKey(string3)) {
                        JSONObject jSONObject5 = (JSONObject) allData.get(string3);
                        JSONObject jSONObject6 = new JSONObject();
                        StringBuilder sb = new StringBuilder();
                        sb.append("https://www.aliyundrive.com/s/");
                        String string4 = jSONObject4.getString("key");
                        sb.append(string4);
                        String sb2 = sb.toString();
                        jSONObject6.put("vod_id", sb2);
                        String string5 = jSONObject4.getString("title");
                        jSONObject6.put("vod_name", string5);
                        jSONObject6.put("vod_pic", str);
                        String string6 = jSONObject4.getString("date");
                        jSONObject6.put("vod_remarks", string6);
                        jSONArray3.put(jSONObject6);

                    }
                    JSONObject jSONObject62 = new JSONObject();
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("https://www.aliyundrive.com/s/");
                    String string42 = jSONObject4.getString("key");
                    sb3.append(string42);
                    String sb22 = sb3.toString();
                    jSONObject62.put("vod_id", sb22);
                    String string52 = jSONObject4.getString("title");
                    jSONObject62.put("vod_name", string52);
                    jSONObject62.put("vod_pic", str);
                    String string62 = jSONObject4.getString("date");
                    jSONObject62.put("vod_remarks", string62);
                    jSONArray3.put(jSONObject62);
                }
            }
            JSONObject jSONObject7 = new JSONObject();
            jSONObject7.put("class", jSONArray2);
            jSONObject7.put("list", jSONArray3);
            return jSONObject7.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public String playerContent(String str, String str2, List<String> list) {
        return pushAgent.playerContent(str, str2, list);
    }

    @Override
    public String searchContent(String str, boolean z) {
        try {
            Map allData = getAllData(this, this, this);
            JSONArray jSONArray = new JSONArray();
            Set keySet = allData.keySet();
            Iterator it = keySet.iterator();
            while (true) {
                if (it.hasNext()) {
                    Object next = it.next();
                    JSONObject jSONObject = (JSONObject) allData.get(next);
                    if (jSONObject.has("cat")) {
                        String string = jSONObject.getString("cat");
                        List<String> list = b;
                        if (list.contains(string)) {
                            String string2 = jSONObject.getString("title");
                            if (string2.contains(str)) {
                                JSONObject jSONObject2 = new JSONObject();
                                StringBuilder sb = new StringBuilder();
                                sb.append("https://www.aliyundrive.com/s/");
                                String string3 = jSONObject.getString("key");
                                sb.append(string3);
                                String sb2 = sb.toString();
                                jSONObject2.put("vod_id", sb2);
                                jSONObject2.put("vod_name", string2);
                                jSONArray.put(jSONObject2);
                            }
                        }
                    }
                } else {
                    JSONObject jSONObject3 = new JSONObject();
                    jSONObject3.put("list", jSONArray);
                    return jSONObject3.toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}