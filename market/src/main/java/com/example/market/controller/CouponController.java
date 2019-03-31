package com.example.market.controller;

import com.example.common.constants.CouponConstant;
import com.example.common.constants.CouponUserConstant;
import com.example.common.constants.TokenConstant;
import com.example.common.entity.*;
import com.example.common.enums.CouponEnum;
import com.example.common.enums.RestEnum;
import com.example.common.feign.UserClient;
import com.example.common.response.RestResponse;
import com.example.common.util.JacksonUtil;
import com.example.common.util.JsonData;
import com.example.common.util.RedisUtil;
import com.example.market.service.CartService;
import com.example.market.service.CouponService;
import com.example.market.service.GrouponRulesService;
import com.example.market.service.Impl.CouponVerifyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.xml.bind.util.JAXBSource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class CouponController {

    @Autowired
    private CouponService couponService;

    @Autowired
    private UserClient userClient;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private GrouponRulesService grouponRulesService;

    @Autowired
    private CartService cartService;

    @Autowired
    private CouponVerifyService couponVerifyService;

    /**
     * 优惠券领取
     *
     * @param //userId 用户ID
     * @param body     请求内容， { couponId: xxx }
     * @return 操作结果
     */
    @PostMapping("receive")
    public RestResponse receive(@RequestBody String body, HttpServletRequest request) {
        RestResponse restResponse = new RestResponse();
        JsonData jsonData = new JsonData(request);
        if (!jsonData.containsKey(TokenConstant.TOKEN)) {
            return restResponse.error(RestEnum.UNLOGIN);
        }
        int userId = redisUtil.getUserId(jsonData.get(TokenConstant.TOKEN).toString());
        Integer couponId = JacksonUtil.parseInteger(body, "couponId");
        if (couponId == null) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }

        jsonData.put("couponId", couponId);
        Coupon coupon = couponService.selective(jsonData).get(0);
        if (coupon == null) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }

        // 当前已领取数量和总数量比较
        Integer total = coupon.getTotal();
        Integer totalCoupons = 0;
        Integer userCounpons = 0;
        try {
            RestResponse<List<CouponUser>> userRestResponse = userClient.getCouponUser(couponId, 0);
            RestResponse<List<CouponUser>> userRestResponse1 = userClient.getCouponUser(couponId, 0, userId);
            if (userRestResponse.getErrno() != RestEnum.OK.code) {
                log.error("CouponController.receive方法错误 userRestResponse.getErrno() != RestEnum.OK.code，Errno为:" + userRestResponse.getErrno() + "错误信息为:" + userRestResponse.getErrmsg());
                //TODO 抛出异常
            }
            if (userRestResponse1.getErrno() != RestEnum.OK.code) {
                log.error("CouponController.receive方法错误 userRestResponse1.getErrno() != RestEnum.OK.code，Errno为:" + userRestResponse1.getErrno() + "错误信息为:" + userRestResponse1.getErrmsg());
                //TODO 抛出异常
            }
            totalCoupons = userRestResponse.getData().size();
            userCounpons = userRestResponse1.getData().size();
        } catch (Exception e) {
            log.error("CouponController.receive方法错误", e.getMessage());
        }

        if ((total != 0) && (totalCoupons >= total)) {
            return restResponse.error(CouponEnum.COUPON_NUM_LIMIT);
        }

        // 当前用户已领取数量和用户限领数量比较
        Integer limit = coupon.getLimit().intValue();

        if ((limit != 0) && (userCounpons >= limit)) {
            return restResponse.error(CouponEnum.COUPON_EXCEED_LIMIT);
        }

        // 优惠券分发类型
        // 例如注册赠券类型的优惠券不能领取
        Short type = coupon.getType();
        if (type.equals(CouponConstant.TYPE_REGISTER)) {
            return restResponse.error(CouponEnum.COUPON_AUTO_SEND);
        } else if (type.equals(CouponConstant.TYPE_CODE)) {
            return restResponse.error(CouponEnum.COUPON_EXCHANGE_FAIL);
        } else if (!type.equals(CouponConstant.TYPE_COMMON)) {
            return restResponse.error(CouponEnum.COUPON_TYPE_FAIL);
        }

        // 优惠券状态，已下架或者过期不能领取
        Short status = coupon.getStatus();
        if (status.equals(CouponConstant.STATUS_OUT)) {
            return restResponse.error(CouponEnum.COUPON_NUM_LIMIT);
        } else if (status.equals(CouponConstant.STATUS_EXPIRED)) {
            return restResponse.error(CouponEnum.COUPON_TIME_FAIL);
        }

        // 用户领券记录
        Short timeType = coupon.getTimeType();
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        if (timeType.equals(CouponConstant.TIME_TYPE_TIME)) {
            startTime = coupon.getStartTime();
            endTime = coupon.getEndTime();
        } else {
            LocalDateTime now = LocalDateTime.now();
            startTime = now;
            endTime = now.plusDays(coupon.getDays());
        }
        try {
            RestResponse<Integer> addRestResponse = userClient.addCouponUser(userId, couponId, 0, null, startTime, endTime, null, LocalDateTime.now(), LocalDateTime.now(), 0);
            if (addRestResponse.getData() == 0 && addRestResponse.getErrno() != RestEnum.OK.code) {
                log.error("CouponController.receive方法错误 addRestResponse.getErrno() != RestEnum.OK.code，Errno为:" + addRestResponse.getErrno() + "错误信息为:" + addRestResponse.getErrmsg());
                //TODO 抛出异常
            }
        } catch (Exception e) {
            log.error("CouponController.receive方法错误", e.getMessage());
        }


        return restResponse;
    }

    @Bean
    public ObjectMapper serializingObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    /**
     * 个人优惠券列表
     *
     * @param //userId
     * @param //status
     * @param //page
     * @param //size
     * @param //sort
     * @param //order
     * @return
     */
    @GetMapping("/coupon/mylist")
    public RestResponse mylist(JsonData jsonData) {
        RestResponse restResponse = new RestResponse();
        if (!jsonData.containsKey(TokenConstant.TOKEN)) {
            return restResponse.error(RestEnum.UNLOGIN);
        }
        int userId = redisUtil.getUserId(jsonData.get(TokenConstant.TOKEN).toString());
        List<CouponUser> couponUserList = null;
        try {
            RestResponse<List<CouponUser>> userRestResponse = userClient.getCouponUser(userId, jsonData.get("status").toString(),0);
            if (userRestResponse.getErrno() != RestEnum.OK.code) {
                log.error("CouponController.mylist方法错误 userRestResponse.getErrno() != RestEnum.OK.code，Errno为:" + userRestResponse.getErrno() + "错误信息为:" + userRestResponse.getErrmsg());
                //TODO 抛出异常
            }
            couponUserList = userRestResponse.getData();
        } catch (Exception e) {
            log.error("CouponController.mylist方法错误", e.getMessage());
        }
        List<CouponVo> couponVoList = change(couponUserList);
        JsonData selective = new JsonData();
        selective.put("couponType",CouponConstant.TYPE_COMMON);
        selective.put("couponStatus",CouponConstant.STATUS_NORMAL);
        selective.put("couponDeleted",0);
        int total = couponService.selective(selective).size();
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("data", couponVoList);
        data.put("count", total);
        return restResponse.success(data);
    }

    private List<CouponVo> change(List<CouponUser> couponList) {
        List<CouponVo> couponVoList = new ArrayList<>(couponList.size());
        JsonData jsonData = new JsonData();
        for (CouponUser couponUser : couponList) {
            Integer couponId = couponUser.getCouponId();
            jsonData.put("couponId", couponId);
            Coupon coupon = couponService.selective(jsonData).get(0);
            CouponVo couponVo = new CouponVo();
            couponVo.setId(coupon.getId());
            couponVo.setName(coupon.getName());
            couponVo.setDesc(coupon.getDesc());
            couponVo.setTag(coupon.getTag());
            couponVo.setMin(coupon.getMin().toPlainString());
            couponVo.setDiscount(coupon.getDiscount().toPlainString());
            couponVo.setStartTime(couponUser.getStartTime());
            couponVo.setEndTime(couponUser.getEndTime());

            couponVoList.add(couponVo);
        }

        return couponVoList;
    }

    /**
     * 当前购物车下单商品订单可用优惠券
     *
     * @param //userId
     * @param //cartId
     * @param //grouponRulesId
     * @return
     */
    @GetMapping("/coupon/selectlist")
    public Object selectlist(JsonData jsonData) {
        RestResponse restResponse = new RestResponse();
        if (!jsonData.containsKey(TokenConstant.TOKEN)) {
            return restResponse.error(RestEnum.UNLOGIN);
        }
        Integer userId = redisUtil.getUserId(jsonData.get(TokenConstant.TOKEN).toString());
        // 团购优惠
        BigDecimal grouponPrice = new BigDecimal(0.00);
        JsonData jsonData1 = new JsonData();
        jsonData1.put("id",jsonData.get("grouponRulesId"));
        jsonData.put("deleted",0);
        List<GrouponRules> grouponRulesList = grouponRulesService.selective(jsonData1);
        if (grouponRulesList.size() != 0) {
            grouponPrice = grouponRulesList.get(0).getDiscount();
        }

        // 商品价格
        List<Cart> checkedGoodsList = null;
        Integer cartId = Integer.valueOf(jsonData.get("cartId").toString());
        if (cartId == null || cartId.equals(0)) {
            JsonData jsonData2 = new JsonData();
            jsonData2.put("userId",userId);
            jsonData2.put("checked",1);
            jsonData2.put("deleted",0);
            checkedGoodsList = cartService.selective(jsonData2);
        } else {
            JsonData jsonData2 = new JsonData();
            jsonData2.put("cartId",cartId);
            Cart cart = cartService.selective(jsonData2).get(0);
            if (cart == null) {
                return restResponse.error(RestEnum.BADARGUMENT);
            }
            checkedGoodsList = new ArrayList<>(1);
            checkedGoodsList.add(cart);
        }
        BigDecimal checkedGoodsPrice = new BigDecimal(0.00);
        for (Cart cart : checkedGoodsList) {
            //  只有当团购规格商品ID符合才进行团购优惠
            if (grouponRulesList.size() != 0 && grouponRulesList.get(0).getGoodsId().equals(cart.getGoodsId())) {
                checkedGoodsPrice = checkedGoodsPrice.add(cart.getPrice().subtract(grouponPrice).multiply(new BigDecimal(cart.getNumber())));
            } else {
                checkedGoodsPrice = checkedGoodsPrice.add(cart.getPrice().multiply(new BigDecimal(cart.getNumber())));
            }
        }

        // 计算优惠券可用情况
        List<CouponUser> couponUserList = null;
        try {
            RestResponse<List<CouponUser>> couponUserRestRespone = userClient.getCouponUser(userId, CouponUserConstant.STATUS_USABLE);
            if (couponUserRestRespone.getErrno() != RestEnum.OK.code) {
                log.error("couponController.selectlist错误 goodsRestResponse.getErrno() != RestEnum.OK.code，Errno为:" + couponUserRestRespone.getErrno() + "错误信息为:" + couponUserRestRespone.getErrmsg());
                //TODO 抛出异常
            }
            couponUserList = couponUserRestRespone.getData();
        }catch (Exception e){
            log.error("couponController.selectlist错误",e.getMessage());
        }
        List<CouponUser> availableCouponUserList = new ArrayList<>(couponUserList.size());
        for (CouponUser couponUser : couponUserList) {
            Coupon coupon = couponVerifyService.checkCoupon(userId, couponUser.getCouponId(), checkedGoodsPrice);
            if (coupon == null) {
                continue;
            }
            availableCouponUserList.add(couponUser);
        }

        List<CouponVo> couponVoList = change(availableCouponUserList);

        return restResponse.success(couponVoList);
    }

    @GetMapping("/getCouponVerify")
    public RestResponse getCouponVerify(Integer userId, Integer couponId, BigDecimal checkedGoodsPrice){
        return new RestResponse(couponVerifyService.checkCoupon(userId, couponId, checkedGoodsPrice));
    }


    @GetMapping("coupon/list")
    public Object list(JsonData jsonData) {
        jsonData.put("deleted",0);
        jsonData.put("couponStatus",jsonData.get("status"));
        jsonData.put("couponType",jsonData.get("type"));
        List<Coupon> couponList = couponService.selective(jsonData);
        long total = PageInfo.of(couponList).getTotal();
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", couponList);
        return new RestResponse<>(data);
    }

    @PostMapping("coupon/create")
    public RestResponse create(@RequestBody Coupon coupon) {
        RestResponse restResponse = validate(coupon);
        if (restResponse != null) {
            return restResponse;
        }

        // 如果是兑换码类型，则这里需要生存一个兑换码
        if (coupon.getType().equals(CouponConstant.TYPE_CODE)){
            String code = couponService.generateCode();
            coupon.setCode(code);
        }

        couponService.insert(coupon);
        return new RestResponse(coupon);
    }

    @GetMapping("coupon/read")
    public RestResponse read(@NotNull Integer id) {
        JsonData jsonData = new JsonData();
        jsonData.put("couponId",id);
        Coupon coupon = couponService.selective(jsonData).get(0);
        return new RestResponse(coupon);
    }

    @PostMapping("coupon/update")
    public Object update(@RequestBody Coupon coupon) {
        Object error = validate(coupon);
        if (error != null) {
            return error;
        }
        if (couponService.updateById(coupon) == 0) {
            return new RestResponse<>(RestEnum.UPDATEDDATAFAILED);
        }
        return new RestResponse<>(coupon);
    }

    @PostMapping("coupon/delete")
    public Object delete(@RequestBody Coupon coupon) {
        if(couponService.deleteById(coupon.getId()) == 0) return new RestResponse<>(RestEnum.SERIOUS);
        return new RestResponse<>();
    }

    private RestResponse validate(Coupon coupon) {
        String name = coupon.getName();
        if(StringUtils.isEmpty(name)){
            return new RestResponse(RestEnum.BADARGUMENT);
        }
        return null;
    }
}
