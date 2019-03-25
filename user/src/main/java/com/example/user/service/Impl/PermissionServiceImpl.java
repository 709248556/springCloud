package com.example.user.service.Impl;

import com.example.common.entity.Permission;
import com.example.common.util.JsonData;
import com.example.user.dao.PermissionDao;
import com.example.user.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service("permissionService")
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionDao permissionDao;

    @Override
    public Set<String> queryByRoleIds(Integer[] roleIds) {
        Set<String> permissions = new HashSet<String>();
        if (roleIds.length == 0) {
            return permissions;
        }
        JsonData jsonData = new JsonData();
        jsonData.put("deleted", 0);
        for (Integer roleId : roleIds) {
            jsonData.put("roleId", roleId);
            Permission permission = permissionDao.selective(jsonData).get(0);
            permissions.add(permission.getPermission());
        }
        return permissions;
    }
}
