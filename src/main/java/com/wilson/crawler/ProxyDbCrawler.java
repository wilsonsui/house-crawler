package com.wilson.crawler;

import com.wilson.entity.ProxyEntity;
import com.wilson.enums.CrawlerEnum;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProxyDbCrawler extends AbstractCrawler {

    @Override
    public CrawlerEnum getEnum() {
        return CrawlerEnum.PROXY_DB;
    }

    @Override
    public List<ProxyEntity> parse(String html) {
        Document document = Jsoup.parse(html);
        Elements elements = document.select("div[class=table-responsive] table tbody tr");
        List<ProxyEntity> proxyList = elements.stream().map(element -> {
            String hostName = element.select("td:eq(0)").select("a").first().text();
            String host = hostName.split(":")[0];
            String port = hostName.split(":")[1];
            String type = element.select("td:eq(4)").first().text();

            ProxyEntity proxyEntity = new ProxyEntity();
            proxyEntity.setHost(host);
            proxyEntity.setPort(Integer.valueOf(port));
            proxyEntity.setType(type);
            proxyEntity.setSource(getEnum().getIdentify());
            return proxyEntity;
        }).collect(Collectors.toList());

        return proxyList;
    }

}