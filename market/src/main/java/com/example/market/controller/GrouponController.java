package com.example.market.controller;

import com.example.common.entity.Groupon;
import com.example.common.entity.GrouponRules;
import com.example.common.response.RestResponse;
import com.example.common.util.JsonData;
import com.example.market.service.GrouponService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class GrouponController {

    @Autowired
    private GrouponService grouponService;

    @GetMapping("/getGrouponByOrderId")
    public RestResponse<List<Groupon>> getGrouponByOrderId(JsonData jsonData) {
        return new RestResponse(grouponService.selective(jsonData));
    }
}
