package com.example.market.controller;

import com.example.common.constants.TokenConstant;
import com.example.common.entity.Groupon;
import com.example.common.entity.GrouponRules;
import com.example.common.enums.RestEnum;
import com.example.common.response.RestResponse;
import com.example.common.util.JsonData;
import com.example.market.service.GrouponRulesService;
import com.example.market.service.GrouponService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class GrouponController {

    @Autowired
    private GrouponService grouponService;

    @Autowired
    private GrouponRulesService grouponRulesService;

    @GetMapping("/getGrouponByOrderId")
    public RestResponse<List<Groupon>> getGrouponByOrderId(JsonData jsonData) {
        return new RestResponse(grouponService.selective(jsonData));
    }

    @GetMapping("groupon/list")
    public Object list(JsonData jsonData) {
        List<GrouponRules> rulesList = grouponRulesService.selective(jsonData);
        long total = PageInfo.of(rulesList).getTotal();
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", rulesList);
        return new RestResponse<>(data);
    }
}
