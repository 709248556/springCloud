package com.example.user.dao;

import com.example.common.entity.Footprint;
import com.example.common.util.JsonData;

import java.util.List;

public interface FootprintMapper {
    List<Footprint> selective(JsonData jsonData);
    int deletive(JsonData jsonData);
}
