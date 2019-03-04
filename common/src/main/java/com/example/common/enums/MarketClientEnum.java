package com.example.common.enums;

import org.springframework.stereotype.Component;

public enum MarketClientEnum {
    GET_ISSUEALL(510,"market服务异常，getIssueAll接口错误"),
    GET_ISSUE(511,"market服务异常,getIssue接口错误"),
    GET_GROUPONRULES(512,"market服务异常,getGrouponRules接口错误"),
    GET_GETCATEGORYLIST(513,"market服务异常,getCategoryList接口错误");

    public final int code;
    public final String errmsg;

    private MarketClientEnum(int code, String errmsg) {
        this.code = code;
        this.errmsg = errmsg;
    }
}
