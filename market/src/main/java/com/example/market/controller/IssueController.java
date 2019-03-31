package com.example.market.controller;

import com.example.common.entity.Issue;
import com.example.common.enums.RestEnum;
import com.example.common.response.RestResponse;
import com.example.common.util.JsonData;
import com.example.market.service.IssueService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @GetMapping("issue/list")
    public RestResponse list(JsonData jsonData) {
        jsonData.put("deleted",0);
        List<Issue> issueList = issueService.selective(jsonData);
        long total = PageInfo.of(issueList).getTotal();
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", issueList);
        return new RestResponse(data);
    }

    @PostMapping("Issuse/create")
    public RestResponse Issusecreate(@RequestBody Issue issue) {
        RestResponse restResponse = validate(issue);
        if (restResponse != null) {
            return restResponse;
        }
        issue.setAddTime(LocalDateTime.now());
        issue.setUpdateTime(LocalDateTime.now());
        if(issueService.insert(issue) == 0) return restResponse.error(RestEnum.SERIOUS);
        return restResponse.success(issue);
    }

    @GetMapping("Issuse/read")
    public Object read(JsonData jsonData) {
        Issue issue = issueService.selective(jsonData).get(0);
        return new RestResponse<>(issue);
    }

    @PostMapping("Issuse/update")
    public Object update(@RequestBody Issue issue) {
        RestResponse restResponse = validate(issue);
        if (restResponse != null) {
            return restResponse;
        }

        issue.setUpdateTime(LocalDateTime.now());
        if (issueService.updateById(issue) == 0) {
            return restResponse.error(RestEnum.UPDATEDDATAFAILED);
        }

        return restResponse.success(issue);
    }

    @PostMapping("Issuse/delete")
    public Object delete(@RequestBody Issue issue) {
        Integer id = issue.getId();
        if (id == null) {
            return new RestResponse(RestEnum.BADARGUMENTVALUE);
        }
        if(issueService.deleteById(id) == 0) new RestResponse(RestEnum.SERIOUS);
        return new RestResponse<>();
    }
    private RestResponse validate(Issue issue) {
        String question = issue.getQuestion();
        if (StringUtils.isEmpty(question)) {
            return new RestResponse(RestEnum.BADARGUMENTVALUE);
        }
        String answer = issue.getAnswer();
        if (StringUtils.isEmpty(answer)) {
            return new RestResponse(RestEnum.BADARGUMENTVALUE);
        }
        return null;
    }
}
