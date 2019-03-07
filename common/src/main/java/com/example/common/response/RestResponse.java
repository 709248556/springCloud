package com.example.common.response;

import com.example.common.enums.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)//实体类的参数查询到的为null的不显示
public class RestResponse<T> {

    private int errno;
    private String errmsg;
    private T data;


    public RestResponse error() {
        this.setErrno(-1);
        this.setErrmsg("错误");
        return this;
    }

    public RestResponse success(T data) {
        this.setErrno(RestEnum.OK.code);
        this.setErrmsg(RestEnum.OK.errmsg);
        this.setData(data);
        return this;
    }

    public RestResponse error(OrderEnum orderEnum) {
        this.setErrno(orderEnum.code);
        this.setErrmsg(orderEnum.errmsg);
        return this;
    }

    public RestResponse error(RestEnum restEnum) {
        this.setErrno(restEnum.code);
        this.setErrmsg(restEnum.errmsg);
        return this;
    }

    public RestResponse error(AuthEnum authEnum) {
        this.setErrno(authEnum.code);
        this.setErrmsg(authEnum.errmsg);
        return this;
    }

    public RestResponse(MarketClientEnum marketClientEnum) {
        this.setErrno(marketClientEnum.code);
        this.setErrmsg(marketClientEnum.errmsg);
    }
    public RestResponse error(GoodsEnum goodsEnum) {
        this.setErrno(goodsEnum.code);
        this.setErrmsg(goodsEnum.errmsg);
        return this;
    }

    public RestResponse error(CouponEnum couponEnum) {
        this.setErrno(couponEnum.code);
        this.setErrmsg(couponEnum.errmsg);
        return this;
    }


    public RestResponse() {
        this(RestEnum.OK.code, RestEnum.OK.errmsg);
    }

    public RestResponse(T data) {
        this(RestEnum.OK.code, RestEnum.OK.errmsg);
        this.data = data;
    }


    public RestResponse(int errno, String errmsg) {
        this.errno = errno;
        this.errmsg = errmsg;
    }

    public int getErrno() {
        return errno;
    }

    public void setErrno(int errno) {
        this.errno = errno;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}