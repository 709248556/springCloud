package com.example.user.service;

import com.example.common.entity.Footprint;
import com.example.common.util.JsonData;

import java.util.List;

public interface FootprintService {
    List<Footprint> selective(JsonData jsonData);
    int deletive(JsonData jsonData);
}
