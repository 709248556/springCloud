package com.example.market.dao;

import com.example.common.entity.SearchKeyword;
import com.example.common.util.JsonData;

import java.util.List;

public interface SearchKeywordDao {
    List<SearchKeyword> selective(JsonData jsonData);
    int insert(SearchKeyword searchKeyword);
    int updateById(SearchKeyword searchKeyword);
    int deleteById(int id);
}
