package com.wilson.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author wilson
 */
@Data
public class KeHouseExcel {
    @ExcelProperty("标题")
    private String title;
    @ExcelProperty("小区名称")
    private String communityName;
    @ExcelProperty("区")
    private String area1;
    @ExcelProperty("商圈")
    private String area2;
    @ExcelProperty("单价")
    private Double unitPrice;
    @ExcelProperty("总价")
    private Double price;//总价
    @ExcelProperty("建筑面积")
    private Double jzArea;//建筑面积
    @ExcelProperty("户型结构")
    private String houseType;//户型结构
    @ExcelProperty("关注人数")
    private Integer follow;//关注人数
    @ExcelProperty("户型")
    private String unitType;//户型
    @ExcelProperty("朝向")
    private String orientation;//朝向
    @ExcelProperty("楼层")
    private String floor;//楼层
    @ExcelProperty("装修类型")
    private String decorationType;//装修类型
    @ExcelProperty("链接")
    private String url;//详情页url
    @ExcelProperty("建筑年代")
    private Integer year;//建筑年代
    @ExcelProperty("电梯有无")
    private String elevator;//电梯有无
    @ExcelProperty("挂牌时间")
    private String listingTimeStr;//挂牌时间
    @ExcelProperty("上次交易时间")
    private String lastTradeTimeStr;//上次交易时间
    @ExcelProperty("抓取时间")
    private String createTimeStr;//创建时间
    @ExcelProperty("房屋年限")
    private String age;//房屋年限
    @ExcelProperty("状态")
    private String statusStr; //'0 下架 1 卖出 '
}
