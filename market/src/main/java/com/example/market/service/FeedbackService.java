package com.example.market.service;

import com.example.common.entity.Feedback;
import com.example.common.util.JsonData;

import java.util.List;

public interface FeedbackService {
    int insertive(JsonData jsonData);
    int insert(Feedback feedback);
    List<Feedback> selective(JsonData jsonData);
}
