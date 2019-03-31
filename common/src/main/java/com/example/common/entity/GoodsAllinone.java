package com.example.common.entity;


public class GoodsAllinone {
    Goods goods;
    GoodsSpecification[] specifications;
    GoodsAttribute[] attributes;
    // 这里采用 Product 再转换到 GoodsProduct
    GoodsProduct[] products;

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public GoodsProduct[] getProducts() {
        return products;
    }

    public void setProducts(GoodsProduct[] products) {
        this.products = products;
    }

    public GoodsSpecification[] getSpecifications() {
        return specifications;
    }

    public void setSpecifications(GoodsSpecification[] specifications) {
        this.specifications = specifications;
    }

    public GoodsAttribute[] getAttributes() {
        return attributes;
    }

    public void setAttributes(GoodsAttribute[] attributes) {
        this.attributes = attributes;
    }

}
