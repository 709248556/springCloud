package com.example.goods.service.Impl;

import com.example.common.entity.GoodsProduct;
import com.example.common.util.JsonData;
import com.example.goods.dao.GoodsProductMapper;
import com.example.goods.service.GoodsProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("goodsProductService")
public class GoodsProductServiceImpl implements GoodsProductService {

    @Autowired
    private GoodsProductMapper goodsProductMapper;

    @Override
    public List<GoodsProduct> selective(JsonData jsonData) {
        return goodsProductMapper.selective(jsonData);
    }
}
