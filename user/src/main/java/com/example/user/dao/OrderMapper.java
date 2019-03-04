package com.example.user.dao;

import com.example.common.entity.Order;
import com.example.common.util.JsonData;

import java.util.List;

public interface OrderMapper {
    List<Order> selective(JsonData jsonData);
}
