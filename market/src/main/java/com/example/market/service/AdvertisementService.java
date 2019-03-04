package com.example.market.service;

import com.example.common.entity.Advertisement;
import com.example.common.util.JsonData;

import java.util.List;

public interface AdvertisementService {
    public List<Advertisement> selective(JsonData jsonData);
}
