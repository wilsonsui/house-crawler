package com.wilson.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 小区信息
 *
 * @author wilson
 */
@TableName("community")
@Data
public class Community {
//    -- auto-generated definition
//    create table community
//            (
//                    id          int auto_increment
//                    primary key,
//                    name        int          not null comment '小区名字',
//                    url         varchar(300) not null comment '小区连接',
//    build_year  int          null comment '建成年代',
//    volume      varchar(50)  null comment '容积率',
//    green       int          null comment '绿化率',
//    total       int          null comment '房屋总数',
//    average     decimal      not null comment '挂牌均价',
//            `change`    decimal      not null comment '均价变化',
//    building    int          null comment '楼栋数',
//    property    varchar(300) null comment '物业费',
//    update_time datetime     null comment '创建时间'
//            )
//    comment '小区表';


    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 小区名字
     */
    private String name;
    /**
     * 小区连接
     */
    private String url;
    /**
     * 建成年代
     */
    private Integer buildYear;
    /**
     * 容积率
     */
    private String volume;
    /**
     * 绿化率
     */
    private Integer green;
    /**
     * 房屋总数
     */
    private Integer total;
    /**
     * 挂牌均价
     */
    private Double average;
    /**
     * 均价变化
     */
    private Double change;
    /**
     * 楼栋数
     */
    private Integer building;
    /**
     * 物业费
     */
    private String property;
    /**
     * 创建时间
     */
    private String updateTime;
}
