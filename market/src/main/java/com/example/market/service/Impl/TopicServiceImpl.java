package com.example.market.service.Impl;

import com.example.common.entity.Topic;
import com.example.common.util.JsonData;
import com.example.market.dao.TopicMapper;
import com.example.market.service.TopicService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("topicService")
public class TopicServiceImpl implements TopicService {

    @Autowired
    private TopicMapper topicMapper;

    @Override
    public List<Topic> selective(JsonData jsonData) {
        if(jsonData.containsKey("topicPage")&&jsonData.containsKey("topicSize"))
            PageHelper.startPage(Integer.valueOf(jsonData.get("topicPage").toString()), Integer.valueOf(jsonData.get("topicSize").toString()));
        return topicMapper.selective(jsonData);
    }
}
