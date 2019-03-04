package com.example.common.feign;

import com.example.common.entity.Comment;
import com.example.common.entity.User;
import com.example.common.hystrix.UserFallback;
import com.example.common.response.RestResponse;
import com.example.common.util.JsonData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "user",fallback = UserFallback.class)
public interface UserClient {

    @GetMapping("/getCommentList")
    RestResponse<List<Comment>> getCommentList(@RequestParam("goodsId") int goodsId,@RequestParam("commentType") int commentType);

    @GetMapping("/getCollectCountNum")
    RestResponse<Integer> getCollectCountNum(@RequestParam("goodsId") int goodsId,@RequestParam("userId") int userId,@RequestParam("collectType") int collectType);

    @GetMapping("/getUser")
    RestResponse<List<User>> getUser(@RequestParam("userId") int userId);

}
