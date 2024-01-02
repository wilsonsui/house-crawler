package com.wilson.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wilson.entity.House;
import com.wilson.mapper.HouseMapper;
import com.wilson.task.HouseTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.PublicKey;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wilson
 */
@Slf4j
@Component
public class CrawlerUtil {
    @Autowired
    HouseMapper houseMapper;

    public void getHouseList() {
        List<String> allUrlList = UrlListUtil.getUrlList();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        AtomicInteger count = new AtomicInteger(0);

        //每个线程处理100个url
        for (List<String> urlList : ListUtil.split(allUrlList, 12)) {
            List<House> houseList = new ArrayList<>();
            for (String url : urlList) {
                Future<List<House>> listFuture = executorService.submit(() -> {
                    String html = HttpRequestUtil.doRequest(url);
//                    TimeUnit.SECONDS.sleep(5);//休眠5秒
                    if (html != null) {
                        List<House> houseList1 = ParseHtmlUtil.parseList(html);
                        houseList.addAll(houseList1);
                    }
                    return houseList;
                });
                List<House> houses = null;
                try {
                    houses = listFuture.get();
                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
//                    throw new RuntimeException(e);
                }
                if (houses == null) {
                    log.error("获取房屋列表异常:{}", url);
                    continue;
                }
                for (House house : houses) {
                    house.setId(null);
                    if (house.getCommunityUrl().contains("su.ke")) {
                        house.setArea("苏州");
                    }
                    if (house.getCommunityUrl().contains("nt.ke")) {
                        house.setArea("南通");
                    }
                    if (house.getCommunityUrl().contains("wx.ke")) {
                        house.setArea("无锡");
                    }
                    try {
                        House houseDB = houseMapper.selectOne(new QueryWrapper<House>().eq("url", house.getUrl()));
                        if (houseDB != null) {
                            //数据库存在则更新
                            house.setId(houseDB.getId());
                            house.setUpdateTime(new Date());
                            if (!Objects.equals(houseDB.getPrice(), house.getPrice())) {
                                house.setChangePrice(house.getPrice());//最新的价格
                                house.setChangeUnitPrice(house.getUnitPrice());//最新抓取到的价格
                            }
                            //再次设置原来价格
                            house.setPrice(houseDB.getPrice());//
                            house.setUnitPrice(houseDB.getUnitPrice());//
                            house.setCreateTime(houseDB.getCreateTime());
                            houseMapper.updateById(house);
                        } else {
                            count.incrementAndGet();
                            houseMapper.insert(house);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        log.error("本次抓取新房屋数据:{} 条", count.get());
    }


    public void updateList() {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
//        for (String string : Arrays.asList("suzhou", "wuxi", "nantong")) {
//            TableNameHelper.setTableName(string);
//            List<House> keHouseList = houseMapper.selectList(new QueryWrapper<House>()
//                    .isNull("area1").isNull("status"));
//            ListUtil.split(keHouseList, 10).forEach(keHouses -> {
//                for (House house : keHouses) {
//                    Future<?> future = executorService.submit(() -> {
//                        log.error("更新房屋数据:{}", house.getUrl());
//                        String url = house.getUrl();
//                        TableNameHelper.setTableName(string);
//                        String html = HttpRequestUtil.doRequest(url);
//                        if (html == null) {
//                            //更新house状态
//                            House update = new House();
//                            update.setId(house.getId());
//                            update.setStatus(0);
//                            houseMapper.updateById(update);
//                        } else {
//                            House houseDetail = ParseHtmlUtil.parseDetail(html);
//                            houseDetail.setUpdateTime(new Date());
//                            houseDetail.setId(house.getId());
//                            houseMapper.updateById(houseDetail);
//                        }
//                    });
//                    try {
//                        future.get();
//                    } catch (Exception e) {
//                        log.error("更新房屋数据异常:{}", house.getUrl());
////                        throw new RuntimeException(e);
//                    }
//                }
//            });
//        }
        executorService.shutdown();
        log.error("更新房屋详情数据结束");
    }
}
