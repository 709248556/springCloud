package com.example.market.service.Impl;

import com.example.common.entity.Issue;
import com.example.common.util.JsonData;
import com.example.market.dao.IssueMapper;
import com.example.market.service.IssueService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("issueService")
public class IssueServiceImpl implements IssueService {
    @Autowired
    private IssueMapper issueMapper;
    @Override
    public List<Issue> selective(JsonData jsonData) {
        if (jsonData.containsKey("page") && jsonData.containsKey("limit"))
            PageHelper.startPage(Integer.valueOf(jsonData.get("page").toString()), Integer.valueOf(jsonData.get("limit").toString()));
        if(jsonData.containsKey("question")){
            jsonData.put("question","%"+jsonData.get("question")+"%");
        }
        return issueMapper.selective(jsonData);
    }

    @Override
    public List<Issue> selectAll() {
        return issueMapper.selectAll();
    }

    @Override
    public int insert(Issue issue) {
        return issueMapper.insert(issue);
    }

    @Override
    public int updateById(Issue issue) {
        return issueMapper.updateById(issue);
    }

    @Override
    public int deleteById(int id) {
        return issueMapper.deleteById(id);
    }
}
