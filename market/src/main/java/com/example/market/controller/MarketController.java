package com.example.market.controller;

import com.example.common.constants.CouponConstant;
import com.example.common.entity.*;
import com.example.common.enums.RestEnum;
import com.example.common.feign.GoodsClient;
import com.example.common.response.RestResponse;
import com.example.common.util.JsonData;
import com.example.market.service.*;
import com.example.market.systemConfig.SystemConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

@Slf4j
@RestController
public class MarketController {

    @Autowired
    private AdvertisementService advertisementService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private GrouponRulesService grouponRulesService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private CouponService couponService;

    /**
     * 首页数据
     *
     * @param userId 当用户已经登录时，非空。为登录状态为null
     * @return 首页数据
     */
    @GetMapping("/home/index")
    public RestResponse index(Integer userId) {
        //优先从缓存中读取
//        if (HomeCacheManager.hasData(HomeCacheManager.INDEX)) {
//            return ResponseUtil.ok(HomeCacheManager.getCacheData(HomeCacheManager.INDEX));
//        }
        JsonData jsonData = new JsonData();
        Map<String, Object> data = new HashMap<>();

        List<Advertisement> bannerList = advertisementService.selective(jsonData);

        jsonData.put("categoryLevel", "L1");
        jsonData.put("categoryDeleted", 0);
        List<Category> categoryList = categoryService.selective(jsonData);

        List<Coupon> couponList = new ArrayList<>();
        jsonData.put("couponPage", 0);
        jsonData.put("couponSize", 3);
        jsonData.put("couponType", CouponConstant.TYPE_COMMON);
        jsonData.put("couponStatus", CouponConstant.STATUS_NORMAL);
        jsonData.put("couponDelete", 0);
        if (userId == null) {
            couponList = couponService.selective(jsonData);
        } else {
            jsonData.put("userId", userId);
            couponList = couponService.selective(jsonData);
        }
        List<Goods> newGoodsList = new ArrayList<>();
        List<Goods> hotGoodsList = new ArrayList<>();
        List<Brand> brandList = new ArrayList<>();
        try {
            RestResponse<List<Goods>> newGoodsRestResponse = goodsClient.getNewGoodsList(0, SystemConfig.getNewLimit(), "add_time", "desc", 1, 1, 0);
            if (newGoodsRestResponse.getErrno() != RestEnum.OK.code) {
                log.error("MarketController.index方法错误 newGoodsRestResponse.getErrno() != RestEnum.OK.code，Errno为:" + newGoodsRestResponse.getErrno() + "错误信息为:" + newGoodsRestResponse.getErrmsg());
                //TODO 抛出异常
            }
            RestResponse<List<Goods>> hostGoodsRestResponse = goodsClient.getHotGoodsList(0, SystemConfig.getHotLimit(), "add_time", "desc", 1, 1, 0);
            if (hostGoodsRestResponse.getErrno() != RestEnum.OK.code) {
                log.error("MarketController.index方法错误 hostGoodsRestResponse.getErrno() != RestEnum.OK.code，Errno为:" + hostGoodsRestResponse.getErrno() + "错误信息为:" + hostGoodsRestResponse.getErrmsg());
                //TODO 抛出异常
            }
            RestResponse<List<Brand>> brandRestResponse = goodsClient.getBrand(0, SystemConfig.getBrandLimit(), "add_time", "desc", 0);
            if (newGoodsRestResponse.getErrno() != RestEnum.OK.code) {
                log.error("MarketController.index方法错误 brandRestResponse.getErrno() != RestEnum.OK.code，Errno为:" + brandRestResponse.getErrno() + "错误信息为:" + brandRestResponse.getErrmsg());
                //TODO 抛出异常
            }
            newGoodsList = newGoodsRestResponse.getData();
            hotGoodsList = hostGoodsRestResponse.getData();
            brandList = brandRestResponse.getData();
        } catch (Exception e) {
            log.error("MarketController.index ERROR", e.getMessage());
        }

        jsonData.put("topicPage", 0);
        jsonData.put("topicSize", SystemConfig.getTopicLimit());
        jsonData.put("topicSort", "add_time");
        jsonData.put("topicOrder", "desc");
        List<Topic> topicList = topicService.selective(jsonData);

        //团购专区
        jsonData.put("grouponPage", 0);
        jsonData.put("grouponSize", 5);
        jsonData.put("grouponSort", "add_time");
        jsonData.put("grouponOrder", "desc");
        jsonData.put("grouponDeleted", 0);
        List<GrouponRules> grouponRules = grouponRulesService.selective(jsonData);
        List<Map<String, Object>> grouponList = new ArrayList<>(grouponRules.size());
        for (GrouponRules rule : grouponRules) {
            Integer goodsId = rule.getGoodsId();
            Goods goods = new Goods();
            try {
                RestResponse<Goods> restResponse = goodsClient.getGoodsById(goodsId);
                if (restResponse.getErrno() != RestEnum.OK.code) {
                    log.error("MarketController.index方法错误 restResponse.getErrno() != RestEnum.OK.code，Errno为:" + restResponse.getErrno() + "错误信息为:" + restResponse.getErrmsg());
                    //TODO 抛出异常
                }
                goods = restResponse.getData();
            } catch (Exception e) {
                log.error("MarketController.index ERROR", e.getMessage());
            }

            if (goods == null)
                continue;

            Map<String, Object> item = new HashMap<>();
            item.put("goods", goods);
            item.put("groupon_price", goods.getRetailPrice().subtract(rule.getDiscount()));
            item.put("groupon_member", rule.getDiscountMember());
            grouponList.add(item);
        }

        List<Map> floorGoodsList = getCategoryList();


        try {
            data.put("banner", bannerList);
            data.put("channel", categoryList);
            data.put("couponList", couponList);
            data.put("newGoodsList", newGoodsList);
            data.put("hotGoodsList", hotGoodsList);
            data.put("brandList", brandList);
            data.put("topicList", topicList);
            data.put("grouponList", grouponList);
            data.put("floorGoodsList", floorGoodsList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //缓存数据
//        HomeCacheManager.loadData(HomeCacheManager.INDEX, data);
//        executorService.shutdown();
        return new RestResponse(data);
    }

    private List<Map> getCategoryList() {
        List<Map> categoryList = new ArrayList<>();
        JsonData jsonData = new JsonData();
        jsonData.put("categoryPage", 0);
        jsonData.put("categorySize", SystemConfig.getCatlogListLimit());
        jsonData.put("categoryLevel", "L1");
        jsonData.put("categoryDeleted", 0);
        List<Category> catL1List = categoryService.selective(jsonData);
        jsonData.remove("categoryLevel");
        jsonData.remove("categoryPage");
        jsonData.remove("categorySize");
        for (Category catL1 : catL1List) {
            jsonData.put("categoryPId", catL1.getId());
            List<Category> catL2List = categoryService.selective(jsonData);
            List<Integer> l2List = new ArrayList<>();
            for (Category catL2 : catL2List) {
                l2List.add(catL2.getId());
            }
            List<Goods> categoryGoods = new ArrayList<>();
            if (l2List.size() != 0) {
                try {
                    RestResponse<List<Goods>> restResponse = goodsClient.getGoodsListByCategoryIdList(l2List, 0, SystemConfig.getCatlogMoreLimit(), "add_time", "desc");
                    if (restResponse.getErrno() != RestEnum.OK.code) {
                        log.error("MarketController.index.getGoodsListByCategoryIdList方法错误 restResponse.getErrno() != RestEnum.OK.code，Errno为:" + restResponse.getErrno() + "错误信息为:" + restResponse.getErrmsg());
                        //TODO 抛出异常
                    }
                    categoryGoods = restResponse.getData();
                } catch (Exception e) {
                    log.error("MarketController.index.getCategoryList ERROR", e.getMessage());
                }

            }

            Map<String, Object> catGoods = new HashMap<>();
            catGoods.put("id", catL1.getId());
            catGoods.put("name", catL1.getName());
            catGoods.put("goodsList", categoryGoods);
            categoryList.add(catGoods);
        }
        return categoryList;
    }
}
