package com.wilson.controller;

import com.wilson.entity.WxKeHouse;
import com.wilson.ke.WxKeCrawlerUtil;
import com.wilson.mapper.WxKeHouseChangeMapper;
import com.wilson.mapper.WxKeHouseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * @author wilson
 */
@RestController
public class WxKeController {

    @Autowired
    WxKeHouseMapper wxKeHouseMapper;

    @Autowired
    WxKeHouseChangeMapper wxKeHouseChangeMapper;

    @Autowired
    WxKeCrawlerUtil keCrawlerUtil;

    //爬取无锡的房子
    @GetMapping("/wxList")
    public String crawlerKeHouse() throws IOException {
        keCrawlerUtil.crawlerAll();
        return "success";
    }

    //更新每个无锡房子的详情
    @GetMapping("/wxUpdate")
    public String updateKeHouse() throws IOException, ExecutionException, InterruptedException {
        keCrawlerUtil.updateKeHouseD();
        return "success";
    }

}
