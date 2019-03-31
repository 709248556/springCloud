package com.example.market.service.Impl;

import com.example.common.entity.Advertisement;
import com.example.common.util.JsonData;
import com.example.market.dao.AdvertisementMapper;
import com.example.market.service.AdvertisementService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service("advertisementService")
public class AdvertisementServiceImpl implements AdvertisementService {

    @Autowired
    private AdvertisementMapper advertisementMapper;

    @Override
    public List<Advertisement> selective(JsonData jsonData) {
        if (jsonData.containsKey("page") && jsonData.containsKey("limit"))
            PageHelper.startPage(Integer.valueOf(jsonData.get("page").toString()), Integer.valueOf(jsonData.get("limit").toString()));
        if(jsonData.containsKey("name")){
            jsonData.put("name","%"+jsonData.get("name")+"%");
        }
        if(jsonData.containsKey("content")){
            jsonData.put("content","%"+jsonData.get("content")+"%");
        }
        return advertisementMapper.selective(jsonData);
    }

    @Override
    public int updateById(Advertisement advertisement) {
        return advertisementMapper.updateById(advertisement);
    }

    @Override
    public int deleteById(int id) {
        return advertisementMapper.deleteById(id);
    }

    @Override
    public int insert(Advertisement advertisement) {
        advertisement.setAddTime(LocalDateTime.now());
        advertisement.setUpdateTime(LocalDateTime.now());
        return advertisementMapper.insert(advertisement);
    }
}
