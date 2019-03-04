package com.example.common.enums;

public enum  GoodsEnum {
    GOODS_UNSHELVE(710, "商品已下架"),
    GOODS_NO_STOCK(711, "库存不足"),
    GOODS_UNKNOWN(712, "未找到对应的商品");

    public final int code;
    public final String errmsg;

    private GoodsEnum(int code, String errmsg) {
        this.code = code;
        this.errmsg = errmsg;
    }

}
