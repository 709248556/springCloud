package com.example.goods.service;

import com.example.common.entity.GoodsAttribute;
import com.example.common.util.JsonData;

import java.util.List;

public interface GoodsAttributeService {

    public List<GoodsAttribute> selective(JsonData jsonData);
}
