package com.wilson.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 房子变更详情
 *
 * @author wilson
 */
@Data
@TableName("house_change_detail")
public class HouseChangeDetail {
    //id
    @TableId(type = IdType.AUTO)
    private Integer id;
    //房子id
    private Integer houseId;
    //变更时间
    private Date changeTime;
    //变更价格
    private Double unitPrice;//单价
    private Double price;//总价
}
