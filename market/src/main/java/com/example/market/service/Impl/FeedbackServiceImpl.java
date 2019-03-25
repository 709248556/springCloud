package com.example.market.service.Impl;

import com.example.common.entity.Feedback;
import com.example.common.util.JsonData;
import com.example.market.dao.FeedbackMapper;
import com.example.market.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("feedbackService")
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackMapper feedbackMapper;

    @Override
    public int insertive(JsonData jsonData) {
        return feedbackMapper.insertive(jsonData);
    }

    @Override
    public int insert(Feedback feedback) {
        return feedbackMapper.insert(feedback);
    }
}
