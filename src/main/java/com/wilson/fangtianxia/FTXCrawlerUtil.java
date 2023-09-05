package com.wilson.fangtianxia;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wilson.mapper.HouseChangeDetailMapper;
import com.wilson.mapper.HouseMapper;
import com.wilson.pojo.House;
import com.wilson.pojo.HouseChangeDetail;
import com.wilson.util.CrawlerUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * 房天下 爬虫类
 *
 * @author wilson
 */

@Slf4j
@Component
public class FTXCrawlerUtil {

    @Autowired
    private HouseMapper houseMapper;

    @Autowired
    private HouseChangeDetailMapper houseChangeDetailMapper;

    @Autowired
    Executor threadPoolExecutor;

    private static String baseUrl = "https://nt.esf.fang.com";

    private Integer pageSize = 60;

    //https://nt.esf.fang.com/house/i32/?rfss=1-11ae26ac08c31ecf06-66
    public List<String> getHouseUrl() {
        List<String> urlList = new ArrayList<>();
        for (Integer i = 60; i <= pageSize; i++) {
            if (i == 1) {
                urlList.add(baseUrl);
            } else {
                urlList.add(baseUrl + "/house/i3" + i + "/");
            }
        }
        return urlList;
    }


    public void crawler(List<String> urlList) {
        for (String url : urlList) {
            String html = CrawlerUtil.doRequest(url);
            log.error("爬取链接：{}", url);
            if (html.contains("跳转中")) {
                //获取真实跳转链接
                Document document = Jsoup.parse(html);
                //真实请求链接
                String hrefUrl = document.select(".btn-redir").get(0).attr("href");
                try {
                    TimeUnit.MILLISECONDS.sleep(400);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                log.error("爬取链接：{}", hrefUrl);
                html = CrawlerUtil.doRequest(hrefUrl);
            }
            List<House> houses = parse(html);
            threadPoolExecutor.execute(() -> {
                log.error("房产数据入库 size：{}", houses.size());
                saveHouse(houses);
            });
            //随机睡眠 一定的时间
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void saveHouse(List<House> houses) {
        for (House house : houses) {
            house.setCreateTime(new Date());
            House one = houseMapper.selectOne(new QueryWrapper<House>()
                    .eq("url", house.getUrl()));
            if (one != null) {
                //不为空 判断 价格是否发生了变化
                if (ObjectUtil.notEqual(one.getPrice(), house.getPrice()) &&
                        ObjectUtil.notEqual(one.getUnitPrice(), house.getUnitPrice())) {
                    house.setId(one.getId());
                    houseMapper.updateById(house);
                    HouseChangeDetail houseChangeDetail = new HouseChangeDetail();
                    houseChangeDetail.setHouseId(one.getId());
                    houseChangeDetail.setUnitPrice(house.getUnitPrice());
                    houseChangeDetail.setPrice(house.getPrice());
                    houseChangeDetail.setChangeTime(new Date());
                    //插入变更详情表中
                    houseChangeDetailMapper.insert(houseChangeDetail);
                }
            } else {
                houseMapper.insert(house);
            }
        }
    }

    public List<House> parse(String html) {
        try {
            log.error("解析房产数据");
            List<House> houseList = new ArrayList<>();
            Document document = Jsoup.parse(html);
            Element shopListEle = document.select("div.shop_list.shop_list_4").first();
            Elements dlElements = shopListEle.select("dl");
            for (Element dlElement : dlElements) {
                House house = new House();
                String url = dlElement.select("a").first().attr("href");
                house.setUrl(baseUrl + url);
                String title = dlElement.select(".tit_shop").text().trim();
                if (StrUtil.isBlank(title)) {
                    continue;
                }
                house.setTitle(title);

                //4室2厅 | 120㎡ | 高层 （共17层） | 南北向 | 2020年建 | 乔振
                String telShopText = dlElement.select(".tel_shop").text();
                log.error("telShopText:{}", telShopText);
                String[] telShopTextArr = telShopText.split("\\|");
                house.setUnitType(telShopTextArr[0].trim());
                house.setJzArea(telShopTextArr[1].replace("㎡", "").trim());
                house.setFloor(telShopTextArr[2].trim());
                house.setOrientation(telShopTextArr[3].trim());
                if (telShopTextArr.length > 4) {
                    house.setBuildYear(telShopTextArr[4].trim());
                }

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
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
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
