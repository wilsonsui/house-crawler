package com.wilson.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author wilson
 */
@TableName("nt_ke_house_change")
@Data
public class NtKeHouseChange {
    //id houseId，单价 总价 更新时间
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer houseId;
    private Double unitPrice;
    private Double price;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;


}
