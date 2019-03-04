package com.example.market.service.Impl;

import com.example.common.entity.Advertisement;
import com.example.common.util.JsonData;
import com.example.market.dao.AdvertisementMapper;
import com.example.market.service.AdvertisementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("advertisementService")
public class AdvertisementServiceImpl implements AdvertisementService {

    @Autowired
    private AdvertisementMapper advertisementMapper;

    @Override
    public List<Advertisement> selective(JsonData jsonData) {
        return advertisementMapper.selective(jsonData);
    }
}
