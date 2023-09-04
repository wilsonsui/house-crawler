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
public class Ip3366Crawler extends AbstractCrawler {
    @Override
    public CrawlerEnum getEnum() {
        return CrawlerEnum.IP3366;
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
                entity.setType(tds.get(3).text().trim());
                entity.setPort(Integer.parseInt(tds.get(1).text()));
                entity.setCountry(tds.get(5).text().trim());
                entity.setSource(getEnum().getIdentify());
                res.add(entity);
            }
        }
        return res;
    }
}
