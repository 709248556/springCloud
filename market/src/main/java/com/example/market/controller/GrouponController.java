package com.example.market.controller;

import com.example.common.constants.TokenConstant;
import com.example.common.entity.Groupon;
import com.example.common.entity.GrouponRules;
import com.example.common.enums.RestEnum;
import com.example.common.response.RestResponse;
import com.example.common.util.JsonData;
import com.example.market.service.GrouponRulesService;
import com.example.market.service.GrouponService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class GrouponController {

    @Autowired
    private GrouponService grouponService;

    @Autowired
    private GrouponRulesService grouponRulesService;

    @GetMapping("/getGrouponByOrderId")
    public RestResponse<List<Groupon>> getGrouponByOrderId(JsonData jsonData) {
        return new RestResponse(grouponService.selective(jsonData));
    }


//    /**
//     * 团购活动详情
//     *
//     * @param userId    用户ID
//     * @param grouponId 团购活动ID
//     * @return 团购活动详情
//     */
//    @GetMapping("/groupon/detail")
//    public Object detail(JsonData jsonData) {
//        RestResponse restResponse = new RestResponse();
//        if (jsonData.containsKey(TokenConstant.TOKEN)) {
//            return restResponse.error(RestEnum.UNLOGIN);
//        }
//        jsonData.put()
//        Groupon groupon = grouponService.selective(jsonData).get(0);
//        if (groupon == null) {
//            return restResponse.error(RestEnum.BADARGUMENT);
//        }
//
//        GrouponRules rules = grouponRulesService.selective(groupon.getRulesId());
//        if (rules == null) {
//            return ResponseUtil.badArgumentValue();
//        }
//
//        // 订单信息
//        LitemallOrder order = orderService.findById(groupon.getOrderId());
//        if (null == order) {
//            return ResponseUtil.fail(ORDER_UNKNOWN, "订单不存在");
//        }
//        if (!map.getUserId().equals(userId)) {
//            return ResponseUtil.fail(ORDER_INVALID, "不是当前用户的订单");
//        }
//        Map<String, Object> orderVo = new HashMap<String, Object>();
//        orderVo.put("id", map.getId());
//        orderVo.put("orderSn", map.getOrderSn());
//        orderVo.put("addTime", map.getAddTime());
//        orderVo.put("consignee", map.getConsignee());
//        orderVo.put("mobile", map.getMobile());
//        orderVo.put("address", map.getAddress());
//        orderVo.put("goodsPrice", map.getGoodsPrice());
//        orderVo.put("freightPrice", map.getFreightPrice());
//        orderVo.put("actualPrice", map.getActualPrice());
//        orderVo.put("orderStatusText", OrderUtil.orderStatusText(order));
//        orderVo.put("handleOption", OrderUtil.build(order));
//        orderVo.put("expCode", map.getShipChannel());
//        orderVo.put("expNo", map.getShipSn());
//
//        List<LitemallOrderGoods> orderGoodsList = orderGoodsService.queryByOid(map.getId());
//        List<Map<String, Object>> orderGoodsVoList = new ArrayList<>(orderGoodsList.size());
//        for (LitemallOrderGoods orderGoods : orderGoodsList) {
//            Map<String, Object> orderGoodsVo = new HashMap<>();
//            orderGoodsVo.put("id", orderGoods.getId());
//            orderGoodsVo.put("orderId", orderGoods.getOrderId());
//            orderGoodsVo.put("goodsId", orderGoods.getGoodsId());
//            orderGoodsVo.put("goodsName", orderGoods.getGoodsName());
//            orderGoodsVo.put("number", orderGoods.getNumber());
//            orderGoodsVo.put("retailPrice", orderGoods.getPrice());
//            orderGoodsVo.put("picUrl", orderGoods.getPicUrl());
//            orderGoodsVo.put("goodsSpecificationValues", orderGoods.getSpecifications());
//            orderGoodsVoList.add(orderGoodsVo);
//        }
//
//        Map<String, Object> result = new HashMap<>();
//        result.put("orderInfo", orderVo);
//        result.put("orderGoods", orderGoodsVoList);
//
//        // 订单状态为已发货且物流信息不为空
//        //"YTO", "800669400640887922"
//        if (map.getOrderStatus().equals(OrderUtil.STATUS_SHIP)) {
//            ExpressInfo ei = expressService.getExpressInfo(map.getShipChannel(), map.getShipSn());
//            result.put("expressInfo", ei);
//        }
//
//        UserVo creator = userService.findUserVoById(groupon.getCreatorUserId());
//        List<UserVo> joiners = new ArrayList<>();
//        joiners.add(creator);
//        int linkGrouponId;
//        // 这是一个团购发起记录
//        if (groupon.getGrouponId() == 0) {
//            linkGrouponId = groupon.getId();
//        } else {
//            linkGrouponId = groupon.getGrouponId();
//
//        }
//        List<LitemallGroupon> groupons = grouponService.queryJoinRecord(linkGrouponId);
//
//        UserVo joiner;
//        for (LitemallGroupon grouponItem : groupons) {
//            joiner = userService.findUserVoById(grouponItem.getUserId());
//            joiners.add(joiner);
//        }
//
//        result.put("linkGrouponId", linkGrouponId);
//        result.put("creator", creator);
//        result.put("joiners", joiners);
//        result.put("groupon", groupon);
//        result.put("rules", rules);
//        return ResponseUtil.ok(result);
//    }
//
//    /**
//     * 参加团购
//     *
//     * @param grouponId 团购活动ID
//     * @return 操作结果
//     */
//    @GetMapping("/groupon/join")
//    public Object join(@NotNull Integer grouponId) {
//        LitemallGroupon groupon = grouponService.queryById(grouponId);
//        if (groupon == null) {
//            return ResponseUtil.badArgumentValue();
//        }
//
//        LitemallGrouponRules rules = rulesService.queryById(groupon.getRulesId());
//        if (rules == null) {
//            return ResponseUtil.badArgumentValue();
//        }
//
//        LitemallGoods goods = goodsService.findById(rules.getGoodsId());
//        if (goods == null) {
//            return ResponseUtil.badArgumentValue();
//        }
//
//        Map<String, Object> result = new HashMap<>();
//        result.put("groupon", groupon);
//        result.put("goods", goods);
//
//        return ResponseUtil.ok(result);
//    }
//
//    /**
//     * 用户开团或入团情况
//     *
//     * @param userId 用户ID
//     * @param showType 显示类型，如果是0，则是当前用户开的团购；否则，则是当前用户参加的团购
//     * @return 用户开团或入团情况
//     */
//    @GetMapping("/groupon/my")
//    public Object my(@LoginUser Integer userId, @RequestParam(defaultValue = "0") Integer showType) {
//        if (userId == null) {
//            return ResponseUtil.unlogin();
//        }
//
//        List<LitemallGroupon> myGroupons;
//        if (showType == 0) {
//            myGroupons = grouponService.queryMyGroupon(userId);
//        } else {
//            myGroupons = grouponService.queryMyJoinGroupon(userId);
//        }
//
//        List<Map<String, Object>> grouponVoList = new ArrayList<>(myGroupons.size());
//
//        LitemallOrder order;
//        LitemallGrouponRules rules;
//        LitemallUser creator;
//        for (LitemallGroupon groupon : myGroupons) {
//            order = orderService.findById(groupon.getOrderId());
//            rules = rulesService.queryById(groupon.getRulesId());
//            creator = userService.findById(groupon.getCreatorUserId());
//
//            Map<String, Object> grouponVo = new HashMap<>();
//            //填充团购信息
//            grouponVo.put("id", groupon.getId());
//            grouponVo.put("groupon", groupon);
//            grouponVo.put("rules", rules);
//            grouponVo.put("creator", creator.getNickname());
//
//            int linkGrouponId;
//            // 这是一个团购发起记录
//            if (groupon.getGrouponId() == 0) {
//                linkGrouponId = groupon.getId();
//                grouponVo.put("isCreator", creator.getId() == userId);
//            } else {
//                linkGrouponId = groupon.getGrouponId();
//                grouponVo.put("isCreator", false);
//            }
//            int joinerCount = grouponService.countGroupon(linkGrouponId);
//            grouponVo.put("joinerCount", joinerCount + 1);
//
//            //填充订单信息
//            grouponVo.put("orderId", map.getId());
//            grouponVo.put("orderSn", map.getOrderSn());
//            grouponVo.put("actualPrice", map.getActualPrice());
//            grouponVo.put("orderStatusText", OrderUtil.orderStatusText(order));
//            grouponVo.put("handleOption", OrderUtil.build(order));
//
//            List<LitemallOrderGoods> orderGoodsList = orderGoodsService.queryByOid(map.getId());
//            List<Map<String, Object>> orderGoodsVoList = new ArrayList<>(orderGoodsList.size());
//            for (LitemallOrderGoods orderGoods : orderGoodsList) {
//                Map<String, Object> orderGoodsVo = new HashMap<>();
//                orderGoodsVo.put("id", orderGoods.getId());
//                orderGoodsVo.put("goodsName", orderGoods.getGoodsName());
//                orderGoodsVo.put("number", orderGoods.getNumber());
//                orderGoodsVo.put("picUrl", orderGoods.getPicUrl());
//                orderGoodsVoList.add(orderGoodsVo);
//            }
//            grouponVo.put("goodsList", orderGoodsVoList);
//            grouponVoList.add(grouponVo);
//        }
//
//        Map<String, Object> result = new HashMap<>();
//        result.put("count", grouponVoList.size());
//        result.put("data", grouponVoList);
//
//        return ResponseUtil.ok(result);
//    }
//
//    /**
//     * 商品所对应的团购规则
//     *
//     * @param goodsId 商品ID
//     * @return 团购规则详情
//     */
//    @GetMapping("/groupon/query")
//    public Object query(@NotNull Integer goodsId) {
//        LitemallGoods goods = goodsService.findById(goodsId);
//        if (goods == null) {
//            return ResponseUtil.fail(GOODS_UNKNOWN, "未找到对应的商品");
//        }
//
//        List<LitemallGrouponRules> rules = rulesService.queryByGoodsId(goodsId);
//
//        return ResponseUtil.ok(rules);
//    }
}
