package com.example.common.hystrix;

import com.example.common.entity.Brand;
import com.example.common.entity.Goods;
import com.example.common.entity.GoodsProduct;
import com.example.common.entity.OrderGoods;
import com.example.common.feign.GoodsClient;
import com.example.common.response.RestResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GoodsFallback implements GoodsClient{


    @Override
    public RestResponse<List<Goods>> getNewGoodsList(int page, int size, String sort, String order, int isNew, int isOnSale, int deleted) {
        return null;
    }

    @Override
    public RestResponse<List<Goods>> getHotGoodsList(int page, int size, String sort, String order, int isHot, int isOnSale, int deleted) {
        return null;
    }

    @Override
    public RestResponse<List<Brand>> getBrand(int page, int size, String sort, String order, int deleted) {
        return null;
    }

    @Override
    public RestResponse<List<Goods>> getGoodsListByCategoryIdList(List<Integer> categoryIdList, int page, int size, String sort, String order) {
        return null;
    }

    @Override
    public RestResponse<Goods> getGoodsById(int goodsId) {
        return null;
    }

    @Override
    public RestResponse<Goods> getSingleGoods(int goodsId, int isOnSale, int deleted) {
        return null;
    }

    @Override
    public RestResponse<List<OrderGoods>> getOrderGoodsByOrderId(int orderId, int orderGoodsDeleted) {
        return null;
    }

    @Override
    public RestResponse<GoodsProduct> getGoodsProductById(int productId) {
        return null;
    }

    @Override
    public RestResponse<Integer> addOrderGoods(OrderGoods orderGoods) {
        return null;
    }

    @Override
    public RestResponse<Integer> reduceStock(int productId, int number) {
        return null;
    }


}
