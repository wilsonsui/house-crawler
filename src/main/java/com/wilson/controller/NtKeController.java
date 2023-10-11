package com.wilson.controller;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wilson.crawler.AbstractCrawler;
import com.wilson.entity.NtKeHouse;
import com.wilson.entity.NtKeHouseChange;
import com.wilson.entity.ProxyEntity;
import com.wilson.fangtianxia.FTXCrawlerUtil;
import com.wilson.ke.NtKeCrawlerUtil;
import com.wilson.mapper.HouseChangeDetailMapper;
import com.wilson.mapper.HouseMapper;
import com.wilson.mapper.NtKeHouseChangeMapper;
import com.wilson.mapper.NtKeHouseMapper;
import com.wilson.util.CrawlerUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

/**
 * @author wilson
 */
@RestController
@Slf4j
@CrossOrigin
public class NtKeController {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    Executor threadPoolExecutor;

    @Autowired
    List<AbstractCrawler> abstractCrawlerList;

    @Autowired
    FTXCrawlerUtil ftxCrawlerUtil;
    @Autowired
    NtKeCrawlerUtil keCrawlerUtil;

    @Autowired
    NtKeHouseMapper keHouseMapper;
    @Autowired
    NtKeHouseChangeMapper keHouseChangeMapper;


    //爬取南通的房子
    @GetMapping("/nt1")
    public String crawlerKeHouse() throws IOException {
        keCrawlerUtil.crawlerAll();
        return "success";
    }

    //更新每个房子的详情
    @GetMapping("/nt2")
    public String updateKeHouse() throws IOException, ExecutionException, InterruptedException {
        keCrawlerUtil.updateKeHouseD();
        return "success";
    }

    /**
     * 根据id 获取详情，价格变化记录
     *
     * @param id
     * @return
     */
    @GetMapping("/detail/{id}")
    public List<Map<String, String>> detail(@PathVariable("id") Integer id) {
        List<NtKeHouseChange> keHouseChangeList = keHouseChangeMapper.selectList(new QueryWrapper<NtKeHouseChange>()
                .eq("house_id", id));

        //根据updateTime排序
        keHouseChangeList.sort((o1, o2) -> {
            if (o1.getUpdateTime().getTime() > o2.getUpdateTime().getTime()) {
                return -1;
            } else if (o1.getUpdateTime().getTime() < o2.getUpdateTime().getTime()) {
                return 1;
            } else {
                return 0;
            }
        });
        List<Map<String, String>> mapList = new ArrayList<>();
        for (NtKeHouseChange keHouseChange : keHouseChangeList) {
            Map<String, String> map = new HashMap<>();
            map.put("date", DateUtil.format(keHouseChange.getUpdateTime(), "yyyy-MM-dd HH:mm:ss"));
            map.put("unitPrice", keHouseChange.getUnitPrice() + "元/平米");
            map.put("price", keHouseChange.getPrice() + "万元");
            mapList.add(map);
        }
        return mapList;
    }

    @PostMapping("/page")
    public Page<NtKeHouse> data(@RequestBody PageReq req) {
        //查询分页数据
        Page<NtKeHouse> houseIPage = new Page<>(req.getPage(), req.getSize());
        QueryWrapper<NtKeHouse> keHouseQueryWrapper = new QueryWrapper<>();
        keHouseQueryWrapper.isNotNull("area1");
        keHouseQueryWrapper.eq("id", 4033);
        Page<NtKeHouse> housePage = keHouseMapper.selectPage(houseIPage, keHouseQueryWrapper);
        return housePage;
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
