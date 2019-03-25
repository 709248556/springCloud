package com.example.common.feign;

import com.example.common.entity.*;
import com.example.common.hystrix.MarketFallback;
import com.example.common.response.RestResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.naming.Name;
import java.math.BigDecimal;
import java.util.List;

@FeignClient(value = "market",fallback = MarketFallback.class)
public interface MarketClient {

    @GetMapping("/getIssueAll")
    RestResponse<List<Issue>> getIssueAll();

    @GetMapping("/getIssue")
    RestResponse<List<Issue>> getIssue(@RequestParam("issueId") int issueId);

    @GetMapping("/getGrouponRules")
    RestResponse<List<GrouponRules>> getGrouponRules(@RequestParam("goodsId") int goodsId);

    @GetMapping("/getGrouponRules")
    RestResponse<List<GrouponRules>> getGrouponRulesByGrouponRulesId(@RequestParam("grouponRulesId") int grouponRulesId);

    @PostMapping("/getCategorylist")
    RestResponse<List<Category>> getCategoryList(@RequestBody List<Integer> categoryIdList);

    @GetMapping("/getGrouponByOrderId")
    RestResponse<List<Groupon>> getGrouponByOrderId(@RequestParam("orderId") int orderId,@RequestParam("grouponDeleted") int grouponDeleted);

    @GetMapping("/getCart")
    RestResponse<List<Cart>> getCartByUidAndChecked(@RequestParam("userId") int userId,@RequestParam("Checked") int checked,@RequestParam("deleted") int deleted);

    @GetMapping("/getCart")
    RestResponse<List<Cart>> getCartByCartId(@RequestParam("cartId") int cartId);

    @GetMapping("/getCouponVerify")
    RestResponse<Coupon> getCouponVerify(@RequestParam("userId") int userId,@RequestParam("couponId") int couponId,@RequestParam("checkedGoodsPrice") BigDecimal checkedGoodsPrice);

    @GetMapping("/getFreightLimit")
    RestResponse<BigDecimal> getFreightLimit();

    @GetMapping("/getFreight")
    RestResponse<BigDecimal> getFreight();

    @GetMapping("/clearGoods")
    RestResponse<Integer> clearGoods(@RequestParam("userId") int userId,@RequestParam("checked") int checked);

}
