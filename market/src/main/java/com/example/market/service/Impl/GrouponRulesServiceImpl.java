package com.example.market.service.Impl;

import com.example.common.entity.GrouponRules;
import com.example.common.util.JsonData;
import com.example.market.dao.GrouponRulesMapper;
import com.example.market.service.GrouponRulesService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service("grouponRulesService")
public class GrouponRulesServiceImpl implements GrouponRulesService {

    @Autowired
    private GrouponRulesMapper grouponRulesMapper;

    @Override
    public List<GrouponRules> selective(JsonData jsonData) {
        if(jsonData.containsKey("grouponPage")&&jsonData.containsKey("grouponSize"))
            PageHelper.startPage(Integer.valueOf(jsonData.get("grouponPage").toString()), Integer.valueOf(jsonData.get("grouponSize").toString()));
        if (jsonData.containsKey("page") && jsonData.containsKey("limit"))
            PageHelper.startPage(Integer.valueOf(jsonData.get("page").toString()), Integer.valueOf(jsonData.get("limit").toString()));
        return grouponRulesMapper.selective(jsonData);
    }

    @Override
    public int updateById(GrouponRules grouponRules) {
        grouponRules.setUpdateTime(LocalDateTime.now());
        return grouponRulesMapper.updateById(grouponRules);
    }

    @Override
    public int insert(GrouponRules grouponRules) {
        grouponRules.setAddTime(LocalDateTime.now());
        grouponRules.setUpdateTime(LocalDateTime.now());
        return grouponRulesMapper.insert(grouponRules);
    }

    @Override
    public int deleteById(int id) {
        return grouponRulesMapper.deleteById(id);
    }
}
