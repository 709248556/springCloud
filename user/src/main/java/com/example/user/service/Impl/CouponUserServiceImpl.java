package com.example.user.service.Impl;

import com.example.common.entity.Coupon;
import com.example.common.entity.CouponUser;
import com.example.common.util.JsonData;
import com.example.user.dao.CouponUserMapper;
import com.example.user.service.CouponUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("couponUserService")
public class CouponUserServiceImpl implements CouponUserService {
    @Autowired
    private CouponUserMapper couponUserMapper;

    @Override
    public List<CouponUser> selective(JsonData jsonData) {
        return couponUserMapper.selective(jsonData);
    }

    @Override
    public int insertive(JsonData jsonData) {
        return couponUserMapper.insertive(jsonData);
    }
}
