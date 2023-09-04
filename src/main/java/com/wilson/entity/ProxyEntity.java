package com.wilson.entity;

import lombok.Data;

import java.util.Date;

/**
 * 代理实体类
 *
 * @author wilson
 */
@Data
public class ProxyEntity {
    private Integer id;

    private String host;

    private int port;

    private String type;//HTTP HTTPS SOCKS


    private String country;//国家

    private Date createTime;

    private String updateTime;//更新时间

    private String source;//来源
}
