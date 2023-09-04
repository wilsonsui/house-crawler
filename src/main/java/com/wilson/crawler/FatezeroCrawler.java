package com.wilson.crawler;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.wilson.entity.ProxyEntity;
import com.wilson.enums.CrawlerEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wilson
 */
@Slf4j
@Component
public class FatezeroCrawler extends AbstractCrawler {
    @Override
    public CrawlerEnum getEnum() {
        return CrawlerEnum.FATE_ZERO;
    }

    @Override
    public List<ProxyEntity> parse(String html) {
        String[] split = html.split("\n");
        if (ObjectUtil.isEmpty(split)) {
            return Collections.emptyList();
        }
        return Arrays.stream(split).map(s -> {
            Map<?, ?> map = JSON.parseObject(s, Map.class);
            ProxyEntity proxyEntity = new ProxyEntity();
            proxyEntity.setHost((String) map.get("host"));
            proxyEntity.setPort((Integer) map.get("port"));
            proxyEntity.setType((String) map.get("type"));
            proxyEntity.setCountry((String) map.get("country"));
            proxyEntity.setSource(getEnum().getIdentify());
            return proxyEntity;
        }).collect(Collectors.toList());
    }
}
