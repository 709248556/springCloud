package com.example.user.dao;

import com.example.common.entity.CouponUser;
import com.example.common.util.JsonData;

import java.util.List;

public interface CouponUserMapper {
    List<CouponUser> selective(JsonData jsonData);
    int insertive(JsonData jsonData);
}
