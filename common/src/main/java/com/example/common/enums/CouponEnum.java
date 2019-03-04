package com.example.common.enums;

public enum CouponEnum {
    COUPON_NUM_LIMIT(740, "优惠券已领完"),
    COUPON_EXCEED_LIMIT(740, "优惠券已经领取过"),
    COUPON_EXCHANGE(740,"优惠券已兑换"),
    COUPON_AUTO_SEND(741, "新用户优惠券自动发送"),
    COUPON_EXCHANGE_FAIL(741,"优惠券只能兑换"),
    COUPON_TYPE_FAIL(741,"优惠券类型不支持"),
    COUPON_TIME_FAIL(741,"优惠券已经过期"),
    COUPON_RECEIVE_ONLY(741,"优惠券只能领取，不能兑换"),
    COUPON_CODE_INVALID(742, "优惠券不正确");

    public final int code;
    public final String errmsg;

    private CouponEnum(int code, String errmsg) {
        this.code = code;
        this.errmsg = errmsg;
    }
}
