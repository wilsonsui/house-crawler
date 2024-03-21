package com.wilson.douban;

import com.alibaba.excel.EasyExcel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ChromeWebDriverTest {
    public static void main(String[] args) throws InterruptedException {
        System.setProperty("webdriver.chrome.driver", "/Users/suishunli/Desktop/selenium/chromedriver-mac-arm64/chromedriver");
        ChromeOptions options = new ChromeOptions();
//        options.setHeadless(true);//无头模式,后端运行
        options.addArguments("--remote-allow-origins=*");//允许跨域
        options.addArguments("--disable-gpu");//谷歌文档提到需要加上这个属性来规避bug
        options.addArguments("blink-settings=imagesEnabled=false");//不加载图片, 提升速度
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);//设置页面加载策略
        WebDriver driver = new ChromeDriver(options);
        driver.get("https://www.douban.com/group/shanghaizufang/discussion?start=0&type=new");
        String pageSource = driver.getPageSource();
        List<DouBanExcel> douBan1 = getDouBan(pageSource);
        List<DouBanExcel> douBanExcelList = new ArrayList<>();
        //把首次进来的数据加入进来
        douBanExcelList.addAll(douBan1);
        //初次休息两秒
        TimeUnit.SECONDS.sleep(3);
        for (int i = 1; i <= 15; i++) {
            System.out.println("当前页数：" + i);
            try {
                //找到页面中的下一页元素，进行点击
                //解析当前页元素
                //解析下一页元素
                WebElement nextElement = driver.findElement(By.linkText("后页>"));
                if (nextElement == null) {
                    break;
                }
                TimeUnit.SECONDS.sleep(1);
                nextElement.click();
                String pageSource1 = driver.getPageSource();
                List<DouBanExcel> douBan = getDouBan(pageSource1);
                douBanExcelList.addAll(douBan);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            } finally {
//                driver.quit();
//                driver.close();
//                return;
            }
        }
        //导出
        EasyExcel.write("豆瓣上海租房小区1.xlsx", DouBanExcel.class).sheet("小区").doWrite(douBanExcelList);
    }


    public static List<DouBanExcel> getDouBan(String html) {
        Document document = Jsoup.parse(html);
        Elements elements = document.select("tbody tr");
        List<DouBanExcel> douBanExcels = new ArrayList<>();
        for (Element element : elements) {
            if (element.hasClass("th")) {
                continue;
            }
            DouBanExcel douBanExcel = new DouBanExcel();
            Elements children = element.children();
            String href = children.get(0).select("a").attr("href");
            douBanExcel.setUrl(href);
            String title = children.get(0).select("a").attr("title");
            douBanExcel.setTitle(title);

            String count = children.get(2).text();
            douBanExcel.setCount(count);

            String time = children.get(3).text();
            douBanExcel.setTime(time);
            douBanExcels.add(douBanExcel);
        }
        return douBanExcels;
    }
}