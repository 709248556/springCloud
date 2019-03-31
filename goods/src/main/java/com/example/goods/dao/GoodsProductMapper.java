package com.example.goods.dao;

import com.example.common.entity.GoodsAttribute;
import com.example.common.entity.GoodsProduct;
import com.example.common.util.JsonData;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GoodsProductMapper {
    List<GoodsProduct> selective(JsonData jsonData);
    int reduceStock(@Param("id") int id,@Param("num") int num);
    int addStock(@Param("id") Integer id, @Param("num") int num);
    int deleteByGid(int id);
    int insert(GoodsProduct goodsProduct);
}
