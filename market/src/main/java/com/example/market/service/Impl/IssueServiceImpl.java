package com.example.market.service.Impl;

import com.example.common.entity.Issue;
import com.example.common.util.JsonData;
import com.example.market.dao.IssueMapper;
import com.example.market.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("issueService")
public class IssueServiceImpl implements IssueService {
    @Autowired
    private IssueMapper issueMapper;
    @Override
    public List<Issue> selective(JsonData jsonData) {
        return issueMapper.selective(jsonData);
    }

    @Override
    public List<Issue> selectAll() {
        return issueMapper.selectAll();
    }
}
