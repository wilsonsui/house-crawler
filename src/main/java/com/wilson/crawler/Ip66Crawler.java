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
public class Ip66Crawler extends AbstractCrawler {

    private final int PAGE_SIZE = 20;

    @Override
    public CrawlerEnum getEnum() {
        return CrawlerEnum.IP66;
    }

    @Override
    public List<String> urlList() {
        String baseUrl = getEnum().getBaseUrl();
        List<String> urlList = new ArrayList<>(PAGE_SIZE);
        for (int i = 1; i <= PAGE_SIZE; i++) {
            urlList.add(baseUrl + i + ".html");
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
            for (int i = 1; i < trs.size(); i++) {
                Element tr = trs.get(i);
                Elements tds = tr.select("td");
                if (tds.size() != 5) {
                    continue;
                }

                ProxyEntity entity = new ProxyEntity();
                entity.setHost(tds.get(0).text().trim());
                entity.setPort(Integer.parseInt(tds.get(1).text()));
                entity.setCountry(tds.get(2).text().trim());
                entity.setSource(getEnum().getIdentify());
                res.add(entity);
            }
        }
        return res;
    }
}