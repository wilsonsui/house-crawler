package com.wilson.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wilson.entity.House;
import com.wilson.entity.HousePrice;
import com.wilson.entity.HouseTraffic;
import com.wilson.mapper.HouseMapper;
import com.wilson.mapper.HousePriceMapper;
import com.wilson.mapper.HouseTrafficMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author wilson
 */
@Slf4j
@Service
public class HouseService extends ServiceImpl<HouseMapper, House> {
    @Autowired
    HouseMapper houseMapper;

    @Autowired
    HouseTrafficMapper houseTrafficMapper;

    @Autowired
    HousePriceMapper housePriceMapper;

    public void saveListHouse(List<House> houseList) {
        for (House house : houseList) {
            house.setId(null);
            if (house.getCommunityUrl().contains("su.ke")) {
                house.setArea("苏州");
            }
            if (house.getCommunityUrl().contains("nt.ke")) {
                house.setArea("南通");
            }
            if (house.getCommunityUrl().contains("wx.ke")) {
                house.setArea("无锡");
            }
            try {
                House houseDB = houseMapper.selectOne(new QueryWrapper<House>().eq("url", house.getUrl()));
                if (houseDB != null) {
                    //数据库存在则更新
                    house.setId(houseDB.getId());
                    house.setUpdateTime(new Date());
                    if (!Objects.equals(houseDB.getPrice(), house.getPrice())) {
                        //记录价格变化，每次价格变动时的变化幅度
                        house.setChangePrice(houseDB.getPrice().subtract(house.getPrice()));//价格变化幅度
                        house.setChangeUnitPrice(houseDB.getUnitPrice().subtract(house.getUnitPrice()));//价格变化幅度

                        //价格发生变化 保存更新价格记录
                        HousePrice housePrice = new HousePrice();
                        housePrice.setHouseId(houseDB.getId());
                        housePrice.setPrice(house.getPrice());
                        housePrice.setCreateTime(new Date());
                        housePriceMapper.insert(housePrice);
                    }
                    //再次设置原来价格
                    house.setUpdateTime(houseDB.getCreateTime());
                    houseMapper.updateById(house);
                } else {
                    houseMapper.insert(house);
                    Integer houseId = house.getId();
                    List<HouseTraffic> houseTrafficList = house.getHouseTrafficList();
                    for (HouseTraffic houseTraffic : houseTrafficList) {
                        houseTraffic.setHouseId(houseId);
                        houseTrafficMapper.insert(houseTraffic);
                    }
                    HousePrice housePrice = new HousePrice();
                    housePrice.setHouseId(houseId);
                    housePrice.setPrice(house.getPrice());
                    housePrice.setCreateTime(new Date());
                    housePriceMapper.insert(housePrice);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void saveHouse(House house) {
        house.setId(null);
        if (house.getCommunityUrl().contains("su.ke")) {
            house.setArea("苏州");
        }
        if (house.getCommunityUrl().contains("nt.ke")) {
            house.setArea("南通");
        }
        if (house.getCommunityUrl().contains("wx.ke")) {
            house.setArea("无锡");
        }
        try {
            House houseDB = houseMapper.selectOne(new QueryWrapper<House>().eq("url", house.getUrl()));
            if (houseDB != null) {
                //数据库存在则更新
                house.setId(houseDB.getId());
                house.setUpdateTime(new Date());
                //价格发生变化了 就保存价格·
                int compareTo = houseDB.getPrice().compareTo(house.getPrice());
                if (compareTo != 0) {
                    //记录价格变化，每次价格变动时的变化幅度
                    house.setChangePrice(houseDB.getPrice().subtract(house.getPrice()));//价格变化幅度
                    house.setChangeUnitPrice(houseDB.getUnitPrice().subtract(house.getUnitPrice()));//价格变化幅度
                    //价格发生变化 保存更新价格记录
                    HousePrice housePrice = new HousePrice();
                    housePrice.setHouseId(houseDB.getId());
                    housePrice.setPrice(house.getPrice());
                    housePrice.setCreateTime(new Date());
                    housePriceMapper.insert(housePrice);
                }
                houseMapper.updateById(house);
                //如果交通信息为null 则保存交通信息
                Integer houseId = house.getId();
                Long count = houseTrafficMapper.selectCount(new QueryWrapper<HouseTraffic>()
                        .eq("house_id", houseId));
                if (count == 0) {
                    List<HouseTraffic> houseTrafficList = house.getHouseTrafficList();
                    if (CollectionUtil.isNotEmpty(houseTrafficList)) {
                        for (HouseTraffic houseTraffic : houseTrafficList) {
                            houseTraffic.setHouseId(houseId);
                            houseTrafficMapper.insert(houseTraffic);
                        }
                    }
                }
            } else {
                house.setCreateTime(new Date());
                houseMapper.insert(house);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
