package com.example.market.controller;

import com.example.common.constants.TokenConstant;
import com.example.common.entity.*;
import com.example.common.enums.GoodsEnum;
import com.example.common.enums.RestEnum;
import com.example.common.enums.UserClientEnum;
import com.example.common.feign.GoodsClient;
import com.example.common.feign.MarketClient;
import com.example.common.feign.UserClient;
import com.example.common.response.RestResponse;
import com.example.common.util.JacksonUtil;
import com.example.common.util.JsonData;
import com.example.common.util.RedisUtil;
import com.example.market.service.CartService;
import com.example.market.service.GrouponRulesService;
import com.example.market.service.Impl.CouponVerifyService;
import com.example.market.systemConfig.SystemConfig;
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
import java.nio.channels.Pipe;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Autowired
    private UserClient userClient;

    @Autowired
    private GrouponRulesService grouponRulesService;

    @Autowired
    private CouponVerifyService couponVerifyService;

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
            insertive.put("goodsId", cart.getGoodsId());
            insertive.put("productId", cart.getProductId());
            insertive.put("addTime", LocalDateTime.now());
            insertive.put("updateTime", LocalDateTime.now());
            insertive.put("number", number);
            cartService.insertive(insertive);
        } else {
            //取得规格的信息,判断规格库存
            int num = cartList.get(0).getNumber() + number;
            if (num > product.getNumber()) {
                return restResponse.error(GoodsEnum.GOODS_NO_STOCK);
            }
            JsonData update = new JsonData();
            update.put("number", num);
            update.put("cartId", cartList.get(0).getId());
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
    public RestResponse index(JsonData jsonData) {
        int userId = redisUtil.getUserId(jsonData.get(TokenConstant.TOKEN).toString());
        jsonData.put("userId", userId);
        jsonData.put("deleted", 0);
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
     * @param body     购物车商品信息， { productIds: xxx }
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
    public Object delete(@RequestBody String body, HttpServletRequest request) {
        RestResponse restResponse = new RestResponse();
        JsonData jsonData = new JsonData(request);
        if (body == null) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }

        List<Integer> productIds = JacksonUtil.parseIntegerList(body, "productIds");

        if (productIds == null || productIds.size() == 0) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }
        jsonData.put("userId", redisUtil.getUserId(jsonData.get(TokenConstant.TOKEN).toString()));
        jsonData.put("deleted", 0);
        for (int productId : productIds) {
            jsonData.put("productId", productId);
            cartService.deletive(jsonData);
        }

        jsonData.put("deleted", 0);
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
     * @param cart     购物车商品信息， { id: xxx, goodsId: xxx, productId: xxx, number: xxx }
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
        jsonData.put("cartId", id);
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
        } catch (Exception e) {
            log.error("CartController.update ERROR", e.getMessage());
        }


        if (goods == null || !goods.getOnSale()) {
            return restResponse.error(GoodsEnum.GOODS_UNSHELVE);
        }


        if (goodsProduct == null || goodsProduct.getNumber() < number) {
            return restResponse.error(GoodsEnum.GOODS_NO_STOCK);
        }

        jsonData.put("number", number);
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
     * @param body     购物车商品信息， { productIds: xxx, isChecked: 1/0 }
     * @return 购物车信息
     */
    @PostMapping("/checked")
    public Object checked(@RequestBody String body, HttpServletRequest request) {
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
        jsonData.put("userId", redisUtil.getUserId(jsonData.get(TokenConstant.TOKEN).toString()));
        jsonData.put("checked", isChecked);
        jsonData.put("deleted", 0);
        for (int productId : productIds) {
            jsonData.put("productId", productId);
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

    /**
     * 购物车下单
     *
     * @param //userId    用户ID
     * @param //cartId    购物车商品ID：
     *                    如果购物车商品ID是空，则下单当前用户所有购物车商品；
     *                    如果购物车商品ID非空，则只下单当前购物车商品。
     * @param //addressId 收货地址ID：
     *                    如果收货地址ID是空，则查询当前用户的默认地址。
     * @param //couponId  优惠券ID：
     *                    如果优惠券ID是空，则自动选择合适的优惠券。
     * @return 购物车操作结果
     */
    @GetMapping("checkout")
    public Object checkout(JsonData jsonData) {
        RestResponse restResponse = new RestResponse();
        if (!jsonData.containsKey(TokenConstant.TOKEN)) {
            return restResponse.error(RestEnum.UNLOGIN);
        }
        int userId = redisUtil.getUserId(jsonData.get(TokenConstant.TOKEN).toString());
        Integer cartId = Integer.valueOf(jsonData.get("cartId").toString());
        Integer addressId = Integer.valueOf(jsonData.get("addressId").toString());
        Integer couponId = Integer.valueOf(jsonData.get("couponId").toString());
        Integer grouponRulesId = Integer.valueOf(jsonData.get("grouponRulesId").toString());
        // 收货地址
        Address checkedAddress = null;
        if (addressId == null || addressId.equals(0)) {
            try {
                RestResponse<List<Address>> addressRestRespone = userClient.getAddress(userId, 1, 0);
                if (addressRestRespone.getErrno() != RestEnum.OK.code) {
                    log.error("addressRestRespone.checkout获取默认地址错误 goodsRestResponse.getErrno() != RestEnum.OK.code，Errno为:" + addressRestRespone.getErrno() + "错误信息为:" + addressRestRespone.getErrmsg());
                    //TODO 抛出异常
                }
                checkedAddress = addressRestRespone.getData().get(0);
            } catch (Exception e) {
                log.error("CartController.checkout获取默认地址错误 ", e.getMessage());
            }

            // 如果仍然没有地址，则是没有收获地址
            // 返回一个空的地址id=0，这样前端则会提醒添加地址
            if (checkedAddress == null) {
                checkedAddress = new Address();
                checkedAddress.setId(0);
                addressId = 0;
            } else {
                addressId = checkedAddress.getId();
            }

        } else {
            try {
                RestResponse<List<Address>> addressRestRespone = userClient.getAddress(addressId);
                if (addressRestRespone.getErrno() != RestEnum.OK.code) {
                    log.error("addressRestRespone.checkout获取新增地址错误 goodsRestResponse.getErrno() != RestEnum.OK.code，Errno为:" + addressRestRespone.getErrno() + "错误信息为:" + addressRestRespone.getErrmsg());
                    //TODO 抛出异常
                }
                checkedAddress = addressRestRespone.getData().get(0);
            } catch (Exception e) {
                log.error("CartController.checkout获取新增地址错误", e.getMessage());
            }
            // 如果null, 则报错
            if (checkedAddress == null) {
                return restResponse.error(RestEnum.BADARGUMENT);
            }
        }

        // 团购优惠
        BigDecimal grouponPrice = new BigDecimal(0.00);
        JsonData jsonData1 = new JsonData();
        jsonData1.put("id", grouponRulesId);
        jsonData1.put("deleted", 0);
        List<GrouponRules> grouponRulesList = grouponRulesService.selective(jsonData1);
        if (grouponRulesList.size() != 0) {
            grouponPrice = grouponRulesList.get(0).getDiscount();
        }

        // 商品价格
        List<Cart> checkedGoodsList = null;
        if (cartId == null || cartId.equals(0)) {
            JsonData jsonData2 = new JsonData();
            jsonData2.put("userId", userId);
            jsonData2.put("checked", 1);
            jsonData2.put("deleted", 0);
            checkedGoodsList = cartService.selective(jsonData2);
        } else {
            JsonData jsonData2 = new JsonData();
            jsonData2.put("cartId", cartId);
            Cart cart = cartService.selective(jsonData).get(0);
            if (cart == null) {
                return restResponse.error(RestEnum.BADARGUMENT);
            }
            checkedGoodsList = new ArrayList<>(1);
            checkedGoodsList.add(cart);
        }
        BigDecimal checkedGoodsPrice = new BigDecimal(0.00);
        for (Cart cart : checkedGoodsList) {
            //  只有当团购规格商品ID符合才进行团购优惠
            if (grouponRulesList.size() != 0 && grouponRulesList.get(0).getGoodsId().equals(cart.getGoodsId())) {
                checkedGoodsPrice = checkedGoodsPrice.add(cart.getPrice().subtract(grouponPrice).multiply(new BigDecimal(cart.getNumber())));
            } else {
                checkedGoodsPrice = checkedGoodsPrice.add(cart.getPrice().multiply(new BigDecimal(cart.getNumber())));
            }
        }

        // 计算优惠券可用情况
        BigDecimal tmpCouponPrice = new BigDecimal(0.00);
        Integer tmpCouponId = 0;
        int tmpCouponLength = 0;
        List<CouponUser> couponUserList = null;
        try {
            RestResponse<List<CouponUser>> couponUserRestRespone = userClient.getCouponUser(userId, 0 + "", 0);
            if (couponUserRestRespone.getErrno() != RestEnum.OK.code) {
                log.error("couponUserRestRespone.checkout获取新增地址错误 goodsRestResponse.getErrno() != RestEnum.OK.code，Errno为:" + couponUserRestRespone.getErrno() + "错误信息为:" + couponUserRestRespone.getErrmsg());
                //TODO 抛出异常
            }
            couponUserList = couponUserRestRespone.getData();
        } catch (Exception e) {
            log.error("CartController.checkout获取新增地址错误", e.getMessage());
        }
        for (CouponUser couponUser : couponUserList) {
            Coupon coupon = couponVerifyService.checkCoupon(userId, couponUser.getCouponId(), checkedGoodsPrice);
            if (coupon == null) {
                continue;
            }

            tmpCouponLength++;
            if (tmpCouponPrice.compareTo(coupon.getDiscount()) == -1) {
                tmpCouponPrice = coupon.getDiscount();
                tmpCouponId = coupon.getId();
            }
        }
        // 获取优惠券减免金额，优惠券可用数量
        int availableCouponLength = tmpCouponLength;
        BigDecimal couponPrice = new BigDecimal(0);
        // 这里存在三种情况
        // 1. 用户不想使用优惠券，则不处理
        // 2. 用户想自动使用优惠券，则选择合适优惠券
        // 3. 用户已选择优惠券，则测试优惠券是否合适
        if (couponId == null || couponId.equals(-1)) {
            couponId = -1;
        } else if (couponId.equals(0)) {
            couponPrice = tmpCouponPrice;
            couponId = tmpCouponId;
        } else {
            Coupon coupon = couponVerifyService.checkCoupon(userId, couponId, checkedGoodsPrice);
            // 用户选择的优惠券有问题
            if (coupon == null) {
                return restResponse.error(RestEnum.BADARGUMENT);
            }
            couponPrice = coupon.getDiscount();
        }

        // 根据订单商品总价计算运费，满88则免运费，否则8元；
        BigDecimal freightPrice = new BigDecimal(0.00);
        if (checkedGoodsPrice.compareTo(SystemConfig.getFreightLimit()) < 0) {
            freightPrice = SystemConfig.getFreight();
        }

        // 可以使用的其他钱，例如用户积分
        BigDecimal integralPrice = new BigDecimal(0.00);

        // 订单费用
        BigDecimal orderTotalPrice = checkedGoodsPrice.add(freightPrice).subtract(couponPrice);
        BigDecimal actualPrice = orderTotalPrice.subtract(integralPrice);

        Map<String, Object> data = new HashMap<>();
        data.put("addressId", addressId);
        data.put("grouponRulesId", grouponRulesId);
        data.put("grouponPrice", grouponPrice);
        data.put("checkedAddress", checkedAddress);
        data.put("couponId", couponId);
        data.put("availableCouponLength", availableCouponLength);
        data.put("goodsTotalPrice", checkedGoodsPrice);
        data.put("freightPrice", freightPrice);
        data.put("couponPrice", couponPrice);
        data.put("orderTotalPrice", orderTotalPrice);
        data.put("actualPrice", actualPrice);
        data.put("checkedGoodsList", checkedGoodsList);
        return restResponse.success(data);
    }

    /**
     * 立即购买
     * <p>
     * 和add方法的区别在于：
     * 1. 如果购物车内已经存在购物车货品，前者的逻辑是数量添加，这里的逻辑是数量覆盖
     * 2. 添加成功以后，前者的逻辑是返回当前购物车商品数量，这里的逻辑是返回对应购物车项的ID
     *
     * @param //userId 用户ID
     * @param cart     购物车商品信息， { goodsId: xxx, productId: xxx, number: xxx }
     * @return 立即购买操作结果
     */
    @PostMapping("fastadd")
    public Object fastadd(@RequestBody Cart cart, HttpServletRequest request) {
        JsonData jsonData = new JsonData(request);
        RestResponse restResponse = new RestResponse();
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
        Goods goods = null;
        GoodsProduct product = null;
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
            product = goodsProductRestResponse.getData();
        } catch (Exception e) {
            log.error("CartController.update ERROR", e.getMessage());
        }
        if (goods == null || !goods.getOnSale()) {
            return restResponse.error(GoodsEnum.GOODS_UNSHELVE);
        }

        //判断购物车中是否存在此规格商品
        jsonData.put("goodsId", goodsId);
        jsonData.put("productId", productId);
        jsonData.put("userId", userId);
        jsonData.put("deleted", 0);
        List<Cart> cartList = cartService.selective(jsonData);
        if (cartList.size() == 0) {
            //取得规格的信息,判断规格库存
            if (product == null || number > product.getNumber()) {
                return restResponse.error(GoodsEnum.GOODS_NO_STOCK);
            }

            cart.setId(null);
            cart.setGoodsSn(goods.getGoodsSn());
            cart.setGoodsName((goods.getName()));
            cart.setPicUrl(goods.getPicUrl());
            cart.setPrice(product.getPrice());
            cart.setSpecifications(product.getSpecifications());
            cart.setUserId(userId);
            cart.setChecked(true);
            cart.setAddTime(LocalDateTime.now());
            cart.setUpdateTime(LocalDateTime.now());
            cartService.insert(cart);
        } else {
            Cart existCart = cartList.get(0);
            JsonData update = new JsonData();
            //取得规格的信息,判断规格库存
            int num = number;
            if (num > product.getNumber()) {
                return restResponse.error(GoodsEnum.GOODS_NO_STOCK);
            }
            update.put("number",num);
            update.put("updateTime",LocalDateTime.now());
            update.put("cartId",existCart.getId());
            if (cartService.updative(update) == 0) {
                return restResponse.error(RestEnum.UPDATEDDATAFAILED);
            }
        }
        return restResponse.success(cartList.get(0) != null ? cartList.get(0).getId() : cart.getId());
    }

    @GetMapping("/getCart")
    public RestResponse getCart(JsonData jsonData){
        return new RestResponse(cartService.selective(jsonData));
    }

    @GetMapping("/clearGoods")
    public RestResponse clearGoods(JsonData jsonData){
        return new RestResponse(cartService.deletive(jsonData));
    }
}
