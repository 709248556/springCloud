package com.example.common.enums;

public enum UserClientEnum {
    GET_COMMENTLIST(506,"user服务异常，获取用户评论失败"),
    GET_COLLECTCOUNTNUM(507,"user服务异常,获取用户收藏"),
    GET_USER(508,"user服务异常，获取用户信息失败");



    public final int code;
    public final String errmsg;

    private UserClientEnum(int code, String errmsg) {
        this.code = code;
        this.errmsg = errmsg;
    }
}
