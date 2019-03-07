package com.example.user.controller;

import com.example.common.constants.TokenConstant;
import com.example.common.entity.Collect;
import com.example.common.entity.Goods;
import com.example.common.enums.RestEnum;
import com.example.common.feign.GoodsClient;
import com.example.common.response.RestResponse;
import com.example.common.util.JacksonUtil;
import com.example.common.util.JsonData;
import com.example.common.util.RedisUtil;
import com.example.user.service.CollectService;
import jdk.nashorn.internal.parser.Token;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class CollectController {
    @Autowired
    private CollectService collectService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private GoodsClient goodsClient;

    @GetMapping("/getCollectCountNum")
    public RestResponse<Integer> getCollectCountNum(JsonData jsonData) {
        Integer num = collectService.countive(jsonData);
        return new RestResponse(num);
    }

    /**
     * 用户收藏列表
     *
     * @param //userId 用户ID
     * @param //type   类型，如果是0则是商品收藏，如果是1则是专题收藏
     * @param //page   分页页数
     * @param //size   分页大小
     * @return 用户收藏列表
     */
    @GetMapping("/collect/list")
    public RestResponse list(JsonData jsonData) {
        RestResponse restResponse = new RestResponse();

        if (!jsonData.containsKey(TokenConstant.TOKEN)) {
            return restResponse.error(RestEnum.UNLOGIN);
        }
        int userId = redisUtil.getUserId(jsonData.get(TokenConstant.TOKEN).toString());
        int size = Integer.valueOf(jsonData.get("size").toString());
        jsonData.put("userId", userId);
        jsonData.put("deleted", 0);
        List<Collect> collectList = collectService.selective(jsonData);
        jsonData.remove("page");
        jsonData.remove("size");
        int count = collectService.selective(jsonData).size();
        int totalPages = (int) Math.ceil((double) count / size);

        List<Object> collects = new ArrayList<>(collectList.size());
        for (Collect collect : collectList) {
            Map<String, Object> c = new HashMap<String, Object>();
            c.put("id", collect.getId());
            c.put("type", collect.getType());
            c.put("valueId", collect.getValueId());
            Goods goods = new Goods();
            try {
                RestResponse<Goods> goodRestResponse = goodsClient.getSingleGoods(collect.getValueId(), 1, 0);
                if (goodRestResponse.getErrno() != RestEnum.OK.code) {
                    log.error("CollectController.list方法错误 goodRestResponse.getErrno() != RestEnum.OK.code，Errno为:" + goodRestResponse.getErrno() + "错误信息为:" + goodRestResponse.getErrmsg());
                    //TODO 抛出异常
                }
                goods = goodRestResponse.getData();
                c.put("name", goods.getName());
                c.put("brief", goods.getBrief());
                c.put("picUrl", goods.getPicUrl());
                c.put("retailPrice", goods.getRetailPrice());
            }catch (Exception e){
                log.error("CollectController.list ERROR", e.getMessage());
            }
            collects.add(c);
        }
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("collectList", collects);
        result.put("totalPages", totalPages);
        return restResponse.success(result);
    }

    /**
     * 用户收藏添加或删除
     * <p>
     * 如果商品没有收藏，则添加收藏；如果商品已经收藏，则删除收藏状态。
     *
     * @param //userId 用户ID
     * @param body   请求内容，{ type: xxx, valueId: xxx }
     * @return 操作结果
     */
    @PostMapping("/collect/addordelete")
    public Object addordelete(@RequestBody String body, HttpServletRequest request) {
        JsonData jsonData = new JsonData(request);
        RestResponse restResponse = new RestResponse();
        if (!jsonData.containsKey(TokenConstant.TOKEN)) {
            return restResponse.error(RestEnum.UNLOGIN);
        }

        Byte type = JacksonUtil.parseByte(body, "type");
        Integer valueId = JacksonUtil.parseInteger(body, "valueId");
        if (!ObjectUtils.allNotNull(type, valueId)) {
            return restResponse.error(RestEnum.UNLOGIN);
        }
        int userId = redisUtil.getUserId(jsonData.get(TokenConstant.TOKEN).toString());
        jsonData.put("userId",userId);
        jsonData.put("type",type);
        jsonData.put("valueId",valueId);
        jsonData.put("deleted",0);
        List<Collect> collectList = collectService.selective(jsonData);

        String handleType = null;
        if (collectList.size() != 0) {
            handleType = "delete";
            JsonData deleted = new JsonData();
            deleted.put("id",collectList.get(0).getId());
            collectService.deletive(deleted);
        } else {
            handleType = "add";
            JsonData insertive = new JsonData();
            insertive.put("userId",userId);
            insertive.put("valueId",valueId);
            insertive.put("type",type);
            insertive.put("addTime", LocalDateTime.now());
            insertive.put("updateTime",LocalDateTime.now());
            collectService.insertive(insertive);
        }
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("type", handleType);
        return restResponse.success(data);
    }
}
