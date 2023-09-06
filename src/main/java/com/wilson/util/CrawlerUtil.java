package com.wilson.util;

import com.wilson.config.RandomUserAgentInterceptor;
import com.wilson.ke.MyCookieJar;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

/**
 * @author wilson
 */
@Slf4j
@Component
public class CrawlerUtil {
    @Autowired
    StringRedisTemplate redisTemplate;

    public static String doRequest(String url) {
        OkHttpClient httpClient = new OkHttpClient.Builder().
                addInterceptor(new RandomUserAgentInterceptor())
                .cookieJar(new MyCookieJar())
                .callTimeout(6000, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS).build();

        Request request = new Request.Builder().url(url).get().build();
        String resp;
        try (Response response = httpClient.newCall(request).execute()) {
            assert response.body() != null;
            if (!response.isSuccessful()) {
                return null;
            }
            resp = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try {
            TimeUnit.MILLISECONDS.sleep(300);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return resp;
    }


    public static String doRequestWithProxy(String url) {
        //构建代理
        String proxyStr = "127.0.0.1";
        int port = 4000;
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyStr, port));
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .cookieJar(new MyCookieJar())
                .addInterceptor(new RandomUserAgentInterceptor()).callTimeout(6000, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS).proxy(proxy).build();

        Request request = new Request.Builder().url(url).get().build();
        String resp;
        try (Response response = httpClient.newCall(request).execute()) {
            assert response.body() != null;
            if (!response.isSuccessful()) {
                return null;
            }
            resp = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try {
            TimeUnit.MILLISECONDS.sleep(300);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return resp;
    }

    public static void main(String[] args) {
        String url = "https://nt.ke.com/ershoufang/rs/";
        String resp = doRequestWithProxy(url);
        System.out.println(resp);
    }
}
