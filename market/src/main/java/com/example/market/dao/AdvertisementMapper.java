package com.example.market.dao;

import com.example.common.entity.Advertisement;
import com.example.common.util.JsonData;

import java.util.List;

public interface AdvertisementMapper {
    List<Advertisement> selective(JsonData jsonData);
}
