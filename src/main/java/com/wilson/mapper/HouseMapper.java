package com.wilson.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wilson.pojo.House;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author wilson
 */
@Mapper
public interface HouseMapper  extends BaseMapper<House> {
}
