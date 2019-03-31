package com.example.goods.dao;

import com.example.common.entity.Goods;
import com.example.common.entity.GoodsAttribute;
import com.example.common.util.JsonData;

import java.util.List;

public interface GoodsAttributeMapper {
    List<GoodsAttribute>  selective(JsonData jsonData);
    int deleteByGid(int id);
    int insert(GoodsAttribute goodsAttribute);
}
