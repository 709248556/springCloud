package com.example.user.service.Impl;

import com.example.common.entity.Admin;
import com.example.common.util.JsonData;
import com.example.user.dao.AdminDao;
import com.example.user.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("adminService")
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminDao adminDao;

    @Override
    public List<Admin> selective(JsonData jsonData) {
        return adminDao.selective(jsonData);
    }
}
