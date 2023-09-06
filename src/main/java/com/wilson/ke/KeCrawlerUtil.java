package com.wilson.ke;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wilson.entity.KeHouse;
import com.wilson.entity.KeHouseChange;
import com.wilson.mapper.KeHouseChangeMapper;
import com.wilson.mapper.KeHouseMapper;
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
import java.util.*;
import java.util.concurrent.*;

/**
 * @author wilson
 */
@Slf4j
@Component
public class KeCrawlerUtil {
    @Autowired
    Executor threadPoolExecutor;

    @Autowired
    KeHouseMapper keHouseMapper;

    @Autowired
    KeHouseChangeMapper keHouseChangeMapper;

    //南通二手房

    private static String baseUrl = "https://nt.ke.com/ershoufang/binjiang3/pg${1}l3/";

    private static Set<String> allUrlList = new LinkedHashSet<>();

    static {
        allUrlList.add("https://nt.ke.com/ershoufang/hongqiao2/pg${1}l3");
        allUrlList.add("https://nt.ke.com/ershoufang/junshan/pg${1}l3");
        allUrlList.add("https://nt.ke.com/ershoufang/langshan/pg${1}l3");
        allUrlList.add("https://nt.ke.com/ershoufang/xincheng1/pg${1}l3");
        allUrlList.add("https://nt.ke.com/ershoufang/binjiang3/pg${1}l3");
        allUrlList.add("https://nt.ke.com/ershoufang/linjiang/pg${1}l3");
        allUrlList.add("https://nt.ke.com/ershoufang/sutong/pg${1}l3");
        allUrlList.add("https://nt.ke.com/ershoufang/haohe/pg${1}l3");
        allUrlList.add("https://nt.ke.com/ershoufang/rengang/pg${1}l3");
        allUrlList.add("https://nt.ke.com/ershoufang/tangzha/pg${1}l3");
        allUrlList.add("https://nt.ke.com/ershoufang/yongxing/pg${1}l3");
        allUrlList.add("https://nt.ke.com/ershoufang/chengdong4/pg${1}l3");
        allUrlList.add("https://nt.ke.com/ershoufang/chenqiao/pg${1}l3");
        allUrlList.add("https://nt.ke.com/ershoufang/chuanjiang/pg${1}l3");
        allUrlList.add("https://nt.ke.com/ershoufang/guanyinshan1/pg${1}l3");
        allUrlList.add("https://nt.ke.com/ershoufang/liuqiao/pg${1}l3");
        allUrlList.add("https://nt.ke.com/ershoufang/pingchao/pg${1}l3");
        allUrlList.add("https://nt.ke.com/ershoufang/qinzao/pg${1}l3");
        allUrlList.add("https://nt.ke.com/ershoufang/sanyu/pg${1}l3");
        allUrlList.add("https://nt.ke.com/ershoufang/shigang/pg${1}l3");
        allUrlList.add("https://nt.ke.com/ershoufang/shizong2/pg${1}l3");
        allUrlList.add("https://nt.ke.com/ershoufang/wujie/pg${1}l3");
        allUrlList.add("https://nt.ke.com/ershoufang/xianfeng/pg${1}l3");
        allUrlList.add("https://nt.ke.com/ershoufang/xingdong1/pg${1}l3");
        allUrlList.add("https://nt.ke.com/ershoufang/xingfu1/pg${1}l3");
        allUrlList.add("https://nt.ke.com/ershoufang/xingren/pg${1}l3");
        allUrlList.add("https://nt.ke.com/ershoufang/xiting/pg${1}l3");
        allUrlList.add("https://nt.ke.com/ershoufang/zhangzhishan/pg${1}l3");
    }


    private Integer pageSize = 100;

    public List<String> getUrlList(Integer startPage) {
        List<String> urlList = new ArrayList<>();
        for (Integer i = startPage; i <= pageSize; i++) {
            urlList.add(baseUrl.replace("${1}", i.toString()));
        }
        return urlList;
    }

    public void crawlerAll() {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        ArrayList<Future<?>> futures = new ArrayList<>();
        for (List<String> stringList : ListUtil.split(new ArrayList<>(allUrlList), 2)) {
            Future<?> future = executorService.submit(() -> {
                for (String url : stringList) {
                    for (Integer i = 1; i <= pageSize; i++) {
                        String newUrl = url.replace("${1}", i.toString());
                        log.error("处理贝壳链接:{}", newUrl);
                        String html = CrawlerUtil.doRequest(newUrl);
                        if (StrUtil.isNotBlank(html)) {
                            List<KeHouse> houseList = parseList(html);
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
        log.error("爬取所有三房列表完毕！");
    }


    public void crawler(List<String> urlList) {
        for (String url : urlList) {
            log.error("处理贝壳链接:{}", url);
            String html = CrawlerUtil.doRequest(url);
            List<KeHouse> houseList = parseList(html);
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
    public List<KeHouse> parseList(String html) {
        List<KeHouse> keHouseList = new ArrayList<>();
        Document document = Jsoup.parse(html);
        Elements elements = document.select("ul[class=sellListContent]").select("li[class=clear]");
        for (Element element : elements) {
            KeHouse keHouse = new KeHouse();
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

    public void updateKeHouseArea1() {
        //每次取出一个
        for (; ; ) {
            KeHouse keHouse = keHouseMapper.selectOne(new QueryWrapper<KeHouse>()
                    .isNull("area1").last("limit 1"));
            if (keHouse != null) {
                log.error("更新房屋数据:{}", keHouse.getUrl());
                String url = keHouse.getUrl();
                String html = CrawlerUtil.doRequest(url);
                parseDetail(html, keHouse);
            } else {
                //直到没有数据
                break;
            }
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        log.error("更新房屋详情数据结束");
    }

    public void updateKeHouseD() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (; ; ) {
            log.error("循环========");
            List<KeHouse> keHouseList = keHouseMapper.selectList(new QueryWrapper<KeHouse>()
                    .isNull("area1").isNull("status").last("limit 1000"));
            if (CollectionUtil.isEmpty(keHouseList)) {
                break;
            }
            List<Future<?>> futureList = new ArrayList<>();
            ListUtil.split(keHouseList, 100).forEach(keHouses -> {
                Future<?> future = executorService.submit(() -> {
                    for (KeHouse keHouse : keHouses) {
                        log.error("更新房屋数据:{}", keHouse.getUrl());
                        String url = keHouse.getUrl();
                        String html = CrawlerUtil.doRequest(url);
                        if (html.equals("404")) {
                            //更新house状态
                            KeHouse update = new KeHouse();
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
    public void parseDetail(String htmlDetail, KeHouse keHouse) {
        if (htmlDetail == null) {
            return;
        }
        KeHouse updateKeHouse = new KeHouse();
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

    public void updateById(KeHouse keHouse) {
        keHouseMapper.updateById(keHouse);
    }

    public void saveAndUpdateKeHouse(List<KeHouse> keHouseList) {
        for (KeHouse keHouse : keHouseList) {
            KeHouse selectedOne = keHouseMapper.selectOne(new QueryWrapper<KeHouse>()
                    .eq("url", keHouse.getUrl()));
            if (selectedOne != null) {
                if (ObjectUtil.notEqual(selectedOne.getPrice(), keHouse.getPrice())) {
                    KeHouseChange keHouseChange = new KeHouseChange();
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
}
