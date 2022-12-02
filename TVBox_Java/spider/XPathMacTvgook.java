package com.github.catvod.spider;

import android.content.Context;
import android.text.TextUtils;

import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.utils.Misc;
import com.github.catvod.utils.okhttp.OkHttpUtil;
import com.github.catvod.xpath.XPathRule;

import org.json.JSONArray;
import org.json.JSONObject;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class XPathMacTvgook extends XPathMac {

    @Override
    public boolean manualVideoCheck() {
        return rule.getHomeUrl().contains("tvgook.com");
		
    }
	
	@Override
    public boolean isVideoFormat(String url) {
       return Misc.isVideoFormat(url)||(url.contains("/ppvod/")&&!url.contains("skipl=1"));
    }
	
	 
}
