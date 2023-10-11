package com.wilson.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

/**
 * @author wilson
 */
public class SeleniumTest {
    public static void main(String[] args) {
        System.setProperty("webdriver.edge.driver", "/Users/suishunli/Desktop/selenium/msedgedriver");
        EdgeOptions edgeOptions = new EdgeOptions();
        edgeOptions.addArguments("--headless");//无界面启动
        edgeOptions.addArguments("--remote-allow-origins=*");//解决403
        edgeOptions.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.96 Safari/537.36");//设置user-agent
        edgeOptions.addArguments("--disable-gpu");//解决bug
        //屏蔽webdriver特征
        edgeOptions.addArguments("--disable-blink-features=AutomationControlled");
        //
        edgeOptions.addArguments("--disable-blink-features");




        WebDriver driver = new EdgeDriver(edgeOptions);
        // 2.打开百度首页
        driver.get("https://www.baidu.com");
        String pageSource = driver.getPageSource();
        System.out.println(pageSource);
        // 5.退出浏览器
//        driver.quit();

    }
}
