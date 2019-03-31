package com.example.market.service.Impl;

import com.example.common.entity.Category;
import com.example.common.util.JsonData;
import com.example.market.dao.CategoryMapper;
import com.example.market.service.CategoryService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service("categoryService")
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public List<Category> selective(JsonData jsonData) {
        if (jsonData.containsKey("categoryPage") && jsonData.containsKey("categorySize"))
            PageHelper.startPage(Integer.valueOf(jsonData.get("categoryPage").toString()), Integer.valueOf(jsonData.get("categorySize").toString()));
        if (jsonData.containsKey("page") && jsonData.containsKey("limit"))
            PageHelper.startPage(Integer.valueOf(jsonData.get("page").toString()), Integer.valueOf(jsonData.get("limit").toString()));
        if(jsonData.containsKey("name")){
            jsonData.put("name","%"+jsonData.get("name")+"%");
        }
        return categoryMapper.selective(jsonData);
    }

    @Override
    public int insert(Category category) {
        category.setAddTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        return categoryMapper.insert(category);
    }

    @Override
    public int updateById(Category category) {
        category.setUpdateTime(LocalDateTime.now());
        return categoryMapper.updateById(category);
    }

    @Override
    public int deleteById(int id) {
        return categoryMapper.deleteById(id);
    }
}
