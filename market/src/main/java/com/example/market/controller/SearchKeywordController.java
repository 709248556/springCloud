package com.example.market.controller;

import com.example.common.entity.SearchKeyword;
import com.example.common.enums.RestEnum;
import com.example.common.response.RestResponse;
import com.example.common.util.JsonData;
import com.example.market.service.SearchKeywordService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import javax.xml.bind.util.JAXBSource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class SearchKeywordController {

    @Autowired
    private SearchKeywordService searchKeywordService;

    @GetMapping("keyword/list")
    public Object list(JsonData jsonData) {
        List<SearchKeyword> brandList = searchKeywordService.selective(jsonData);
        long total = PageInfo.of(brandList).getTotal();
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", brandList);
        return new RestResponse<>(data);
    }

    @PostMapping("keyword/create")
    public Object create(@RequestBody SearchKeyword keywords) {
        Object error = validate(keywords);
        if (error != null) {
            return error;
        }

        keywords.setAddTime(LocalDateTime.now());
        keywords.setUpdateTime(LocalDateTime.now());
        if(searchKeywordService.insert(keywords) == 0) return new RestResponse<>(RestEnum.SERIOUS);
        return new RestResponse<>(keywords);
    }

    @GetMapping("keyword/read")
    public Object read(JsonData jsonData) {
        SearchKeyword brand = searchKeywordService.selective(jsonData).get(0);
        return new RestResponse<>(brand);
    }

    @PostMapping("keyword/update")
    public Object update(@RequestBody SearchKeyword keywords) {
        Object error = validate(keywords);
        if (error != null) {
            return error;
        }
        if (searchKeywordService.updateById(keywords) == 0) {
            return new RestResponse<>(RestEnum.UPDATEDDATAFAILED);
        }
        return new RestResponse<>(keywords);
    }

    @PostMapping("keyword/delete")
    public Object delete(@RequestBody SearchKeyword keyword) {
        Integer id = keyword.getId();
        if (id == null) {
            return new RestResponse(RestEnum.BADARGUMENTVALUE);
        }
        if(searchKeywordService.deleteById(id) == 0) return new RestResponse<>(RestEnum.SERIOUS);
        return new RestResponse();
    }

    private RestResponse validate(SearchKeyword keywords) {
        String keyword = keywords.getKeyword();
        if (StringUtils.isEmpty(keyword)) {
            return new RestResponse(RestEnum.BADARGUMENTVALUE);
        }
        String url = keywords.getUrl();
        if (StringUtils.isEmpty(url)) {
            return new RestResponse(RestEnum.BADARGUMENTVALUE);
        }
        return null;
    }
}
