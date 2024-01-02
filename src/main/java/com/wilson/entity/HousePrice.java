package com.wilson.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author wilson
 */
@Data
@TableName("house_price")
public class HousePrice {
    //create table house_price
    //(
    //    id          int      not null
    //        primary key,
    //    price       decimal  null comment '价格',
    //    house_id    int      not null comment '房子id',
    //    create_time datetime null comment '创建时间'
    //);
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer houseId;
    /**
     * 最新的房屋价格
     */
    private BigDecimal price;
    private Date createTime;

}
