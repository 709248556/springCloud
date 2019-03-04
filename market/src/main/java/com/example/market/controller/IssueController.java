package com.example.market.controller;

import com.example.common.entity.Issue;
import com.example.common.response.RestResponse;
import com.example.common.util.JsonData;
import com.example.market.service.IssueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class IssueController {

    @Autowired
    private IssueService issueService;

    @GetMapping("/getIssueAll")
    public RestResponse<List<Issue>> getIssueAll() {
        return new RestResponse<>(issueService.selectAll());
    }

    @GetMapping("/getIssue")
    public RestResponse<List<Issue>> getIssue(JsonData jsonData){
        return new RestResponse<>(issueService.selective(jsonData));
    }
}
