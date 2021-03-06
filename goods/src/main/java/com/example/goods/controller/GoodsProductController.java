package com.example.goods.controller;

import com.example.common.entity.GoodsProduct;
import com.example.common.response.RestResponse;
import com.example.common.util.JsonData;
import com.example.goods.service.GoodsProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class GoodsProductController {

    @Autowired
    private GoodsProductService goodsProductService;

    @GetMapping("/getGoodsProductById")
    public RestResponse<GoodsProduct> getGoodsProductByProductId(JsonData jsonData){
        return new RestResponse<>(goodsProductService.selective(jsonData).get(0));
    }

    @PostMapping("/reduceStock")
    public RestResponse<Integer> reduceStock(int productId,int number){
        return new RestResponse<>(goodsProductService.reduceStock(productId,number));
    }

    @GetMapping("getGoodsProductAll")
    public RestResponse<List<GoodsProduct>> getGoodsProductAll(JsonData jsonData){
        return new RestResponse<>(goodsProductService.selective(jsonData));
    }

    @GetMapping("/addStock")
    public RestResponse addStock(int productId,int number){
        return new RestResponse(goodsProductService.addStock(productId,number));
    }
}
