package com.wilson.ke;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

import java.util.ArrayList;
import java.util.List;

public class MyCookieJar implements CookieJar {
    private List<Cookie> cookies = new ArrayList<>();

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        this.cookies.addAll(cookies);
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        return cookies;
    }
}