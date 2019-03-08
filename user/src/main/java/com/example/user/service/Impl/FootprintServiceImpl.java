package com.example.user.service.Impl;

import com.example.common.entity.Footprint;
import com.example.common.util.JsonData;
import com.example.user.dao.FootprintMapper;
import com.example.user.service.FootprintService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("footprintService")
public class FootprintServiceImpl implements FootprintService {

    @Autowired
    private FootprintMapper footprintMapper;

    @Override
    public List<Footprint> selective(JsonData jsonData) {
        if(jsonData.containsKey("page")&&jsonData.containsKey("size")){
            PageHelper.startPage(Integer.valueOf(jsonData.get("page").toString()), Integer.valueOf(jsonData.get("size").toString()));
        }
        return footprintMapper.selective(jsonData);
    }

    @Override
    public int deletive(JsonData jsonData) {
        return footprintMapper.deletive(jsonData);
    }
}
