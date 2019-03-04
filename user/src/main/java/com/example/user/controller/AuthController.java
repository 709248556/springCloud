package com.example.user.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.config.WxMaConfig;
import com.alibaba.fastjson.JSON;
import com.example.common.constants.TokenConstant;
import com.example.common.entity.User;
import com.example.common.entity.UserInfo;
import com.example.common.entity.WxLoginInfo;
import com.example.common.enums.AuthEnum;
import com.example.common.enums.RestEnum;
import com.example.common.response.RestResponse;
import com.example.common.util.*;
import com.example.common.util.bcrypt.BCryptPasswordEncoder;
import com.example.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private WxMaService wxService;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 账号登录
     *
     * @param body    请求内容，{ username: xxx, password: xxx }
     * @param request 请求对象
     * @return 登录结果
     */
    @PostMapping("/login")
    public RestResponse login(@RequestBody String body, HttpServletRequest request, JsonData jsonData, Model model) {

        RestResponse restResponse = new RestResponse();

        String username = jsonData.get("username").toString();
        String password = jsonData.get("password").toString();
        if (username == null || password == null) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }
        jsonData.remove("password");
        List<User> userList = userService.selective(jsonData);
        User user = null;
        if (userList.size() > 1) {
            return restResponse.error(RestEnum.SERIOUS);
        } else if (userList.size() == 0) {
            return restResponse.error(RestEnum.BADARGUMENTVALUE);
        } else {
            user = userList.get(0);
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(password, user.getPassword())) {
            return restResponse.error(AuthEnum.AUTH_INVALID_ACCOUNT);
        }

        // userInfo
//        WxUser wxUser = new WxUser();
//        wxUser.setNickName(username);
//        wxUser.setAvatarUrl(user.getAvatar());

//        // token
//        UserToken userToken = UserTokenManager.generateToken(user.getId());
//
//        model.addAttribute("token", userToken.getToken());
//        model.addAttribute("tokenExpire", userToken.getExpireTime().toString());
//        model.addAttribute("userInfo", userInfo);
        return new RestResponse(model);
    }

    /**
     * 微信登录
     *
     * @param //wxLoginInfo 请求内容，{ code: xxx, userInfo: xxx }
     * @param request       请求对象
     * @return 登录结果
     */
    @PostMapping("/login_by_weixin")
    public RestResponse loginByWeixin(@RequestBody WxLoginInfo wxLoginInfo,HttpServletRequest request) {
        JsonData jsonData = new JsonData();
        UserInfo userInfo = wxLoginInfo.getUserInfo();
        RestResponse restResponse = new RestResponse();
        String code = wxLoginInfo.getCode();
        if (code == null || userInfo == null) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }

        String sessionKey = null;
        String openId = null;
        try {
            WxMaJscode2SessionResult result = this.wxService.getUserService().getSessionInfo(code);
            sessionKey = result.getSessionKey();
            openId = result.getOpenid();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (sessionKey == null || openId == null) {
            return restResponse.error();
        }
        jsonData.put("weixinOpenid", openId);
        User user = userService.selective(jsonData).get(0);
        if (user == null) {
            jsonData.put("username", openId);
            jsonData.put("password", openId);
            jsonData.put("avatar", userInfo.getAvatarUrl());
            jsonData.put("nickname", userInfo.getNickName());
            jsonData.put("gender", userInfo.getGender());
            jsonData.put("userLevel", 0);
            jsonData.put("status", 0);
            jsonData.put("lastLoginTime", LocalDateTime.now());
            jsonData.put("lastLoginIp", IpUtil.client(request));


            if (userService.insertive(jsonData) == 0)
                return restResponse.error(RestEnum.SERIOUS);


            // 新用户发送注册优惠券,异步
//            couponAssignService.assignForRegister(user.getId());
        } else {
            jsonData.put("lastLoginTime", LocalDateTime.now());
            jsonData.put("lastLoginIp", IpUtil.client(request));
            jsonData.put("weixinOpenid",openId);
            if (userService.updative(jsonData) == 0) {
                return restResponse.error(RestEnum.UPDATEDDATAFAILED);
            }
        }

        // token
        Map<String, String> userMap = new HashMap<String, String>();
        userMap.put("username",user.getUsername().toString());
        String token = JwtUtil.genToken(userMap);
        redisUtil.set(token, JSON.toJSONString(user),TokenConstant.TIME);

        Map<Object, Object> result = new HashMap<Object, Object>();
        result.put("token", token);
        result.put("userInfo", userInfo);
        return new RestResponse<>(result);
    }
}
