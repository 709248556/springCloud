package com.example.market.dao;

import com.example.common.entity.Advertisement;
import com.example.common.util.JsonData;

import java.util.List;

public interface AdvertisementMapper {
    List<Advertisement> selective(JsonData jsonData);
    int updateById(Advertisement advertisement);
    int deleteById(int id);
    int insert(Advertisement advertisement);
}
