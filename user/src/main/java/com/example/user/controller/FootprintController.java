package com.example.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.common.constants.TokenConstant;
import com.example.common.entity.Footprint;
import com.example.common.entity.Goods;
import com.example.common.enums.RestEnum;
import com.example.common.feign.GoodsClient;
import com.example.common.response.RestResponse;
import com.example.common.util.JacksonUtil;
import com.example.common.util.JsonData;
import com.example.common.util.RedisUtil;
import com.example.user.service.FootprintService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/footprint")
public class FootprintController {

    @Autowired
    private FootprintService footprintService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private GoodsClient goodsClient;

    /**
     * 删除用户足迹
     *
     * @param //userId 用户ID
     * @param body   请求内容， { id: xxx }
     * @return 删除操作结果
     */
    @PostMapping("delete")
    public RestResponse delete(@RequestBody String body, HttpServletRequest request) {
        RestResponse restResponse = new RestResponse();
        JsonData jsonData = new JsonData(request);
        if (!jsonData.containsKey(TokenConstant.TOKEN)) {
            return restResponse.error(RestEnum.UNLOGIN);
        }
        if (body == null) {
            return  restResponse.error(RestEnum.UNLOGIN);
        }
        int userId = redisUtil.getUserId(jsonData.get(TokenConstant.TOKEN).toString());
        Integer footprintId = JacksonUtil.parseInteger(body, "id");
        if (footprintId == null) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }
        jsonData.put("footprintId",footprintId);
        Footprint footprint = footprintService.selective(jsonData).get(0);

        if (footprint == null) {
            return  restResponse.error(RestEnum.BADARGUMENT);
        }

        if (!footprint.getUserId().equals(userId)) {
            return  restResponse.error(RestEnum.BADARGUMENT);
        }

        footprintService.deletive(jsonData);
        return restResponse;
    }

    /**
     * 用户足迹列表
     *
     * @param //page 分页页数
     * @param //size 分页大小
     * @return 用户足迹列表
     */
    @GetMapping("/list")
    public RestResponse list(JsonData jsonData) {
        RestResponse restResponse = new RestResponse();
        if (!jsonData.containsKey(TokenConstant.TOKEN)) {
            return restResponse.error(RestEnum.UNLOGIN);
        }
        int userId = redisUtil.getUserId(jsonData.get(TokenConstant.TOKEN).toString());
        jsonData.put("userId",userId);
        jsonData.put("deleted",0);
        jsonData.put("order","DESC");
        jsonData.put("sort","add_time");
        List<Footprint> footprintList = footprintService.selective(jsonData);
        long count = PageInfo.of(footprintList).getTotal();
        int totalPages = (int) Math.ceil((double) count / Integer.valueOf(jsonData.get("size").toString()));

        List<Object> footprintVoList = new ArrayList<>(footprintList.size());
        for (Footprint footprint : footprintList) {
            Map<String, Object> c = new HashMap<String, Object>();
            c.put("id", footprint.getId());
            c.put("goodsId", footprint.getGoodsId());
            c.put("addTime", footprint.getAddTime());
            Goods goods = new Goods();
            try {
                RestResponse<Goods> goodRestResponse = goodsClient.getSingleGoods(footprint.getGoodsId(), 1, 0);
                if (goodRestResponse.getErrno() != RestEnum.OK.code) {
                    log.error("FootprintController.list方法错误 goodRestResponse.getErrno() != RestEnum.OK.code，Errno为:" + goodRestResponse.getErrno() + "错误信息为:" + goodRestResponse.getErrmsg());
                    //TODO 抛出异常
                }
                goods = goodRestResponse.getData();
                c.put("name", goods.getName());
                c.put("brief", goods.getBrief());
                c.put("picUrl", goods.getPicUrl());
                c.put("retailPrice", goods.getRetailPrice());
            }catch (Exception e){
                log.error("FootprintController.list ERROR", e.getMessage());
            }

            footprintVoList.add(c);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("footprintList", footprintVoList);
        result.put("totalPages", totalPages);
        return restResponse.success(result);
    }
}
