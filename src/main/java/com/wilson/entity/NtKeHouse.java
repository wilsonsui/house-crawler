package com.wilson.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * 贝壳二手房实体类
 *
 * @author wilson
 */
@Data
@TableName("nt_ke_house")
@ToString
public class NtKeHouse {
    //id ,houseId,标题,小区名称,区域1，区域2，单价，总价，建筑面积，户型，朝向，楼层，装修类型
    //电梯有无，梯户比例，挂牌时间，上次交易时间，产权
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String title;
    private String communityName;
    private String area1;
    private String area2;
    private Double unitPrice;
    private Double price;//总价
    private Double jzArea;//建筑面积
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
    private String age;//房屋年限
    private Integer status; //'0 下架 1 卖出 '

    private Double changeUnitPrice;//价格变化
    private Double changePrice;//价格变化


}
