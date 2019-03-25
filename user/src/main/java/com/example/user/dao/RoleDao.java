package com.example.user.dao;

import com.example.common.entity.Role;
import com.example.common.util.JsonData;

import java.util.List;

public interface RoleDao {
    List<Role> selective(JsonData jsonData);
}
