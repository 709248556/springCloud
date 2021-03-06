package com.example.market.dao;

import com.example.common.entity.Issue;
import com.example.common.util.JsonData;

import java.util.List;

public interface IssueMapper {
    List<Issue> selectAll();
    List<Issue> selective(JsonData jsonData);
    int insert(Issue issue);
    int updateById(Issue issue);
    int deleteById(int id);
}
