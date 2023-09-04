package com.wilson.util;

import com.wilson.config.RandomUserAgentInterceptor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author wilson
 */
@Slf4j
public class CrawlerUtil {
    public static String doRequest(String url) {
        OkHttpClient httpClient = new OkHttpClient.Builder().
                addInterceptor(new RandomUserAgentInterceptor()).callTimeout(600, TimeUnit.SECONDS).build();

        Request request = new Request.Builder().url(url).get().build();
        String resp;
        try (Response response = httpClient.newCall(request).execute()) {
            assert response.body() != null;
            if (!response.isSuccessful()) {
                return null;
            }
            resp = response.body().string();
        } catch (IOException e) {
            return null;
        }
        try {
            TimeUnit.MILLISECONDS.sleep(900);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return resp;
    }
}
