package com.wilson.chrome.service;

import com.wilson.entity.House;
import com.wilson.entity.HouseTraffic;
import com.wilson.service.HouseService;
import com.wilson.util.ParseHtmlUtil;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author wilson
 */
@Slf4j
@Service
public class CrawleService {

    private final HouseService houseService;

    @Autowired
    public CrawleService(HouseService houseService) {
        this.houseService = houseService;
    }

    public void crawlAndSaveData(String url) {
        WebDriver webDriver = createWebDriver();
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(20)); // 设置等待超时时间为10秒
        try {
            // 进行爬取逻辑，将数据保存到数据库
            webDriver.get(url);
            String pageSource = webDriver.getPageSource();
            List<House> houseList = ParseHtmlUtil.parseList(pageSource);
            while (true) {
                List<WebElement> webElements = webDriver.findElements(By.linkText("下一页"));
                if (webElements.size() <= 0) {
                    break;
                }
                webElements.get(0).click();
                pageSource = webDriver.getPageSource();
                houseList.addAll(ParseHtmlUtil.parseList(pageSource));
            }
            for (House houseDetail : houseList) {
                TimeUnit.SECONDS.sleep(3);
                CompletableFuture.runAsync(() -> {
                    log.error("开始爬取数据:{}", houseDetail.getUrl());
                    String houseUrl = houseDetail.getUrl();
                    webDriver.get(houseUrl);
                    String htmlDetail = webDriver.getPageSource();
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
                    try {
                        houseService.saveHouse(houseDetail);
                    } catch (Exception e) {
                        log.error("保存数据异常:{}", e.getMessage());
                    }
                });
            }
        } catch (Exception e) {
            log.error("爬取数据异常:{}", e.getMessage());
        } finally {
            // 关闭 WebDriver
            webDriver.quit();
        }
    }

//    public static void main(String[] args) {
////        for (int i = 0; i < 100; i++) {
//            System.setProperty("webdriver.chrome.driver", "/Users/suishunli/Desktop/selenium/chromedriver-mac-arm64/chromedriver");
//            WebDriver webDriver = createWebDriver();
//            WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(20)); // 设置等待超时时间为10秒
//            webDriver.get("https://su.ke.com/ershoufang/107108541337.html");
//            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div[class='loading']")));
//            WebElement trafficElement = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("li[data-bl='traffic']")));
//            trafficElement.click();
//            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("li[data-index='subway0']")));
//            String trafficHtmlDetail = webDriver.getPageSource();
//            List<HouseTraffic> houseTrafficList = ParseHtmlUtil.parseTraffic(trafficHtmlDetail);
//            System.out.println(houseTrafficList);
////        }
//    }


//    public static void main(String[] args) {
//        for (int i = 0; i < 100; i++) {
//            System.setProperty("webdriver.chrome.driver", "/Users/suishunli/Desktop/selenium/chromedriver-mac-arm64/chromedriver");
//            WebDriver webDriver = createWebDriver();
//            WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(10)); // 设置等待超时时间为10秒
//            webDriver.get("https://su.ke.com/ershoufang/107108541337.html");
//            //首次loading消失
//            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div[class='loading']")));
//            //等待元素是否可点击
//            WebElement trafficElement = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("li[data-bl='traffic']")));
//            trafficElement.click();
//            //等待loading消失
//            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div[class='loading']")));
//            String trafficHtmlDetail = webDriver.getPageSource();
//            System.out.println("1");
////        List<HouseTraffic> houseTrafficList = ParseHtmlUtil.parseTraffic(trafficHtmlDetail);
////        System.out.println(houseTrafficList);
//        }
//    }

    private static WebDriver createWebDriver() {
        ChromeOptions options = new ChromeOptions();
        options.setHeadless(true);//无头模式,后端运行
        options.addArguments("--remote-allow-origins=*");//允许跨域
//        options.addArguments("--disable-gpu");//谷歌文档提到需要加上这个属性来规避bug
        options.addArguments("blink-settings=imagesEnabled=false");//不加载图片, 提升速度
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);//设置页面加载策略
        WebDriver driver = new ChromeDriver(options);
        return driver;
    }

}
