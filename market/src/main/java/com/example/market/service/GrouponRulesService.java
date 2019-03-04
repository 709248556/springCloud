package com.example.market.service;

import com.example.common.entity.GrouponRules;
import com.example.common.util.JsonData;

import java.util.List;

public interface GrouponRulesService {
    List<GrouponRules> selective(JsonData jsonData);
}
