package com.wilson.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 房屋交通信息
 *
 * @author wilson
 */
@Data
@TableName("house_traffic")
public class HouseTraffic {
    @TableId(type = com.baomidou.mybatisplus.annotation.IdType.AUTO)
    private Long id;
    private Integer houseId;
    //站点名称
    private String subway;
    //距离
    private String distance;
}
