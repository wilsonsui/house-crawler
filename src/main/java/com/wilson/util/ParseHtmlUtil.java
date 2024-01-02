package com.wilson.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.log.Log;
import com.wilson.entity.House;
import com.wilson.entity.HouseTraffic;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author wilson
 */
@Slf4j
public class ParseHtmlUtil {
    /**
     * 解析列表页
     *
     * @param html
     * @return
     */
    public static List<House> parseList(String html) {
        List<House> keHouseList = new ArrayList<>();
        Document document = Jsoup.parse(html);
        Elements elements = document.select("ul[class=sellListContent]").select("li[class=clear]");
        for (Element element : elements) {
            House house = new House();
            String title = element.select("div[class=title]").select("a").text();
            house.setTitle(title);
            String href = element.select("div[class=title]").select("a").attr("href");
            house.setUrl(href);
            String houseInfo = element.select("div[class=houseInfo]").text();
            String[] houseInfoArr = houseInfo.split("\\|");
            for (String s : houseInfoArr) {
                if (s.contains("年建")) {
                    house.setYear(Integer.parseInt(s.trim().replace("年建", "")));
                }
                if (s.contains("平米")) {
                    house.setJzArea(Double.parseDouble(s.trim().replace("平米", "")));
                }
            }
            String positionInfo = element.select("div[class=positionInfo]").text();
            house.setCommunityName(positionInfo.trim());
            house.setCommunityUrl(element.select("div[class=positionInfo]").select("a").attr("href"));
            String totalPrice = element.select("div[class=totalPrice totalPrice2]").select("span").text();
            house.setPrice(BigDecimal.valueOf(Double.parseDouble(totalPrice.trim())));
            String unitPrice = element.select("div[class=unitPrice]").select("span").text();
            house.setUnitPrice(BigDecimal.valueOf(Double.parseDouble(unitPrice.replace("元/平", "").replace(",", ""))));
            String followInfo = element.select("div[class=followInfo]").text().split("\\/")[0].trim();
            house.setFollow(Integer.parseInt(followInfo.replace("人关注", "")));
            keHouseList.add(house);
        }
        return keHouseList;
    }

    /**
     * 解析详情
     *
     * @param htmlDetail
     * @return
     */
    public static House parseDetail(String htmlDetail) {
        House updateHouse = new House();
        updateHouse.setUpdateTime(new Date());
        Document document = Jsoup.parse(htmlDetail);
        //崇川区 虹桥
        String text = document.selectFirst("div[class=areaName]")
                .selectFirst("span[class=info]").text();
        updateHouse.setArea1(text.split(" ")[0]);
        updateHouse.setArea2(text.split(" ")[1]);
        //电梯有无
        for (Element element : document.selectFirst("div[class=base]").selectFirst("div[class=content]").selectFirst("ul").select("li")) {
            String span = element.text();
            if (span.contains("配备电梯")) {
                updateHouse.setElevator(span.replace("配备电梯", "").trim());
            } else if (span.contains("梯户比例")) {
                updateHouse.setTihubili(span.replace("梯户比例", "").trim());
            } else if (span.contains("装修情况")) {
                updateHouse.setDecorationType(span.replace("装修情况", "").trim());
            } else if (span.contains("房屋户型")) {
                updateHouse.setUnitType(span.replace("房屋户型", "").trim());
            } else if (span.contains("所在楼层")) {
                updateHouse.setFloor(span.replace("所在楼层", "").replace("咨询楼层", "").trim());
            } else if (span.contains("建筑面积")) {
                updateHouse.setJzArea(Double.parseDouble(span.replace("建筑面积", "").replace("㎡", "").trim()));
            } else if (span.contains("户型结构")) {
                updateHouse.setHouseType(span.replace("户型结构", "").trim());
            } else if (span.contains("房屋朝向")) {
                updateHouse.setOrientation(span.replace("房屋朝向", "").trim());
            }
        }
        for (Element element : document.selectFirst("div[class=transaction]").selectFirst("div[class=content]").selectFirst("ul").select("li")) {
            String span = element.text();
            if (span.contains("挂牌时间")) {
                if (span.contains("年")) {
                    updateHouse.setListingTime(DateUtil.parse(span.replace("挂牌时间", "").trim(), "yyyy年MM月dd日"));
                }
            } else if (span.contains("交易权属")) {
                updateHouse.setPropertyRight(span.replace("交易权属", "").trim());
            } else if (span.contains("上次交易")) {
                if (span.contains("年")) {
                    updateHouse.setLastTradeTime(DateUtil.parse(span.replace("上次交易", "").trim(), "yyyy年MM月dd日"));
                }
            } else if (span.contains("房屋年限")) {
                updateHouse.setAge(span.replace("房屋年限", "").trim());
            }
        }
        return updateHouse;
    }


    public static void parseDetail(String htmlDetail, House updateHouse) {
        log.error("抓取详情链接:{}", updateHouse.getUrl());
        updateHouse.setUpdateTime(new Date());
        Document document = Jsoup.parse(htmlDetail);
        //崇川区 虹桥
        String text = document.selectFirst("div[class=areaName]")
                .selectFirst("span[class=info]").text();
        updateHouse.setArea1(text.split(" ")[0]);
        updateHouse.setArea2(text.split(" ")[1]);
        updateHouse.setPrice(BigDecimal.valueOf(Double.parseDouble(document.selectFirst("span[class=total]").text().replace("万", ""))));
        updateHouse.setUnitPrice(BigDecimal.valueOf(Double.parseDouble(document.selectFirst("span[class=unitPriceValue]").text())));
        //电梯有无
        for (Element element : document.selectFirst("div[class=base]").selectFirst("div[class=content]").selectFirst("ul").select("li")) {
            String span = element.text();
            if (span.contains("配备电梯")) {
                updateHouse.setElevator(span.replace("配备电梯", "").trim());
            } else if (span.contains("梯户比例")) {
                updateHouse.setTihubili(span.replace("梯户比例", "").trim());
            } else if (span.contains("装修情况")) {
                updateHouse.setDecorationType(span.replace("装修情况", "").trim());
            } else if (span.contains("房屋户型")) {
                updateHouse.setUnitType(span.replace("房屋户型", "").trim());
            } else if (span.contains("所在楼层")) {
                updateHouse.setFloor(span.replace("所在楼层", "").replace("咨询楼层", "").trim());
            } else if (span.contains("建筑面积")) {
                updateHouse.setJzArea(Double.parseDouble(span.replace("建筑面积", "").replace("㎡", "").trim()));
            } else if (span.contains("户型结构")) {
                updateHouse.setHouseType(span.replace("户型结构", "").trim());
            } else if (span.contains("房屋朝向")) {
                updateHouse.setOrientation(span.replace("房屋朝向", "").trim());
            }
        }
        for (Element element : document.selectFirst("div[class=transaction]").selectFirst("div[class=content]").selectFirst("ul").select("li")) {
            String span = element.text();
            if (span.contains("挂牌时间")) {
                if (span.contains("年")) {
                    updateHouse.setListingTime(DateUtil.parse(span.replace("挂牌时间", "").trim(), "yyyy年MM月dd日"));
                }
            } else if (span.contains("交易权属")) {
                updateHouse.setPropertyRight(span.replace("交易权属", "").trim());
            } else if (span.contains("上次交易")) {
                if (span.contains("年")) {
                    updateHouse.setLastTradeTime(DateUtil.parse(span.replace("上次交易", "").trim(), "yyyy年MM月dd日"));
                }
            } else if (span.contains("房屋年限")) {
                updateHouse.setAge(span.replace("房屋年限", "").trim());
            }
        }
    }


    public static List<HouseTraffic> parseTraffic(String htmlDetail) {
        List<HouseTraffic> houseTrafficList = new ArrayList<>();
        Document document = Jsoup.parse(htmlDetail);
        Elements select = document.select("div[class=contentBox]");
        for (Element element : select) {
            HouseTraffic houseTraffic = new HouseTraffic();
            Element first = element.selectFirst("span[class=itemText itemTitle]");
            String title = first.text();
            houseTraffic.setSubway(title);
            Element selectFirst = element.selectFirst("span[class=itemText itemdistance]");
            String distance = selectFirst.text();
            houseTraffic.setDistance(distance);
            houseTrafficList.add(houseTraffic);
        }
        return houseTrafficList;
    }
}
