package com.example.market.dao;

import com.example.common.entity.GrouponRules;
import com.example.common.util.JsonData;

import java.util.List;

public interface GrouponRulesMapper {
    List<GrouponRules> selective(JsonData jsonData);
}
