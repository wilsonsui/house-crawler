package com.wilson.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author wilson
 */
@Data
@TableName("house")
public class House {
    //id
    @TableId(type = IdType.AUTO)
    private Integer id;
    //房子标题
    private String title;
    //小区名称
    private String communityName;
    //小区地址
    private String communityAddress;
    //区域
    private String area1;
    //商圈
    private String area2;
    //户型
    private String unitType;
    //总价格 w
    private Double price;
    //单价 w/m2
    private Double unitPrice;
    //建筑面积
    private String jzArea;
    //使用面积
    private String syArea;
    //朝向
    private String orientation;
    //装修类型 精装修 毛坯
    private String decorationType;
    //楼层
    private String floor;
    //建筑年代
    private String buildYear;
    //有无电梯
    private String elevator;
    //产权
    private String propertyRight;
    //房屋类型
    private String houseType;
    //建筑结构
    private String buildStructure;
    //建筑类别
    private String buildType;
    //挂牌时间
    private Date listingTime;
    //房子链接
    private String url;
    //创建时间
    private Date createTime;
    //更新时间
    private Date updateTime;
}
