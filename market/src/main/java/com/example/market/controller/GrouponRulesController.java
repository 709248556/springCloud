package com.example.market.controller;

import com.example.common.entity.GrouponRules;
import com.example.common.response.RestResponse;
import com.example.common.util.JsonData;
import com.example.market.service.GrouponRulesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class GrouponRulesController {

    @Autowired
    private GrouponRulesService grouponRulesService;

    @GetMapping("/getGrouponRules")
    public RestResponse<List<GrouponRules>> getGrouponRules(JsonData jsonData){
        return new RestResponse(grouponRulesService.selective(jsonData));
    }
}
