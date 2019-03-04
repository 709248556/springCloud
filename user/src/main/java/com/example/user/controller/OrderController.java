package com.example.user.controller;

import com.alibaba.fastjson.JSON;
import com.example.common.constants.OrderConstant;
import com.example.common.constants.TokenConstant;
import com.example.common.entity.Groupon;
import com.example.common.entity.Order;
import com.example.common.entity.OrderGoods;
import com.example.common.entity.User;
import com.example.common.enums.OrderEnum;
import com.example.common.enums.RestEnum;
import com.example.common.feign.GoodsClient;
import com.example.common.feign.MarketClient;
import com.example.common.response.RestResponse;
import com.example.common.util.JsonData;
import com.example.common.util.OrderUtil;
import com.example.common.util.RedisUtil;
import com.example.user.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        int  userId = redisUtil.getUserId(jsonData.get(TokenConstant.TOKEN).toString());
        jsonData.put("userId",userId);
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
        int  userId = redisUtil.getUserId(jsonData.get(TokenConstant.TOKEN).toString());
        jsonData.put("userId",userId);
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
        int  userId = redisUtil.getUserId(jsonData.get(TokenConstant.TOKEN).toString());
        jsonData.put("userId",userId);
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
        }catch (Exception e){
            log.error("OrderController.list方法错误", e.getMessage());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("orderInfo", orderVo);
        result.put("orderGoods", orderGoodsList);

        // 订单状态为已发货且物流信息不为空
        //"YTO", "800669400640887922"
        if (order.getOrderStatus().equals(OrderConstant.STATUS_SHIP)) {
//            ExpressInfo ei = expressService.getExpressInfo(order.getShipChannel(), order.getShipSn());
            result.put("expressInfo", null);
        }

        return restResponse.success(result);
    }

    /**
     * 提交订单
     *
     * @param userId 用户ID
     * @param body   订单信息，{ cartId：xxx, addressId: xxx, couponId: xxx, message: xxx, grouponRulesId: xxx,  grouponLinkId: xxx}
     * @return 提交订单操作结果
     */
//    @PostMapping("submit")
//    public Object submit(@LoginUser Integer userId, @RequestBody String body) {
//        return wxOrderService.submit(userId, body);
//    }

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
