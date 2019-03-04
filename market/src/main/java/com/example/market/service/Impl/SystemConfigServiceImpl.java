package com.example.market.service.Impl;

import com.example.common.entity.SystemConfigVo;
import com.example.common.util.JsonData;
import com.example.market.dao.SystemConfigMapper;
import com.example.market.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("systemConfigService")
public class SystemConfigServiceImpl implements SystemConfigService {

    @Autowired
    private SystemConfigMapper systemConfigMapper;

    @Override
    public List<SystemConfigVo> selective(JsonData jsonData) {
        return systemConfigMapper.selective(jsonData);
    }
}
