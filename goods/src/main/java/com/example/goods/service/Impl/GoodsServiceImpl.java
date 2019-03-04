package com.example.goods.service.Impl;

import com.example.common.entity.Goods;
import com.example.common.util.JsonData;
import com.example.goods.dao.GoodsMapper;
import com.example.goods.service.GoodsService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("goodsService")
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    /**
     * 获取所有在售物品总数
     *
     * @return
     */
    @Override
    public int queryOnSale() {
        return goodsMapper.countByExample();
    }

    /**
     * 获取某个商品信息,包含完整信息
     */
    @Override
    public List<Goods> selective(JsonData jsonData) {
        if (jsonData.containsKey("page") && jsonData.containsKey("size"))
            PageHelper.startPage(Integer.valueOf(jsonData.get("page").toString()), Integer.valueOf(jsonData.get("size").toString()));
        return goodsMapper.selective(jsonData);
    }
}
