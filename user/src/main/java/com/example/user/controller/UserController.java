package com.example.user.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.common.entity.Admin;
import com.example.common.entity.User;
import com.example.common.enums.AuthEnum;
import com.example.common.enums.RestEnum;
import com.example.common.response.RestResponse;
import com.example.common.util.JacksonUtil;
import com.example.common.util.JsonData;
import com.example.common.util.RedisUtil;
import com.example.common.util.bcrypt.BCryptPasswordEncoder;
import com.example.user.service.UserService;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class UserController {

    @Autowired
    private UserService userService;



    @GetMapping("/getUser")
    public RestResponse<List<User>>  getUser(JsonData jsonData){
        return new RestResponse(userService.selective(jsonData));
    }

    @GetMapping("/getUserAll")
    public RestResponse<List<User>> getUserAll(JsonData jsonData){
        return new RestResponse(userService.selective(jsonData));
    }

}
