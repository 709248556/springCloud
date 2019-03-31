package com.example.user.dao;

import com.example.common.entity.Order;
import com.example.common.util.JsonData;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderMapper {
    List<Order> selective(JsonData jsonData);
    int insert(Order order);
    int updative(JsonData jsonData);
    int update(@Param("lastUpdateTime") LocalDateTime lastUpdateTime, @Param("order") Order order);
}
