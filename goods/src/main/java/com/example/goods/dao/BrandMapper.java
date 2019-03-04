package com.example.goods.dao;

import com.example.common.entity.Brand;
import com.example.common.util.JsonData;

import java.util.List;

public interface BrandMapper {
    List<Brand> selective(JsonData jsonData);
}
