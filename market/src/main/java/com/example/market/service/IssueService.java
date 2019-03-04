package com.example.market.service;

import com.example.common.entity.Issue;
import com.example.common.util.JsonData;

import java.util.List;

public interface IssueService {
    List<Issue> selective(JsonData jsonData);
    List<Issue> selectAll();
}
