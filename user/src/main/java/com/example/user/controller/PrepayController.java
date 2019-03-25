package com.example.user.controller;

import com.example.common.enums.OrderEnum;
import com.example.common.response.RestResponse;
import com.example.common.util.JacksonUtil;
import com.example.common.util.JsonData;
import com.example.user.service.OrderService;
import com.github.binarywang.wxpay.util.SignUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class PrepayController {

    @Autowired
    private OrderService orderService;

    @PostMapping("prepay")
    public RestResponse prepay(){
        RestResponse restResponse = new RestResponse();
        //模拟订单支付失败
        restResponse.error(OrderEnum.ORDER_INVALID_PAY);
        return  restResponse;
    }

    @PostMapping("paySuccess")
    public RestResponse paySuccess(@RequestBody String body){
        RestResponse restResponse = new RestResponse();
        //模拟订单支付成功
        JsonData jsonData = new JsonData();
        Integer orderId = JacksonUtil.parseInteger(body, "orderId");
        jsonData.put("orderId",orderId);
        jsonData.put("updateTime", LocalDateTime.now());
        jsonData.put("orderStatus",201);
        orderService.updative(jsonData);
        return  restResponse;
    }
}
