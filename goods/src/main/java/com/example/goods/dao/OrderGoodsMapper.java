package com.example.goods.dao;

import com.example.common.entity.OrderGoods;
import com.example.common.util.JsonData;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderGoodsMapper {
    List<OrderGoods> selective(JsonData jsonData);
    int insert(OrderGoods orderGoods);

}
