package com.example.user.controller;

import com.example.common.entity.Admin;
import com.example.common.response.RestResponse;
import com.example.common.util.JsonData;
import com.example.user.service.AdminService;
import com.example.user.service.PermissionService;
import com.example.user.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;

public class ShiroController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private PermissionService permissionService;

    @GetMapping("/getAdminList")
    RestResponse<List<Admin>> getAdminList(JsonData jsonData){
        return  new RestResponse<>(adminService.selective(jsonData));
    }

    @PostMapping("/getRolesList")
    RestResponse<Set<String>> getRolesList(Integer[] roleIds){
        return  new RestResponse<>(roleService.queryByIds(roleIds));
    }

    @PostMapping("/getPermissionsList")
    RestResponse<Set<String>> getPermissionsList(Integer[] roleIds){
        return  new RestResponse<>(permissionService.queryByRoleIds(roleIds));
    }
}
