package com.example.user.service.Impl;

import com.example.common.entity.User;
import com.example.common.util.JsonData;
import com.example.user.dao.UserMapper;
import com.example.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> selective(JsonData jsonData) {
        return userMapper.selective(jsonData);
    }

    @Override
    public int insertive(JsonData jsonData) {
        return userMapper.insertive(jsonData);
    }

    @Override
    public int updative(JsonData jsonData) {
        return userMapper.updative(jsonData);
    }
}
