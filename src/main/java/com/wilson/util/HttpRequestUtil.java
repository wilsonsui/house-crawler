package com.wilson.util;

import cn.hutool.http.*;

import java.net.HttpCookie;
import java.util.Random;

/**
 * @author wilson
 */

public class HttpRequestUtil {

    private final static String[] userAgents = {
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36 Edg/92.0.902.84",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36",
    };

    private static String getRandomUserAgent() {
        int index = new Random().nextInt(userAgents.length);
        return userAgents[index];
    }

    /**
     * 请求地址 返回页面
     *
     * @param url
     * @return
     */
    public static String doRequest(String url) {
        HttpRequest request = HttpUtil.createRequest(Method.GET, url)
                .header(Header.USER_AGENT, getRandomUserAgent())
                .header(Header.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
                .header(Header.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.9,en;q=0.8,ja;q=0.7,zh-TW;q=0.6")
                .setConnectionTimeout(10000)
                .setReadTimeout(10000);
        HttpResponse execute = request.execute();
        if (execute.getStatus() == 200) {
            return execute.body();
        }
        return null;
    }


}
