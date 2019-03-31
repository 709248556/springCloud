package com.example.market.service;

import com.example.common.entity.Topic;
import com.example.common.util.JsonData;
import com.github.pagehelper.PageHelper;

import java.util.List;

public interface TopicService {
    List<Topic> selective(JsonData jsonData);
    int insert(Topic topic);
    int updateById(Topic topic);
    int deleteById(int id);
}
