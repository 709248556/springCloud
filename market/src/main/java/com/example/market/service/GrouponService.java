package com.example.market.service;

import com.example.common.entity.Groupon;
import com.example.common.util.JsonData;

import java.util.List;

public interface GrouponService {
    List<Groupon> selective(JsonData jsonData);
}
