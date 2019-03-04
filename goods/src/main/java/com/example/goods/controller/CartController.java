package com.example.goods.controller;

import com.example.common.response.RestResponse;
import com.example.common.util.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class CartController {

    /**
     * 购物车商品货品数量
     * <p>
     * 如果用户没有登录，则返回空数据。
     *
     * @param userId 用户ID
     * @return 购物车商品货品数量
     */
    @GetMapping("goodscount")
    public RestResponse goodscount(Integer userId, JsonData jsonData) {
        if (userId == null) {
            return new RestResponse<>();
        }

//        int goodsCount = 0;
//        List<LitemallCart> cartList = cartService.queryByUid(userId);
//        for (LitemallCart cart : cartList) {
//            goodsCount += cart.getNumber();
//        }

//        return new RestResponse<>(goodsCount);
        return new RestResponse<>();
    }
}
