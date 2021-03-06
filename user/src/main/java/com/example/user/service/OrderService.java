package com.example.user.service;

import com.example.common.entity.Order;
import com.example.common.response.RestResponse;
import com.example.common.util.JacksonUtil;
import com.example.common.util.JsonData;

import java.util.List;

public interface OrderService {
    List<Order> selective(JsonData jsonData);
    int insert(Order order);
    int updative(JsonData jsonData);
    RestResponse refund(Integer orderId,String refundMoney);
    RestResponse ship(Integer orderId ,String shipSn,String shipChannel);
}
