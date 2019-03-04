package com.example.market.service.Impl;

import com.example.common.entity.Groupon;
import com.example.common.entity.GrouponRules;
import com.example.common.util.JsonData;
import com.example.market.dao.GrouponMapper;
import com.example.market.service.GrouponRulesService;
import com.example.market.service.GrouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("rouponRulesService")
public class GrouponServiceImpl implements GrouponService {

    @Autowired
    private GrouponMapper grouponMapper;

    @Override
    public List<Groupon> selective(JsonData jsonData) {
        return grouponMapper.selective(jsonData);
    }
}
