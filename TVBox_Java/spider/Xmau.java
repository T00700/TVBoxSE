package com.github.catvod.spider;

import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;


import com.github.catvod.utils.Misc;
import com.github.catvod.utils.okhttp.OKCallBack;
import com.github.catvod.utils.okhttp.OkHttpUtil;
import okhttp3.Call;
import okhttp3.Response;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Calendar;
import java.net.URLDecoder;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Xmau extends Spider {
    @Override
    public void init(Context context) {super.init(context);}


    @Override
    public String homeContent(boolean filter) {
        try {
            JSONObject cateobj = new JSONObject();
            JSONArray classes = new JSONArray();
            JSONObject result = new JSONObject();
            JSONArray videos = new JSONArray();
            JSONObject v = new JSONObject();
            cateobj.put("type_name", "test111");
            cateobj.put("type_id", "1");
            classes.put(cateobj);
            result.put("class", classes);
            v.put("vod_id", "testid222");
            v.put("vod_name", "testname333");
            v.put("vod_pic","https://pic.rmb.bdstatic.com/bjh/1d0b02d0f57f0a42201f92caba5107ed.jpeg");
            v.put("vod_remarks", "testmark444");
            videos.put(v);
            result.put("list", videos);
            return result.toString();
        } catch (
                Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }

    @Override
    public String detailContent(List<String> ids) {
        try {

            JSONObject result = new JSONObject();
            JSONObject vodList = new JSONObject();
            vodList.put("vod_id", "id");
            vodList.put("vod_name", "Xmau解密练习，写固定播放连结，有需要请自行补齐");
            vodList.put("vod_pic", "https://pic.rmb.bdstatic.com/bjh/1d0b02d0f57f0a42201f92caba5107ed.jpeg");
            vodList.put("vod_year", "year9999");
            vodList.put("type_name", "type7777");
            vodList.put("vod_area","are0000a" );
            vodList.put("vod_actor", "actor0987");
            vodList.put("vod_content", "content1234567890");
            String playList = "";
            List<String> vodItems = new ArrayList<>();
            String pl1 = "playname1$playlink1";
            String pl2 = "playname2$playlink2";
            vodItems.add(pl1);
            vodItems.add(pl2);
            if (vodItems.size() > 0)
                playList = TextUtils.join("#", vodItems);
            if (playList.length() == 0)
                playList = "nothing here";
            Map<String, String> vod_play = new TreeMap<>();
            vod_play.put("test", playList);
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


    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) {
        try {
            long currentTime = System.currentTimeMillis()/ 1000;
            String apikey=getapikey();
            String deturl ="http://eg.xmau.cn/lvdou_api.php/v1.vod/detail?vod_id=8055&rel_limit=10&apikey="+apikey+"&keytime="+currentTime;
            String detsrc = OkHttpUtil.string(deturl, getHeaders(deturl));
            JSONObject detsrcobj = new JSONObject(detsrc);
            JSONObject detsrcobjdata= detsrcobj.getJSONObject("data");
            String requrlsrc = detsrcobjdata.getString("vod_play_url");
            String requrl = requrlsrc.substring(3);
            currentTime = System.currentTimeMillis()/ 1000;
            apikey=getapikey();
            String reqapiurl="https://h5.freejson.xyz/?url="+requrl+"&apikey="+apikey+"&keytime="+currentTime;
            String reqapirsp = OkHttpUtil.string(reqapiurl, getHeaders(reqapiurl));
            Document doc = Jsoup.parse(reqapirsp);
            Elements allScript = doc.select("script");
            String iv="";
            String enyurl="";
            for (int i = 0; i < allScript.size(); i++) {
                String scContent = allScript.get(i).toString();
                if (scContent.contains("var le_token")) {
                    System.out.println("fpplog scContent:"+scContent);
                    int start = scContent.indexOf('"');
                    int end = scContent.lastIndexOf('"') + 1;
                    iv = scContent.substring(start+1, end-1);
                }
                if (scContent.contains("var config")) {
                    int start = scContent.indexOf("var config =");
                    int end = scContent.lastIndexOf('}') + 1;
                    String j1 = scContent.substring(start, end);
                    start = j1.indexOf('(');
                    end = j1.lastIndexOf(')') ;
                    enyurl = j1.substring(start+2, end-1);
                    break;
                }
            }
            String enyurlb64dec=toHex(Base64.decode(enyurl.getBytes(), Base64.DEFAULT));
            String key = "A42EAC0C2B408472";
            String videolink=decrypt(enyurlb64dec,key,iv);
            JSONObject result = new JSONObject();
            result.put("url", videolink);
            result.put("header", new JSONObject(getHeaders(videolink)).toString());
            result.put("parse", 0);
            result.put("playUrl", "");
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }



    private HashMap<String, String> getHeaders(String url) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("User-Agent","User-Agent:okhttp/4.1.0");
        return headers;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    protected String getapikey() {
        try {
            Calendar cal=Calendar.getInstance();
            int y=cal.get(Calendar.YEAR);
            int h=cal.get(Calendar.HOUR_OF_DAY);
            int m=cal.get(Calendar.MINUTE);
            String apikeyori=y+":"+(h<10?("0"+h):h)+":"+y+":"+(m<10?("0"+m):m)+":lvdoutv-1.0.0";
            String apikey=Misc.MD5(apikeyori,StandardCharsets.UTF_8);
            return apikey;
        } catch (Exception exception) {
            SpiderDebug.log(exception);
        }
        return null;
    }
    protected String decrypt(String src, String KEY, String IV) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), "AES");
            AlgorithmParameterSpec paramSpec = new IvParameterSpec(IV.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, keySpec, paramSpec);
            return new String(cipher.doFinal(hex2byte(src)));
        } catch (Exception exception) {
            SpiderDebug.log(exception);
        }
        return null;
    }
    protected String encrypt(String src, String KEY, String IV) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), "AES");
            AlgorithmParameterSpec paramSpec = new IvParameterSpec(IV.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, paramSpec);
            return bytesToHexStr(cipher.doFinal(src.getBytes())).toUpperCase();
        } catch (Exception exception) {
            SpiderDebug.log(exception);
        }
        return null;
    }

    protected byte[] hex2byte(String hex) throws IllegalArgumentException {
        if (hex.length() % 2 != 0){
            throw new IllegalArgumentException("invalid hex string");
        }
        char[] arr = hex.toCharArray();
        byte[] b = new byte[hex.length() / 2];
        for (int i = 0, j = 0, l = hex.length(); i < l; i++, j++) {
            String swap ="" + arr[i++] + arr[i];
            int byteint = Integer.parseInt(swap, 16) & 0xFF;
            b[j] = new Integer(byteint).byteValue();
        }
        return b;
    }

    protected String bytesToHexStr(byte[] bytes) {
        StringBuilder hexStr = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            hexStr.append(hex);
        }
        return hexStr.toString();
    }


    private static final char[] DIGITS
            = {'0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private static final String toHex(byte[] data) {
        final StringBuffer sb = new StringBuffer(data.length * 2);
        for (int i = 0; i < data.length; i++) {
            sb.append(DIGITS[(data[i] >>> 4) & 0x0F]);
            sb.append(DIGITS[data[i] & 0x0F]);
        }
        return sb.toString();
    }




}
