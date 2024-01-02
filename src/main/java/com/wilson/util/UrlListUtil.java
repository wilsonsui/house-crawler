package com.wilson.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author wilson
 */
public class UrlListUtil {

    public static Set<String> allUrlList = new LinkedHashSet<>();

    static {
        //苏州 90页
        allUrlList.add("https://su.ke.com/ershoufang/wuzhong/su1dp1ie2sf1a3a4a5l3l4l5l6p2p3p4/");
        allUrlList.add("https://su.ke.com/ershoufang/gusu/su1dp1ie2sf1a3a4a5l3l4l5l6p2p3p4/");
        allUrlList.add("https://su.ke.com/ershoufang/gaoxinqu24/su1dp1ie2sf1a3a4a5l3l4l5l6p2p3p4/");
        allUrlList.add("https://su.ke.com/ershoufang/wujiang/su1dp1ie2sf1a3a4a5l3l4l5l6p2p3p4/");
        allUrlList.add("https://su.ke.com/ershoufang/kunshan/su1dp1ie2sf1a3a4a5l3l4l5l6p2p3p4/");
        allUrlList.add("https://su.ke.com/ershoufang/gongyeyuan/su1dp1ie2sf1a3a4a5l3l4l5l6p2p3p4/");


        //南通四大区 近地铁， 最大60页
//        allUrlList.add("https://nt.ke.com/ershoufang/nantongjingjijishukaifaqu/su1sf1a4a5a6a7a8l3l4l5p3p4/"); //53页
//        allUrlList.add("https://nt.ke.com/ershoufang/chongchuanqu/su1sf1a4a5a6a7a8l3l4l5p3p4/");
//        allUrlList.add("https://nt.ke.com/ershoufang/tongzhouqu/su1sf1a4a5a6a7a8l3l4l5p3p4/");
//        allUrlList.add("https://nt.ke.com/ershoufang/gangzhaqu/su1sf1a4a5a6a7a8l3l4l5p3p4/");
//////        //无锡 40页
//        allUrlList.add("https://wx.ke.com/ershoufang/binhu/su1sf1a4a5a6l3l4l5p5p6/");
//        allUrlList.add("https://wx.ke.com/ershoufang/liangxi/su1sf1a4a5a6l3l4l5p5p6/");
//        allUrlList.add("https://wx.ke.com/ershoufang/xinwu/su1sf1a4a5a6l3l4l5p5p6/");
//        allUrlList.add("https://wx.ke.com/ershoufang/huishan/su1sf1a4a5a6l3l4l5p5p6/");
//        allUrlList.add("https://wx.ke.com/ershoufang/xishan/su1sf1a4a5a6l3l4l5p5p6/");

    }

    public static void main(String[] args) {
        //处理苏州下级二手房列表
        String base = "https://su.ke.com/ershoufang/su1dp1ie2sf1a3a4a5l3l4l5l6p2p3p4/";
        String html = HttpRequestUtil.doRequest(base);
        Document document = Jsoup.parse(html);
        System.out.println(document);


    }


    private static Integer pageSize = 0;

    public static List<String> getUrlList() {
        List<String> urlList = new ArrayList<>();
        for (String baseUrl : allUrlList) {
            if (baseUrl.contains("wx.ke")) {
                pageSize = 40;
            }
            if (baseUrl.contains("su.ke")) {
                pageSize = 100;
            }
            if (baseUrl.contains("nt.ke")) {
                pageSize = 60;
            }
            for (int i = 1; i <= pageSize; i++) {
                urlList.add(baseUrl.replace("${1}", Integer.toString(i)));
            }
        }
        return urlList;
    }
}
