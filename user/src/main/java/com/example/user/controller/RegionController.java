package com.example.user.controller;

import com.example.common.entity.Region;
import com.example.common.response.RestResponse;
import com.example.common.util.JsonData;
import com.example.user.service.RegionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/region")
public class RegionController {

    @Autowired
    private RegionService regionService;

    /**
     * 区域数据
     * <p>
     * 根据父区域ID，返回子区域数据。
     * 如果父区域ID是0，则返回省级区域数据；
     *
     * @param //pid 父区域ID
     * @return 区域数据
     */
    @GetMapping("list")
    public Object list(JsonData jsonData) {
        List<Region> regionList = regionService.selective(jsonData);
        return new RestResponse<>(regionList);
    }
}
