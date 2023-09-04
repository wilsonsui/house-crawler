package com.wilson.controller;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wilson.config.RandomUserAgentInterceptor;
import com.wilson.crawler.AbstractCrawler;
import com.wilson.entity.ProxyEntity;
import com.wilson.fangtianxia.FTXCrawlerUtil;
import com.wilson.mapper.HouseChangeDetailMapper;
import com.wilson.mapper.HouseMapper;
import com.wilson.pojo.House;
import com.wilson.pojo.HouseChangeDetail;
import com.wilson.queue.VerifyQueue;
import com.wilson.util.CrawlerUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisKeyValueTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * @author wilson
 */
@RestController
public class IndexController {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    Executor threadPoolExecutor;

    @Autowired
    List<AbstractCrawler> abstractCrawlerList;

    @GetMapping("/crawlTask")
    public void crawlTask() {
        for (AbstractCrawler crawler : abstractCrawlerList) {
            for (String url : crawler.urlList()) {
                String html = CrawlerUtil.doRequest(url);
                List<ProxyEntity> proxyList = crawler.parse(html);
                proxyList.stream().forEach(proxyEntity -> {
                    stringRedisTemplate.opsForValue().set(proxyEntity.getHost(), JSONObject.toJSONString(proxyEntity));
                });
            }
        }
    }

    @Autowired
    HouseMapper houseMapper;
    @Autowired
    HouseChangeDetailMapper houseChangeDetailMapper;


    @GetMapping("/verify")
    public void verify() throws IOException {
//        VerifyQueue verifyQueue = new VerifyQueue();
//        //取出redis中所有的key
//        Set<String> keys = stringRedisTemplate.keys("*");
//        keys.stream().forEach(key -> {
//            String s = stringRedisTemplate.opsForValue().get(key);
//            ProxyEntity proxyEntity = JSON.parseObject(s, ProxyEntity.class);
//            verifyQueue.add(proxyEntity);
//        });
//        verifyQueue.stream().forEach(obj -> {
//            threadPoolExecutor.execute(() -> {
//                verify(obj);
//            });
//        });
        List<House> test = FTXCrawlerUtil.test();
        for (House house : test) {
            house.setCreateTime(new Date());
            House one = houseMapper.selectOne(new QueryWrapper<House>()
                    .eq("url", house.getUrl()));
            if (one != null) {
                //不为空 判断 价格是否发生了变化
                if (!one.getPrice().equals(house.getPrice())) {
                    house.setId(one.getId());
                    houseMapper.updateById(house);
                    HouseChangeDetail houseChangeDetail = new HouseChangeDetail();
                    houseChangeDetail.setHouseId(one.getId());
                    houseChangeDetail.setUnitPrice(house.getUnitPrice());
                    houseChangeDetail.setPrice(house.getPrice());
                    houseChangeDetail.setChangeTime(new Date());
                    //插入变更详情表中
                    houseChangeDetailMapper.insert(houseChangeDetail);
                }
            } else {
                houseMapper.insert(house);
            }

        }

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
}
