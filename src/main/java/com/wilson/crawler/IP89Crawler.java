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
public class IP89Crawler extends AbstractCrawler {
    @Override
    public CrawlerEnum getEnum() {
        return CrawlerEnum.IP89;
    }

    private Integer pageSize = 79;

    @Override
    public List<String> urlList() {
        List<String> urlList = new ArrayList<>();
        String baseUrl = getEnum().getBaseUrl();
        for (Integer i = 0; i < pageSize; i++) {
            String url = baseUrl + "index_" + (i + 1) + ".html";
            urlList.add(url);
        }
        return urlList;
    }

    @Override
    public List<ProxyEntity> parse(String html) {
        List<ProxyEntity> res = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Elements tables = doc.select("tbody");
        for (Element table : tables) {
            Elements trs = table.select("tr");
            for (int i = 1; i < trs.size(); i++) {
                Element tr = trs.get(i);
                Elements tds = tr.select("td");
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
