package com.example.goods.service;

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
    public List<GoodsSpecificationVo> getGoodsSpecificationVoList(JsonData jsonData);
}
