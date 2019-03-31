package com.example.goods.controller;

import com.example.common.constants.TokenConstant;
import com.example.common.constants.TypeConstant;
import com.example.common.entity.*;
import com.example.common.enums.GoodsEnum;
import com.example.common.enums.RestEnum;
import com.example.common.feign.MarketClient;
import com.example.common.feign.UserClient;
import com.example.common.response.RestResponse;
import com.example.common.util.JsonData;
import com.example.common.util.RedisUtil;
import com.example.goods.service.*;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private GoodsAttributeService goodsAttributeService;

    @Autowired
    private GoodsSpecificationService goodsSpecificationService;

    @Autowired
    private GoodsProductService goodsProductService;

    @Autowired
    private MarketClient marketClient;

    @Autowired
    private BrandService brandService;

    @Autowired
    private UserClient userClient;

    @Autowired
    private RedisUtil redisUtil;


    /**
     * 商品详情
     * <p>
     * 用户可以不登录。
     * 如果用户登录，则记录用户足迹以及返回用户收藏信息。
     */
    @GetMapping("/detail")
    public RestResponse detail(JsonData jsonData) {
        jsonData.put("goodsId", jsonData.get("id"));

        // 商品信息
        Goods info = goodsService.selective(jsonData).get(0);
        // 商品属性
        List<GoodsAttribute> goodsAttributeList = goodsAttributeService.selective(jsonData);

        // 商品规格 返回的是定制的GoodsSpecificationVo
        List<GoodsSpecificationVo> goodsSpecificationVoList = goodsSpecificationService.getGoodsSpecificationVoList(jsonData);

        // 商品规格对应的数量和价格
        List<GoodsProduct> goodsProducts = goodsProductService.selective(jsonData);

        // 商品问题，这里是一些通用问题
        List<Issue> issueList = new ArrayList<>();
        try {
            RestResponse<List<Issue>> restResponse = marketClient.getIssueAll();
            if (restResponse.getErrno() != RestEnum.OK.code) {
                log.error("GoodsController.detail方法错误 restResponse.getErrno() != RestEnum.OK.code，Errno为:" + restResponse.getErrno() + "错误信息为:" + restResponse.getErrmsg());
                //TODO 抛出异常
            }
            issueList = restResponse.getData();
        } catch (Exception e) {
            log.error("GoodsController.detail方法错误", e.getMessage());
        }

        // 商品品牌商
        jsonData.put("brandId", info.getBrandId());
        Brand brand = new Brand();
        if (info.getBrandId() != 0) brand = brandService.selective(jsonData).get(0);

        // 评论
        Map<String, Object> commentList = new HashMap<>();
        try {
            RestResponse<List<Comment>> comentsListRestResponse = userClient.getCommentList(Integer.valueOf(jsonData.get("id").toString()), TypeConstant.GOODS_TYPE);
            if (comentsListRestResponse.getErrno() != RestEnum.OK.code) {
                log.error("GoodsController.detail方法错误 comentsListRestResponse.getErrno() != RestEnum.OK.code，Errno为:" + comentsListRestResponse.getErrno() + "错误信息为:" + comentsListRestResponse.getErrmsg());
                //TODO 抛出异常
            }
            List<Comment> comments = comentsListRestResponse.getData();
            List<Map<String, Object>> commentsVo = new ArrayList<>(comments.size());
            long commentCount = PageInfo.of(comments).getTotal();
            for (Comment comment : comments) {
                Map<String, Object> c = new HashMap<>();
                c.put("id", comment.getId());
                c.put("addTime", comment.getAddTime());
                c.put("content", comment.getContent());
                RestResponse<List<User>> restResponse = userClient.getUser(comment.getUserId());
                if (restResponse.getErrno() != RestEnum.OK.code) {
                    log.error("GoodsController.detail方法错误 restResponse.getErrno() != RestEnum.OK.code，Errno为:" + restResponse.getErrno());
                    log.error("错误信息为:" + restResponse.getErrmsg());
                    //TODO 抛出异常
                }
                User user = restResponse.getData().get(0);
                c.put("nickname", user.getNickname());
                c.put("avatar", user.getAvatar());
                c.put("picList", comment.getPicUrls());
                commentsVo.add(c);
            }

            commentList.put("count", commentCount);
            commentList.put("data", commentsVo);
        } catch (Exception e) {
            log.error("GoodsController.detail方法错误", e.getMessage());
        }


        //团购信息
        List<GrouponRules> grouponRulesList = new ArrayList<>();
        try {
            RestResponse<List<GrouponRules>> restResponse = marketClient.getGrouponRules(Integer.valueOf(jsonData.get("goodsId").toString()));
            if (restResponse.getErrno() != RestEnum.OK.code) {
                log.error("GoodsController.detail方法错误 restResponse.getErrno() != RestEnum.OK.code，Errno为:" + restResponse.getErrno() + "错误信息为:" + restResponse.getErrmsg());
                //TODO 抛出异常
            }
            grouponRulesList = restResponse.getData();
        } catch (Exception e) {
            log.error("GoodsController.detail方法错误", e.getMessage());
        }


        // 用户收藏
        int userHasCollect = 0;
        if (jsonData.containsKey(TokenConstant.TOKEN)) {
            int userId = redisUtil.getUserId(jsonData.get(TokenConstant.TOKEN).toString());
            try {
                RestResponse<Integer> restResponse = userClient.getCollectCountNum(Integer.valueOf(jsonData.get("id").toString()), userId, TypeConstant.GOODS_TYPE);
                if (restResponse.getErrno() != RestEnum.OK.code) {
                    log.error("GoodsController.detail方法错误 restResponse.getErrno() != RestEnum.OK.code，Errno为:" + restResponse.getErrno() + "错误信息为:" + restResponse.getErrmsg());
                    //TODO 抛出异常
                }
                userHasCollect = restResponse.getData();
            } catch (Exception e) {
                log.error("GoodsController.detail方法错误", e.getMessage());
            }

        }

//        // 记录用户的足迹 异步处理
//        if (userId != null) {
//            executorService.execute(() -> {
//                Footprint footprint = new Footprint();
//                footprint.setUserId(userId);
//                footprint.setGoodsId(id);
//                footprintService.add(footprint);
//            });
//        }

        Map<String, Object> data = new HashMap<>();
        try {
            data.put("info", info);
            data.put("userHasCollect", userHasCollect);
            data.put("issue", issueList);
            data.put("comment", commentList);
            data.put("specificationList", goodsSpecificationVoList);
            data.put("productList", goodsProducts);
            data.put("attribute", goodsAttributeList);
            data.put("brand", brand);
            data.put("groupon", grouponRulesList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //商品分享图片地址
        data.put("shareImage", info.getShareUrl());
        return new RestResponse(data);
    }


    @GetMapping("/id")
    public RestResponse getById(Model model, JsonData jsonData) {
        List<Goods> info = goodsService.selective(jsonData);
        model.addAttribute("goods", info);
        return new RestResponse(model);
    }

    @GetMapping("/count")
    public RestResponse goodsCount(Model model) {
        int goodsCount = goodsService.queryOnSale();
        model.addAttribute("goodsCount", goodsCount);
        RestResponse restResponse = new RestResponse(model);
        return restResponse;
    }

    @GetMapping("/list")
    public RestResponse list(JsonData jsonData) {

//        //添加到搜索历史
//        if (userId != null && !StringUtils.isNullOrEmpty(keyword)) {
//            LitemallSearchHistory searchHistoryVo = new LitemallSearchHistory();
//            searchHistoryVo.setKeyword(keyword);
//            searchHistoryVo.setUserId(userId);
//            searchHistoryVo.setFrom("wx");
//            searchHistoryService.save(searchHistoryVo);
//        }

        //查询列表数据
        if (jsonData.containsKey("categoryId") && Integer.valueOf(jsonData.get("categoryId").toString()) == 0) {
            jsonData.remove("categoryId");
        }

        List<Goods> goodsList = goodsService.selective(jsonData);

        // 查询商品所属类目列表。
        List<Integer> categoryIdList = new ArrayList<>();
        for (Goods goods : goodsList) {
            if (!categoryIdList.contains(goods.getCategoryId()))
                categoryIdList.add(goods.getCategoryId());
        }
        List<Category> categoryList = new ArrayList<>();
        if (categoryIdList.size() != 0) {
            try {
                RestResponse<List<Category>> restResponse = marketClient.getCategoryList(categoryIdList);
                if (restResponse.getErrno() != RestEnum.OK.code) {
                    log.error("GoodsController.detail方法错误 restResponse.getErrno() != RestEnum.OK.code，Errno为:" + restResponse.getErrno() + "错误信息为:" + restResponse.getErrmsg());
                    //TODO 抛出异常
                }
                categoryList = restResponse.getData();
            } catch (Exception e) {
                log.error("GoodsController.list ERROR", e.getMessage());
            }
        } else {
            categoryList = new ArrayList<>(0);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("goodsList", goodsList);
        data.put("count", PageInfo.of(goodsList).getTotal());
        data.put("filterCategoryList", categoryList);

        return new RestResponse(data);
    }

    /**
     * 商品详情页面“大家都在看”推荐商品
     *
     * @param //id, 商品ID
     * @return 商品详情页面推荐商品
     */
    @GetMapping("/related")
    public RestResponse related(JsonData jsonData) {
        jsonData.put("goodsId", jsonData.get("id"));
        Goods goods = goodsService.selective(jsonData).get(0);
        if (goods == null) {
            return new RestResponse(RestEnum.BADARGUMENTVALUE);
        }

        // 目前的商品推荐算法仅仅是推荐同类目的其他商品
        // 查找六个相关商品
        jsonData.remove("goodsId");
        jsonData.put("page", 0);
        jsonData.put("size", 6);
        jsonData.put("categoryId", goods.getCategoryId());
        List<Goods> goodsList = goodsService.selective(jsonData);
        Map<String, Object> data = new HashMap<>();
        data.put("goodsList", goodsList);
        return new RestResponse(data);
    }

    @PostMapping("/getGoodsListByCategoryIdList")
    public RestResponse<List<Goods>> getGoodsListByCategoryIdList(JsonData jsonData, @RequestParam("categoryIdList") List<Integer> categoryIdList) {
        List<Goods> goodsList = new ArrayList<>();
        for (int categoryId : categoryIdList) {
            if (goodsList.size() == 4) continue;
            jsonData.put("categoryId", categoryId);
            List<Goods> list = goodsService.selective(jsonData);
            for (int i = 0; i < list.size(); i++) {
                goodsList.add(list.get(i));
            }

        }
        return new RestResponse<>(goodsList);
    }

    @GetMapping("/getGoodsList")
    public RestResponse<List<Goods>> getGoodsList(JsonData jsonData) {
        List<Goods> goodsList = goodsService.selective(jsonData);
        return new RestResponse(goodsList);
    }

    @GetMapping("/getGoodsById")
    public RestResponse<List<Goods>> getGoodsById(JsonData jsonData) {
        Goods goods = goodsService.selective(jsonData).get(0);
        return new RestResponse(goods);
    }

    @GetMapping("/getGoodsAll")
    public RestResponse<List<Goods>> getGoodsAll(JsonData jsonData) {
        return new RestResponse<>(goodsService.selective(jsonData));
    }

    /**
     * 查询商品
     *
     * @param /goodsSn
     * @param /name
     * @param /page
     * @param /limit
     * @param /sort
     * @param /order
     * @return
     */
    @GetMapping("/goodslist")
    public RestResponse Goodslist(JsonData jsonData) {
        List<Goods> goodsList = goodsService.selective(jsonData);
        long total = PageInfo.of(goodsList).getTotal();
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", goodsList);
        return new RestResponse<>(data);
    }

    /**
     * 编辑商品
     *
     * @param goodsAllinone
     * @return
     */
    @PostMapping("/update")
    public RestResponse update(@RequestBody GoodsAllinone goodsAllinone) {
        RestResponse restResponse = validate(goodsAllinone);
        if (restResponse != null) {
            return restResponse;
        }

        Goods goods = goodsAllinone.getGoods();
        GoodsAttribute[] attributes = goodsAllinone.getAttributes();
        GoodsSpecification[] specifications = goodsAllinone.getSpecifications();
        GoodsProduct[] products = goodsAllinone.getProducts();

        Integer id = goods.getId();
        // 检查是否存在购物车商品或者订单商品
        // 如果存在则拒绝修改商品。
//        if (orderGoodsService.checkExist(id)) {
//            return ResponseUtil.fail(GOODS_UPDATE_NOT_ALLOWED, "商品已经在订单中，不能修改");
//        }
//        if (cartService.checkExist(id)) {
//            return ResponseUtil.fail(GOODS_UPDATE_NOT_ALLOWED, "商品已经在购物车中，不能修改");
//        }
        return goodsService.update(goods,specifications,attributes,products);
    }

    /**
     * 删除商品
     *
     * @param goods
     * @return
     */
    @PostMapping("/delete")
    public Object delete(@RequestBody Goods goods) {
        Integer id = goods.getId();
        if (id == null) {
            return new RestResponse<>(RestEnum.BADARGUMENT);
        }
        return goodsService.delete(goods);
    }

    /**
     * 添加商品
     *
     * @param goodsAllinone
     * @return
     */
    @PostMapping("/create")
    public RestResponse create(@RequestBody GoodsAllinone goodsAllinone) {
        RestResponse restResponse = validate(goodsAllinone);
        if (restResponse != null) {
            return restResponse;
        }

        Goods goods = goodsAllinone.getGoods();
        GoodsAttribute[] attributes = goodsAllinone.getAttributes();
        GoodsSpecification[] specifications = goodsAllinone.getSpecifications();
        GoodsProduct[] products = goodsAllinone.getProducts();

        String name = goods.getName();
        if (goodsService.checkExistByName(name)) {
            return restResponse.error(GoodsEnum.GOODS_NAME_EXIST);
        }
        return goodsService.create(goods,attributes,specifications,products);
    }

    /**
     * 商品详情
     *
     * @param id
     * @return
     */
    @GetMapping("/goodsDetail")
    public RestResponse detail(@NotNull Integer id) {
        JsonData jsonData = new JsonData();
        jsonData.put("goodsId",id);
        Goods goods = goodsService.selective(jsonData).get(0);
        List<GoodsProduct> products = goodsProductService.queryByGid(id);
        List<GoodsSpecification> specifications = goodsSpecificationService.queryByGid(id);
        List<GoodsAttribute> attributes = goodsAttributeService.queryByGid(id);

        Integer categoryId = goods.getCategoryId();
        Category category = null;
        try {
            RestResponse<Category> restResponse = marketClient.getCategoryById(categoryId);
            if (restResponse.getErrno() != RestEnum.OK.code) {
                log.error("GoodsController.goodsDetail方法错误 restResponse.getErrno() != RestEnum.OK.code，Errno为:" + restResponse.getErrno() + "错误信息为:" + restResponse.getErrmsg());
                //TODO 抛出异常
            }
            category = restResponse.getData();
        }catch (Exception e){
            log.error("GoodsController.goodsDetail ERROR", e.getMessage());
        }

        Integer[] categoryIds = new Integer[]{};
        if (category != null) {
            Integer parentCategoryId = category.getPid();
            categoryIds = new Integer[]{parentCategoryId, categoryId};
        }

        Map<String, Object> data = new HashMap<>();
        data.put("goods", goods);
        data.put("specifications", specifications);
        data.put("products", products);
        data.put("attributes", attributes);
        data.put("categoryIds", categoryIds);

        return new RestResponse(data);

    }


    private RestResponse validate(GoodsAllinone goodsAllinone) {
        RestResponse restResponse = new RestResponse();
        Goods goods = goodsAllinone.getGoods();
        String name = goods.getName();
        if (StringUtils.isEmpty(name)) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }
        String goodsSn = goods.getGoodsSn();
        if (StringUtils.isEmpty(goodsSn)) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }
        // 品牌商可以不设置，如果设置则需要验证品牌商存在
        Integer brandId = goods.getBrandId();
        if (brandId != null && brandId != 0) {
            JsonData jsonData = new JsonData();
            jsonData.put("id", brandId);
            if (brandService.selective(jsonData).size() == 0) {
                return restResponse.error(RestEnum.BADARGUMENT);
            }
        }
        // 分类可以不设置，如果设置则需要验证分类存在
        Integer categoryId = goods.getCategoryId();
        if (categoryId != null && categoryId != 0) {
            Category category = null;
            try {
                RestResponse<Category> categoryRestResponse = marketClient.getCategoryById(categoryId);
                if (categoryRestResponse.getErrno() != RestEnum.OK.code) {
                    log.error("GoodsController.validate方法错误 categoryRestResponse.getErrno() != RestEnum.OK.code，Errno为:" + categoryRestResponse.getErrno() + "错误信息为:" + categoryRestResponse.getErrmsg());
                    //TODO 抛出异常
                }
                category = categoryRestResponse.getData();
            } catch (Exception e) {
                log.error("GoodsController.validate ERROR", e.getMessage());
            }
            if (category == null) {
                return restResponse.error(RestEnum.BADARGUMENT);
            }
        }

        GoodsAttribute[] attributes = goodsAllinone.getAttributes();
        for (GoodsAttribute attribute : attributes) {
            String attr = attribute.getAttribute();
            if (StringUtils.isEmpty(attr)) {
                return restResponse.error(RestEnum.BADARGUMENT);
            }
            String value = attribute.getValue();
            if (StringUtils.isEmpty(value)) {
                return restResponse.error(RestEnum.BADARGUMENT);
            }
        }

       GoodsSpecification[] specifications = goodsAllinone.getSpecifications();
        for (GoodsSpecification specification : specifications) {
            String spec = specification.getSpecification();
            if (StringUtils.isEmpty(spec)) {
                return restResponse.error(RestEnum.BADARGUMENT);
            }
            String value = specification.getValue();
            if (StringUtils.isEmpty(value)) {
                return restResponse.error(RestEnum.BADARGUMENT);
            }
        }

        GoodsProduct[] products = goodsAllinone.getProducts();
        for (GoodsProduct product : products) {
            Integer number = product.getNumber();
            if (number == null || number < 0) {
                return restResponse.error(RestEnum.BADARGUMENT);
            }

            BigDecimal price = product.getPrice();
            if (price == null) {
                return restResponse.error(RestEnum.BADARGUMENT);
            }

            String[] productSpecifications = product.getSpecifications();
            if (productSpecifications.length == 0) {
                return restResponse.error(RestEnum.BADARGUMENT);
            }
        }

        return null;
    }

}
