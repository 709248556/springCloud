package com.example.market.controller;

import com.example.common.entity.Goods;
import com.example.common.entity.GoodsProduct;
import com.example.common.entity.Order;
import com.example.common.entity.User;
import com.example.common.enums.RestEnum;
import com.example.common.feign.GoodsClient;
import com.example.common.feign.UserClient;
import com.example.common.response.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class DashboardController {
    @Autowired
    private UserClient userClient;
    @Autowired
    private GoodsClient goodsClient;

    @GetMapping("dashboard")
    public RestResponse dashboard(Model model) {
        try {
            RestResponse<List<User>> userRestRespone = userClient.getUserAll(0);
            RestResponse<List<Goods>> goodsRestRespone = goodsClient.getGoodsAll(0);
            RestResponse<List<GoodsProduct>> goodsProductRestRespone = goodsClient.getGoodsProductAll(0);
            RestResponse<List<Order>> orderRestRespone = userClient.getOrderAll(0);

            if (userRestRespone.getErrno() != RestEnum.OK.code) {
                log.error("DashboardController.dashboard方法错误 userRestRespone.getErrno() != RestEnum.OK.code，Errno为:" + userRestRespone.getErrno() + "错误信息为:" + userRestRespone.getErrmsg());
                //TODO 抛出异常
            }
            if (goodsRestRespone.getErrno() != RestEnum.OK.code) {
                log.error("DashboardController.dashboard方法错误 goodsRestRespone.getErrno() != RestEnum.OK.code，Errno为:" + goodsRestRespone.getErrno() + "错误信息为:" + goodsRestRespone.getErrmsg());
                //TODO 抛出异常
            }
            if (goodsProductRestRespone.getErrno() != RestEnum.OK.code) {
                log.error("DashboardController.dashboard方法错误 goodsProductRestRespone.getErrno() != RestEnum.OK.code，Errno为:" + goodsProductRestRespone.getErrno() + "错误信息为:" + goodsProductRestRespone.getErrmsg());
                //TODO 抛出异常
            }
            if (orderRestRespone.getErrno() != RestEnum.OK.code) {
                log.error("DashboardController.dashboard方法错误 orderRestRespone.getErrno() != RestEnum.OK.code，Errno为:" + orderRestRespone.getErrno() + "错误信息为:" + orderRestRespone.getErrmsg());
                //TODO 抛出异常
            }

            model.addAttribute("userTotal",userRestRespone.getData().size());
            model.addAttribute("goodsTotal",goodsRestRespone.getData().size());
            model.addAttribute("productTotal",goodsProductRestRespone.getData().size());
            model.addAttribute("orderTotal",orderRestRespone.getData().size());
        }catch (Exception e){
            log.error("DashboardController.dashboard ERROR", e.getMessage());
        }
        return new RestResponse(model);
    }
}
