package com.example.user.dao;

import com.example.common.entity.User;
import com.example.common.util.JsonData;

import java.util.List;

public interface UserMapper {
    List<User> selective(JsonData jsonData);
    int insertive(JsonData jsonData);
    int updative(JsonData jsonData);
}
