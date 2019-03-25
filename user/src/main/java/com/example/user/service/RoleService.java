package com.example.user.service;

import com.example.common.entity.Role;
import com.example.common.util.JsonData;

import java.util.List;
import java.util.Set;

public interface RoleService {
    List<Role> selective(JsonData jsonData);
    Set<String> queryByIds(Integer[] roleIds);
}
