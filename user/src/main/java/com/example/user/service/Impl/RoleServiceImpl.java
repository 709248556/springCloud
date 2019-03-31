package com.example.user.service.Impl;

import com.example.common.entity.Role;
import com.example.common.util.JsonData;
import com.example.user.dao.RoleDao;
import com.example.user.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service("roleService")
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleDao roleDao;

    @Override
    public List<Role> selective(JsonData jsonData) {
        return roleDao.selective(jsonData);
    }

    public Set<String> queryByIds(Integer[] roleIds) {
        Set<String> roles = new HashSet<String>();
        if (roleIds.length == 0) {
            return roles;
        }
        JsonData jsonData = new JsonData();
        jsonData.put("enabled", 1);
        jsonData.put("deleted", 0);
        jsonData.put("roleIds",roleIds);
        List<Role> roleList = roleDao.selective(jsonData);
        for (Role role : roleList) {
            roles.add(role.getName());
        }

        return roles;

    }
}
