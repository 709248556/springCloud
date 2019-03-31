package com.example.goods.service.Impl;

import com.example.common.entity.GoodsAttribute;
import com.example.common.util.JsonData;
import com.example.goods.dao.GoodsAttributeMapper;
import com.example.goods.service.GoodsAttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service("goodsAttributeService")
public class GoodsAttributeServiceImpl implements GoodsAttributeService {
    @Autowired
    private GoodsAttributeMapper goodsAttributeMapper;

    @Override
    public List<GoodsAttribute> selective(JsonData jsonData) {
        return goodsAttributeMapper.selective(jsonData);
    }

    @Override
    public int deleteByGid(int id) {
        return goodsAttributeMapper.deleteByGid(id);
    }

    @Override
    public int insert(GoodsAttribute goodsAttribute) {
        return goodsAttributeMapper.insert(goodsAttribute);
    }

    @Override
    public List<GoodsAttribute> queryByGid(int id) {
        JsonData jsonData = new JsonData();
        jsonData.put("goodsId",id);
        jsonData.put("deleted",0);
        return goodsAttributeMapper.selective(jsonData);
    }
}
