package com.example.common.feign;

import com.example.common.entity.Comment;
import com.example.common.entity.CouponUser;
import com.example.common.entity.User;
import com.example.common.hystrix.UserFallback;
import com.example.common.response.RestResponse;
import com.example.common.util.JsonData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@FeignClient(value = "user",fallback = UserFallback.class)
public interface UserClient {

    @GetMapping("/getCommentList")
    RestResponse<List<Comment>> getCommentList(@RequestParam("goodsId") int goodsId,@RequestParam("commentType") int commentType);

    @GetMapping("/getCollectCountNum")
    RestResponse<Integer> getCollectCountNum(@RequestParam("goodsId") int goodsId,@RequestParam("userId") int userId,@RequestParam("deleted") int deleted);

    @GetMapping("/getUser")
    RestResponse<List<User>> getUser(@RequestParam("userId") int userId);

    @GetMapping("/getCouponUser")
    RestResponse<List<CouponUser>> getCouponUser(@RequestParam("couponId") int couponId,@RequestParam("deleted") int deleted);

    @GetMapping("/getCouponUser")
    RestResponse<List<CouponUser>> getCouponUser(@RequestParam("couponId") int couponId,@RequestParam("deleted") int deleted,@RequestParam("userId") int userId);

    @GetMapping("/getCouponUser")
    RestResponse<List<CouponUser>> getCouponUser(@RequestParam("userId") int userId,@RequestParam("status") String status,@RequestParam("deleted") int deleted);

    @PostMapping("/addCouponUser")
    RestResponse<Integer> addCouponUser
            (@RequestParam("userId") int userId, @RequestParam("couponId") int couponId, @RequestParam("status") int status,@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam("usedTime") LocalDateTime usedTime,
             @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam("startTime") LocalDateTime startTime, @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam("endTime") LocalDateTime endTime, @RequestParam("orderId") Integer orderId,
             @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam("addTime") LocalDateTime addTime,@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam("updateTime") LocalDateTime updateTime,
             @RequestParam("deleted") int deleted);

}
