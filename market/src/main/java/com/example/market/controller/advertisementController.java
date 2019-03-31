package com.example.market.controller;

import com.example.common.entity.Advertisement;
import com.example.common.enums.RestEnum;
import com.example.common.response.RestResponse;
import com.example.common.util.JsonData;
import com.example.market.service.AdvertisementService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class advertisementController {

    @Autowired
    private AdvertisementService advertisementService;

    @GetMapping("advertisement/list")
    public RestResponse list(JsonData jsonData) {
        jsonData.put("deleted",0);
        List<Advertisement> adList = advertisementService.selective(jsonData);
        long total = PageInfo.of(adList).getTotal();
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", adList);
        return new RestResponse<>(data);
    }

    @PostMapping("advertisement/create")
    public Object create(@RequestBody Advertisement advertisement) {
        RestResponse restResponse = validate(advertisement);
        if (restResponse != null) {
            return restResponse;
        }
        advertisementService.insert(advertisement);
        return new RestResponse<>(advertisement);
    }

    @PostMapping("advertisement/update")
    public Object update(@RequestBody Advertisement advertisement) {
        RestResponse restResponse = validate(advertisement);
        if (restResponse != null) {
            return restResponse;
        }
        advertisement.setUpdateTime(LocalDateTime.now());
        if (advertisementService.updateById(advertisement) == 0) {
            return restResponse.error(RestEnum.UPDATEDDATAFAILED);
        }

        return restResponse;
    }

    @PostMapping("advertisement/delete")
    public Object delete(@RequestBody  Advertisement advertisement) {
        Integer id = advertisement.getId();
        if (id == null) {
            return new RestResponse(RestEnum.BADARGUMENT);
        }
        if(advertisementService.deleteById(id) == 0) return new RestResponse<>(RestEnum.SERIOUS);
        return new RestResponse();
    }

    private RestResponse validate(Advertisement ad) {
        String name = ad.getName();
        if (StringUtils.isEmpty(name)) {
            return new RestResponse(RestEnum.BADARGUMENT);
        }
        String content = ad.getContent();
        if (StringUtils.isEmpty(content)) {
            return new RestResponse(RestEnum.BADARGUMENT);
        }
        return null;
    }
}
