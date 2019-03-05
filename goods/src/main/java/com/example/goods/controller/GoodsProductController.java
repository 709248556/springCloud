package com.example.goods.controller;

import com.example.common.entity.GoodsProduct;
import com.example.common.response.RestResponse;
import com.example.common.util.JsonData;
import com.example.goods.service.GoodsProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class GoodsProductController {

    @Autowired
    private GoodsProductService goodsProductService;

    @GetMapping("/getGoodsProductById")
    public RestResponse<GoodsProduct> getGoodsProductByProductId(JsonData jsonData){
        return new RestResponse<>(goodsProductService.selective(jsonData).get(0));
    }
}
