package com.example.goods.service.Impl;

import com.example.common.entity.*;
import com.example.common.response.RestResponse;
import com.example.common.util.JsonData;
import com.example.goods.dao.GoodsMapper;
import com.example.goods.service.GoodsAttributeService;
import com.example.goods.service.GoodsProductService;
import com.example.goods.service.GoodsService;
import com.example.goods.service.GoodsSpecificationService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("goodsService")
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private GoodsSpecificationService specificationService;

    @Autowired
    private GoodsProductService productService;

    @Autowired
    private GoodsAttributeService attributeService;

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
        if (jsonData.containsKey("page") && jsonData.containsKey("limit"))
            PageHelper.startPage(Integer.valueOf(jsonData.get("page").toString()), Integer.valueOf(jsonData.get("limit").toString()));
        if (jsonData.containsKey("name")) {
            jsonData.put("name", "%" + jsonData.get("name") + "%");
        }
        if (jsonData.containsKey("goodsSn")) {
            jsonData.put("goodsSn", "%" + jsonData.get("goodsSn") + "%");
        }
        return goodsMapper.selective(jsonData);
    }


    /**
     * 编辑商品
     * <p>
     * TODO
     * 目前商品修改的逻辑是
     * 1. 更新litemall_goods表
     * 2. 逻辑删除litemall_goods_specification、litemall_goods_attribute、litemall_goods_product
     * 3. 添加litemall_goods_specification、litemall_goods_attribute、litemall_goods_product
     * <p>
     * 这里商品三个表的数据采用删除再添加的策略是因为
     * 商品编辑页面，支持管理员添加删除商品规格、添加删除商品属性，因此这里仅仅更新是不可能的，
     * 只能删除三个表旧的数据，然后添加新的数据。
     * 但是这里又会引入新的问题，就是存在订单商品货品ID指向了失效的商品货品表。
     * 因此这里会拒绝管理员编辑商品，如果订单或购物车中存在商品。
     * 所以这里可能需要重新设计。
     */
    @Transactional
    @Override
    public RestResponse update(Goods goods, GoodsSpecification[] specifications, GoodsAttribute[] attributes, GoodsProduct[] products) {

        //将生成的分享图片地址写入数据库
//            String url = qCodeService.createGoodShareImage(goods.getId().toString(), goods.getPicUrl(), goods.getName());
//            goods.setShareUrl(url);

        // 商品基本信息表litemall_goods
        goods.setUpdateTime(LocalDateTime.now());
        if (goodsMapper.updateById(goods) == 0) {
            throw new RuntimeException("更新数据失败");
        }

        Integer gid = goods.getId();
        if (specificationService.deleteByGid(gid) == 0) throw new RuntimeException("更新数据失败");
        if (attributeService.deleteByGid(gid) == 0) throw new RuntimeException("更新数据失败");
        if (productService.deleteByGid(gid) == 0) throw new RuntimeException("更新数据失败");

        // 商品规格表litemall_goods_specification
        for (GoodsSpecification specification : specifications) {
            specification.setGoodsId(goods.getId());
            specification.setAddTime(LocalDateTime.now());
            specification.setUpdateTime(LocalDateTime.now());
            if (specificationService.insert(specification) == 0) throw new RuntimeException("插入数据失败");
        }

        // 商品参数表litemall_goods_attribute
        for (GoodsAttribute attribute : attributes) {
            attribute.setGoodsId(goods.getId());
            attribute.setAddTime(LocalDateTime.now());
            attribute.setUpdateTime(LocalDateTime.now());
            if (attributeService.insert(attribute) == 0) throw new RuntimeException("插入数据失败");
        }

        // 商品货品表litemall_product
        for (GoodsProduct product : products) {
            product.setGoodsId(goods.getId());
            product.setAddTime(LocalDateTime.now());
            product.setUpdateTime(LocalDateTime.now());
            if (productService.insert(product) == 0) throw new RuntimeException("插入数据失败");
        }
//            qCodeService.createGoodShareImage(goods.getId().toString(), goods.getPicUrl(), goods.getName());

        return new RestResponse();
    }

    @Override
    public RestResponse delete(Goods goods) {

        Integer gid = goods.getId();
        if (goodsMapper.deleteById(gid) == 0) throw new RuntimeException("更新数据失败");
        if (specificationService.deleteByGid(gid) == 0) throw new RuntimeException("更新数据失败");
        if (attributeService.deleteByGid(gid) == 0) throw new RuntimeException("更新数据失败");
        if (productService.deleteByGid(gid) == 0) throw new RuntimeException("更新数据失败");
        return new RestResponse();
    }

    @Override
    public RestResponse create(Goods goods, GoodsAttribute[] attributes, GoodsSpecification[] specifications, GoodsProduct[] products) {
        // 商品规格表litemall_goods_specification
        for (GoodsSpecification specification : specifications) {
            specification.setGoodsId(goods.getId());
            if (specificationService.insert(specification) == 0) throw new RuntimeException("插入数据失败");
        }

        // 商品参数表litemall_goods_attribute
        for (GoodsAttribute attribute : attributes) {
            attribute.setGoodsId(goods.getId());
            if (attributeService.insert(attribute) == 0) throw new RuntimeException("插入数据失败");
        }

        // 商品货品表litemall_product
        for (GoodsProduct product : products) {
            product.setGoodsId(goods.getId());
            if (productService.insert(product) == 0) throw new RuntimeException("插入数据失败");
        }
        return new RestResponse();
    }

    @Override
    public Boolean checkExistByName(String name) {
        JsonData jsonData = new JsonData();
        jsonData.put("realName", name);
        jsonData.put("isOnSale", 1);
        jsonData.put("deleted", 0);
        return goodsMapper.selective(jsonData).size() == 0;
    }

}
