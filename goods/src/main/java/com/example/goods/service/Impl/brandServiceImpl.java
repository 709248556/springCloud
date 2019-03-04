package com.example.goods.service.Impl;

import com.example.common.entity.Brand;
import com.example.common.util.JsonData;
import com.example.goods.dao.BrandMapper;
import com.example.goods.service.BrandService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("brandService")
public class brandServiceImpl implements BrandService {
    @Autowired
    private BrandMapper brandMapper;
    @Override
    public List<Brand> selective(JsonData jsonData) {
        if (jsonData.containsKey("brandPage") && jsonData.containsKey("brandSize"))
            PageHelper.startPage(Integer.valueOf(jsonData.get("brandPage").toString()), Integer.valueOf(jsonData.get("brandSize").toString()));
        return brandMapper.selective(jsonData);
    }
}
