package com.wilson.crawler;

import com.wilson.entity.ProxyEntity;
import com.wilson.enums.CrawlerEnum;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class KuaiDaiLiCrawler extends AbstractCrawler {

    @Override
    public CrawlerEnum getEnum() {
        return CrawlerEnum.KUAIDAILI;
    }

    private Integer pageSize = 4;
    private String fix = "/";


    @Override
    public List<String> urlList() {
        List<String> urlList = new ArrayList<>();
        String baseUrl = getEnum().getBaseUrl();
        for (Integer i = 0; i < pageSize; i++) {
            String url = baseUrl + (i + 1) + fix;
            urlList.add(url);
        }
        return urlList;
    }

    @Override
    public List<ProxyEntity> parse(String responseData) {
        List<ProxyEntity> res = new ArrayList<>();
        Document doc = Jsoup.parse(responseData);
        Elements tables = doc.select("tbody");
        for (Element table : tables) {
            Elements trs = table.select("tr");
            for (Element tr : trs) {
                Elements tds = tr.select("td");
                if (tds.size() != 8) {
                    continue;
                }
                ProxyEntity enity = new ProxyEntity();
                enity.setHost(tds.get(0).text().trim());
                enity.setPort(Integer.parseInt(tds.get(1).text()));
                enity.setCountry(tds.get(4).text().trim());
                enity.setSource(getEnum().getIdentify());
                res.add(enity);
            }
        }
        return res;
    }
}