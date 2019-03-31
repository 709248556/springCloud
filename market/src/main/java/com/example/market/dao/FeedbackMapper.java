package com.example.market.dao;

import com.example.common.entity.Feedback;
import com.example.common.util.JsonData;

import java.util.List;

public interface FeedbackMapper {
    int insertive(JsonData jsonData);
    int insert(Feedback feedback);
    List<Feedback> selective(JsonData jsonData);
}
