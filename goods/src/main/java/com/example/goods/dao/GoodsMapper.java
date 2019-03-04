package com.example.goods.dao;

import com.example.common.entity.Goods;
import com.example.common.util.JsonData;

import java.util.List;

public interface GoodsMapper {
    int countByExample();

    List<Goods>  selective(JsonData jsonData);
}
