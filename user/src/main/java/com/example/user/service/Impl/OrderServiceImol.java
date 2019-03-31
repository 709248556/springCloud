package com.example.user.service.Impl;

import com.example.common.constants.TypeConstant;
import com.example.common.entity.Comment;
import com.example.common.entity.Order;
import com.example.common.entity.OrderGoods;
import com.example.common.enums.OrderEnum;
import com.example.common.enums.RestEnum;
import com.example.common.feign.GoodsClient;
import com.example.common.response.RestResponse;
import com.example.common.util.JacksonUtil;
import com.example.common.util.JsonData;
import com.example.common.util.OrderUtil;
import com.example.user.dao.OrderMapper;
import com.example.user.service.OrderService;
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.result.WxPayRefundResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service("orderService")
@Slf4j
public class OrderServiceImol implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private GoodsClient goodsClient;


    @Override
    public List<Order> selective(JsonData jsonData) {
        if (jsonData.containsKey("page") && jsonData.containsKey("limit"))
            PageHelper.startPage(Integer.valueOf(jsonData.get("page").toString()), Integer.valueOf(jsonData.get("limit").toString()));
        if (jsonData.containsKey("orderSn")) {
            jsonData.put("orderSn", "%" + jsonData.get("orderSn") + "%");
        }
        return orderMapper.selective(jsonData);
    }

    @Override
    public int insert(Order order) {
        return orderMapper.insert(order);
    }

    @Override
    public int updative(JsonData jsonData) {
        return orderMapper.updative(jsonData);
    }


    /**
     * 订单退款
     * <p>
     * 1. 检测当前订单是否能够退款;
     * 2. 微信退款操作;
     * 3. 设置订单退款确认状态；
     * 4. 订单商品库存回库。
     * <p>
     * TODO
     * 虽然接入了微信退款API，但是从安全角度考虑，建议开发者删除这里微信退款代码，采用以下两步走步骤：
     * 1. 管理员登录微信官方支付平台点击退款操作进行退款
     * 2. 管理员登录litemall管理后台点击退款操作进行订单状态修改和商品库存回库
     *
     * @param /body 订单信息，{ orderId：xxx }
     * @return 订单退款操作结果
     */
    @Override
    @Transactional
    public RestResponse refund(Integer orderId,String refundMoney) {
        RestResponse restResponse = new RestResponse();
        JsonData jsonData = new JsonData();
        jsonData.put("oderId",orderId);
        Order order = orderMapper.selective(jsonData).get(0);
        if (order == null) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }

        if (order.getActualPrice().compareTo(new BigDecimal(refundMoney)) != 0) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }

        // 如果订单不是退款状态，则不能退款
        if (!order.getOrderStatus().equals(OrderUtil.STATUS_REFUND)) {
            return restResponse.error(OrderEnum.ADMIN_INVALID_PASSWORD);
        }

        // 微信退款
//        WxPayRefundRequest wxPayRefundRequest = new WxPayRefundRequest();
//        wxPayRefundRequest.setOutTradeNo(order.getOrderSn());
//        wxPayRefundRequest.setOutRefundNo("refund_" + order.getOrderSn());
        // 元转成分
//        Integer totalFee = order.getActualPrice().multiply(new BigDecimal(100)).intValue();
//        wxPayRefundRequest.setTotalFee(totalFee);
//        wxPayRefundRequest.setRefundFee(totalFee);
//
//        WxPayRefundResult wxPayRefundResult = null;
//        try {
//            wxPayRefundResult = wxPayService.refund(wxPayRefundRequest);
//        } catch (WxPayException e) {
//            e.printStackTrace();
//            return ResponseUtil.fail(ORDER_REFUND_FAILED, "订单退款失败");
//        }
//        if (!wxPayRefundResult.getReturnCode().equals("SUCCESS")) {
//            logger.warn("refund fail: " + wxPayRefundResult.getReturnMsg());
//            return ResponseUtil.fail(ORDER_REFUND_FAILED, "订单退款失败");
//        }
//        if (!wxPayRefundResult.getResultCode().equals("SUCCESS")) {
//            logger.warn("refund fail: " + wxPayRefundResult.getReturnMsg());
//            return ResponseUtil.fail(ORDER_REFUND_FAILED, "订单退款失败");
//        }

        // 设置订单取消状态
        order.setOrderStatus(OrderUtil.STATUS_REFUND_CONFIRM);
        LocalDateTime preUpdateTime = order.getUpdateTime();
        order.setUpdateTime(LocalDateTime.now());
        if (orderMapper.update(preUpdateTime,order) == 0) {
            throw new RuntimeException("更新数据已失效");
        }

        // 商品货品数量增加
        List<OrderGoods> orderGoodsList = null;
        try {
            RestResponse<List<OrderGoods>> orderGoodsRestResponse = goodsClient.getOrderGoodsByOrderId(orderId,0);
            if (orderGoodsRestResponse.getErrno() != RestEnum.OK.code) {
                log.error("goodsClient.getOrderGoodsByOrderId方法错误 orderGoodsRestResponse.getErrno() != RestEnum.OK.code，Errno为:" + orderGoodsRestResponse.getErrno() + "错误信息为:" + orderGoodsRestResponse.getErrmsg());
                //TODO 抛出异常
            }
            orderGoodsList = orderGoodsRestResponse.getData();
        } catch (Exception e) {
            log.error("OrderController.submit方法错误", e.getMessage());
        }

        for (OrderGoods orderGoods : orderGoodsList) {
            Integer productId = orderGoods.getProductId();
            Short number = orderGoods.getNumber();
            int result = 0;
            try {
                RestResponse<Integer> orderGoodsRestResponse = goodsClient.addStock(productId, number);
                if (orderGoodsRestResponse.getErrno() != RestEnum.OK.code) {
                    log.error("goodsClient.addStock方法错误 orderGoodsRestResponse.getErrno() != RestEnum.OK.code，Errno为:" + orderGoodsRestResponse.getErrno() + "错误信息为:" + orderGoodsRestResponse.getErrmsg());
                    //TODO 抛出异常
                }
                result = orderGoodsRestResponse.getData();
            } catch (Exception e) {
                log.error("OrderController.submit方法错误", e.getMessage());
            }

            if (result == 0) {
                throw new RuntimeException("商品货品库存增加失败");
            }
        }

        //TODO 发送邮件和短信通知，这里采用异步发送
        // 退款成功通知用户, 例如“您申请的订单退款 [ 单号:{1} ] 已成功，请耐心等待到账。”
        // 注意订单号只发后6位
//        notifyService.notifySmsTemplate(order.getMobile(), NotifyType.REFUND, new String[]{order.getOrderSn().substring(8, 14)});

        return restResponse;
    }

    /**
     * 发货
     * 1. 检测当前订单是否能够发货
     * 2. 设置订单发货状态
     *
     * @param //body 订单信息，{ orderId：xxx, shipSn: xxx, shipChannel: xxx }
     * @param //body 订单信息，{ orderId：xxx, shipSn: xxx, shipChannel: xxx }
     * @return 订单操作结果
     * 成功则 { errno: 0, errmsg: '成功' }
     * 失败则 { errno: XXX, errmsg: XXX }
     */
    @Override
    public RestResponse ship(Integer orderId ,String shipSn,String shipChannel) {
        JsonData jsonData = new JsonData();
        RestResponse restResponse = new RestResponse();
        jsonData.put("orderId",orderId);
        Order order = orderMapper.selective(jsonData).get(0);
        if (order == null) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }

        // 如果订单不是已付款状态，则不能发货
        if (!order.getOrderStatus().equals(OrderUtil.STATUS_PAY)) {
            return restResponse.error(OrderEnum.ORDER_INVALID_RECEIPT);
        }

        order.setOrderStatus(OrderUtil.STATUS_SHIP);
        order.setShipSn(shipSn);
        order.setShipChannel(shipChannel);
        order.setShipTime(LocalDateTime.now());

        LocalDateTime preUpdateTime = order.getUpdateTime();
        order.setUpdateTime(LocalDateTime.now());

        if (orderMapper.update(preUpdateTime, order) == 0) {
            return restResponse.error(RestEnum.UPDATEDDATAFAILED);
        }

        //TODO 发送邮件和短信通知，这里采用异步发送
        // 发货会发送通知短信给用户:          *
        // "您的订单已经发货，快递公司 {1}，快递单 {2} ，请注意查收"
//        notifyService.notifySmsTemplate(order.getMobile(), NotifyType.SHIP, new String[]{shipChannel, shipSn});

        return restResponse;
    }
}
