package com.wilson.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@TableName("house")
@ToString
public class House {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String area;
    private String title;
    private String communityName;
    private String communityUrl;
    private String area1;
    private String area2;
    private BigDecimal unitPrice;
    private BigDecimal price;//总价
    private Double jzArea;//建筑面积
    private BigDecimal clArea;//测量面积
    private String houseType;//户型结构
    private Integer follow;//关注人数
    private String unitType;//户型
    private String orientation;//朝向
    private String floor;//楼层
    private String decorationType;//装修类型
    private String url;//详情页url
    private String elevator;//电梯有无
    private String tihubili;//梯户比例
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date listingTime;//挂牌时间
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date lastTradeTime;//上次交易时间
    private String propertyRight;//产权
    private Integer year;//建筑年代
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")

    private Date createTime;//创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;//更新时间
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer status; //'0 下架 1 卖出 '
    private BigDecimal changeUnitPrice;//价格变化
    private BigDecimal changePrice;//价格变化

    private String age;//房龄

    @TableField(exist = false)
    private List<HouseTraffic> houseTrafficList;


}