package com.wilson.crawler;

import com.wilson.entity.ProxyEntity;
import com.wilson.enums.CrawlerEnum;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 爬虫基类 抽象类
 *
 * @author wilson
 */
public abstract class AbstractCrawler {

    /**
     * 获取爬虫网页枚举
     *
     * @return
     */
    public abstract CrawlerEnum getEnum();


    /**
     * 解析网页数据
     *
     * @param html
     * @return
     */
    public abstract List<ProxyEntity> parse(String html);

    /**
     * 默认实现返回baseurl，如有分页 可重写
     *
     * @return
     */
    public List<String> urlList() {
        return Collections.singletonList(getEnum().getBaseUrl());
    }

    ;
}
