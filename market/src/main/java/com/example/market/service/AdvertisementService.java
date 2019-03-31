package com.example.market.service;

import com.example.common.entity.Advertisement;
import com.example.common.util.JsonData;

import java.util.List;

public interface AdvertisementService {
     List<Advertisement> selective(JsonData jsonData);
     int updateById(Advertisement advertisement);
     int deleteById(int id);
     int insert(Advertisement advertisement);
}
