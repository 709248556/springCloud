package com.example.user.service;

import com.example.common.entity.CouponUser;
import com.example.common.util.JsonData;

import java.util.List;

public interface CouponUserService {
    List<CouponUser> selective(JsonData jsonData);
    int insertive (JsonData jsonData);
}
