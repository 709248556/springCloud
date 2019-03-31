package com.example.common.enums;

public enum  OrderEnum {
    ORDER_UNKNOWN(720, "订单不存在"),
    ORDER_INVALID(721, "不是当前用户的订单"),
    ORDER_PAY_FAIL(724, "订单不能支付"),
    // 订单当前状态下不支持用户的操作，例如商品未发货状态用户执行确认收货是不可能的。
    ORDER_INVALID_CANCEL(725, "订单不能取消"),
    ORDER_INVALID_PAY(725, "订单不能支付"),
    ORDER_INVALID_RECEIPT(620, "订单不能确认收货"),
    ORDER_INVALID_DELETE(725, "订单不能删除"),
    ORDER_INVALID_COMMENT(725, "当前商品不能评价"),
    ORDER_COMMENTED(726, "订单商品已评价"),
    ORDER_COMMENT_EXPIRED(727, "当前商品评价时间已经过期"),
    ORDER_REPLY_EXIST(622, "订单商品已回复!"),
    ADMIN_INVALID_PASSWORD(620,"订单不能退款");
    public final int code;
    public final String errmsg;

    private OrderEnum(int code, String errmsg) {
        this.code = code;
        this.errmsg = errmsg;
    }
}
