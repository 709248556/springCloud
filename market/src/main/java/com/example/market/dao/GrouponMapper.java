package com.example.market.dao;

import com.example.common.entity.Coupon;
import com.example.common.entity.Groupon;
import com.example.common.util.JsonData;

import java.util.List;

public interface GrouponMapper {
    List<Groupon> selective(JsonData jsonData);
}
