package com.example.user.service.Impl;

import com.example.common.entity.Collect;
import com.example.common.util.JsonData;
import com.example.user.dao.CollectMapper;
import com.example.user.service.CollectService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("collectService")
public class CollectServiceImpl implements CollectService {

    @Autowired
    private CollectMapper collectionMapper;

    @Override
    public Integer countive(JsonData jsonData) {
        return collectionMapper.countive(jsonData);
    }

    @Override
    public List<Collect> selective(JsonData jsonData) {
        if(jsonData.containsKey("page")&&jsonData.containsKey("size")){
            PageHelper.startPage(Integer.valueOf(jsonData.get("page").toString()), Integer.valueOf(jsonData.get("size").toString()));
        }
        return collectionMapper.selective(jsonData);
    }

    @Override
    public int deletive(JsonData jsonData) {
        return collectionMapper.deletive(jsonData);
    }

    @Override
    public int insertive(JsonData jsonData) {
        return collectionMapper.insertive(jsonData);
    }
}
