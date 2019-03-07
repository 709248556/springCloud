package com.example.user.controller;

import com.example.common.entity.CouponUser;
import com.example.common.response.RestResponse;
import com.example.common.util.JsonData;
import com.example.user.service.CouponUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.bind.util.JAXBSource;

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
}
