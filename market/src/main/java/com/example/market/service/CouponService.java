package com.example.market.service;

import com.example.common.entity.Coupon;
import com.example.common.util.JsonData;

import java.util.List;

public interface CouponService {
    public List<Coupon> selective(JsonData jsonData);
}
