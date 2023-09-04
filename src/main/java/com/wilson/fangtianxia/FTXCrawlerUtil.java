package com.wilson.fangtianxia;

import com.wilson.pojo.House;
import com.wilson.util.CrawlerUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 房天下 爬虫类
 *
 * @author wilson
 */

@Slf4j
public class FTXCrawlerUtil {
    private static String baseUrl = "https://nt.esf.fang.com";

    private Integer pageSize = 50;

    //https://nt.esf.fang.com/house/i32/?rfss=1-11ae26ac08c31ecf06-66
    private List<String> getHouseUrl() {
        List<String> urlList = new ArrayList<>();
        for (Integer i = 1; i < pageSize; i++) {
            if (i == 1) {
                urlList.add(baseUrl);
            } else {
                urlList.add(baseUrl + "/house/i3" + i + "/");
            }
        }
        return urlList;
    }


    public List<House> crawler(List<String> urlList) {
        List<House> houseList = new ArrayList<>();
        for (String url : urlList) {
            String html = CrawlerUtil.doRequest(url);
            if (html.contains("跳转中")) {
                //获取真实跳转链接
                Document document = Jsoup.parse(html);
                //真实请求链接
                String hrefUrl = document.select(".btn-redir").get(0).attr("href");
                System.out.println(hrefUrl);
                html = CrawlerUtil.doRequest(hrefUrl);
            }
            System.out.println(html);
        }
        return houseList;
    }

    public static List<House> parse(String html) {


        return null;
    }

    public static List<House> test() throws IOException {
        File file = new File("/Users/suishunli/爬虫project/free-proxy-crawler/src/main/java/com/wilson/controller/test.html");
        List<House> houseList = new ArrayList<>();
        Document document = Jsoup.parse(file);
        Element shopListEle = document.select("div.shop_list.shop_list_4").first();
        Elements dlElements = shopListEle.select("dl");
        log.debug(dlElements.toString());
        for (Element dlElement : dlElements) {
            House house = new House();
            String url = dlElement.select("a").first().attr("href");
            house.setUrl(baseUrl + url);
            String title = dlElement.select(".tit_shop").text().trim();
            house.setTitle(title);
            //4室2厅 | 120㎡ | 高层 （共17层） | 南北向 | 2020年建 | 乔振
            String telShopText = dlElement.select(".tel_shop").text();
            String[] telShopTextArr = telShopText.split("\\|");
            house.setUnitType(telShopTextArr[0].trim());
            house.setJzArea(telShopTextArr[1].replace("㎡", "").trim());
            house.setFloor(telShopTextArr[2].trim());
            house.setOrientation(telShopTextArr[3].trim());
            house.setBuildYear(telShopTextArr[4].trim());

            String communityName = dlElement.selectFirst(".add_shop a").text().trim();
            house.setCommunityName(communityName);
            String communityAddress = dlElement.selectFirst(".add_shop span").text().trim();
            house.setCommunityAddress(communityAddress);

            Element element = dlElement.selectFirst(".price_right");
            Elements elements = element.select("span");
            String price = elements.get(0).text().trim();
            house.setPrice(Double.parseDouble(price.replace("万", "")));
            String unitPrice = elements.get(1).text().trim();
            house.setUnitPrice(Double.parseDouble(unitPrice.replace("元/㎡", "")));
            houseList.add(house);
        }
        return houseList;

    }
}
