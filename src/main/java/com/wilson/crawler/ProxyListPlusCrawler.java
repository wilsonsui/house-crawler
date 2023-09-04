package com.wilson.crawler;

import com.wilson.entity.ProxyEntity;
import com.wilson.enums.CrawlerEnum;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wilson
 */
@Slf4j
@Component
public class ProxyListPlusCrawler extends AbstractCrawler {
    @Override
    public CrawlerEnum getEnum() {
        return CrawlerEnum.PROXY_LIST_PLUS;
    }

    private final int PAGE_SIZE = 6;
    private final String url = "Fresh-HTTP-Proxy-List-";

    @Override
    public List<String> urlList() {
        String baseUrl = getEnum().getBaseUrl();
        List<String> urlList = new ArrayList<>(PAGE_SIZE);
        for (int i = 1; i <= PAGE_SIZE; i++) {
            urlList.add(baseUrl + url + i);
        }
        return urlList;
    }

    @Override
    public List<ProxyEntity> parse(String html) {
        Document document = Jsoup.parse(html);
        Elements elements = document.select("div[id=page] table[class=bg] tbody tr:gt(1)");
        List<ProxyEntity> proxyList = new ArrayList<>();

        for (Element element : elements) {
            try {
                String host = element.select("td:eq(1)").first().text();
                if(element.select("td:eq(2)").first().text().trim().contains("5b7")){
                    continue;
                }
                Integer port = Integer.valueOf(element.select("td:eq(2)").first().text().trim());
                String country = element.select("td:eq(4)").first().text();
                String type = element.select("td:eq(6)").first().text() == "yes" ? "https" : "http";
                ProxyEntity proxyEntity = new ProxyEntity();
                proxyEntity.setHost(host);
                proxyEntity.setPort(port);
                proxyEntity.setType(type);
                proxyEntity.setCountry(country);
                proxyEntity.setSource(getEnum().getIdentify());
                proxyList.add(proxyEntity);
            } catch (Exception e) {
                e.printStackTrace();
                log.error(e.toString());
            }
        }

        return proxyList;
    }
}
