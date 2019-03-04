package com.example.user.service.Impl;

import com.example.common.util.JsonData;
import com.example.user.dao.CollectMapper;
import com.example.user.service.CollectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("collectService")
public class CollectServiceImpl implements CollectService {

    @Autowired
    private CollectMapper collectionMapper;

    @Override
    public int countive(JsonData jsonData) {
        return collectionMapper.countive(jsonData);
    }
}
