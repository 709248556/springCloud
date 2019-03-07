package com.example.user.dao;

import com.example.common.entity.Collect;
import com.example.common.util.JsonData;

import java.util.List;

public interface CollectMapper {
    Integer countive(JsonData jsonData);
    List<Collect> selective(JsonData jsonData);
    int deletive(JsonData jsonData);
    int insertive(JsonData jsonData);
}
