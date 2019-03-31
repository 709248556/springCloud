package com.example.market.service.Impl;

import com.example.common.entity.Feedback;
import com.example.common.util.JsonData;
import com.example.market.dao.FeedbackMapper;
import com.example.market.service.FeedbackService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public List<Feedback> selective(JsonData jsonData) {
        if (jsonData.containsKey("page") && jsonData.containsKey("limit"))
            PageHelper.startPage(Integer.valueOf(jsonData.get("page").toString()), Integer.valueOf(jsonData.get("limit").toString()));
        if(jsonData.containsKey("username")){
            jsonData.put("username","%"+jsonData.get("username")+"%");
        }
        return feedbackMapper.selective(jsonData);
    }
}
