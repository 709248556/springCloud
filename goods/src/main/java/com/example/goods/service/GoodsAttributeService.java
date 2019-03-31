package com.example.goods.service;

import com.example.common.entity.GoodsAttribute;
import com.example.common.util.JsonData;

import java.util.List;

public interface GoodsAttributeService {

    List<GoodsAttribute> selective(JsonData jsonData);

    int deleteByGid(int id);

    int insert(GoodsAttribute goodsAttribute);

    List<GoodsAttribute> queryByGid(int id);
}
