package com.example.common.feign;

import com.example.common.entity.Brand;
import com.example.common.entity.Goods;
import com.example.common.entity.OrderGoods;
import com.example.common.hystrix.GoodsFallback;
import com.example.common.hystrix.MarketFallback;
import com.example.common.response.RestResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "goods",fallback = GoodsFallback.class)
public interface GoodsClient {

    @GetMapping("/getGoodsList")
    RestResponse<List<Goods>> getNewGoodsList(@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam("sort") String sort, @RequestParam("order") String order, @RequestParam("isNew") int isNew, @RequestParam("isOnSale") int isOnSale, @RequestParam("deleted") int deleted);

    @GetMapping("/getGoodsList")
    RestResponse<List<Goods>> getHotGoodsList(@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam("sort") String sort, @RequestParam("order") String order, @RequestParam("isHot") int isHot, @RequestParam("isOnSale") int isOnSale, @RequestParam("deleted") int deleted);

    @GetMapping("/getBrand")
    RestResponse<List<Brand>> getBrand(@RequestParam("brandPage") int brandPage,@RequestParam("brandSize") int brandSize,@RequestParam("brandSort") String brandSort,@RequestParam("brandOrder") String brandOrder,@RequestParam("brandDeleted") int brandDeleted);

    @PostMapping("/getGoodsListByCategoryIdList")
    RestResponse<List<Goods>> getGoodsListByCategoryIdList(@RequestParam("categoryIdList")List<Integer> categoryIdList,@RequestParam("page")int page,@RequestParam("size")int size,@RequestParam("sort") String sort, @RequestParam("order")String order);

    @GetMapping("/getGoodsById")
    RestResponse<Goods> getGoodsById(@RequestParam("goodsId") int goodsId);

    @GetMapping("/getOrderGoodsByOrderId")
    RestResponse<List<OrderGoods>> getOrderGoodsByOrderId(@RequestParam("orderId") int orderId,@RequestParam("orderGoodsDeleted") int orderGoodsDeleted);

}