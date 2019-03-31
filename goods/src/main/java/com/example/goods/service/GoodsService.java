package com.example.goods.service;

import com.example.common.entity.*;
import com.example.common.response.RestResponse;
import com.example.common.util.JsonData;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public interface GoodsService {
    /**
     * 获取所有在售物品总数
     */
     int queryOnSale();

    /**
     * 获取某个商品信息,包含完整信息
     */
     List<Goods>  selective(JsonData jsonData);

    RestResponse update(Goods goods, GoodsSpecification[] specifications, GoodsAttribute[] attributes, GoodsProduct[] products);

    RestResponse delete(Goods goods);

    RestResponse create(Goods goods,GoodsAttribute[] attributes,GoodsSpecification[] specifications,GoodsProduct[] products);

    Boolean checkExistByName(String name);

}

