package com.example.common.hystrix;

import com.example.common.entity.*;
import com.example.common.enums.UserClientEnum;
import com.example.common.feign.UserClient;
import com.example.common.response.RestResponse;
import com.example.common.util.JsonData;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Component
public class UserFallback implements UserClient {
    @Override
    public RestResponse<List<Comment>> getCommentList(int goodsId,int commentType) {
        return new RestResponse(UserClientEnum.GET_COMMENTLIST);
    }

    @Override
    public RestResponse<Integer> getCollectCountNum(int goodsId, int userId,int collectType) {
        return new RestResponse(UserClientEnum.GET_COLLECTCOUNTNUM);
    }

    @Override
    public RestResponse<List<User>> getUser(int userId) {
        return new RestResponse(UserClientEnum.GET_USER);
    }

    @Override
    public RestResponse<List<CouponUser>> getCouponUser(int couponId, int deleted) {
        return null;
    }

    @Override
    public RestResponse<List<CouponUser>> getCouponUser(int couponId, int deleted, int userId) {
        return null;
    }

    @Override
    public RestResponse<List<CouponUser>> getCouponUser(int userId, String status, int deleted) {
        return null;
    }

    @Override
    public RestResponse<List<CouponUser>> getCouponUser(int userId, int couponId, String status) {
        return null;
    }

    @Override
    public RestResponse<List<CouponUser>> getCouponUser(int userId, String status) {
        return null;
    }

    @Override
    public RestResponse<Integer> addCouponUser(int userId, int couponId, int status, LocalDateTime usedTime, LocalDateTime startTime, LocalDateTime endTime, Integer orderId, LocalDateTime addTime, LocalDateTime updateTime, int deleted) {
        return null;
    }

    @Override
    public RestResponse<List<Address>> getAddress(int userId, int isDefault, int deleted) {
        return null;
    }

    @Override
    public RestResponse<List<Address>> getAddress(int id) {
        return null;
    }

    @Override
    public RestResponse<List<Admin>> getAdminList(String username, int deleted) {
        return null;
    }

    @Override
    public RestResponse<Set<String>> getRolesList(Integer[] roleIds) {
        return null;
    }

    @Override
    public RestResponse<Set<String>> getPermissionsList(Integer[] roleIds) {
        return null;
    }

    @Override
    public RestResponse<List<User>> getUserAll(int deleted) {
        return null;
    }

    @Override
    public RestResponse<List<Order>> getOrderAll(int deleted) {
        return null;
    }


}
