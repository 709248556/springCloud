package com.example.market.dao;

import com.example.common.entity.Category;
import com.example.common.entity.GrouponRules;
import com.example.common.util.JsonData;

import java.util.List;

public interface CategoryMapper {
    List<Category> selective(JsonData jsonData);
    int insert(Category category);
    int updateById(Category category);
    int deleteById(int id);
}
