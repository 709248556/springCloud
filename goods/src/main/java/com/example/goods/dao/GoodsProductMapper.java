package com.example.goods.dao;

import com.example.common.entity.GoodsProduct;
import com.example.common.util.JsonData;

import java.util.List;

public interface GoodsProductMapper {
    List<GoodsProduct> selective(JsonData jsonData);
}
