package com.wilson.config;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Random;

public class RandomUserAgentInterceptor implements Interceptor {
    private final String[] userAgents = {
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36 Edg/92.0.902.84",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36",
    };

    @Override
    public Response intercept(Chain chain) throws IOException {
        String randomUserAgent = getRandomUserAgent();

        Request request = chain.request()
                .newBuilder()
                .header("User-Agent", randomUserAgent)
                //添加cookies

                .build();

        return chain.proceed(request);
    }

    private String getRandomUserAgent() {
        int index = new Random().nextInt(userAgents.length);
        return userAgents[index];
    }
}