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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static com.wilson.util.UrlListUtil.allUrlList;

/**
 * @author wilson
 */
@Slf4j
@SpringBootTest
public class HouseNewTask {
    @Autowired
    CrawleListService crawleListService;

    @Autowired
    private CustomQueue<House> customQueue;

    @Autowired
    HouseService houseService;

    @Test
    public void 爬详情() {
        System.setProperty("webdriver.chrome.driver", "/Users/suishunli/Desktop/selenium/chromedriver-mac-arm64/chromedriver");
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        long l = System.currentTimeMillis();
        while (true) {
            //查询前10条
            List<House> houseList = houseService.list(new QueryWrapper<House>()
                    .isNull("area1")
                    .last("limit 11"));
            if (CollectionUtil.isEmpty(houseList)) {
                break;
            }
            List<Future<?>> list = new ArrayList<>();
            houseList.forEach(house -> {
                Future<?> submit = executorService.submit(() -> {
                    crawleListService.crawDetail(house);
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
        }

        log.error("总耗时:{}", System.currentTimeMillis() - l);
    }

    @Test
    public void 爬列表() {
        System.setProperty("webdriver.chrome.driver", "/Users/suishunli/Desktop/selenium/chromedriver-mac-arm64/chromedriver");
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        long l = System.currentTimeMillis();
        List<Future<?>> list = new ArrayList<>();
        allUrlList.forEach(url -> {
            Future<?> submit = executorService.submit(() -> {
                crawleListService.crawlList(url);
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
