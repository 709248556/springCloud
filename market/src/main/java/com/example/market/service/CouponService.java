package com.example.market.service;

import com.example.common.entity.Coupon;
import com.example.common.util.JsonData;

import java.util.List;

public interface CouponService {
    List<Coupon> selective(JsonData jsonData);
    String generateCode();
    int insert(Coupon coupon);
    int updateById(Coupon coupon);
    int deleteById(int id);
}
