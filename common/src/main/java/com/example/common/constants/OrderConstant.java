package com.example.common.constants;


import com.example.common.entity.Order;

import java.util.ArrayList;
import java.util.List;

/*
 * 订单流程：下单成功－》支付订单－》发货－》收货
 * 订单状态：
 * 101 订单生成，未支付；102，下单未支付用户取消；103，下单未支付超期系统自动取消
 * 201 支付完成，商家未发货；202，订单生产，已付款未发货，用户申请退款；203，管理员执行退款操作，确认退款成功；
 * 301 商家发货，用户未确认；
 * 401 用户确认收货，订单结束； 402 用户没有确认收货，但是快递反馈已收获后，超过一定时间，系统自动确认收货，订单结束。
 *
 * 当101用户未付款时，此时用户可以进行的操作是取消或者付款
 * 当201支付完成而商家未发货时，此时用户可以退款
 * 当301商家已发货时，此时用户可以有确认收货
 * 当401用户确认收货以后，此时用户可以进行的操作是退货、删除、去评价或者再次购买
 * 当402系统自动确认收货以后，此时用户可以删除、去评价、或者再次购买
 */
public class OrderConstant {

    public static final Short STATUS_CREATE = 101;
    public static final Short STATUS_PAY = 201;
    public static final Short STATUS_SHIP = 301;
    public static final Short STATUS_CONFIRM = 401;
    public static final Short STATUS_CANCEL = 102;
    public static final Short STATUS_AUTO_CANCEL = 103;
    public static final Short STATUS_REFUND = 202;
    public static final Short STATUS_REFUND_CONFIRM = 203;
    public static final Short STATUS_AUTO_CONFIRM = 402;


    public static String orderStatusText(Order order) {
        int status = order.getOrderStatus().intValue();

        if (status == 101) {
            return "未付款";
        }

        if (status == 102) {
            return "已取消";
        }

        if (status == 103) {
            return "已取消(系统)";
        }

        if (status == 201) {
            return "已付款";
        }

        if (status == 202) {
            return "订单取消，退款中";
        }

        if (status == 203) {
            return "已退款";
        }

        if (status == 301) {
            return "已发货";
        }

        if (status == 401) {
            return "已收货";
        }

        if (status == 402) {
            return "已收货(系统)";
        }

        throw new IllegalStateException("orderStatus不支持");
    }
    public static boolean isCreateStatus(Order order) {
        return OrderConstant.STATUS_CREATE == order.getOrderStatus().shortValue();
    }

    public static boolean isPayStatus(Order order) {
        return OrderConstant.STATUS_PAY == order.getOrderStatus().shortValue();
    }

    public static boolean isShipStatus(Order order) {
        return OrderConstant.STATUS_SHIP == order.getOrderStatus().shortValue();
    }

    public static boolean isConfirmStatus(Order order) {
        return OrderConstant.STATUS_CONFIRM == order.getOrderStatus().shortValue();
    }

    public static boolean isCancelStatus(Order order) {
        return OrderConstant.STATUS_CANCEL == order.getOrderStatus().shortValue();
    }

    public static boolean isAutoCancelStatus(Order order) {
        return OrderConstant.STATUS_AUTO_CANCEL == order.getOrderStatus().shortValue();
    }

    public static boolean isRefundStatus(Order order) {
        return OrderConstant.STATUS_REFUND == order.getOrderStatus().shortValue();
    }

    public static boolean isRefundConfirmStatus(Order order) {
        return OrderConstant.STATUS_REFUND_CONFIRM == order.getOrderStatus().shortValue();
    }

    public static boolean isAutoConfirmStatus(Order order) {
        return OrderConstant.STATUS_AUTO_CONFIRM == order.getOrderStatus().shortValue();
    }
}
