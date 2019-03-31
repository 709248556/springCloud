package com.example.market.controller;

import com.example.common.entity.Topic;
import com.example.common.enums.RestEnum;
import com.example.common.response.RestResponse;
import com.example.common.util.JsonData;
import com.example.market.service.TopicService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class TopicController {

    @Autowired
    private TopicService topicService;

    @GetMapping("topic/list")
    public RestResponse list(JsonData jsonData) {
        jsonData.put("deleted",0);
        List<Topic> topicList = topicService.selective(jsonData);
        long total = PageInfo.of(topicList).getTotal();
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", topicList);
        return new RestResponse<>(data);
    }
    @PostMapping("topic/create")
    public RestResponse create(@RequestBody Topic topic) {
        RestResponse restResponse = validate(topic);
        if (restResponse != null) {
            return restResponse;
        }
        if(topicService.insert(topic) == 0) return restResponse.error(RestEnum.SERIOUS);
        return restResponse.success(topic);
    }

    @GetMapping("topic/read")
    public RestResponse read(JsonData jsonData) {
        Topic topic = topicService.selective(jsonData).get(0);
        return new RestResponse(topic);
    }

    @PostMapping("topic/update")
    public RestResponse update(@RequestBody Topic topic) {
        RestResponse restResponse = validate(topic);
        if (restResponse != null) {
            return restResponse;
        }
        if (topicService.updateById(topic) == 0) {
            return restResponse.error(RestEnum.UPDATEDDATAFAILED);
        }
        return restResponse.success(topic);
    }

    @PostMapping("topic/delete")
    public RestResponse delete(@RequestBody Topic topic) {
        topicService.deleteById(topic.getId());
        return new RestResponse();
    }
    private RestResponse validate(Topic topic) {
        String title = topic.getTitle();
        if (StringUtils.isEmpty(title)) {
            return new RestResponse(RestEnum.BADARGUMENT);
        }
        String content = topic.getContent();
        if (StringUtils.isEmpty(content)) {
            return new RestResponse(RestEnum.BADARGUMENT);
        }
        BigDecimal price = topic.getPrice();
        if (price == null) {
            return new RestResponse(RestEnum.BADARGUMENT);
        }
        return null;
    }
}
