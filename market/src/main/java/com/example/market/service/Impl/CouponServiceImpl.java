package com.example.market.service.Impl;

import com.example.common.entity.Coupon;
import com.example.common.util.JsonData;
import com.example.market.dao.CouponMapper;
import com.example.market.service.CouponService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("grouponService")
public class CouponServiceImpl implements CouponService {
    @Autowired
    private CouponMapper couponMapper;

    @Override
    public List<Coupon> selective(JsonData jsonData) {
        if(jsonData.containsKey("couponPage")&&jsonData.containsKey("couponSize"))
            PageHelper.startPage(Integer.valueOf(jsonData.get("couponPage").toString()), Integer.valueOf(jsonData.get("couponSize").toString()));
        return couponMapper.selective(jsonData);
    }
}
