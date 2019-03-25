package com.example.user.service;

import com.example.common.entity.Admin;
import com.example.common.util.JsonData;

import java.util.List;

public interface AdminService {
    List<Admin> selective(JsonData jsonData);
}
