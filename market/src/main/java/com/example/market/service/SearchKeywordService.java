package com.example.market.service;

import com.example.common.entity.SearchKeyword;
import com.example.common.util.JsonData;

import java.util.List;

public interface SearchKeywordService {
    List<SearchKeyword> selective(JsonData jsonData);
    int insert(SearchKeyword searchKeyword);
    int updateById(SearchKeyword searchKeyword);
    int deleteById(int id);
}
