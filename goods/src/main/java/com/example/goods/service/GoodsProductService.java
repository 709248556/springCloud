package com.example.goods.service;

import com.example.common.entity.GoodsProduct;
import com.example.common.util.JsonData;

import java.util.List;

public interface GoodsProductService {
    List<GoodsProduct> selective(JsonData jsonData);
}
