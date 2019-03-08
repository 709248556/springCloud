package com.example.user.controller;

import com.example.common.constants.TokenConstant;
import com.example.common.entity.Address;
import com.example.common.entity.Region;
import com.example.common.enums.RestEnum;
import com.example.common.response.RestResponse;
import com.example.common.util.JsonData;
import com.example.common.util.RedisUtil;
import com.example.common.util.RegexUtil;
import com.example.user.service.AddressService;
import com.example.user.service.GetRegionService;
import com.example.user.service.RegionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/address")
public class AddressController extends GetRegionService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private AddressService addressService;

    @Autowired
    private RegionService regionService;

    /**
     * 用户收货地址列表
     *
     * @param //userId 用户ID
     * @return 收货地址列表
     */
    @GetMapping("list")
    public RestResponse list(JsonData jsonData) {
        RestResponse restResponse = new RestResponse();
        if (!jsonData.containsKey(TokenConstant.TOKEN)) {
            return restResponse.error(RestEnum.UNLOGIN);
        }
        int userId = redisUtil.getUserId(jsonData.get(TokenConstant.TOKEN).toString());
        jsonData.put("userId", userId);
        jsonData.put("deleted", 0);
        List<Address> addressList = addressService.selective(jsonData);
        List<Map<String, Object>> addressVoList = new ArrayList<>(addressList.size());
        List<Region> regionList = getRegions();
        for (Address address : addressList) {
            Map<String, Object> addressVo = new HashMap<>();
            addressVo.put("id", address.getId());
            addressVo.put("name", address.getName());
            addressVo.put("mobile", address.getMobile());
            addressVo.put("isDefault", address.getDefault());
            String province = regionList.stream().filter(region -> region.getId().equals(address.getProvinceId())).findAny().orElse(null).getName();
            String city = regionList.stream().filter(region -> region.getId().equals(address.getCityId())).findAny().orElse(null).getName();
            String area = regionList.stream().filter(region -> region.getId().equals(address.getAreaId())).findAny().orElse(null).getName();
            String detailedAddress = "";
            String addr = address.getAddress();
            detailedAddress = province + city + area + " " + addr;
            addressVo.put("detailedAddress", detailedAddress);

            addressVoList.add(addressVo);
        }
        return restResponse.success(addressVoList);
    }

    /**
     * 收货地址详情
     *
     * @param //userId 用户ID
     * @param //id     收货地址ID
     * @return 收货地址详情
     */
    @GetMapping("detail")
    public Object detail(JsonData jsonData) {
        RestResponse restResponse = new RestResponse();
        if (!jsonData.containsKey(TokenConstant.TOKEN)) {
            return restResponse.error(RestEnum.UNLOGIN);
        }
        Address address = addressService.selective(jsonData).get(0);
        if (address == null) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }

        Map<Object, Object> data = new HashMap<Object, Object>();
        data.put("id", address.getId());
        data.put("name", address.getName());
        data.put("provinceId", address.getProvinceId());
        data.put("cityId", address.getCityId());
        data.put("areaId", address.getAreaId());
        data.put("mobile", address.getMobile());
        data.put("address", address.getAddress());
        data.put("isDefault", address.getDefault());
        jsonData.put("id", address.getProvinceId());
        String pname = regionService.findById(jsonData).getName();
        data.put("provinceName", pname);
        jsonData.put("id", address.getCityId());
        String cname = regionService.findById(jsonData).getName();
        data.put("cityName", cname);
        jsonData.put("id", address.getAreaId());
        String dname = regionService.findById(jsonData).getName();
        data.put("areaName", dname);
        return restResponse.success(data);
    }

    private Object validate(Address address) {
        String name = address.getName();
        if (StringUtils.isEmpty(name)) {
            return new RestResponse(RestEnum.BADARGUMENT);
        }

        // 测试收货手机号码是否正确
        String mobile = address.getMobile();
        if (StringUtils.isEmpty(mobile)) {
            return new RestResponse(RestEnum.BADARGUMENT);
        }
        if (!RegexUtil.isMobileExact(mobile)) {
            return new RestResponse(RestEnum.BADARGUMENT);
        }

        Integer pid = address.getProvinceId();
        if (pid == null) {
            return new RestResponse(RestEnum.BADARGUMENT);
        }
        JsonData jsonData = new JsonData();
        jsonData.put("id", pid);
        if (regionService.findById(jsonData) == null) {
            return new RestResponse(RestEnum.BADARGUMENT);
        }

        Integer cid = address.getCityId();
        if (cid == null) {
            return new RestResponse(RestEnum.BADARGUMENT);
        }
        jsonData.put("id", cid);
        if (regionService.findById(jsonData) == null) {
            return new RestResponse(RestEnum.BADARGUMENT);
        }

        Integer aid = address.getAreaId();
        if (aid == null) {
            return new RestResponse(RestEnum.BADARGUMENT);
        }
        jsonData.put("id", aid);
        if (regionService.findById(jsonData) == null) {
            return new RestResponse(RestEnum.BADARGUMENT);
        }

        String detailedAddress = address.getAddress();
        if (StringUtils.isEmpty(detailedAddress)) {
            return new RestResponse(RestEnum.BADARGUMENT);
        }

        Boolean isDefault = address.getDefault();
        if (isDefault == null) {
            return new RestResponse(RestEnum.BADARGUMENT);
        }
        return null;
    }

    /**
     * 添加或更新收货地址
     *
     * @param //userId 用户ID
     * @param address  用户收货地址
     * @return 添加或更新操作结果
     */
    @PostMapping("save")
    public Object save(@RequestBody Address address, HttpServletRequest request) {
        JsonData jsonData = new JsonData(request);
        RestResponse restResponse = new RestResponse();
        if (!jsonData.containsKey(TokenConstant.TOKEN)) {
            return restResponse.error(RestEnum.UNLOGIN);
        }
        Object error = validate(address);
        int userId = redisUtil.getUserId(jsonData.get(TokenConstant.TOKEN).toString());
        if (error != null) {
            return error;
        }

        if (address.getDefault()) {
            // 重置其他收获地址的默认选项
            JsonData resetDefault = new JsonData();
            resetDefault.put("userId", userId);
            resetDefault.put("isDefault", 0);
            resetDefault.put("updateTime", LocalDateTime.now());
            resetDefault.put("deleted", 0);
            addressService.updative(resetDefault);
        }

        if (address.getId() == null || address.getId().equals(0)) {
            JsonData insertive = new JsonData();
            insertive.put("userId", userId);
            insertive.put("addTime", LocalDateTime.now());
            insertive.put("updateTime", LocalDateTime.now());
            insertive.put("name",address.getName());
            insertive.put("provinceId",address.getProvinceId());
            insertive.put("cityId",address.getCityId());
            insertive.put("areaId",address.getAreaId());
            insertive.put("address",address.getAddress());
            insertive.put("mobile",address.getMobile());
            insertive.put("deleted",0);
            addressService.insertive(insertive);
        } else {
            JsonData updative = new JsonData();
            updative.put("id", address.getId());
            updative.put("userId", userId);
            updative.put("updateTime", LocalDateTime.now());
            updative.put("name",address.getName());
            updative.put("provinceId",address.getProvinceId());
            updative.put("cityId",address.getCityId());
            updative.put("areaId",address.getAreaId());
            updative.put("address",address.getAddress());
            updative.put("mobile",address.getMobile());
            if(address.getDefault()) updative.put("isDefault", 1);
            if (addressService.updative(updative) == 0) {
                return restResponse.error(RestEnum.UPDATEDDATAFAILED);
            }
        }
        return restResponse.success(address.getId());
    }

    /**
     * 删除收货地址
     *
     * @param //userId 用户ID
     * @param address  用户收货地址，{ id: xxx }
     * @return 删除操作结果
     */
    @PostMapping("delete")
    public Object delete(@RequestBody Address address, HttpServletRequest request) {
        JsonData jsonData = new JsonData(request);
        RestResponse restResponse = new RestResponse();
        if (!jsonData.containsKey(TokenConstant.TOKEN)) {
            return restResponse.error(RestEnum.UNLOGIN);
        }
        Integer id = address.getId();
        if (id == null) {
            return restResponse.error(RestEnum.BADARGUMENT);
        }
        jsonData.put("id", id);
        addressService.deletive(jsonData);
        return restResponse;
    }

}
