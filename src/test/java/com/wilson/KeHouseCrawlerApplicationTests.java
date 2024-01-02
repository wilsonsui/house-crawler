package com.wilson;

import com.wilson.chrome.service.CrawleService;
import com.wilson.entity.House;
import com.wilson.mapper.HouseMapper;
import com.wilson.task.HouseTask;
import com.wilson.util.UrlListUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

import static com.wilson.util.UrlListUtil.allUrlList;

@Slf4j
@SpringBootTest
class KeHouseCrawlerApplicationTests {
    @Resource
    HouseTask houseTask;

    @Resource
    HouseMapper houseMapper;

    @Resource
    CrawleService crawleService;

    @Test
    public void 浏览器爬取() {
        long l = System.currentTimeMillis();
        System.setProperty("webdriver.chrome.driver", "/Users/suishunli/Desktop/selenium/chromedriver-mac-arm64/chromedriver");
        allUrlList.forEach(url -> {
            crawleService.crawlAndSaveData(url);
            log.error("处理下一个链接");
        });
        log.error("总耗时:{}", System.currentTimeMillis() - l);
    }


    @Test
    void 抓去列表() {
        houseTask.list();
    }

    @Test
    void 更新列表() {
        houseTask.updateDatail();
    }


}
