package com.example.goods.controller;

import com.example.common.response.RestResponse;
import com.example.common.util.JsonData;
import com.example.goods.service.BrandService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class BrandController {

    @Autowired
    private BrandService brandService;

    @GetMapping("/getBrand")
    public RestResponse getBrand(JsonData jsonData){
        return new RestResponse(brandService.selective(jsonData));
    }
}
