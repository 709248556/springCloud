package com.example.user.controller;

import com.example.common.response.RestResponse;
import com.example.common.util.JsonData;
import com.example.user.service.CollectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class CollectController {
    @Autowired
    private CollectService collectService;

    @GetMapping("/getCollectCountNum")
    public RestResponse<Integer> getCollectCountNum(JsonData jsonData){
         return  new RestResponse(collectService.countive(jsonData));
    }
}
