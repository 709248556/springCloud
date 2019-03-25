package com.example.goods.controller;

import com.example.common.constants.TokenConstant;
import com.example.common.entity.OrderGoods;
import com.example.common.response.RestResponse;
import com.example.common.util.JsonData;
import com.example.common.util.RedisUtil;
import com.example.goods.service.OrderGoodsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
public class OrderGoodsController {
    @Autowired
    private OrderGoodsService orderGoodsService;



    @GetMapping("/getOrderGoodsByOrderId")
    public RestResponse<List<OrderGoods>> getOrderGoodsByOrderId(JsonData jsonData) {
        return new RestResponse<>(orderGoodsService.selective(jsonData));
    }

    @PostMapping("/addOrderGoods")
    public RestResponse addOrderGoods(@RequestBody OrderGoods orderGoods){
        return new RestResponse(orderGoodsService.insert(orderGoods));
    }

}
