package com.wilson.chrome;

import org.openqa.selenium.By;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * @author wilson
 */

public class ChromeWebDriverTest {
    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "/Users/suishunli/Desktop/selenium/chromedriver-mac-arm64/chromedriver");
        ChromeOptions options = new ChromeOptions();
//        options.setHeadless(true);//无头模式,后端运行
        options.addArguments("--remote-allow-origins=*");//允许跨域
        options.addArguments("--disable-gpu");//谷歌文档提到需要加上这个属性来规避bug
        options.addArguments("blink-settings=imagesEnabled=false");//不加载图片, 提升速度
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);//设置页面加载策略
        WebDriver driver = new ChromeDriver(options);

        driver.get("https://su.ke.com/ershoufang/gongyeyuan/pg1su1dp1ie2sf1a3a4a5l2l3l4l5l6p2p3p4/");
        String pageSource = driver.getPageSource();
        for (int i = 2; i <= 31; i++) {
            //找到页面中的下一页元素，进行点击
            //解析当前页元素
            //解析下一页元素
            WebElement nextElement = driver.findElement(By.linkText("下一页"));
            if (nextElement == null) {
                break;
            }
            nextElement.click();
        }
    }
}
