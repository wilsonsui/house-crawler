package com.wilson.task;

import cn.hutool.core.collection.ListUtil;
import com.wilson.entity.House;
import com.wilson.mapper.HouseMapper;
import com.wilson.util.CrawlerUtil;
import com.wilson.util.HttpRequestUtil;
import com.wilson.util.ParseHtmlUtil;
import com.wilson.util.UrlListUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author wilson
 */
@Component
@Slf4j
public class HouseTask {
    @Autowired
    CrawlerUtil crawlerUtil;

    //每30分钟执行一次
    @Scheduled(cron = "0 0/30 * * * ?")
    public void list() {
//        crawlerUtil.getHouseList();
        log.error("定时任务开始抓取列表");
    }

    @Scheduled(cron = "0 0/50 * * * ?")
    public void updateDatail() {
//        crawlerUtil.updateList();
    }

}
