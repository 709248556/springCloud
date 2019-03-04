package com.example.goods.service;

import com.example.common.entity.Goods;
import com.example.common.entity.GoodsAttribute;
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
    public int queryOnSale();

    /**
     * 获取某个商品信息,包含完整信息
     */
    public List<Goods>  selective(JsonData jsonData);

}

