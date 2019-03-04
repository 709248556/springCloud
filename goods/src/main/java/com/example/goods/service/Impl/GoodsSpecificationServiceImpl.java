package com.example.goods.service.Impl;

import com.example.common.entity.GoodsSpecification;
import com.example.common.entity.GoodsSpecificationVo;
import com.example.common.util.JsonData;
import com.example.goods.dao.GoodsSpecificationMapper;
import com.example.goods.service.GoodsSpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("goodsSpecificationService")
public class GoodsSpecificationServiceImpl implements GoodsSpecificationService{
    @Autowired
    private GoodsSpecificationMapper goodsSpecificationMapper;
    @Override
    public List<GoodsSpecificationVo> getGoodsSpecificationVoList(JsonData jsonData) {
        List<GoodsSpecification> goodsSpecificationList = goodsSpecificationMapper.selective(jsonData);
        Map<String,GoodsSpecificationVo> map = new HashMap<>();
        List<GoodsSpecificationVo> specificationVoList = new ArrayList<>();
        for (GoodsSpecification goodsSpecification : goodsSpecificationList) {
            String specification = goodsSpecification.getSpecification();
            GoodsSpecificationVo goodsSpecificationVo = map.get(specification);
            if (goodsSpecificationVo == null) {
                goodsSpecificationVo = new GoodsSpecificationVo();
                goodsSpecificationVo.setName(specification);
                List<GoodsSpecification> valueList = new ArrayList<>();
                valueList.add(goodsSpecification);
                goodsSpecificationVo.setValueList(valueList);
                map.put(specification, goodsSpecificationVo);
                specificationVoList.add(goodsSpecificationVo);
            } else {
                List<GoodsSpecification> valueList = goodsSpecificationVo.getValueList();
                valueList.add(goodsSpecification);
            }
        }
        return specificationVoList;
    }
}
