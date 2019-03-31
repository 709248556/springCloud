package com.example.market.dao;

import com.example.common.entity.Coupon;
import com.example.common.util.JsonData;

import java.util.List;

public interface CouponMapper {
    List<Coupon> selective(JsonData jsonData);
    int insert(Coupon coupon);
    int updateById(Coupon coupon);
    int deleteById(int id);
}
