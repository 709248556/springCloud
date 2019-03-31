package com.example.goods.controller;

import com.example.common.annotation.RequiresPermissionsDesc;
import com.example.common.entity.Brand;
import com.example.common.enums.RestEnum;
import com.example.common.response.RestResponse;
import com.example.common.util.JsonData;
import com.example.goods.service.BrandService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
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

    @GetMapping("/getBrandAll")
    public RestResponse getBrandAll(JsonData jsonData){
        return new RestResponse(brandService.selective(jsonData));
    }

    @PostMapping("/brand/create")
    public RestResponse create(@RequestBody Brand brand) {
        RestResponse restResponse = new RestResponse();
        if (StringUtils.isEmpty(brand)) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }
        if (StringUtils.isEmpty(brand.getName())) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }
        if (StringUtils.isEmpty(brand.getDesc())) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }
        if (brand.getFloorPrice() == null) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }
        if (brandService.insert(brand) == 0) restResponse.error(RestEnum.SERIOUS);
        return restResponse;
    }
    @GetMapping("brand/read")
    public RestResponse read(JsonData jsonData) {
        Brand brand = brandService.selective(jsonData).get(0);
        return new RestResponse(brand);
    }

    @PostMapping("brand/update")
    public Object update(@RequestBody Brand brand) {
        RestResponse restResponse = validate(brand);
        if (restResponse != null) {
            return restResponse;
        }
        if (brandService.updateById(brand) == 0) {
            return restResponse.error(RestEnum.UPDATEDDATAFAILED);
        }
        return restResponse.success(brand);
    }

    @PostMapping("brand/delete")
    public Object delete(@RequestBody Brand brand) {
        RestResponse restResponse = new RestResponse();
        Integer id = brand.getId();
        if (id == null) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }
        if(brandService.deleteById(id) == 0) return restResponse.error(RestEnum.UNKONWERROR);
        return restResponse;
    }
    private RestResponse validate(Brand brand) {
        RestResponse restResponse = new RestResponse();
        String name = brand.getName();
        if (StringUtils.isEmpty(name)) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }

        String desc = brand.getDesc();
        if (StringUtils.isEmpty(desc)) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }

        BigDecimal price = brand.getFloorPrice();
        if (price == null) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }
        return null;
    }
}
