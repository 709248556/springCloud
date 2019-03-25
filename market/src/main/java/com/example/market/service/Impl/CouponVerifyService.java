package com.example.market.service.Impl;

import com.example.common.constants.CouponConstant;
import com.example.common.constants.CouponUserConstant;
import com.example.common.entity.Coupon;
import com.example.common.entity.CouponUser;
import com.example.common.enums.RestEnum;
import com.example.common.feign.UserClient;
import com.example.common.response.RestResponse;
import com.example.common.util.JsonData;
import com.example.market.service.CouponService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.plugin.javascript.JSClassLoader;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service("couponVerifyService")
public class CouponVerifyService {

    @Autowired
    private UserClient userClient;
    @Autowired
    private CouponService couponService;

    /**
     * 检测优惠券是否适合
     *
     * @param userId
     * @param couponId
     * @param checkedGoodsPrice
     * @return
     */
    public Coupon checkCoupon(Integer userId, Integer couponId, BigDecimal checkedGoodsPrice) {
        JsonData jsonData = new JsonData();
        jsonData.put("couponId",couponId);
        Coupon coupon = couponService.selective(jsonData).get(0);
        CouponUser couponUser = null;
        try {
            RestResponse<List<CouponUser>> couponUserRestRespone = userClient.getCouponUser(userId,couponId, CouponUserConstant.STATUS_USABLE);
            if (couponUserRestRespone.getErrno() != RestEnum.OK.code) {
                log.error("CouponVerifyService.checkout错误 goodsRestResponse.getErrno() != RestEnum.OK.code，Errno为:" + couponUserRestRespone.getErrno() + "错误信息为:" + couponUserRestRespone.getErrmsg());
                //TODO 抛出异常
            }
            couponUser = couponUserRestRespone.getData().get(0);
        }catch (Exception e){
            log.error("CartController.checkout获取新增地址错误",e.getMessage());
        }
        if (coupon == null || couponUser == null) {
            return null;
        }

        // 检查是否超期
        Short timeType = coupon.getTimeType();
        Short days = coupon.getDays();
        LocalDateTime now = LocalDateTime.now();
        if (timeType.equals(CouponConstant.TIME_TYPE_TIME)) {
            if (now.isBefore(coupon.getStartTime()) || now.isAfter(coupon.getEndTime())) {
                return null;
            }
        }
        else if(timeType.equals(CouponConstant.TIME_TYPE_DAYS)) {
            LocalDateTime expired = couponUser.getAddTime().plusDays(days);
            if (now.isAfter(expired)) {
                return null;
            }
        }
        else {
            return null;
        }

        // 检测商品是否符合
        // TODO 目前仅支持全平台商品，所以不需要检测
        Short goodType = coupon.getGoodsType();
        if (!goodType.equals(CouponConstant.GOODS_TYPE_ALL)) {
            return null;
        }

        // 检测订单状态
        Short status = coupon.getStatus();
        if (!status.equals(CouponConstant.STATUS_NORMAL)) {
            return null;
        }
        // 检测是否满足最低消费
        if (checkedGoodsPrice.compareTo(coupon.getMin()) == -1) {
            return null;
        }

        return coupon;
    }

}