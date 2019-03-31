package com.example.user.controller;

import com.example.common.annotation.RequiresPermissionsDesc;
import com.example.common.entity.CouponUser;
import com.example.common.entity.Order;
import com.example.common.response.RestResponse;
import com.example.common.util.JsonData;
import com.example.user.service.CouponUserService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.bind.util.JAXBSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class CouponUserController {

    @Autowired
    private CouponUserService couponUserService;

    @GetMapping("/getCouponUser")
    public RestResponse getCouponUser(JsonData jsonData){
        return new RestResponse(couponUserService.selective(jsonData));
    }

    @PostMapping("/addCouponUser")
    public RestResponse addCouponUser(JsonData jsonData){
        return new RestResponse(couponUserService.insertive(jsonData));
    }

    @RequiresPermissions("admin:coupon:listuser")
    @RequiresPermissionsDesc(menu={"推广管理" , "优惠券管理"}, button="查询用户")
    @GetMapping("coupon/listuser")
    public RestResponse listuser(JsonData jsonData) {
        jsonData.put("deleted",0);
        List<CouponUser> couponList = couponUserService.selective(jsonData);
        long total = PageInfo.of(couponList).getTotal();
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", couponList);
        return new RestResponse(data);
    }
}
