package com.example.goods.service;

import com.example.common.entity.GoodsSpecification;
import com.example.common.entity.GoodsSpecificationVo;
import com.example.common.util.JsonData;

import java.util.List;

public interface GoodsSpecificationService {

    /**
     * [
     * {
     * name: '',
     * valueList: [ {}, {}]
     * },
     * {
     * name: '',
     * valueList: [ {}, {}]
     * }
     * ]
     *
     */
    List<GoodsSpecificationVo> getGoodsSpecificationVoList(JsonData jsonData);

    int deleteByGid(int id);

    int insert(GoodsSpecification goodsSpecification);


    List<GoodsSpecification> queryByGid(int id);

}
