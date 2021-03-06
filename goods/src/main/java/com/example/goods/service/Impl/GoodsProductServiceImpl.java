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

    @Override
    public int reduceStock(int productId, int number) {
        return goodsProductMapper.reduceStock(productId, number);
    }

    @Override
    public int addStock(int productId, int number) {
        return goodsProductMapper.addStock(productId, number);
    }

    @Override
    public int deleteByGid(int id) {
        return goodsProductMapper.deleteByGid(id);
    }

    @Override
    public int insert(GoodsProduct goodsProduct) {
        return goodsProductMapper.insert(goodsProduct);
    }

    @Override
    public List<GoodsProduct> queryByGid(int id) {
        JsonData jsonData = new JsonData();
        jsonData.put("goodsId",id);
        jsonData.put("delete",0);
        return goodsProductMapper.selective(jsonData);
    }
}
