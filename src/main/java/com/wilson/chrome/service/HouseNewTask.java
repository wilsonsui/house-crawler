package com.wilson.chrome.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wilson.chrome.CustomQueue;
import com.wilson.entity.House;
import com.wilson.service.HouseService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static com.wilson.util.UrlListUtil.allUrlList;

/**
 * @author wilson
 */
@Slf4j
@SpringBootTest
@Component
public class HouseNewTask {
    @Autowired
    CrawleListService crawleListService;

    @Autowired
    private CustomQueue<House> customQueue;

    @Autowired
    HouseService houseService;

    @Scheduled(cron = "0 0/50 * * * ?")
    @Test
    public void 爬详情() {
        System.setProperty("webdriver.chrome.driver", "/Users/suishunli/Desktop/selenium/chromedriver-mac-arm64/chromedriver");
        ExecutorService executorService = Executors.newFixedThreadPool(11);
        long l = System.currentTimeMillis();
        while (true) {
            //查询前10条
            List<House> houseList = houseService.list(new QueryWrapper<House>()
                            .isNull("area1")
//                    .isNull("cl_area")
                            .isNull("status")
//                            .eq("status", 0)
//                    .orderByAsc("create_time")
                            .last("limit 7")
            );
            if (CollectionUtil.isEmpty(houseList)) {
                break;
            }
            List<Future<?>> list = new ArrayList<>();
            houseList.forEach(house -> {
                Future<?> submit = executorService.submit(() -> {
                    crawleListService.crawDetail(house);
                    try {
                        TimeUnit.MILLISECONDS.sleep(800);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
                list.add(submit);
            });
            for (Future<?> future : list) {
                try {
                    future.get();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
//            break;
        }

        log.error("总耗时:{}", System.currentTimeMillis() - l);
    }

    //每30分钟执行一次

    @Scheduled(cron = "0 0/30 * * * ?")
    @Test
    public void 爬列表()   {
        System.setProperty("webdriver.chrome.driver", "/Users/suishunli/Desktop/selenium/chromedriver-mac-arm64/chromedriver");
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        long l = System.currentTimeMillis();
        List<Future<?>> list = new ArrayList<>();
        allUrlList.forEach(url -> {
            Future<?> submit = executorService.submit(() -> {
                crawleListService.crawlList(url);
                try {
                    TimeUnit.MILLISECONDS.sleep(800);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            list.add(submit);
        });
        for (Future<?> future : list) {
            try {
                future.get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        log.error("总耗时:{}", System.currentTimeMillis() - l);
    }
}
