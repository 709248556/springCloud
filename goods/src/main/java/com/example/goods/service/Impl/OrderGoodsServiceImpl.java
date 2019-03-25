package com.example.goods.service.Impl;

import com.example.common.entity.OrderGoods;
import com.example.common.util.JsonData;
import com.example.goods.dao.OrderGoodsMapper;
import com.example.goods.service.OrderGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("orderGoodsService")
public class OrderGoodsServiceImpl implements OrderGoodsService {

    @Autowired
    private OrderGoodsMapper orderGoodsMapper;

    @Override
    public List<OrderGoods> selective(JsonData jsonData) {
        return orderGoodsMapper.selective(jsonData);
    }

    @Override
    public int insert(OrderGoods orderGoods) {
        return orderGoodsMapper.insert(orderGoods);
    }
}
