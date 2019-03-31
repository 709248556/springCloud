package com.example.market.service.Impl;

import com.example.common.entity.Topic;
import com.example.common.util.JsonData;
import com.example.market.dao.TopicMapper;
import com.example.market.service.TopicService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service("topicService")
public class TopicServiceImpl implements TopicService {

    @Autowired
    private TopicMapper topicMapper;

    @Override
    public List<Topic> selective(JsonData jsonData) {
        if (jsonData.containsKey("topicPage") && jsonData.containsKey("topicSize"))
            PageHelper.startPage(Integer.valueOf(jsonData.get("topicPage").toString()), Integer.valueOf(jsonData.get("topicSize").toString()));
        if (jsonData.containsKey("page") && jsonData.containsKey("limit"))
            PageHelper.startPage(Integer.valueOf(jsonData.get("page").toString()), Integer.valueOf(jsonData.get("limit").toString()));
        if (jsonData.containsKey("title")) {
            jsonData.put("title", "%" + jsonData.get("title") + "%");
        }
        if (jsonData.containsKey("subtitle")) {
            jsonData.put("subtitle", "%" + jsonData.get("subtitle") + "%");
        }
        return topicMapper.selective(jsonData);
    }

    @Override
    public int insert(Topic topic) {
        topic.setAddTime(LocalDateTime.now());
        topic.setUpdateTime(LocalDateTime.now());
        return topicMapper.insert(topic);
    }

    @Override
    public int updateById(Topic topic) {
        topic.setUpdateTime(LocalDateTime.now());
        return topicMapper.updateById(topic);
    }

    @Override
    public int deleteById(int id) {
        return topicMapper.deleteById(id);
    }
}
