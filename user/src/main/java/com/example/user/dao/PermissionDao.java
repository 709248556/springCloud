package com.example.user.dao;

import com.example.common.entity.Permission;
import com.example.common.util.JsonData;

import java.util.List;

public interface PermissionDao {
    List<Permission> selective(JsonData jsonData);
}
