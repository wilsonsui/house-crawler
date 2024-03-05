package com.wilson.chrome.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wilson.chrome.CustomQueue;
import com.wilson.entity.House;
import com.wilson.entity.HousePrice;
import com.wilson.entity.HouseTraffic;
import com.wilson.service.HousePriceService;
import com.wilson.service.HouseService;
import com.wilson.service.HouseTrafficService;
import com.wilson.util.ParseHtmlUtil;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 只负责抓取列表页
 *
 * @author wilson
 */
@Slf4j
@Service
public class CrawleListService {

    @Autowired
    HouseService houseService;
    @Autowired
    HouseTrafficService houseTrafficService;

    @Autowired
    HousePriceService housePriceService;

    public void crawlList(String url) {
//        log.error("爬取一个列表数据开始:{}", url);
        WebDriver webDriver = createWebDriver();
        try {
            // 进行爬取逻辑，将数据保存到数据库
            webDriver.get(url);
            String pageSource = webDriver.getPageSource();
            List<House> houseList = ParseHtmlUtil.parseList(pageSource);
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(700);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                List<WebElement> webElements = webDriver.findElements(By.linkText("下一页"));
                if (webElements.size() <= 0) {
                    break;
                }
                webElements.get(0).click();

                pageSource = webDriver.getPageSource();
                houseList.addAll(ParseHtmlUtil.parseList(pageSource));
            }
            for (House houseDetail : houseList) {
                houseService.saveHouse(houseDetail);
            }
        } catch (Exception e) {
            log.error("爬取列表数据异常:{}", e.getMessage());
        } finally {
            // 关闭 WebDriver
            webDriver.close();
            webDriver.quit();
        }
    }


    public void crawDetail(House houseDetail) {
        WebDriver webDriver = createWebDriver();
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(20)); // 设置等待超时时间为10秒
        String htmlDetail = "";
        try {
            String houseUrl = houseDetail.getUrl();
            webDriver.get(houseUrl);
            htmlDetail = webDriver.getPageSource();
            if (htmlDetail.contains("未找到页面")) {
                log.error("找不到页面 houseId:{}", houseDetail.getId());
                House house = new House();
                house.setId(houseDetail.getId());
                if (houseDetail.getStatus() == null || houseDetail.getStatus() == 0) {//原来已经是0了 再次找不到就设置为1
                    house.setStatus(1);
                } else {
                    house.setStatus(0);
                }
                houseService.updateById(house);
                return;
            }
            ParseHtmlUtil.parseDetail(htmlDetail, houseDetail);
            //首次loading消失
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div[class='loading']")));
            //等待元素是否可点击
            WebElement trafficElement = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("li[data-bl='traffic']")));
            trafficElement.click();
            //等待loading消失
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div[class='loading']")));
            String trafficHtmlDetail = webDriver.getPageSource();
            List<HouseTraffic> houseTrafficList = ParseHtmlUtil.parseTraffic(trafficHtmlDetail);
            houseDetail.setHouseTrafficList(houseTrafficList);
            houseService.saveHouse(houseDetail);
        } catch (Exception e) {
            log.error("抓取详情报错:{},{}", e.getMessage(), houseDetail.getUrl());
        } finally {
            webDriver.quit();
        }
    }


    private static WebDriver createWebDriver() {
        ChromeOptions options = new ChromeOptions();
        Logger.getLogger("org.openqa.selenium").setLevel(Level.WARNING);
        options.setHeadless(true);//无头模式,后端运行
        options.addArguments("--remote-allow-origins=*");//允许跨域
        options.addArguments("--disable-extensions");//禁用扩展
//        options.addArguments("--disable-gpu");//谷歌文档提到需要加上这个属性来规避bug
        options.addArguments("blink-settings=imagesEnabled=false");//不加载图片, 提升速度
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage");//禁用沙盒
        options.addArguments("--disable-infobars");//禁用浏览器正在被自动化程序控制的提示
        options.addArguments("--disable-web-security");//禁用web安全
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);//设置页面加载策略
        WebDriver driver = new ChromeDriver(options);
        return driver;
    }
}
