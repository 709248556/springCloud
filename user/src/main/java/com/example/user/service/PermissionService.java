package com.example.user.service;

import java.util.List;
import java.util.Set;

public interface PermissionService {
    Set<String> queryByRoleIds(Integer[] roleIds);
}
