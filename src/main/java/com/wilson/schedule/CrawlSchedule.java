package com.wilson.schedule;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wilson.config.RandomUserAgentInterceptor;
import com.wilson.crawler.AbstractCrawler;
import com.wilson.entity.ProxyEntity;
import com.wilson.queue.VerifyQueue;
import com.wilson.util.CrawlerUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * 爬虫定时任务
 *
 * @author wilson
 */
@Slf4j
@Component
public class CrawlSchedule {
    @Autowired
    VerifyQueue verifyQueue;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    Executor threadPoolExecutor;

    @Autowired
    List<AbstractCrawler> abstractCrawlerList;

    @Scheduled(cron = "0/60 * * * * ?")
    @Async("myExecutor")
    public void verify() {
        log.error("校验IP是否可用:开始");
        VerifyQueue verifyQueue = new VerifyQueue();
        //取出redis中所有的key
        Set<String> keys = stringRedisTemplate.keys("*");
        keys.stream().forEach(key -> {
            String s = stringRedisTemplate.opsForValue().get(key);
            ProxyEntity proxyEntity = JSON.parseObject(s, ProxyEntity.class);
            verifyQueue.add(proxyEntity);
        });
        verifyQueue.stream().forEach(obj -> {
            threadPoolExecutor.execute(() -> {
                verify(obj);
            });
        });
    }

    //验证代理是否可用方法
    public void verify(ProxyEntity proxyEntity) {
        String type = proxyEntity.getType();
        Proxy.Type proxyType;
        if ("http".equalsIgnoreCase(type) || "https".equalsIgnoreCase(type)) {
            proxyType = Proxy.Type.HTTP;
        } else if ("socket".equalsIgnoreCase(type)) {
            proxyType = Proxy.Type.SOCKS;
        } else {
            proxyType = Proxy.Type.HTTP;
        }

        Proxy proxy = new Proxy(proxyType, new InetSocketAddress(proxyEntity.getHost(), proxyEntity.getPort()));
        OkHttpClient client = new OkHttpClient.Builder().proxy(proxy)
                .connectTimeout(2, TimeUnit.SECONDS)
                .readTimeout(2, TimeUnit.SECONDS)
                .addInterceptor(new RandomUserAgentInterceptor()).build();
        Request request = new Request.Builder()
                .url("https://www.baidu.com/")
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                proxyEntity.setUpdateTime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
                stringRedisTemplate.opsForValue().set(proxyEntity.getHost(), JSONObject.toJSONString(proxyEntity));
            } else {
                stringRedisTemplate.delete(proxyEntity.getHost());
            }
        } catch (IOException e) {
            stringRedisTemplate.delete(proxyEntity.getHost());
        } finally {
        }
    }


    /**
     * 爬虫任务
     */
//    @PostConstruct
//    @Scheduled(cron = "0 0/10 * * * ?")
//    @Async("myExecutor")
    public void crawlTask() {
        log.info("爬取代理开始");
        for (AbstractCrawler crawler : abstractCrawlerList) {
            if (crawler.getClass() != com.wilson.crawler.XiaoHuanCrawler.class) {
                continue;
            }
            for (String url : crawler.urlList()) {
                String html = CrawlerUtil.doRequest(url);
                if (html == null) {
                    continue;
                }
                List<ProxyEntity> proxyList = crawler.parse(html);
                proxyList.stream().forEach(proxyEntity -> {
                    log.info("爬取到代理：{}", proxyEntity);
                    stringRedisTemplate.opsForValue().set(proxyEntity.getHost(), JSONObject.toJSONString(proxyEntity));
                });
            }
        }
        log.info("爬取代理结束");
    }
}
