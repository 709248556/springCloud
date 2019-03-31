package com.example.goods.dao;

import com.example.common.entity.GoodsSpecification;
import com.example.common.util.JsonData;

import java.util.List;

public interface GoodsSpecificationMapper {
    List<GoodsSpecification> selective(JsonData jsonData);
    int deleteByGid(int id);
    int insert(GoodsSpecification goodsSpecification);
}
