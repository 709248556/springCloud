package com.example.market.dao;

import com.example.common.entity.SystemConfigVo;
import com.example.common.util.JsonData;

import java.util.List;

public interface SystemConfigMapper {
    List<SystemConfigVo> selective(JsonData jsonData);
}