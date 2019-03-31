package com.example.market.controller;

import com.example.common.annotation.RequiresPermissionsDesc;
import com.example.common.entity.Brand;
import com.example.common.entity.CatVo;
import com.example.common.entity.Category;
import com.example.common.entity.Goods;
import com.example.common.enums.RestEnum;
import com.example.common.feign.GoodsClient;
import com.example.common.response.RestResponse;
import com.example.common.util.JsonData;
import com.example.market.service.CategoryService;
import com.github.pagehelper.PageInfo;
import com.rabbitmq.client.AMQP;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;
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

    @Autowired
    private GoodsClient goodsClient;

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

    @GetMapping("category/list")
    public RestResponse list(JsonData jsonData) {
        List<Category> collectList = categoryService.selective(jsonData);
        long total = PageInfo.of(collectList).getTotal();
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", collectList);
        return new RestResponse(data);
    }

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

    @GetMapping("/getCategoryAndBrand")
    public RestResponse getCategoryAndBrand() {
        JsonData jsonData = new JsonData();
        // 管理员设置“所属分类”
        jsonData.put("categoryLevel","L1");
        jsonData.put("deleted",0);
        List<Category> l1CatList = categoryService.selective(jsonData);
        List<CatVo> categoryList = new ArrayList<>(l1CatList.size());
        JsonData jsonData1 = new JsonData();
        for (Category l1 : l1CatList) {
            CatVo l1CatVo = new CatVo();
            l1CatVo.setValue(l1.getId());
            l1CatVo.setLabel(l1.getName());
            jsonData1.put("pid",l1.getId());
            jsonData1.put("deleted",0);
            List<Category> l2CatList = categoryService.selective(jsonData1);
            List<CatVo> children = new ArrayList<>(l2CatList.size());
            for (Category l2 : l2CatList) {
                CatVo l2CatVo = new CatVo();
                l2CatVo.setValue(l2.getId());
                l2CatVo.setLabel(l2.getName());
                children.add(l2CatVo);
            }
            l1CatVo.setChildren(children);

            categoryList.add(l1CatVo);
        }

        // http://element-cn.eleme.io/#/zh-CN/component/select
        // 管理员设置“所属品牌商”
        List<Brand> list = null;
        try {
            RestResponse<List<Brand>> brandRestResponse = goodsClient.getBrandAll(0);
            if (brandRestResponse.getErrno() != RestEnum.OK.code) {
                log.error("CategoryController.getCategoryAndBrand方法错误 brandRestResponse.getErrno() != RestEnum.OK.code，Errno为:" + brandRestResponse.getErrno() + "错误信息为:" + brandRestResponse.getErrmsg());
                //TODO 抛出异常
            }
            list = brandRestResponse.getData();
        }catch (Exception e){
            log.error("CategoryController.getCategoryAndBrand ERROR ,", e.getMessage());
        }
        List<Map<String, Object>> brandList = new ArrayList<>(l1CatList.size());
        for (Brand brand : list) {
            Map<String, Object> b = new HashMap<>(2);
            b.put("value", brand.getId());
            b.put("label", brand.getName());
            brandList.add(b);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("categoryList", categoryList);
        data.put("brandList", brandList);
        return new RestResponse<>(data);
    }

    @PostMapping("category/create")
    public RestResponse create(@RequestBody Category category) {
        RestResponse restResponse = new RestResponse();
        restResponse = validate(category);
        if (restResponse != null) {
            return restResponse;
        }
        categoryService.insert(category);
        return restResponse.success(category);
    }
    private RestResponse validate(Category category) {
        RestResponse restResponse = new RestResponse();
        String name = category.getName();
        if (StringUtils.isEmpty(name)) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }

        String level = category.getLevel();
        if (StringUtils.isEmpty(level)) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }
        if (!level.equals("L1") && !level.equals("L2")) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }

        Integer pid = category.getPid();
        if (level.equals("L2") && (pid == null)) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }

        return null;
    }
    @GetMapping("category/read")
    public RestResponse read(JsonData jsonData) {
        Category category = categoryService.selective(jsonData).get(0);
        return new RestResponse<>(category);
    }
    @PostMapping("category/update")
    public Object update(@RequestBody Category category) {
        RestResponse restResponse = new RestResponse();
        restResponse = validate(category);
        if (restResponse != null) {
            return restResponse;
        }

        if (categoryService.updateById(category) == 0) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }
        return restResponse;
    }
    @PostMapping("category/delete")
    public RestResponse delete(@RequestBody Category category) {
        RestResponse restResponse = new RestResponse();
        Integer id = category.getId();
        if (id == null) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }
        categoryService.deleteById(id);
        return restResponse;
    }

    @GetMapping("getCategoryById")
    RestResponse<Category> getCategoryById(JsonData jsonData){
        return new RestResponse<>(categoryService.selective(jsonData).get(0));
    }
}