package com.example.user.controller;

import com.alibaba.fastjson.JSON;
import com.example.common.constants.CouponUserConstant;
import com.example.common.constants.OrderConstant;
import com.example.common.constants.TokenConstant;
import com.example.common.entity.*;
import com.example.common.enums.OrderEnum;
import com.example.common.enums.RestEnum;
import com.example.common.feign.GoodsClient;
import com.example.common.feign.MarketClient;
import com.example.common.response.RestResponse;
import com.example.common.util.JacksonUtil;
import com.example.common.util.JsonData;
import com.example.common.util.OrderUtil;
import com.example.common.util.RedisUtil;
import com.example.user.service.AddressService;
import com.example.user.service.CouponUserService;
import com.example.user.service.OrderService;
import com.example.user.service.RegionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.security.cert.TrustAnchor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.example.common.enums.RestEnum.BADARGUMENT;
import static com.example.common.enums.RestEnum.GROUPON_EXPIRED;

@Slf4j
@RestController
public class OrderController {
    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private OrderService orderService;

    @Autowired
    private MarketClient marketClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private AddressService addressService;

    @Autowired
    private RegionService regionService;

    @Autowired
    private CouponUserService couponUserService;


    /**
     * 用户个人页面数据
     * <p>
     * 目前是用户订单统计信息
     *
     * @param //userId 用户ID
     * @return 用户个人页面数据
     */
    @GetMapping("/index")
    public RestResponse index(JsonData jsonData, Model model) {
        RestResponse restResponse = new RestResponse();
        int userId = redisUtil.getUserId(jsonData.get(TokenConstant.TOKEN).toString());
        jsonData.put("userId", userId);
        jsonData.put("orderDeleted", 0);
        List<Order> orderList = orderService.selective(jsonData);

        int unpaid = 0;//待付款
        int unship = 0;//待发货
        int unrecv = 0;////待收货
        int uncomment = 0;//待评价
        for (Order order : orderList) {
            if (OrderConstant.isCreateStatus(order)) {
                unpaid++;
            } else if (OrderConstant.isPayStatus(order)) {
                unship++;
            } else if (OrderConstant.isShipStatus(order)) {
                unrecv++;
            } else if (OrderConstant.isConfirmStatus(order) || OrderConstant.isAutoConfirmStatus(order)) {
                uncomment += order.getComments();
            } else {
                // do nothing
            }
        }

        Map<Object, Object> orderInfo = new HashMap<Object, Object>();
        orderInfo.put("unpaid", unpaid);
        orderInfo.put("unship", unship);
        orderInfo.put("unrecv", unrecv);
        orderInfo.put("uncomment", uncomment);
        model.addAttribute("order", orderInfo);
        return restResponse.success(model);
    }

    /**
     * 订单列表
     *
     * @param //userId   用户ID
     * @param //showType 订单信息
     * @return 订单列表
     */
    @GetMapping("/list")
    public RestResponse list(JsonData jsonData, Model model) {
        RestResponse restResponse = new RestResponse<>();
        int userId = redisUtil.getUserId(jsonData.get(TokenConstant.TOKEN).toString());
        jsonData.put("userId", userId);
        if (!jsonData.containsKey("userId")) {
            return restResponse.error(RestEnum.UNLOGIN);
        }
        Short orderStatus = OrderUtil.orderStatus(Integer.valueOf(jsonData.get("showType").toString())).get(0);
        jsonData.put("orderStatus", orderStatus);
        jsonData.put("orderSort", "add_time");
        jsonData.put("orderOrder", "DESC");
        jsonData.put("orderDeleted", 0);
        List<Order> orderList = orderService.selective(jsonData);
        List<Map<String, Object>> orderVoList = new ArrayList<>(orderList.size());
        for (Order order : orderList) {
            Map<String, Object> orderVo = new HashMap<>();
            orderVo.put("id", order.getId());
            orderVo.put("orderSn", order.getOrderSn());
            orderVo.put("actualPrice", order.getActualPrice());
            orderVo.put("orderStatusText", OrderConstant.orderStatusText(order));
            orderVo.put("handleOption", OrderUtil.build(order));
            List<OrderGoods> orderGoodsList = new ArrayList<>();
            try {
                RestResponse<List<Groupon>> grouponRestResponse = marketClient.getGrouponByOrderId(order.getId(), 0);
                RestResponse<List<OrderGoods>> orderGoodsRestResponse = goodsClient.getOrderGoodsByOrderId(order.getId(), 0);
                if (grouponRestResponse.getErrno() != RestEnum.OK.code) {
                    log.error("OrderController.list方法错误 grouponRestResponse.getErrno() != RestEnum.OK.code，Errno为:" + grouponRestResponse.getErrno() + "错误信息为:" + grouponRestResponse.getErrmsg());
                    //TODO 抛出异常
                }
                if (orderGoodsRestResponse.getErrno() != RestEnum.OK.code) {
                    log.error("OrderController.list方法错误 orderGoodsRestResponse.getErrno() != RestEnum.OK.code，Errno为:" + orderGoodsRestResponse.getErrno() + "错误信息为:" + orderGoodsRestResponse.getErrmsg());
                    //TODO 抛出异常
                }
                if (grouponRestResponse.getData().size() == 0) {
                    orderVo.put("isGroupin", false);
                } else {
                    orderVo.put("isGroupin", true);
                }
                orderGoodsList = orderGoodsRestResponse.getData();
            } catch (Exception e) {
                log.error("OrderController.list方法错误", e.getMessage());
            }
            List<Map<String, Object>> orderGoodsVoList = new ArrayList<>(orderGoodsList.size());
            for (OrderGoods orderGoods : orderGoodsList) {
                Map<String, Object> orderGoodsVo = new HashMap<>();
                orderGoodsVo.put("id", orderGoods.getId());
                orderGoodsVo.put("goodsName", orderGoods.getGoodsName());
                orderGoodsVo.put("number", orderGoods.getNumber());
                orderGoodsVo.put("picUrl", orderGoods.getPicUrl());
                orderGoodsVoList.add(orderGoodsVo);
            }
            orderVo.put("goodsList", orderGoodsVoList);

            orderVoList.add(orderVo);
        }
        model.addAttribute("count", orderList.size() - 1);
        model.addAttribute("data", orderVoList);
        return new RestResponse(model);
    }

    /**
     * 订单详情
     *
     * @param //userId  用户ID
     * @param //orderId 订单ID
     * @return 订单详情
     */
    @GetMapping("detail")
    public Object detail(JsonData jsonData) {
        RestResponse restResponse = new RestResponse<>();
        int userId = redisUtil.getUserId(jsonData.get(TokenConstant.TOKEN).toString());
        jsonData.put("userId", userId);
        if (!jsonData.containsKey("userId")) {
            return restResponse.error(RestEnum.UNLOGIN);
        }
        // 订单信息
        Order order = orderService.selective(jsonData).get(0);
        if (null == order) {
            return restResponse.error(OrderEnum.ORDER_UNKNOWN);
        }
        Map<String, Object> orderVo = new HashMap<String, Object>();
        orderVo.put("id", order.getId());
        orderVo.put("orderSn", order.getOrderSn());
        orderVo.put("addTime", order.getAddTime());
        orderVo.put("consignee", order.getConsignee());
        orderVo.put("mobile", order.getMobile());
        orderVo.put("address", order.getAddress());
        orderVo.put("goodsPrice", order.getGoodsPrice());
        orderVo.put("freightPrice", order.getFreightPrice());
        orderVo.put("actualPrice", order.getActualPrice());
        orderVo.put("orderStatusText", OrderConstant.orderStatusText(order));
        orderVo.put("handleOption", OrderUtil.build(order));
        orderVo.put("expCode", order.getShipChannel());
        orderVo.put("expNo", order.getShipSn());
        List<OrderGoods> orderGoodsList = new ArrayList<>();
        try {
            RestResponse<List<OrderGoods>> orderGoodsRestResponse = goodsClient.getOrderGoodsByOrderId(order.getId(), 0);
            if (orderGoodsRestResponse.getErrno() != RestEnum.OK.code) {
                log.error("OrderController.list方法错误 orderGoodsRestResponse.getErrno() != RestEnum.OK.code，Errno为:" + orderGoodsRestResponse.getErrno() + "错误信息为:" + orderGoodsRestResponse.getErrmsg());
                //TODO 抛出异常
            }
        } catch (Exception e) {
            log.error("OrderController.list方法错误", e.getMessage());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("orderInfo", orderVo);
        result.put("orderGoods", orderGoodsList);

        // 订单状态为已发货且物流信息不为空
        //"YTO", "800669400640887922"
        if (order.getOrderStatus().equals(OrderConstant.STATUS_SHIP)) {
//            ExpressInfo ei = expressService.getExpressInfo(map.getShipChannel(), map.getShipSn());
            result.put("expressInfo", null);
        }

        return restResponse.success(result);
    }


    /**
     * 判断某个团购活动是否已经过期
     *
     * @return
     */
    public boolean isExpired(GrouponRules rules) {
        return (rules == null || rules.getExpireTime().isBefore(LocalDateTime.now()));
    }

    //生成订单地址
    private String detailedAddress(Address litemallAddress) {
        Integer provinceId = litemallAddress.getProvinceId();
        Integer cityId = litemallAddress.getCityId();
        Integer areaId = litemallAddress.getAreaId();
        JsonData jsonData = new JsonData();
        jsonData.put("id", provinceId);
        String provinceName = regionService.findById(jsonData).getName();
        jsonData.put("id", cityId);
        String cityName = regionService.findById(jsonData).getName();
        jsonData.put("id", areaId);
        String areaName = regionService.findById(jsonData).getName();
        String fullRegion = provinceName + " " + cityName + " " + areaName;
        return fullRegion + " " + litemallAddress.getAddress();
    }


    /**
     * 提交订单
     *
     * @param //userId 用户ID
     * @param body     订单信息，{ cartId：xxx, addressId: xxx, couponId: xxx, message: xxx, grouponRulesId: xxx,  grouponLinkId: xxx}
     * @return 提交订单操作结果
     */
    @PostMapping("submit")
    public Object submit(@RequestBody String body, HttpServletRequest request) {
        RestResponse restResponse = new RestResponse();
        JsonData jsonData = new JsonData(request);
        if (!jsonData.containsKey(TokenConstant.TOKEN)) {
            return restResponse.error(RestEnum.UNLOGIN);
        }
        if (body == null) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }
        int userId = redisUtil.getUserId(jsonData.get(TokenConstant.TOKEN).toString());
        Integer cartId = JacksonUtil.parseInteger(body, "cartId");
        Integer addressId = JacksonUtil.parseInteger(body, "addressId");
        Integer couponId = JacksonUtil.parseInteger(body, "couponId");
        String message = JacksonUtil.parseString(body, "message");
        Integer grouponRulesId = JacksonUtil.parseInteger(body, "grouponRulesId");
        Integer grouponLinkId = JacksonUtil.parseInteger(body, "grouponLinkId");

        //如果是团购项目,验证活动是否有效
        if (grouponRulesId != null && grouponRulesId > 0) {
            GrouponRules rules = null;
            try {
                RestResponse<List<GrouponRules>> restResponse1 = marketClient.getGrouponRulesByGrouponRulesId(grouponRulesId);
                if (restResponse1.getErrno() != RestEnum.OK.code) {
                    log.error("OrderController.submit方法错误 restResponse1.getErrno() != RestEnum.OK.code，Errno为:" + restResponse1.getErrno() + "错误信息为:" + restResponse1.getErrmsg());
                    //TODO 抛出异常
                }
                rules = restResponse1.getData().get(0);
            } catch (Exception e) {
                log.error("OrderController.submit方法错误", e.getMessage());
            }

            //找不到记录
            if (rules == null) {
                return restResponse.error(RestEnum.BADARGUMENT);
            }
            //团购活动已经过期
            if (isExpired(rules)) {
                return restResponse.error(GROUPON_EXPIRED);
            }
        }

        if (cartId == null || addressId == null || couponId == null) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }

        // 收货地址
        JsonData jsonData1 = new JsonData();
        jsonData1.put("id", addressId);
        Address checkedAddress = addressService.selective(jsonData1).get(0);
        if (checkedAddress == null) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }

        // 团购优惠
        BigDecimal grouponPrice = new BigDecimal(0.00);
        GrouponRules grouponRules = null;
        try {
            RestResponse<List<GrouponRules>> restResponse1 = marketClient.getGrouponRulesByGrouponRulesId(grouponRulesId);
            if (restResponse1.getErrno() != RestEnum.OK.code) {
                log.error("OrderController.submit方法错误 restResponse1.getErrno() != RestEnum.OK.code，Errno为:" + restResponse1.getErrno() + "错误信息为:" + restResponse1.getErrmsg());
                //TODO 抛出异常
            }
            if (restResponse1.getData().size() != 0)
                grouponRules = restResponse1.getData().get(0);
        } catch (Exception e) {
            log.error("OrderController.submit方法错误", e.getMessage());
        }
        if (grouponRules != null) {
            grouponPrice = grouponRules.getDiscount();
        }
        // 货品价格
        List<Cart> checkedGoodsList = null;
        if (cartId.equals(0)) {
            try {
                RestResponse<List<Cart>> restResponse1 = marketClient.getCartByUidAndChecked(userId, 1, 0);
                if (restResponse1.getErrno() != RestEnum.OK.code) {
                    log.error("OrderController.submit方法错误 restResponse1.getErrno() != RestEnum.OK.code，Errno为:" + restResponse1.getErrno() + "错误信息为:" + restResponse1.getErrmsg());
                    //TODO 抛出异常
                }
                checkedGoodsList = restResponse1.getData();
            } catch (Exception e) {
                log.error("OrderController.submit方法错误", e.getMessage());
            }
        } else {
            Cart cart = null;
            try {
                RestResponse<List<Cart>> restResponse1 = marketClient.getCartByCartId(cartId);
                if (restResponse1.getErrno() != RestEnum.OK.code) {
                    log.error("OrderController.submit方法错误 restResponse1.getErrno() != RestEnum.OK.code，Errno为:" + restResponse1.getErrno() + "错误信息为:" + restResponse1.getErrmsg());
                    //TODO 抛出异常
                }
                if (restResponse1.getData().size() != 0)
                    cart = restResponse1.getData().get(0);
            } catch (Exception e) {
                log.error("OrderController.submit方法错误", e.getMessage());
            }

            checkedGoodsList = new ArrayList<>(1);
            checkedGoodsList.add(cart);
        }
        if (checkedGoodsList.size() == 0) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }
        BigDecimal checkedGoodsPrice = new BigDecimal(0.00);
        for (Cart checkGoods : checkedGoodsList) {
            //  只有当团购规格商品ID符合才进行团购优惠
            if (grouponRules != null && grouponRules.getGoodsId().equals(checkGoods.getGoodsId())) {
                checkedGoodsPrice = checkedGoodsPrice.add(checkGoods.getPrice().subtract(grouponPrice).multiply(new BigDecimal(checkGoods.getNumber())));
            } else {
                checkedGoodsPrice = checkedGoodsPrice.add(checkGoods.getPrice().multiply(new BigDecimal(checkGoods.getNumber())));
            }
        }

        // 获取可用的优惠券信息
        // 使用优惠券减免的金额
        BigDecimal couponPrice = new BigDecimal(0.00);
        // 如果couponId=0则没有优惠券，couponId=-1则不使用优惠券
        if (couponId != 0 && couponId != -1) {
            Coupon coupon = null;

            try {
                RestResponse<Coupon> restResponse1 = marketClient.getCouponVerify(userId, couponId, checkedGoodsPrice);
                if (restResponse1.getErrno() != RestEnum.OK.code) {
                    log.error("OrderController.submit方法错误 restResponse1.getErrno() != RestEnum.OK.code，Errno为:" + restResponse1.getErrno() + "错误信息为:" + restResponse1.getErrmsg());
                    //TODO 抛出异常
                }
                coupon = restResponse1.getData();
            } catch (Exception e) {
                log.error("OrderController.submit方法错误", e.getMessage());
            }
            if (coupon == null) {
                return restResponse.error(BADARGUMENT);
            }
            couponPrice = coupon.getDiscount();
        }


        // 根据订单商品总价计算运费，满足条件（例如88元）则免运费，否则需要支付运费（例如8元）；
        BigDecimal freightPrice = new BigDecimal(0.00);
        BigDecimal freightLimit = null;
        BigDecimal freight = null;
        try {
            RestResponse<BigDecimal> restResponse1 = marketClient.getFreight();
            RestResponse<BigDecimal> restResponse2 = marketClient.getFreightLimit();
            if (restResponse1.getErrno() != RestEnum.OK.code) {
                log.error("OrderController.submit方法错误 restResponse1.getErrno() != RestEnum.OK.code，Errno为:" + restResponse1.getErrno() + "错误信息为:" + restResponse1.getErrmsg());
                //TODO 抛出异常
            }
            if (restResponse2.getErrno() != RestEnum.OK.code) {
                log.error("OrderController.submit方法错误 restResponse2.getErrno() != RestEnum.OK.code，Errno为:" + restResponse2.getErrno() + "错误信息为:" + restResponse2.getErrmsg());
                //TODO 抛出异常
            }
            freight = restResponse1.getData();
            freightLimit = restResponse2.getData();
        } catch (Exception e) {
            log.error("OrderController.submit方法错误", e.getMessage());
        }
        if (checkedGoodsPrice.compareTo(freightLimit) < 0) {
            freightPrice = freight;
        }

        // 可以使用的其他钱，例如用户积分
        BigDecimal integralPrice = new BigDecimal(0.00);

        // 订单费用
        BigDecimal orderTotalPrice = checkedGoodsPrice.add(freightPrice).subtract(couponPrice);
        // 最终支付费用
        BigDecimal actualPrice = orderTotalPrice.subtract(integralPrice);

        Integer orderId = null;
        Order order = null;
        // 订单
        order = new Order();
        order.setUserId(userId);
        order.setOrderSn(OrderUtil.generateOrderSn(userId));
        order.setOrderStatus(OrderConstant.STATUS_CREATE);
        order.setConsignee(checkedAddress.getName());
        order.setMobile(checkedAddress.getMobile());
        order.setMessage(message);
        String detailedAddress = detailedAddress(checkedAddress);
        order.setAddress(detailedAddress);
        order.setGoodsPrice(checkedGoodsPrice);
        order.setFreightPrice(freightPrice);
        order.setCouponPrice(couponPrice);
        order.setIntegralPrice(integralPrice);
        order.setOrderPrice(orderTotalPrice);
        order.setActualPrice(actualPrice);

        // 有团购活动
        if (grouponRules != null) {
            order.setGrouponPrice(grouponPrice);    //  团购价格
        } else {
            order.setGrouponPrice(new BigDecimal(0.00));    //  团购价格
        }

        // 添加订单表项
        order.setAddTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        orderService.insert(order);
        orderId = order.getId();

        // 添加订单商品表项
        for (Cart cartGoods : checkedGoodsList) {
            // 订单商品
            OrderGoods orderGoods = new OrderGoods();
            orderGoods.setOrderId(order.getId());
            orderGoods.setGoodsId(cartGoods.getGoodsId());
            orderGoods.setGoodsSn(cartGoods.getGoodsSn());
            orderGoods.setProductId(cartGoods.getProductId());
            orderGoods.setGoodsName(cartGoods.getGoodsName());
            orderGoods.setPicUrl(cartGoods.getPicUrl());
            orderGoods.setPrice(cartGoods.getPrice());
            orderGoods.setNumber(cartGoods.getNumber());
            orderGoods.setSpecifications(cartGoods.getSpecifications());
            orderGoods.setAddTime(LocalDateTime.now());
            orderGoods.setUpdateTime(LocalDateTime.now());
            try {
                RestResponse<Integer> restResponse1 = goodsClient.addOrderGoods(orderGoods);
                if (restResponse1.getErrno() != RestEnum.OK.code && restResponse1.getData() == null) {
                    log.error("OrderController.submit方法错误 restResponse1.getErrno() != RestEnum.OK.code，Errno为:" + restResponse1.getErrno() + "错误信息为:" + restResponse1.getErrmsg());
                    //TODO 抛出异常
                }
            } catch (Exception e) {
                log.error("OrderController.submit方法错误", e.getMessage());
            }
        }

        // 删除购物车里面的商品信息
        try {
            RestResponse<Integer> restResponse1 = marketClient.clearGoods(userId, 1);
            if (restResponse1.getErrno() != RestEnum.OK.code && restResponse1.getData() == 0) {
                log.error("OrderController.submit方法错误 restResponse1.getErrno() != RestEnum.OK.code，Errno为:" + restResponse1.getErrno() + "错误信息为:" + restResponse1.getErrmsg());
                //TODO 抛出异常
            }
        } catch (Exception e) {
            log.error("OrderController.submit方法错误", e.getMessage());
        }

        // 商品货品数量减少
        for (Cart checkGoods : checkedGoodsList) {
            Integer productId = checkGoods.getProductId();
            GoodsProduct product = null;
            try {
                RestResponse<GoodsProduct> restResponse1 = goodsClient.getGoodsProductById(productId);
                if (restResponse1.getErrno() != RestEnum.OK.code) {
                    log.error("OrderController.submit方法错误 restResponse1.getErrno() != RestEnum.OK.code，Errno为:" + restResponse1.getErrno() + "错误信息为:" + restResponse1.getErrmsg());
                    //TODO 抛出异常
                }
                product = restResponse1.getData();
            } catch (Exception e) {
                log.error("OrderController.submit方法错误", e.getMessage());
            }

            Integer remainNumber = product.getNumber() - checkGoods.getNumber();
            if (remainNumber < 0) {
                throw new RuntimeException("下单的商品货品数量大于库存量");
            }
            try {
                RestResponse<Integer> restResponse1 = goodsClient.reduceStock(productId, checkGoods.getNumber());
                if (restResponse1.getErrno() != RestEnum.OK.code) {
                    log.error("OrderController.submit方法错误 restResponse1.getErrno() != RestEnum.OK.code，Errno为:" + restResponse1.getErrno() + "错误信息为:" + restResponse1.getErrmsg());
                    //TODO 抛出异常
                }
                if (restResponse1.getData() == 0) {
                    throw new RuntimeException("商品货品库存减少失败");
                }
            } catch (Exception e) {
                log.error("OrderController.submit方法错误", e.getMessage());
            }


        }

        // 如果使用了优惠券，设置优惠券使用状态
        if (couponId != 0 && couponId != -1) {
            JsonData jsonData2 = new JsonData();
            jsonData2.put("userId", userId);
            jsonData2.put("couponId", couponId);
            jsonData2.put("status", CouponUserConstant.STATUS_USABLE);
            jsonData2.put("updateStatus", CouponUserConstant.STATUS_USED);
            jsonData2.put("deleted", 0);
            jsonData2.put("usedTime", LocalDateTime.now());
            jsonData2.put("orderId", orderId);
            jsonData2.put("updateTime", LocalDateTime.now());
            couponUserService.updative(jsonData2);
        }

        //如果是团购项目，添加团购信息,异步
//        if (grouponRulesId != null && grouponRulesId > 0) {
//            Groupon groupon = new Groupon();
//            groupon.setOrderId(orderId);
//            groupon.setPayed(false);
//            groupon.setUserId(userId);
//            groupon.setRulesId(grouponRulesId);

//            //参与者
//            if (grouponLinkId != null && grouponLinkId > 0) {
//                //参与的团购记录
//                Groupon baseGroupon = grouponService.queryById(grouponLinkId);
//                groupon.setCreatorUserId(baseGroupon.getCreatorUserId());
//                groupon.setGrouponId(grouponLinkId);
//                groupon.setShareUrl(baseGroupon.getShareUrl());
//            } else {
//                groupon.setCreatorUserId(userId);
//                groupon.setGrouponId(0);
//            }
//
//            grouponService.createGroupon(groupon);
//        }

        Map<String, Object> data = new HashMap<>();
        data.put("orderId", orderId);
        return restResponse.success(data);
    }

    /**
     * 取消订单
     *
     * @param userId 用户ID
     * @param body   订单信息，{ orderId：xxx }
     * @return 取消订单操作结果
     */
//    @PostMapping("cancel")
//    public Object cancel(@LoginUser Integer userId, @RequestBody String body) {
//        return wxOrderService.cancel(userId, body);
//    }
}
