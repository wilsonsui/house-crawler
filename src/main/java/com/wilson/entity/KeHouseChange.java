package com.wilson.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author wilson
 */
@TableName("ke_house_change")
@Data
public class KeHouseChange {
    //id houseId，单价 总价 更新时间
    private Integer id;
    private Integer houseId;
    private Double unitPrice;
    private Double price;
    private Date updateTime;


}
