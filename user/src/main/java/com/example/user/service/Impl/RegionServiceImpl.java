package com.example.user.service.Impl;

import com.example.common.entity.Region;
import com.example.common.util.JsonData;
import com.example.user.dao.RegionMapper;
import com.example.user.service.RegionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class RegionServiceImpl implements RegionService {

    @Resource
    private RegionMapper regionMapper;

    @Override
    public List<Region> getAll() {
        JsonData jsonData = new JsonData();
        jsonData.put("notType", 4);
        return regionMapper.selective(jsonData);
    }

    @Override
    public Region findById(JsonData jsonData) {
        return regionMapper.selective(jsonData).get(0);
    }

    @Override
    public List<Region> selective(JsonData jsonData) {
        return regionMapper.selective(jsonData);
    }

}
