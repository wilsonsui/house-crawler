package com.wilson.enums;

import com.wilson.crawler.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author wilson
 */
@AllArgsConstructor
public enum CrawlerEnum {
    PROXY_LIST_PLUS("proxylistplus", "https://list.proxylistplus.com/", ProxyListPlusCrawler.class),
    FATE_ZERO("fatezero", "http://proxylist.fatezero.org/proxy.list", FatezeroCrawler.class),
    PROXY_DB("proxydb", "http://proxydb.net/", ProxyDbCrawler.class),
    KUAIDAILI("kuaidaili", "https://www.kuaidaili.com/free/inha/", KuaiDaiLiCrawler.class),
    IP66("66ip", "http://www.66ip.cn/", Ip66Crawler.class),
    IP3366("ip3366", "http://www.ip3366.net/", Ip66Crawler.class),
    IP89("ip89", "https://www.89ip.cn/", IP89Crawler.class),
    KAI_XIN("kxdaili", "http://www.kxdaili.com/dailiip/", IP89Crawler.class),

    ;

    @Getter
    private String identify;
    @Getter
    private String baseUrl;
    @Getter
    private Class<? extends AbstractCrawler> clazz;


}
