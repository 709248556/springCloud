package com.example.goods.controller;

import com.example.common.annotation.RequiresPermissionsDesc;
import com.example.common.entity.Brand;
import com.example.common.enums.RestEnum;
import com.example.common.response.RestResponse;
import com.example.common.util.JsonData;
import com.example.goods.service.BrandService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class BrandController {

    @Autowired
    private BrandService brandService;

    @GetMapping("/getBrand")
    public RestResponse getBrand(JsonData jsonData) {
        return new RestResponse(brandService.selective(jsonData));
    }

    /**
     * 品牌列表
     *
     * @param //page 分页页数
     * @param //size 分页大小
     * @return 品牌列表
     */
    @GetMapping("/brand/list")
    public Object list(JsonData jsonData) {
        jsonData.put("brandPage", jsonData.get("page"));
        jsonData.put("brandSize", jsonData.get("size"));
        jsonData.put("deleted", 0);
        jsonData.put("brandSort", "add_time");
        jsonData.put("brandOrder", "desc");
        List<Brand> brandList = brandService.selective(jsonData);
        jsonData.remove("brandPage");
        jsonData.remove("brandSize");
        int total = brandService.selective(jsonData).size();
        int totalPages = (int) Math.ceil((double) total / Integer.valueOf(jsonData.get("size").toString()));

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("brandList", brandList);
        data.put("totalPages", totalPages);
        return new RestResponse();
    }

    /**
     * 品牌详情
     *
     * @param //id 品牌ID
     * @return 品牌详情
     */
    @GetMapping("/brand/detail")
    public Object detail(JsonData jsonData) {
        RestResponse restResponse = new RestResponse();
        jsonData.put("brandId", jsonData.get("id"));
        Brand entity = brandService.selective(jsonData).get(0);
        if (entity == null) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("brand", entity);
        return restResponse.success(data);
    }

    @RequiresPermissions("admin:brand:list")
    @RequiresPermissionsDesc(menu = {"商场管理", "品牌管理"}, button = "查询")
    @GetMapping("/brandList")
    public RestResponse brandList(JsonData jsonData) {
        jsonData.put("delted", 0);
        List<Brand> brandList = brandService.selective(jsonData);
        long total = PageInfo.of(brandList).getTotal();
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", brandList);
        return new RestResponse(data);
    }
}
