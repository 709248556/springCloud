package com.example.market.service.Impl;

import com.example.common.entity.SearchKeyword;
import com.example.common.util.JsonData;
import com.example.market.dao.SearchKeywordDao;
import com.example.market.service.SearchKeywordService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service("searchKeywordService")
public class SearchKeywordServiceImpl implements SearchKeywordService {
    @Autowired
    private SearchKeywordDao searchKeywordDao;

    @Override
    public List<SearchKeyword> selective(JsonData jsonData) {
        if (jsonData.containsKey("page") && jsonData.containsKey("limit"))
            PageHelper.startPage(Integer.valueOf(jsonData.get("page").toString()), Integer.valueOf(jsonData.get("limit").toString()));
        if(jsonData.containsKey("keyword")){
            jsonData.put("keyword","%"+jsonData.get("keyword")+"%");
        }
        return searchKeywordDao.selective(jsonData);
    }

    @Override
    public int insert(SearchKeyword searchKeyword) {
        return searchKeywordDao.insert(searchKeyword);
    }

    @Override
    public int updateById(SearchKeyword searchKeyword) {
        searchKeyword.setUpdateTime(LocalDateTime.now());
        return searchKeywordDao.updateById(searchKeyword);
    }

    @Override
    public int deleteById(int id) {
        return searchKeywordDao.deleteById(id);
    }
}
