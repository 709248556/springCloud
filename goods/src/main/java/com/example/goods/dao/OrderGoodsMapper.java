package com.example.goods.dao;

import com.example.common.entity.OrderGoods;
import com.example.common.util.JsonData;

import java.util.List;

public interface OrderGoodsMapper {
    List<OrderGoods> selective(JsonData jsonData);
}
