package com.example.user.dao;

import com.example.common.entity.Admin;
import com.example.common.util.JsonData;

import java.util.List;

public interface AdminDao {
    List<Admin> selective(JsonData jsonData);
}
