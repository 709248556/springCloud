package com.example.market.service;

import com.example.common.entity.SystemConfigVo;
import com.example.common.util.JsonData;

import java.util.List;

public interface SystemConfigService {
    public List<SystemConfigVo> selective(JsonData jsonData);
}
