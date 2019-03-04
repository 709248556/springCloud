package com.example.market.systemConfig;

import com.example.common.entity.SystemConfigVo;
import com.example.common.util.JsonData;
import com.example.market.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 该类用于自动初始化数据库配置到BaseConfig中，以便BaseConfig的子类调用
 */
@Component
class ConfigService {
    private static ConfigService configService;
    @Autowired
    private SystemConfigService systemConfigService;

    //不允许实例化
    private ConfigService() {

    }

    static ConfigService getConfigService() {
        return configService;
    }

    @PostConstruct
    public void inist() {
        configService = this;
        configService.inistConfigs();
    }

    /**
     * 根据 prefix 重置该 prefix 下所有设置
     *
     * @param prefix
     */
    public void reloadConfig(String prefix) {
        JsonData jsonData = new JsonData();
        List<SystemConfigVo> list = systemConfigService.selective(jsonData);
        for (SystemConfigVo item : list) {
            //符合条件，添加
            if (item.getKeyName().startsWith(prefix))
                BaseConfig.addConfig(item.getKeyName(), item.getKeyValue());
        }
    }

    /**
     * 读取全部配置
     */
    private void inistConfigs() {
        JsonData jsonData = new JsonData();
        List<SystemConfigVo> list = systemConfigService.selective(jsonData);
        for (SystemConfigVo item : list) {
            BaseConfig.addConfig(item.getKeyName(), item.getKeyValue());
        }
    }
}