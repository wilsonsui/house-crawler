package com.wilson.selenium;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
/**
 * @author wilson
 */
public class SeleniumTest {
    public static void main(String[] args) {
        System.setProperty("webdriver.edge.driver", "/Users/suishunli/Desktop/selenium/msedgedriver");
        WebDriver driver = new EdgeDriver();
        // 2.打开百度首页
        driver.get("https://www.baidu.com");
        // 5.退出浏览器
//        driver.quit();

    }
}
