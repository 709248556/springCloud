package com.example.common.hystrix;

import com.example.common.entity.*;
import com.example.common.enums.MarketClientEnum;
import com.example.common.feign.MarketClient;
import com.example.common.response.RestResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class MarketFallback implements MarketClient {
    @Override
    public RestResponse<List<Issue>> getIssueAll() {
        return new RestResponse(MarketClientEnum.GET_ISSUEALL);
    }

    @Override
    public RestResponse<List<Issue>> getIssue(int issueId) {
        return new RestResponse(MarketClientEnum.GET_ISSUE);
    }

    @Override
    public RestResponse<List<GrouponRules>> getGrouponRules(int goodsId) {
        return new RestResponse(MarketClientEnum.GET_GROUPONRULES);
    }

    @Override
    public RestResponse<List<GrouponRules>> getGrouponRulesByGrouponRulesId(int grouponRulesId) {
        return null;
    }

    @Override
    public RestResponse<List<Category>> getCategoryList(List<Integer> categoryIdList) {
        return new RestResponse(MarketClientEnum.GET_GETCATEGORYLIST);
    }

    @Override
    public RestResponse<List<Groupon>> getGrouponByOrderId(int orderId, int grouponDeleted) {
        return null;
    }

    @Override
    public RestResponse<List<Cart>> getCartByUidAndChecked(int userId, int checked, int deleted) {
        return null;
    }

    @Override
    public RestResponse<List<Cart>> getCartByCartId(int cartId) {
        return null;
    }

    @Override
    public RestResponse<Coupon> getCouponVerify(int userId, int couponId, BigDecimal checkedGoodsPrice) {
        return null;
    }

    @Override
    public RestResponse<BigDecimal> getFreightLimit() {
        return null;
    }

    @Override
    public RestResponse<BigDecimal> getFreight() {
        return null;
    }

    @Override
    public RestResponse<Integer> clearGoods(int userId, int checked) {
        return null;
    }


}
