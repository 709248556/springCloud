package com.example.common.enums;

public enum RestEnum {
    OK(0, "成功"),
    BADARGUMENT(401, "参数不对"),
    BADARGUMENTVALUE(402, "参数值不对"),
    UNLOGIN(501, "请登录"),
    SERIOUS(502, "系统内部错误"),
    UNSUPPORT(503, "业务不支持"),
    UPDATEDDATEEXPIRED(504, "更新数据已经失效"),
    UPDATEDDATAFAILED(505, "更新数据失败"),

    GROUPON_EXPIRED(730, "团购活动已过期!");

    public final int code;
    public final String errmsg;

    private RestEnum(int code, String errmsg) {
        this.code = code;
        this.errmsg = errmsg;
    }

}
