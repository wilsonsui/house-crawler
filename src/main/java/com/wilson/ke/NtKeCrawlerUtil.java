package com.wilson.ke;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wilson.entity.NtKeHouse;
import com.wilson.entity.NtKeHouseChange;
import com.wilson.mapper.NtKeHouseChangeMapper;
import com.wilson.mapper.NtKeHouseMapper;
import com.wilson.util.CrawlerUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sound.midi.Soundbank;
import java.io.File;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author wilson
 */
@Slf4j
@Component
public class NtKeCrawlerUtil {
    @Autowired
    Executor threadPoolExecutor;

    @Autowired
    NtKeHouseMapper keHouseMapper;

    @Autowired
    NtKeHouseChangeMapper keHouseChangeMapper;

    //南通二手房

    private static String baseUrl = "https://nt.ke.com/ershoufang";

    private static Set<String> allUrlList = new LinkedHashSet<>();

    static {
        //https://nt.ke.com/ershoufang/nantongjingjijishukaifaqu/l3l4p2p3/
        //https://nt.ke.com/ershoufang/gangzhaqu/l3l4p2p3/
        //https://nt.ke.com/ershoufang/suxitongyuanqu/l3l4p2p3/

        //四大区 近地铁， 34室 100-150w
        allUrlList.add("https://nt.ke.com/ershoufang/nantongjingjijishukaifaqu/pg${1}sf1su1l3l4l5p2p3/");
        allUrlList.add("https://nt.ke.com/ershoufang/gangzhaqu/pg${1}sf1su1l3l4l5p2p3/");
        allUrlList.add("https://nt.ke.com/ershoufang/suxitongyuanqu/pg${1}sf1su1l3l4l5p2p3/");
        allUrlList.add("https://nt.ke.com/ershoufang/chongchuanqu/pg${1}sf1su1l3l4l5p2p3/");
        allUrlList.add("https://nt.ke.com/ershoufang/tongzhouqu/pg${1}sf1su1l3l4l5p2p3/");

//        allUrlList.add("https://nt.ke.com/ershoufang/hongqiao2/pg${1}l3");
//        allUrlList.add("https://nt.ke.com/ershoufang/junshan/pg${1}l3");
//        allUrlList.add("https://nt.ke.com/ershoufang/langshan/pg${1}l3");
//        allUrlList.add("https://nt.ke.com/ershoufang/xincheng1/pg${1}l3");
//        allUrlList.add("https://nt.ke.com/ershoufang/haohe/pg${1}l3");
//        allUrlList.add("https://nt.ke.com/ershoufang/rengang/pg${1}l3");
//        allUrlList.add("https://nt.ke.com/ershoufang/tangzha/pg${1}l3");
//        allUrlList.add("https://nt.ke.com/ershoufang/yongxing/pg${1}l3");
//        allUrlList.add("https://nt.ke.com/ershoufang/chengdong4/pg${1}l3");
//        allUrlList.add("https://nt.ke.com/ershoufang/chenqiao/pg${1}l3");
//        allUrlList.add("https://nt.ke.com/ershoufang/guanyinshan1/pg${1}l3");
//        allUrlList.add("https://nt.ke.com/ershoufang/qinzao/pg${1}l3");
//        allUrlList.add("https://nt.ke.com/ershoufang/xingfu1/pg${1}l3");

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
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        ArrayList<Future<?>> futures = new ArrayList<>();
        for (List<String> stringList : ListUtil.split(new ArrayList<>(allUrlList), 2)) {
            Future<?> future = executorService.submit(() -> {
                for (String url : stringList) {
                    for (Integer i = 1; i <= pageSize; i++) {
                        String newUrl = url.replace("${1}", i.toString());
                        log.error("处理贝壳链接:{}", newUrl);
                        String html = CrawlerUtil.doRequest(newUrl);
                        if (StrUtil.isNotBlank(html)) {
                            List<NtKeHouse> houseList = parseList(html);
                            if (CollectionUtil.isEmpty(houseList)) {
                                //跳出内层循环
                                break;
                            }
                            saveAndUpdateKeHouse(houseList);
                        }
                        try {
                            TimeUnit.SECONDS.sleep(10);
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
            List<NtKeHouse> houseList = parseList(html);
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
    public List<NtKeHouse> parseList(String html) {
        List<NtKeHouse> keHouseList = new ArrayList<>();
        Document document = Jsoup.parse(html);
        Elements elements = document.select("ul[class=sellListContent]").select("li[class=clear]");
        for (Element element : elements) {
            NtKeHouse keHouse = new NtKeHouse();
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
            NtKeHouse keHouse = keHouseMapper.selectOne(new QueryWrapper<NtKeHouse>()
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
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        for (; ; ) {
            log.error("循环========");
            List<NtKeHouse> keHouseList = keHouseMapper.selectList(new QueryWrapper<NtKeHouse>()
                    .isNull("area1").isNull("status")
                    .last("limit 10"));
            if (CollectionUtil.isEmpty(keHouseList)) {
                break;
            }
            List<Future<?>> futureList = new ArrayList<>();
            ListUtil.split(keHouseList, 5).forEach(keHouses -> {
                Future<?> future = executorService.submit(() -> {
                    for (NtKeHouse keHouse : keHouses) {
                        log.error("更新房屋数据:{}", keHouse.getUrl());
                        String url = keHouse.getUrl();
                        String html = CrawlerUtil.doRequest(url);
                        if (html.equals("404")) {
                            //更新house状态
                            NtKeHouse update = new NtKeHouse();
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
    public void parseDetail(String htmlDetail, NtKeHouse keHouse) {
        if (htmlDetail == null) {
            return;
        }
        NtKeHouse updateKeHouse = new NtKeHouse();
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

    public static void main(String[] args) {
        String path = "/Users/suishunli/Documents/《神烦警探》1-8季 强烈推荐! 喜剧_犯罪 (2013-2021)";
        //遍历文件夹下的所有文件夹及文件 获取名字
        File file = new File(path);
        File[] files = file.listFiles();
        for (File file1 : files) {
            if (file1.isDirectory()) {
                for (File listFile : file1.listFiles()) {
                    if (listFile.isFile()) {
                        //获取文件后缀
                        String name = listFile.getName();
                        String suffix = name.substring(name.lastIndexOf(".") + 1);
                        if (Arrays.asList("mp4", "mkv").contains(suffix)) {
                            System.out.println(listFile.getName());
                            String oldFileName = listFile.getName();
//                            神烦警探.S05E16..mp4
//                            神烦警探.S01E01.HR-HDTV.1024x576.中英双语-电波字幕组V2更多资源-XH1080.com.mkv
                            String newName = oldFileName.replace("Brooklyn.Nine.Nine.", "").replace("HR-HDTV.1024x576.中英双语-电波字幕组更多资源-XH1080.com", "");
                            listFile.renameTo(new File(listFile.getParent() + "/" + newName));
                        }
                    }

                }


            }


        }

    }

    public void updateById(NtKeHouse keHouse) {
        keHouseMapper.updateById(keHouse);
    }

    public void saveAndUpdateKeHouse(List<NtKeHouse> keHouseList) {
        for (NtKeHouse keHouse : keHouseList) {
            NtKeHouse selectedOne = keHouseMapper.selectOne(new QueryWrapper<NtKeHouse>()
                    .eq("url", keHouse.getUrl()));
            if (selectedOne != null) {
                if (ObjectUtil.notEqual(selectedOne.getPrice(), keHouse.getPrice())) {
                    NtKeHouseChange keHouseChange = new NtKeHouseChange();
                    keHouseChange.setHouseId(selectedOne.getId());
                    keHouseChange.setUnitPrice(keHouse.getUnitPrice());
                    keHouseChange.setPrice(keHouse.getPrice());
                    keHouseChange.setUpdateTime(new Date());
                    keHouseChangeMapper.insert(keHouseChange);
                }
                //更新
                keHouse.setId(selectedOne.getId());
                keHouse.setUpdateTime(new Date());
                if (!Objects.equals(selectedOne.getPrice(), keHouse.getPrice())) {
                    keHouse.setChangePrice(keHouse.getPrice());//最新的价格
                    keHouse.setChangeUnitPrice(keHouse.getUnitPrice());//最新抓取到的价格
                }
                //再次设置原来价格
                keHouse.setPrice(selectedOne.getPrice());//
                keHouse.setUnitPrice(selectedOne.getUnitPrice());//
                keHouse.setCreateTime(selectedOne.getCreateTime());
                keHouseMapper.updateById(keHouse);
            } else {
                keHouseMapper.insert(keHouse);
            }

        }
    }
}
