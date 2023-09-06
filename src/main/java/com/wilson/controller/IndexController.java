package com.wilson.controller;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wilson.config.RandomUserAgentInterceptor;
import com.wilson.crawler.AbstractCrawler;
import com.wilson.entity.ProxyEntity;
import com.wilson.fangtianxia.FTXCrawlerUtil;
import com.wilson.ke.KeCrawlerUtil;
import com.wilson.mapper.HouseChangeDetailMapper;
import com.wilson.mapper.HouseMapper;
import com.wilson.pojo.House;
import com.wilson.pojo.HouseChangeDetail;
import com.wilson.queue.VerifyQueue;
import com.wilson.util.CrawlerUtil;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * @author wilson
 */
@RestController
@Slf4j
public class IndexController {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    Executor threadPoolExecutor;

    @Autowired
    List<AbstractCrawler> abstractCrawlerList;

    @Autowired
    FTXCrawlerUtil ftxCrawlerUtil;
    @Autowired
    KeCrawlerUtil keCrawlerUtil;

    //爬取南通的房子
    @GetMapping("/keHouse")
    public String crawlerKeHouse() throws IOException {
        new Thread(() -> {
            keCrawlerUtil.crawlerAll();
        }).start();
        return "success";
    }

    //更新每个房子的详情
    @GetMapping("/updateKeHouse")
    public String updateKeHouse() throws IOException, ExecutionException, InterruptedException {
        keCrawlerUtil.updateKeHouseD();
        return "success";
    }

    @GetMapping("/crawlIP")
    public void crawlTask() {
        for (AbstractCrawler crawler : abstractCrawlerList) {
            threadPoolExecutor.execute(() -> {
                for (String url : crawler.urlList()) {
                    String html = CrawlerUtil.doRequest(url);
                    List<ProxyEntity> proxyList = crawler.parse(html);
                    proxyList.stream().forEach(proxyEntity -> {
                        stringRedisTemplate.opsForValue().set(proxyEntity.getHost(), JSONObject.toJSONString(proxyEntity));
                    });
                }
            });

        }
    }

    @Autowired
    HouseMapper houseMapper;
    @Autowired
    HouseChangeDetailMapper houseChangeDetailMapper;


    //    @GetMapping("/verify")
    //20秒执行一次 cron表达式

    @GetMapping("/crawlerHouse")
    public void crawler() throws IOException {
        List<String> houseUrl = ftxCrawlerUtil.getHouseUrl();
        ftxCrawlerUtil.crawler(houseUrl);
    }

}
