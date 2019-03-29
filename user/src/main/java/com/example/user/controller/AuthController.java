package com.example.user.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.config.WxMaConfig;
import com.alibaba.fastjson.JSON;
import com.example.common.constants.TokenConstant;
import com.example.common.entity.*;
import com.example.common.enums.AuthEnum;
import com.example.common.enums.RestEnum;
import com.example.common.response.RestResponse;
import com.example.common.util.*;
import com.example.common.util.bcrypt.BCryptPasswordEncoder;
import com.example.user.service.PermissionService;
import com.example.user.service.RoleService;
import com.example.user.service.UserService;
import com.example.user.util.PermissionShiro;
import com.example.user.util.PermissionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

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

    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionService permissionService;

//    /**
//     * 账号登录
//     *
//     * @param body    请求内容，{ username: xxx, password: xxx }
//     * @param request 请求对象
//     * @return 登录结果
//     */
//    @PostMapping("/login")
//    public RestResponse login(@RequestBody String body, HttpServletRequest request, JsonData jsonData, Model model) {
//
//        RestResponse restResponse = new RestResponse();
//
//        String username = jsonData.get("username").toString();
//        String password = jsonData.get("password").toString();
//        if (username == null || password == null) {
//            return restResponse.error(RestEnum.BADARGUMENT);
//        }
//        jsonData.remove("password");
//        List<User> userList = userService.selective(jsonData);
//        User user = null;
//        if (userList.size() > 1) {
//            return restResponse.error(RestEnum.SERIOUS);
//        } else if (userList.size() == 0) {
//            return restResponse.error(RestEnum.BADARGUMENTVALUE);
//        } else {
//            user = userList.get(0);
//        }
//
//        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//        if (!encoder.matches(password, user.getPassword())) {
//            return restResponse.error(AuthEnum.AUTH_INVALID_ACCOUNT);
//        }
//
//        // userInfo
////        WxUser wxUser = new WxUser();
////        wxUser.setNickName(username);
////        wxUser.setAvatarUrl(user.getAvatar());
//
////        // token
////        UserToken userToken = UserTokenManager.generateToken(user.getId());
////
////        model.addAttribute("token", userToken.getToken());
////        model.addAttribute("tokenExpire", userToken.getExpireTime().toString());
////        model.addAttribute("userInfo", userInfo);
//        return new RestResponse(model);
//    }

    /*
     *  { username : value, password : value }
     */
    @PostMapping("/login")
    public RestResponse login(@RequestBody String body) {
        String username = JacksonUtil.parseString(body, "username");
        String password = JacksonUtil.parseString(body, "password");

        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return new RestResponse(RestEnum.BADARGUMENT);
        }

        Subject currentUser = SecurityUtils.getSubject();
        try {
            currentUser.login(new UsernamePasswordToken(username, password));
        } catch (UnknownAccountException uae) {
            return new RestResponse(AuthEnum.AUTH_INVALID_ACCOUNT);
        } catch (LockedAccountException lae) {
            return new RestResponse(AuthEnum.ADMIN_INVALID_ACCOUNT);

        } catch (AuthenticationException ae) {
            return new RestResponse(RestEnum.UNKONWERROR);
        }
        return new RestResponse(currentUser.getSession().getId());
    }

    @RequiresAuthentication
    @GetMapping("/info")
    public RestResponse info() {
        Subject currentUser = SecurityUtils.getSubject();
        Admin admin = (Admin) currentUser.getPrincipal();

        Map<String, Object> data = new HashMap<>();
        data.put("name", admin.getUsername());
        data.put("avatar", admin.getAvatar());

        Integer[] roleIds = admin.getRoleIds();
        Set<String> roles = roleService.queryByIds(roleIds);
        Set<String> permissions = permissionService.queryByRoleIds(roleIds);
        data.put("roles", roles);
        // NOTE
        // 这里需要转换perms结构，因为对于前端而已API形式的权限更容易理解
        data.put("perms", toAPI(permissions));
        return new RestResponse(data);
    }

    @Autowired
    private ApplicationContext context;
    private HashMap<String, String> systemPermissionsMap = null;

    private Collection<String> toAPI(Set<String> permissions) {
        if (systemPermissionsMap == null) {
            systemPermissionsMap = new HashMap<>();
            final String basicPackage = "org.linlinjava.litemall.admin";
            List<PermissionShiro> systemPermissions = PermissionUtil.listPermission(context, basicPackage);
            for (PermissionShiro permission : systemPermissions) {
                String perm = permission.getRequiresPermissions().value()[0];
                String api = permission.getApi();
                systemPermissionsMap.put(perm, api);
            }
        }
        Collection<String> apis = new HashSet<>();
        for (String perm : permissions) {
            String api = systemPermissionsMap.get(perm);
            apis.add(api);

            if (perm.equals("*")) {
                apis.clear();
                apis.add("*");
                return apis;
//                return systemPermissionsMap.values();

            }
        }
        return apis;
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

    @RequiresAuthentication
    @PostMapping("profile/password")
    public RestResponse password(@RequestBody String body){
        RestResponse restResponse = new RestResponse();
        JsonData jsonData = new JsonData();
        String oldPassword = JacksonUtil.parseString(body, "oldPassword");
        String newPassword = JacksonUtil.parseString(body, "newPassword");
        if (StringUtils.isEmpty(oldPassword)) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }
        if (StringUtils.isEmpty(newPassword)) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }
        Subject currentUser = SecurityUtils.getSubject();
        Admin admin = (Admin) currentUser.getPrincipal();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(oldPassword, admin.getPassword())) {
            return restResponse.error(AuthEnum.AUTH_INVALID_ACCOUNT);
        }
        //密码加密
        String encodedNewPassword = encoder.encode(newPassword);
        jsonData.put("password",encodedNewPassword);
        jsonData.put("userId",admin.getId());
        if(userService.updative(jsonData) ==  0) return restResponse.error(RestEnum.UNKONWERROR);
        return restResponse;
    }
    @RequiresAuthentication
    @PostMapping("/logout")
    public RestResponse login() {
        Subject currentUser = SecurityUtils.getSubject();
        currentUser.logout();
        return new RestResponse();
    }
}
