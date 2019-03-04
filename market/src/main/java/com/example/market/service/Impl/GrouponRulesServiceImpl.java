package com.example.market.service.Impl;

import com.example.common.entity.GrouponRules;
import com.example.common.util.JsonData;
import com.example.market.dao.GrouponRulesMapper;
import com.example.market.service.GrouponRulesService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("grouponRulesService")
public class GrouponRulesServiceImpl implements GrouponRulesService {

    @Autowired
    private GrouponRulesMapper grouponRulesMapper;

    @Override
    public List<GrouponRules> selective(JsonData jsonData) {
        if(jsonData.containsKey("grouponPage")&&jsonData.containsKey("grouponSize"))
            PageHelper.startPage(Integer.valueOf(jsonData.get("grouponPage").toString()), Integer.valueOf(jsonData.get("grouponSize").toString()));
        return grouponRulesMapper.selective(jsonData);
    }
}
