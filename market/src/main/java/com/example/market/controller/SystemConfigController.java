package com.example.market.controller;

import com.example.common.entity.SystemConfigVo;
import com.example.common.response.RestResponse;
import com.example.market.systemConfig.SystemConfig;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class SystemConfigController {
    /**
     * 新品首发页面的横幅
     *
     * @return 新品首发页面的横幅
     */
    @GetMapping("/new")
    public RestResponse newGoods(Model model) {
        Map<String, String> bannerInfo = new HashMap<>();
        bannerInfo.put("url", "");
        bannerInfo.put("name", SystemConfig.getNewBannerTitle());
        bannerInfo.put("imgUrl", SystemConfig.getNewImageUrl());

        model.addAttribute("bannerInfo", bannerInfo);
        return new RestResponse(model);
    }
    /**
     * 人气推荐页面的横幅
     *
     * @return 人气推荐页面的横幅
     */
    @GetMapping("/hot")
    public RestResponse hotGoods(Model model) {
        Map<String, String> bannerInfo = new HashMap<>();
        bannerInfo.put("url", "");
        bannerInfo.put("name", SystemConfig.getHotBannerTitle());
        bannerInfo.put("imgUrl", SystemConfig.getHotImageUrl());
        model.addAttribute("bannerInfo", bannerInfo);
        return new RestResponse(model);
    }

    @GetMapping("/getFreightLimit")
    public RestResponse getSystemConfig(){
        return new RestResponse(SystemConfig.getFreightLimit());
    }
    @GetMapping("/getFreight")
    public RestResponse getFreight(){
        return new RestResponse(SystemConfig.getFreight());
    }

}
