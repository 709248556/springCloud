package com.example.market.controller;

import com.example.common.entity.Goods;
import com.example.common.entity.GrouponRules;
import com.example.common.enums.RestEnum;
import com.example.common.feign.GoodsClient;
import com.example.common.response.RestResponse;
import com.example.common.util.JsonData;
import com.example.market.service.GrouponRulesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
public class GrouponRulesController {

    @Autowired
    private GrouponRulesService grouponRulesService;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private GrouponRulesService rulesService;

    @GetMapping("/getGrouponRules")
    public RestResponse<List<GrouponRules>> getGrouponRules(JsonData jsonData){
        return new RestResponse(grouponRulesService.selective(jsonData));
    }

    @PostMapping("/update")
    public RestResponse update(@RequestBody GrouponRules grouponRules) {
        RestResponse restResponse = validate(grouponRules);
        if (restResponse != null) {
            return restResponse;
        }

        Integer goodsId = grouponRules.getGoodsId();
        Goods goods = null;
        try {
            RestResponse<Goods> goodsRestResponse = goodsClient.getGoodsById(goodsId);
            if (goodsRestResponse.getErrno() != RestEnum.OK.code) {
                log.error("GrouponRulesController.update方法错误 goodsRestRespone.getErrno() != RestEnum.OK.code，Errno为:" + goodsRestResponse.getErrno() + "错误信息为:" + goodsRestResponse.getErrmsg());
                //TODO 抛出异常
            }
            goods = goodsRestResponse.getData();
        }catch (Exception e){
            log.error("GrouponRulesController.update ERROR", e.getMessage());
        }
        if (goods == null) {
            return new RestResponse(RestEnum.BADARGUMENT);
        }

        grouponRules.setGoodsName(goods.getName());
        grouponRules.setPicUrl(goods.getPicUrl());

        if (rulesService.updateById(grouponRules) == 0) {
            return restResponse.error(RestEnum.UPDATEDDATAFAILED);
        }

        return restResponse;
    }

    @PostMapping("/create")
    public RestResponse create(@RequestBody GrouponRules grouponRules) {
        RestResponse restResponse = validate(grouponRules);
        if (restResponse != null) {
            return restResponse;
        }

        Integer goodsId = grouponRules.getGoodsId();
        Goods goods = null;
        try {
            RestResponse<Goods> goodsRestResponse = goodsClient.getGoodsById(goodsId);
            if (goodsRestResponse.getErrno() != RestEnum.OK.code) {
                log.error("GrouponRulesController.create方法错误 goodsRestRespone.getErrno() != RestEnum.OK.code，Errno为:" + goodsRestResponse.getErrno() + "错误信息为:" + goodsRestResponse.getErrmsg());
                //TODO 抛出异常
            }
            goods = goodsRestResponse.getData();
        }catch (Exception e){
            log.error("GrouponRulesController.create ERROR", e.getMessage());
        }
        if (goods == null) {
            return new RestResponse(RestEnum.BADARGUMENT);
        }

        grouponRules.setGoodsName(goods.getName());
        grouponRules.setPicUrl(goods.getPicUrl());

        if(rulesService.insert(grouponRules) == 0) return restResponse.error(RestEnum.SERIOUS);

        return restResponse.success(grouponRules);
    }

    @PostMapping("/delete")
    public RestResponse delete(@RequestBody GrouponRules grouponRules) {
        Integer id = grouponRules.getId();
        if (id == null) {
            return new RestResponse(RestEnum.BADARGUMENT);
        }

        if(rulesService.deleteById(id) == 0) return new RestResponse(RestEnum.SERIOUS);
        return new RestResponse();
    }
    private RestResponse validate(GrouponRules grouponRules) {
        Integer goodsId = grouponRules.getGoodsId();
        if (goodsId == null) {
            return new RestResponse(RestEnum.BADARGUMENT);
        }
        BigDecimal discount = grouponRules.getDiscount();
        if (discount == null) {
            return new RestResponse(RestEnum.BADARGUMENT);
        }
        Integer discountMember = grouponRules.getDiscountMember();
        if (discountMember == null) {
            return new RestResponse(RestEnum.BADARGUMENT);
        }
        LocalDateTime expireTime = grouponRules.getExpireTime();
        if (expireTime == null) {
            return new RestResponse(RestEnum.BADARGUMENT);
        }

        return null;
    }
}
