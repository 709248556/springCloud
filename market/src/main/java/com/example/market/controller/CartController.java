package com.example.market.controller;

import com.example.common.constants.TokenConstant;
import com.example.common.entity.Cart;
import com.example.common.entity.Goods;
import com.example.common.entity.GoodsProduct;
import com.example.common.enums.GoodsEnum;
import com.example.common.enums.RestEnum;
import com.example.common.enums.UserClientEnum;
import com.example.common.feign.GoodsClient;
import com.example.common.feign.MarketClient;
import com.example.common.response.RestResponse;
import com.example.common.util.JacksonUtil;
import com.example.common.util.JsonData;
import com.example.common.util.RedisUtil;
import com.example.market.service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.common.enums.GoodsEnum.GOODS_UNSHELVE;

@Slf4j
@RestController
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private GoodsClient goodsClient;

    /**
     * 购物车商品货品数量
     * <p>
     * 如果用户没有登录，则返回空数据。
     *
     * @param //userId 用户ID
     * @return 购物车商品货品数量
     */
    @GetMapping("/goodscount")
    public RestResponse goodscount(JsonData jsonData) {
        RestResponse restResponse = new RestResponse<>();
        if (!jsonData.containsKey(TokenConstant.TOKEN)) {
            return restResponse.success(0);
        }
        int userId = redisUtil.getUserId(jsonData.get(TokenConstant.TOKEN).toString());

        jsonData.put("userId", userId);
        jsonData.put("deleted", 0);

        int goodsCount = 0;

        List<Cart> cartList = cartService.selective(jsonData);
        for (Cart cart : cartList) {
            goodsCount += cart.getNumber();
        }

        return restResponse.success(goodsCount);
    }

    /**
     * 加入商品到购物车
     * <p>
     * 如果已经存在购物车货品，则增加数量；
     * 否则添加新的购物车货品项。
     *
     * @param //userId 用户ID
     * @param cart     购物车商品信息， { goodsId: xxx, productId: xxx, number: xxx }
     * @return 加入购物车操作结果
     */
    @PostMapping("/add")
    public RestResponse add(@RequestBody Cart cart, HttpServletRequest request) {
        RestResponse restResponse = new RestResponse();
        JsonData jsonData = new JsonData(request);

        if (!jsonData.containsKey(TokenConstant.TOKEN)) {
            return restResponse.error(RestEnum.UNLOGIN);
        }
        if (cart == null) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }
        int userId = redisUtil.getUserId(jsonData.get(TokenConstant.TOKEN).toString());

        Integer productId = cart.getProductId();
        Integer number = cart.getNumber().intValue();
        Integer goodsId = cart.getGoodsId();
        if (!ObjectUtils.allNotNull(productId, number, goodsId)) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }

        //判断商品是否可以购买
        Goods goods = new Goods();
        GoodsProduct product = new GoodsProduct();
        try {
            RestResponse<Goods> goodsRestResponse = goodsClient.getGoodsById(goodsId);
            RestResponse<GoodsProduct> goodsProductRestResponse = goodsClient.getGoodsProductById(productId);
            if (goodsRestResponse.getErrno() != RestEnum.OK.code) {
                log.error("CartController.add方法错误 goodsRestResponse.getErrno() != RestEnum.OK.code，Errno为:" + goodsRestResponse.getErrno() + "错误信息为:" + goodsRestResponse.getErrmsg());
                //TODO 抛出异常
            }
            if (goodsProductRestResponse.getErrno() != RestEnum.OK.code) {
                log.error("CartController.add方法错误 goodsProductRestResponse.getErrno() != RestEnum.OK.code，Errno为:" + goodsProductRestResponse.getErrno() + "错误信息为:" + goodsProductRestResponse.getErrmsg());
                //TODO 抛出异常
            }
            goods = goodsRestResponse.getData();
            product = goodsProductRestResponse.getData();
        } catch (Exception e) {
            log.error("CartController.add ERROR ,", e.getMessage());
        }


        if (goods == null || !goods.getOnSale()) {
            return restResponse.error(GoodsEnum.GOODS_UNSHELVE);
        }

        jsonData.put("goodsId", goodsId);
        jsonData.put("productId", productId);
        jsonData.put("userId", userId);
        jsonData.put("deleted", 0);
        //判断购物车中是否存在此规格商品
        List<Cart> cartList = cartService.selective(jsonData);
        if (cartList.size() == 0) {
            //取得规格的信息,判断规格库存
            if (product == null || number > product.getNumber()) {
                return restResponse.error(GoodsEnum.GOODS_NO_STOCK);
            }
            JsonData insertive = new JsonData();
            insertive.put("goodsSn", goods.getGoodsSn());
            insertive.put("goodsName", goods.getName());
            insertive.put("picUrl", goods.getPicUrl());
            insertive.put("price", product.getPrice());
            insertive.put("specifications", product.getSpecifications());
            insertive.put("userId", userId);
            insertive.put("checked", 1);
            insertive.put("goodsId",cart.getGoodsId());
            insertive.put("productId",cart.getProductId());
            insertive.put("addTime", LocalDateTime.now());
            insertive.put("updateTime", LocalDateTime.now());
            insertive.put("number",number);
            cartService.insertive(insertive);
        } else {
            //取得规格的信息,判断规格库存
            int num = cartList.get(0).getNumber() + number;
            if (num > product.getNumber()) {
                return restResponse.error(GoodsEnum.GOODS_NO_STOCK);
            }
            JsonData update = new JsonData();
            update.put("number",num);
            update.put("cartId",cartList.get(0).getId());
            if (cartService.updative(update) == 0) {
                return restResponse.error(RestEnum.UPDATEDDATAFAILED);
            }
        }

        jsonData.put("userId", userId);
        jsonData.remove("goodsId");
        jsonData.remove("productId");
        int goodsCount = 0;
        List<Cart> newCartList = cartService.selective(jsonData);
        for (Cart cart1 : newCartList) {
            goodsCount += cart1.getNumber();
        }

        return restResponse.success(goodsCount);
    }

    /**
     * 用户购物车信息
     *
     * @param //userId 用户ID
     * @return 用户购物车信息
     */
    @GetMapping("/index")
    public Object index(JsonData jsonData) {
        int userId = redisUtil.getUserId(jsonData.get(TokenConstant.TOKEN).toString());
        jsonData.put("userId",userId);
        jsonData.put("deleted",0);
        List<Cart> cartList = cartService.selective(jsonData);
        Integer goodsCount = 0;
        BigDecimal goodsAmount = new BigDecimal(0.00);
        Integer checkedGoodsCount = 0;
        BigDecimal checkedGoodsAmount = new BigDecimal(0.00);
        for (Cart cart : cartList) {
            goodsCount += cart.getNumber();
            goodsAmount = goodsAmount.add(cart.getPrice().multiply(new BigDecimal(cart.getNumber())));
            if (cart.getChecked()) {
                checkedGoodsCount += cart.getNumber();
                checkedGoodsAmount = checkedGoodsAmount.add(cart.getPrice().multiply(new BigDecimal(cart.getNumber())));
            }
        }
        Map<String, Object> cartTotal = new HashMap<>();
        cartTotal.put("goodsCount", goodsCount);
        cartTotal.put("goodsAmount", goodsAmount);
        cartTotal.put("checkedGoodsCount", checkedGoodsCount);
        cartTotal.put("checkedGoodsAmount", checkedGoodsAmount);

        Map<String, Object> result = new HashMap<>();
        result.put("cartList", cartList);
        result.put("cartTotal", cartTotal);

        return new RestResponse<>(result);
    }

    /**
     * 购物车商品删除
     *
     * @param //userId 用户ID
     * @param body   购物车商品信息， { productIds: xxx }
     * @return 购物车信息
     * 成功则
     * {
     * errno: 0,
     * errmsg: '成功',
     * data: xxx
     * }
     * 失败则 { errno: XXX, errmsg: XXX }
     */
    @PostMapping("/delete")
    public Object delete(@RequestBody String body,HttpServletRequest request) {
        RestResponse restResponse = new RestResponse();
        JsonData jsonData = new JsonData(request);
        if (body == null) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }

        List<Integer> productIds = JacksonUtil.parseIntegerList(body, "productIds");

        if (productIds == null || productIds.size() == 0) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }
        jsonData.put("userId",redisUtil.getUserId(jsonData.get(TokenConstant.TOKEN).toString()));
        jsonData.put("deleted",1);
        for(int productId : productIds){
            jsonData.put("productId",productId);
            cartService.updative(jsonData);
        }

        jsonData.put("deleted",0);
        jsonData.remove("productId");
        List<Cart> cartList = cartService.selective(jsonData);
        Integer goodsCount = 0;
        BigDecimal goodsAmount = new BigDecimal(0.00);
        Integer checkedGoodsCount = 0;
        BigDecimal checkedGoodsAmount = new BigDecimal(0.00);
        for (Cart cart : cartList) {
            goodsCount += cart.getNumber();
            goodsAmount = goodsAmount.add(cart.getPrice().multiply(new BigDecimal(cart.getNumber())));
            if (cart.getChecked()) {
                checkedGoodsCount += cart.getNumber();
                checkedGoodsAmount = checkedGoodsAmount.add(cart.getPrice().multiply(new BigDecimal(cart.getNumber())));
            }
        }
        Map<String, Object> cartTotal = new HashMap<>();
        cartTotal.put("goodsCount", goodsCount);
        cartTotal.put("goodsAmount", goodsAmount);
        cartTotal.put("checkedGoodsCount", checkedGoodsCount);
        cartTotal.put("checkedGoodsAmount", checkedGoodsAmount);

        Map<String, Object> result = new HashMap<>();
        result.put("cartList", cartList);
        result.put("cartTotal", cartTotal);

        return new RestResponse<>(result);
    }

    /**
     * 修改购物车商品货品数量
     *
     * @param //userId 用户ID
     * @param cart   购物车商品信息， { id: xxx, goodsId: xxx, productId: xxx, number: xxx }
     * @return 修改结果
     */
    @PostMapping("/update")
    public RestResponse update(@RequestBody Cart cart) {
        RestResponse restResponse = new RestResponse();
        if (cart == null) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }
        Integer productId = cart.getProductId();
        Integer number = cart.getNumber().intValue();
        Integer goodsId = cart.getGoodsId();
        Integer id = cart.getId();
        if (!ObjectUtils.allNotNull(id, productId, number, goodsId)) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }

        JsonData jsonData = new JsonData();
        jsonData.put("cartId",id);
        //判断是否存在该订单
        // 如果不存在，直接返回错误
        Cart existCart = cartService.selective(jsonData).get(0);
        if (existCart == null) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }

        // 判断goodsId和productId是否与当前cart里的值一致
        if (!existCart.getGoodsId().equals(goodsId)) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }
        if (!existCart.getProductId().equals(productId)) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }


        Goods goods = new Goods();
        GoodsProduct goodsProduct = new GoodsProduct();
        try {
            //判断商品是否可以购买
            RestResponse<Goods> goodsRestResponse = goodsClient.getGoodsById(goodsId);
            //取得规格的信息,判断规格库存
            RestResponse<GoodsProduct> goodsProductRestResponse = goodsClient.getGoodsProductById(productId);
            if (goodsRestResponse.getErrno() != RestEnum.OK.code) {
                log.error("CartController.add方法错误 goodsRestResponse.getErrno() != RestEnum.OK.code，Errno为:" + goodsRestResponse.getErrno() + "错误信息为:" + goodsRestResponse.getErrmsg());
                //TODO 抛出异常
            }
            if (goodsProductRestResponse.getErrno() != RestEnum.OK.code) {
                log.error("CartController.add方法错误 goodsProductRestResponse.getErrno() != RestEnum.OK.code，Errno为:" + goodsProductRestResponse.getErrno() + "错误信息为:" + goodsProductRestResponse.getErrmsg());
                //TODO 抛出异常
            }
            goods = goodsRestResponse.getData();
            goodsProduct = goodsProductRestResponse.getData();
        }catch (Exception e){
            log.error("CartController.update ERROR",e.getMessage());
        }


        if (goods == null || !goods.getOnSale()) {
            return restResponse.error(GoodsEnum.GOODS_UNSHELVE);
        }


        if (goodsProduct == null || goodsProduct.getNumber() < number) {
            return restResponse.error(GoodsEnum.GOODS_NO_STOCK);
        }

        jsonData.put("number",number);
        if (cartService.updative(jsonData) == 0) {
            return restResponse.error(RestEnum.UPDATEDDATAFAILED);
        }
        return restResponse;
    }
    /**
     * 购物车商品货品勾选状态
     * <p>
     * 如果原来没有勾选，则设置勾选状态；如果商品已经勾选，则设置非勾选状态。
     *
     * @param //userId 用户ID
     * @param body   购物车商品信息， { productIds: xxx, isChecked: 1/0 }
     * @return 购物车信息
     */
    @PostMapping("/checked")
    public Object checked(@RequestBody String body,HttpServletRequest request) {
        RestResponse restResponse = new RestResponse();
        JsonData jsonData = new JsonData(request);
        if (body == null) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }

        List<Integer> productIds = JacksonUtil.parseIntegerList(body, "productIds");
        if (productIds == null) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }

        Integer checkValue = JacksonUtil.parseInteger(body, "isChecked");
        if (checkValue == null) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }
        Boolean isChecked = (checkValue == 1);
        jsonData.put("userId",redisUtil.getUserId(jsonData.get(TokenConstant.TOKEN).toString()));
        jsonData.put("checked",isChecked);
        jsonData.put("deleted",0);
        for (int productId : productIds){
            jsonData.put("productId",productId);
            cartService.updative(jsonData);
        }

        List<Cart> cartList = cartService.selective(jsonData);
        Integer goodsCount = 0;
        BigDecimal goodsAmount = new BigDecimal(0.00);
        Integer checkedGoodsCount = 0;
        BigDecimal checkedGoodsAmount = new BigDecimal(0.00);
        for (Cart cart : cartList) {
            goodsCount += cart.getNumber();
            goodsAmount = goodsAmount.add(cart.getPrice().multiply(new BigDecimal(cart.getNumber())));
            if (cart.getChecked()) {
                checkedGoodsCount += cart.getNumber();
                checkedGoodsAmount = checkedGoodsAmount.add(cart.getPrice().multiply(new BigDecimal(cart.getNumber())));
            }
        }
        Map<String, Object> cartTotal = new HashMap<>();
        cartTotal.put("goodsCount", goodsCount);
        cartTotal.put("goodsAmount", goodsAmount);
        cartTotal.put("checkedGoodsCount", checkedGoodsCount);
        cartTotal.put("checkedGoodsAmount", checkedGoodsAmount);

        Map<String, Object> result = new HashMap<>();
        result.put("cartList", cartList);
        result.put("cartTotal", cartTotal);

        return new RestResponse<>(result);
    }
}
