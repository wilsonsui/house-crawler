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
 * 小幻想ip 爬取 分页参数加密
 *
 * @author wilson
 */
@Slf4j
@Component
public class XiaoHuanCrawler extends AbstractCrawler {

    private List<String> pageList() {
        List<String> pageList = new ArrayList<>();
        pageList.add("b97827cc");//1
        pageList.add("4ce63706");//2
        pageList.add("5crfe930");//3
        pageList.add("f3k1d581");//4
        pageList.add("ce1d45977");//5
        pageList.add("881aaf7b5");//6
        pageList.add("eas7a436");//7
        pageList.add("981o917f5");//8
        pageList.add("2d28bd81a");//9
        pageList.add("a42g5985d");//10
        pageList.add("came0299");//11
        pageList.add("e92k59727");//12
        return pageList;
    }


    @Override
    public List<String> urlList() {
        String baseUrl = getEnum().getBaseUrl();
        ArrayList<String> urlList = new ArrayList<>();
        for (String s : pageList()) {
            urlList.add(baseUrl + "?page=" + s);
        }
        return urlList;
    }

    @Override
    public CrawlerEnum getEnum() {
        return CrawlerEnum.XIAO_HUAN_IP;
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
                String trim = tds.get(4).text().trim();
                if (trim.equals("支持")) {
                    entity.setType("HTTPS");
                }else{
                    entity.setType("HTTP");
                }
                entity.setSource(getEnum().getIdentify());
                res.add(entity);
            }
        }
        return res;
    }
}
