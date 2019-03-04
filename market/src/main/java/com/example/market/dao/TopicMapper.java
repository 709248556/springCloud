package com.example.market.dao;

import com.example.common.entity.Topic;
import com.example.common.util.JsonData;

import java.util.List;

public interface TopicMapper {
    List<Topic> selective(JsonData jsonData);
}
