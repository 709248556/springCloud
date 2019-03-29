package com.example.user.service.Impl;

import com.example.common.entity.Order;
import com.example.common.util.JsonData;
import com.example.user.dao.OrderMapper;
import com.example.user.service.OrderService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("orderService")
public class OrderServiceImol implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public List<Order> selective(JsonData jsonData) {
        if (jsonData.containsKey("page") && jsonData.containsKey("limit"))
            PageHelper.startPage(Integer.valueOf(jsonData.get("page").toString()), Integer.valueOf(jsonData.get("limit").toString()));
        if(jsonData.containsKey("name")){
            jsonData.put("name","%"+jsonData.get("name")+"%");
        }
        return orderMapper.selective(jsonData);
    }

    @Override
    public int insert(Order order) {
        return orderMapper.insert(order);
    }

    @Override
    public int updative(JsonData jsonData) {
        return orderMapper.updative(jsonData);
    }
}
