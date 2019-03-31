package com.example.goods.service;

import com.example.common.entity.OrderGoods;
import com.example.common.util.JsonData;

import java.util.List;

public interface OrderGoodsService {
    List<OrderGoods> selective(JsonData jsonData);

    int insert(OrderGoods orderGoods);
}
