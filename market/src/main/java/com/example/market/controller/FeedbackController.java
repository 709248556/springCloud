package com.example.market.controller;

import com.example.common.constants.TokenConstant;
import com.example.common.entity.Feedback;
import com.example.common.entity.User;
import com.example.common.enums.RestEnum;
import com.example.common.feign.UserClient;
import com.example.common.response.RestResponse;
import com.example.common.util.JsonData;
import com.example.common.util.RedisUtil;
import com.example.common.util.RegexUtil;
import com.example.market.service.FeedbackService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class FeedbackController {

    @Autowired
    private UserClient userClient;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private FeedbackService feedbackService;

    /**
     * 添加意见反馈
     *
     * @param //userId 用户ID
     * @param feedback 意见反馈
     * @return 操作结果
     */
    @PostMapping("/feedback/submit")
    public Object submit(@RequestBody Feedback feedback, HttpServletRequest request) {
        JsonData jsonData = new JsonData(request);
        RestResponse restResponse = new RestResponse();
        if (!jsonData.containsKey(TokenConstant.TOKEN)) {
            return restResponse.error(RestEnum.UNLOGIN);
        }
        Object error = validate(feedback);
        if (error != null) {
            return error;
        }
        int userId = redisUtil.getUserId(jsonData.get(TokenConstant.TOKEN).toString());
        User user = new User();
        try {
            RestResponse<List<User>> userRestResponse = userClient.getUser(userId);
            if (userRestResponse.getErrno() != RestEnum.OK.code) {
                log.error("FeedBackController.submit方法错误 userRestResponse.getErrno() != RestEnum.OK.code，Errno为:" + userRestResponse.getErrno() + "错误信息为:" + userRestResponse.getErrmsg());
                //TODO 抛出异常
            }
            user = userRestResponse.getData().get(0);
        }catch (Exception e) {
            log.error("FeedBackController.submit ERROR ,", e.getMessage());
        }

        String username = user.getUsername();
        feedback.setId(null);
        feedback.setUserId(userId);
        feedback.setUsername(username);
        feedback.setAddTime(LocalDateTime.now());
        feedback.setUpdateTime(LocalDateTime.now());
        //状态默认是0，1表示状态已发生变化
        feedback.setStatus(1);

        feedbackService.insert(feedback);

        return restResponse;
    }

    private Object validate(Feedback feedback) {
        RestResponse restResponse = new RestResponse();
        String content = feedback.getContent();
        if (StringUtils.isEmpty(content)) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }

        String type = feedback.getFeedType();
        if (StringUtils.isEmpty(type)) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }

        Boolean hasPicture = feedback.getHasPicture();
        if (hasPicture == null || !hasPicture) {
            feedback.setPicUrls(new String[0]);
        }

        // 测试手机号码是否正确
        String mobile = feedback.getMobile();
        if (StringUtils.isEmpty(mobile)) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }
        if (!RegexUtil.isMobileExact(mobile)) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }
        return null;
    }

    @GetMapping("feedback/list")
    public Object list(JsonData jsonData) {
        List<Feedback> feedbackList = feedbackService.selective(jsonData);
        long total = PageInfo.of(feedbackList).getTotal();
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", feedbackList);
        return new RestResponse<>(data);
    }
}
