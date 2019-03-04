package com.example.market.controller;

import com.example.common.entity.Category;
import com.example.common.response.RestResponse;
import com.example.common.util.JsonData;
import com.example.market.service.CategoryService;
import com.rabbitmq.client.AMQP;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 商品分类类目
     *
     * @param //id 分类类目ID
     * @return 商品分类类目
     */
    @GetMapping("/category")
    public RestResponse category(JsonData jsonData) {
        jsonData.put("categoryId", jsonData.get("id"));
        Category cur = categoryService.selective(jsonData).get(0);
        Category parent = null;
        List<Category> children = null;

        if (cur.getPid() == 0) {
            parent = cur;
            jsonData.remove("categoryId");
            jsonData.put("parentId", cur.getId());
            children = categoryService.selective(jsonData);
            cur = children.size() > 0 ? children.get(0) : cur;
        } else {
            jsonData.put("categoryId", cur.getPid());
            parent = categoryService.selective(jsonData).get(0);
            jsonData.remove("categoryId");
            jsonData.put("parentId", cur.getPid());
            children = categoryService.selective(jsonData);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("currentCategory", cur);
        data.put("parentCategory", parent);
        data.put("brotherCategory", children);
        return new RestResponse(data);
    }

    @PostMapping("/getCategorylist")
    public RestResponse<List<Category>> getCategorylist(@RequestBody List<Integer> categoryIdList){
        JsonData jsonData = new JsonData();
        List<Category> categoryList = new ArrayList<>();
        for (int categoryId : categoryIdList){
            jsonData.put("categoryId",categoryId);
            categoryList.add(categoryService.selective(jsonData).get(0));
        }
        return new RestResponse(categoryList);
    }
}