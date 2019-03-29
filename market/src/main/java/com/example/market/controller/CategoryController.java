package com.example.market.controller;

import com.example.common.annotation.RequiresPermissionsDesc;
import com.example.common.entity.Category;
import com.example.common.response.RestResponse;
import com.example.common.util.JsonData;
import com.example.market.service.CategoryService;
import com.github.pagehelper.PageInfo;
import com.rabbitmq.client.AMQP;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
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
    /**
     * 分类详情
     *
     * @param //id   分类类目ID。
     *             如果分类类目ID是空，则选择第一个分类类目。
     *             需要注意，这里分类类目是一级类目
     * @return 分类详情
     */
    @GetMapping("/catalog/index")
    public RestResponse index(JsonData jsonData) {

        JsonData jsonData1 = new JsonData();
        jsonData1.put("categoryLevel","L1");
        jsonData.put("categoryDeleted",0);

        // 所有一级分类目录
        List<Category> l1CatList = categoryService.selective(jsonData1);

        // 当前一级分类目录
        Category currentCategory = null;
        if (!jsonData.containsKey("id")) {
            JsonData jsonData2 = new JsonData();
            jsonData2.put("categoryId",jsonData.get("id"));
            currentCategory = categoryService.selective(jsonData2).get(0);
        } else {
            currentCategory = l1CatList.get(0);
        }

        // 当前一级分类目录对应的二级分类目录
        List<Category> currentSubCategory = null;
        if (null != currentCategory) {
            JsonData jsonData2 = new JsonData();
            jsonData2.put("parentId",currentCategory.getId());
            jsonData2.put("categoryDeleted",0);
            currentSubCategory = categoryService.selective(jsonData2);
        }

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("categoryList", l1CatList);
        data.put("currentCategory", currentCategory);
        data.put("currentSubCategory", currentSubCategory);
        return new RestResponse<>(data);
    }

    /**
     * 当前分类栏目
     *
     * @param //id 分类类目ID
     * @return 当前分类栏目
     */
    @GetMapping("/catalog/current")
    public Object current(JsonData jsonData) {
        jsonData.put("categoryId",Integer.valueOf(jsonData.get("id").toString()));
        // 当前分类
        Category currentCategory = categoryService.selective(jsonData).get(0);
        jsonData.remove("categoryId");
        jsonData.put("parentId",currentCategory.getId());
        List<Category> currentSubCategory = categoryService.selective(jsonData);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("currentCategory", currentCategory);
        data.put("currentSubCategory", currentSubCategory);
        return new RestResponse(data);
    }

    @RequiresPermissions("admin:category:list")
    @RequiresPermissionsDesc(menu={"商场管理" , "类目管理"}, button="查询")
    @GetMapping("category/list")
    public RestResponse list(JsonData jsonData) {
        List<Category> collectList = categoryService.selective(jsonData);
        long total = PageInfo.of(collectList).getTotal();
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", collectList);
        return new RestResponse(data);
    }

    @RequiresPermissions("admin:category:list")
    @GetMapping("category/l1")
    public Object catL1() {
        JsonData jsonData = new JsonData();
        // 所有一级分类目录
        jsonData.put("categoryLevel","L1");
        jsonData.put("deleted",0);
        List<Category> l1CatList = categoryService.selective(jsonData);
        List<Map<String, Object>> data = new ArrayList<>(l1CatList.size());
        for (Category category : l1CatList) {
            Map<String, Object> d = new HashMap<>(2);
            d.put("value", category.getId());
            d.put("label", category.getName());
            data.add(d);
        }
        return new RestResponse<>(data);
    }
}