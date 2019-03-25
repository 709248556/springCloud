package com.example.common.enums;

public enum AuthEnum {
    UNAUTHZ(506, "无操作权限"),
    ADMIN_INVALID_ACCOUNT(605,"用户帐号已锁定不可用"),
    AUTH_INVALID_ACCOUNT(700, "账号密码不对"),
    AUTH_CAPTCHA_UNSUPPORT(701, "小程序后台验证码服务不支持"),
    AUTH_CAPTCHA_FREQUENCY(702, "验证码未超时1分钟，不能发送"),
    AUTH_CAPTCHA_UNMATCH(703, "验证码错误"),
    AUTH_NAME_REGISTERED(704, "用户名已注册"),
    AUTH_MOBILE_REGISTERED(705, "手机号已注册"),
    AUTH_MOBILE_UNREGISTERED(706, "手机号未注册"),
    AUTH_INVALID_MOBILE(707, "手机号格式不正确"),
    AUTH_OPENID_UNACCESS(708, "openid 获取失败"),
    AUTH_OPENID_BINDED(709, "openid已绑定账号");

    public final int code;
    public final String errmsg;

    private AuthEnum(int code, String errmsg) {
        this.code = code;
        this.errmsg = errmsg;
    }
}
