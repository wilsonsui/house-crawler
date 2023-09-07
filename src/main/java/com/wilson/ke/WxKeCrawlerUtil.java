package com.wilson.ke;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wilson.entity.NtKeHouse;
import com.wilson.entity.NtKeHouseChange;
import com.wilson.entity.WxKeHouse;
import com.wilson.entity.WxKeHouseChange;
import com.wilson.mapper.NtKeHouseChangeMapper;
import com.wilson.mapper.NtKeHouseMapper;
import com.wilson.mapper.WxKeHouseChangeMapper;
import com.wilson.mapper.WxKeHouseMapper;
import com.wilson.util.CrawlerUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;

/**
 * 无锡二手房爬取数据
 *
 * @author wilson
 */
@Slf4j
@Component
public class WxKeCrawlerUtil {
    @Autowired
    Executor threadPoolExecutor;

    @Autowired
    WxKeHouseMapper keHouseMapper;

    @Autowired
    WxKeHouseChangeMapper keHouseChangeMapper;


    private static Set<String> allUrlList = new LinkedHashSet<>();

    static {
        allUrlList.add("https://wx.ke.com/ershoufang/changguangxidaxuecheng/pg${1}l3l4");
        allUrlList.add("https://wx.ke.com/ershoufang/haianchengshiminzhongxin/pg${1}l3l4");
        allUrlList.add("https://wx.ke.com/ershoufang/heliekou/pg${1}l3l4");
        allUrlList.add("https://wx.ke.com/ershoufang/huazhuang/pg${1}l3l4");
        allUrlList.add("https://wx.ke.com/ershoufang/hudai/pg${1}l3l4");
        allUrlList.add("https://wx.ke.com/ershoufang/lihuxincheng/pg${1}l3l4");
        allUrlList.add("https://wx.ke.com/ershoufang/mashan/pg${1}l3l4");
        allUrlList.add("https://wx.ke.com/ershoufang/meiyuan/pg${1}l3l4");
        allUrlList.add("https://wx.ke.com/ershoufang/nanquan1/pg${1}l3l4");
        allUrlList.add("https://wx.ke.com/ershoufang/rongchuangwenhualvyoucheng/pg${1}l3l4");
        allUrlList.add("https://wx.ke.com/ershoufang/rongxiang/pg${1}l3l4");
        allUrlList.add("https://wx.ke.com/ershoufang/taihushanshuicheng/pg${1}l3l4");
        allUrlList.add("https://wx.ke.com/ershoufang/xintiyuzhongxin/pg${1}l3l4");
        allUrlList.add("https://wx.ke.com/ershoufang/beidajie/pg${1}l3l4");
        allUrlList.add("https://wx.ke.com/ershoufang/guangruiguangfeng/pg${1}l3l4");
        allUrlList.add("https://wx.ke.com/ershoufang/hubinshangyejie/pg${1}l3l4");
        allUrlList.add("https://wx.ke.com/ershoufang/minfengzhuangqian/pg${1}l3l4");
        allUrlList.add("https://wx.ke.com/ershoufang/qingmingqiao/pg${1}l3l4");
        allUrlList.add("https://wx.ke.com/ershoufang/sanyangguangchang/pg${1}l3l4");
        allUrlList.add("https://wx.ke.com/ershoufang/wanxiangchengdongjiang/pg${1}l3l4");
        allUrlList.add("https://wx.ke.com/ershoufang/wuaiguangchang/pg${1}l3l4");
        allUrlList.add("https://wx.ke.com/ershoufang/yangming/pg${1}l3l4");
        allUrlList.add("https://wx.ke.com/ershoufang/yinglongqiao/pg${1}l3l4");
        allUrlList.add("https://wx.ke.com/ershoufang/zhongqiao/pg${1}l3l4");

        allUrlList.add("https://wx.ke.com/ershoufang/hongshan4/pg${1}l3l4");
        allUrlList.add("https://wx.ke.com/ershoufang/jiangxi/pg${1}l3l4");
        allUrlList.add("https://wx.ke.com/ershoufang/meicun/pg${1}l3l4");
        allUrlList.add("https://wx.ke.com/ershoufang/shengtaiyuan/pg${1}l3l4");
        allUrlList.add("https://wx.ke.com/ershoufang/shuofang/pg${1}l3l4");
        allUrlList.add("https://wx.ke.com/ershoufang/tangnan/pg${1}l3l4");
        allUrlList.add("https://wx.ke.com/ershoufang/tangtieqiao/pg${1}l3l4");
        allUrlList.add("https://wx.ke.com/ershoufang/wangzhuang/pg${1}l3l4");
        allUrlList.add("https://wx.ke.com/ershoufang/xinan2/pg${1}l3l4");

        allUrlList.add("https://wx.ke.com/ershoufang/huangxiang/pg${1}l3l4");
        allUrlList.add("https://wx.ke.com/ershoufang/liutan/pg${1}l3l4");
        allUrlList.add("https://wx.ke.com/ershoufang/luoshe/pg${1}l3l4");
        allUrlList.add("https://wx.ke.com/ershoufang/qianqiao/pg${1}l3l4");
        allUrlList.add("https://wx.ke.com/ershoufang/qianzhou/pg${1}l3l4");
        allUrlList.add("https://wx.ke.com/ershoufang/shanbei/pg${1}l3l4");
        allUrlList.add("https://wx.ke.com/ershoufang/shitangwan/pg${1}l3l4");
        allUrlList.add("https://wx.ke.com/ershoufang/xizhang/pg${1}l3l4");
        allUrlList.add("https://wx.ke.com/ershoufang/yangshan/pg${1}l3l4");
        allUrlList.add("https://wx.ke.com/ershoufang/yanqiao/pg${1}l3l4");
        allUrlList.add("https://wx.ke.com/ershoufang/yuqi/pg${1}l3l4");

        allUrlList.add("https://wx.ke.com/ershoufang/anzhen/pg${1}l3l4");
        allUrlList.add("https://wx.ke.com/ershoufang/dongbeitang/pg${1}l3l4");
        allUrlList.add("https://wx.ke.com/ershoufang/donggang/pg${1}l3l4");
        allUrlList.add("https://wx.ke.com/ershoufang/dongting/pg${1}l3l4");
        allUrlList.add("https://wx.ke.com/ershoufang/ehu/pg${1}l3l4");
        allUrlList.add("https://wx.ke.com/ershoufang/fangqian/pg${1}l3l4");
        allUrlList.add("https://wx.ke.com/ershoufang/guangyi/pg${1}l3l4");
        allUrlList.add("https://wx.ke.com/ershoufang/shangmadun/pg${1}l3l4");
        allUrlList.add("https://wx.ke.com/ershoufang/xibei/pg${1}l3l4");
        allUrlList.add("https://wx.ke.com/ershoufang/yangjian/pg${1}l3l4");

    }


    private Integer pageSize = 100;


    public void crawlerAll() {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        ArrayList<Future<?>> futures = new ArrayList<>();
        for (List<String> stringList : ListUtil.split(new ArrayList<>(allUrlList), 2)) {
            Future<?> future = executorService.submit(() -> {
                for (String url : stringList) {
                    for (Integer i = 1; i <= pageSize; i++) {
                        String newUrl = url.replace("${1}", i.toString());
                        log.error("处理wx贝壳链接:{}", newUrl);
                        String html = CrawlerUtil.doRequest(newUrl);
                        if (StrUtil.isNotBlank(html)) {
                            List<WxKeHouse> houseList = parseList(html);
                            if (CollectionUtil.isEmpty(houseList)) {
                                //跳出内层循环
                                break;
                            }
                            threadPoolExecutor.execute(() -> {
                                saveAndUpdateKeHouse(houseList);
                            });
                        }
                        try {
                            TimeUnit.SECONDS.sleep(3);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
            futures.add(future);
        }
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        log.error("爬取所有wx三四房列表完毕！");
    }


    public void crawler(List<String> urlList) {
        for (String url : urlList) {
            log.error("处理wx贝壳链接:{}", url);
            String html = CrawlerUtil.doRequest(url);
            List<WxKeHouse> houseList = parseList(html);
            if (CollectionUtil.isEmpty(houseList)) {
                break;
            }
            threadPoolExecutor.execute(() -> {
                saveAndUpdateKeHouse(houseList);
            });
            try {
                TimeUnit.SECONDS.sleep(8);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }

    /**
     * 解析列表页 获取房子的数据
     *
     * @param html
     * @return
     */
    public List<WxKeHouse> parseList(String html) {
        List<WxKeHouse> keHouseList = new ArrayList<>();
        Document document = Jsoup.parse(html);
        Elements elements = document.select("ul[class=sellListContent]").select("li[class=clear]");
        for (Element element : elements) {
            WxKeHouse keHouse = new WxKeHouse();
            String title = element.select("div[class=title]").select("a").text();
            keHouse.setTitle(title);
            String href = element.select("div[class=title]").select("a").attr("href");
            keHouse.setUrl(href);
            String houseInfo = element.select("div[class=houseInfo]").text();
            String[] houseInfoArr = houseInfo.split("\\|");
            for (String s : houseInfoArr) {
                if (s.contains("年建")) {
                    keHouse.setYear(Integer.parseInt(s.trim().replace("年建", "")));
                }
            }
            String positionInfo = element.select("div[class=positionInfo]").text();
            keHouse.setCommunityName(positionInfo.trim());
            String totalPrice = element.select("div[class=totalPrice totalPrice2]").select("span").text();
            keHouse.setPrice(Double.parseDouble(totalPrice.trim()));
            String unitPrice = element.select("div[class=unitPrice]").select("span").text();
            keHouse.setUnitPrice(Double.parseDouble(unitPrice.replace("元/平", "").replace(",", "")));
            String followInfo = element.select("div[class=followInfo]").text().split("\\/")[0].trim();
            keHouse.setFollow(Integer.parseInt(followInfo.replace("人关注", "")));
            keHouse.setCreateTime(new Date());
            keHouseList.add(keHouse);
        }
        return keHouseList;
    }


    public void updateKeHouseD() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (; ; ) {
            log.error("循环========");
            List<WxKeHouse> keHouseList = keHouseMapper.selectList(new QueryWrapper<WxKeHouse>()
                    .isNull("area1").isNull("status").last("limit 1000"));
            if (CollectionUtil.isEmpty(keHouseList)) {
                break;
            }
            List<Future<?>> futureList = new ArrayList<>();
            ListUtil.split(keHouseList, 100).forEach(keHouses -> {
                Future<?> future = executorService.submit(() -> {
                    for (WxKeHouse keHouse : keHouses) {
                        log.error("更新房屋数据:{}", keHouse.getUrl());
                        String url = keHouse.getUrl();
                        String html = CrawlerUtil.doRequest(url);
                        if (html.equals("404")) {
                            //更新house状态
                            WxKeHouse update = new WxKeHouse();
                            update.setId(keHouse.getId());
                            update.setStatus(0);
                            keHouseMapper.updateById(update);
                        } else {
                            parseDetail(html, keHouse);
                        }
                    }
                });
                futureList.add(future);
            });
            for (Future<?> future : futureList) {
                future.get();
            }
            //生成10之内的随机数
            Random random = new Random();
            int i = random.nextInt(10);
            try {
                TimeUnit.SECONDS.sleep(i);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        executorService.shutdown();
        log.error("更新房屋详情数据结束");
    }


    /**
     * 解析详情页 获取房子的详情数据
     *
     * @param htmlDetail
     * @return
     */
    public void parseDetail(String htmlDetail, WxKeHouse keHouse) {
        if (htmlDetail == null) {
            return;
        }
        WxKeHouse updateKeHouse = new WxKeHouse();
        updateKeHouse.setId(keHouse.getId());
        updateKeHouse.setUpdateTime(new Date());
        Document document = Jsoup.parse(htmlDetail);
        //崇川区 虹桥
        String text = document.selectFirst("div[class=areaName]")
                .selectFirst("span[class=info]").text();
        updateKeHouse.setArea1(text.split(" ")[0]);
        updateKeHouse.setArea2(text.split(" ")[1]);
        //电梯有无
        for (Element element : document.selectFirst("div[class=base]").selectFirst("div[class=content]").selectFirst("ul").select("li")) {
            String span = element.text();
            if (span.contains("配备电梯")) {
                updateKeHouse.setElevator(span.replace("配备电梯", "").trim());
            } else if (span.contains("梯户比例")) {
                updateKeHouse.setTihubili(span.replace("梯户比例", "").trim());
            } else if (span.contains("装修情况")) {
                updateKeHouse.setDecorationType(span.replace("装修情况", "").trim());
            } else if (span.contains("房屋户型")) {
                updateKeHouse.setUnitType(span.replace("房屋户型", "").trim());
            } else if (span.contains("所在楼层")) {
                updateKeHouse.setFloor(span.replace("所在楼层", "").replace("咨询楼层", "").trim());
            } else if (span.contains("建筑面积")) {
                updateKeHouse.setJzArea(Double.parseDouble(span.replace("建筑面积", "").replace("㎡", "").trim()));
            } else if (span.contains("户型结构")) {
                updateKeHouse.setHouseType(span.replace("户型结构", "").trim());
            } else if (span.contains("房屋朝向")) {
                updateKeHouse.setOrientation(span.replace("房屋朝向", "").trim());
            }
        }
        for (Element element : document.selectFirst("div[class=transaction]").selectFirst("div[class=content]").selectFirst("ul").select("li")) {
            String span = element.text();
            if (span.contains("挂牌时间")) {
                if (span.contains("年")) {
                    updateKeHouse.setListingTime(DateUtil.parse(span.replace("挂牌时间", "").trim(), "yyyy年MM月dd日"));
                }
            } else if (span.contains("交易权属")) {
                updateKeHouse.setPropertyRight(span.replace("交易权属", "").trim());
            } else if (span.contains("上次交易")) {
                if (span.contains("年")) {
                    updateKeHouse.setLastTradeTime(DateUtil.parse(span.replace("上次交易", "").trim(), "yyyy年MM月dd日"));
                }
            } else if (span.contains("房屋年限")) {
                updateKeHouse.setAge(span.replace("房屋年限", "").trim());
            }
        }
        //根据id 更新房子数据
        updateById(updateKeHouse);
    }

    public void updateById(WxKeHouse keHouse) {
        keHouseMapper.updateById(keHouse);
    }

    public void saveAndUpdateKeHouse(List<WxKeHouse> keHouseList) {
        for (WxKeHouse keHouse : keHouseList) {
            WxKeHouse selectedOne = keHouseMapper.selectOne(new QueryWrapper<WxKeHouse>()
                    .eq("url", keHouse.getUrl()));
            if (selectedOne != null) {
                if (ObjectUtil.notEqual(selectedOne.getPrice(), keHouse.getPrice())) {
                    WxKeHouseChange keHouseChange = new WxKeHouseChange();
                    keHouseChange.setHouseId(selectedOne.getId());
                    keHouseChange.setUnitPrice(keHouse.getUnitPrice());
                    keHouseChange.setPrice(keHouse.getPrice());
                    keHouseChange.setUpdateTime(new Date());
                    keHouseChangeMapper.insert(keHouseChange);
                }
                //更新
                keHouse.setId(selectedOne.getId());
                keHouse.setUpdateTime(new Date());
                //如果是更新 原来价格不变
                keHouse.setPrice(selectedOne.getPrice());
                keHouse.setUnitPrice(selectedOne.getUnitPrice());
                keHouseMapper.updateById(keHouse);
            } else {
                keHouseMapper.insert(keHouse);
            }

        }
    }

    public static void main(String[] args) {
        String html = "<div>\n" +
                "                                  <b>A</b>\n" +
                "                                      <a href=\"/ershoufang/anzhen/l3l4\">安镇</a>\n" +
                "                                                    <b>D</b>\n" +
                "                                      <a href=\"/ershoufang/dongbeitang/l3l4\">东北塘</a>\n" +
                "                                      <a href=\"/ershoufang/donggang/l3l4\">东港</a>\n" +
                "                                      <a href=\"/ershoufang/dongting/l3l4\">东亭</a>\n" +
                "                                                    <b>E</b>\n" +
                "                                      <a href=\"/ershoufang/ehu/l3l4\">鹅湖</a>\n" +
                "                                                    <b>F</b>\n" +
                "                                      <a href=\"/ershoufang/fangqian/l3l4\">坊前</a>\n" +
                "                                                    <b>G</b>\n" +
                "                                      <a href=\"/ershoufang/guangyi/l3l4\">广益</a>\n" +
                "                                                    <b>S</b>\n" +
                "                                      <a href=\"/ershoufang/shangmadun/l3l4\">上马墩</a>\n" +
                "                                                    <b>X</b>\n" +
                "                                      <a href=\"/ershoufang/xibei/l3l4\">锡北</a>\n" +
                "                                                    <b>Y</b>\n" +
                "                                      <a href=\"/ershoufang/yangjian/l3l4\">羊尖</a>\n" +
                "                                                </div>";

        Jsoup.parse(html).select("div").select("a").forEach(element -> {
//            allUrlList.add("https://nt.ke.com/ershoufang/hongqiao2/pg${1}l3");
            System.out.println("allUrlList.add(\"https://nt.ke.com" + element.attr("href").replace("l3l4", "pg${1}l3l4") + "\");");
        });
    }
}
