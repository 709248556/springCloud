package com.example.market.service.Impl;

import com.example.common.constants.CouponConstant;
import com.example.common.entity.Coupon;
import com.example.common.util.JsonData;
import com.example.market.dao.CouponMapper;
import com.example.market.service.CouponService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service("grouponService")
public class CouponServiceImpl implements CouponService {
    @Autowired
    private CouponMapper couponMapper;

    @Override
    public List<Coupon> selective(JsonData jsonData) {
        if (jsonData.containsKey("couponPage") && jsonData.containsKey("couponSize"))
            PageHelper.startPage(Integer.valueOf(jsonData.get("couponPage").toString()), Integer.valueOf(jsonData.get("couponSize").toString()));
        if (jsonData.containsKey("page") && jsonData.containsKey("limit"))
            PageHelper.startPage(Integer.valueOf(jsonData.get("page").toString()), Integer.valueOf(jsonData.get("limit").toString()));
        if (jsonData.containsKey("name")) {
            jsonData.put("name", "%" + jsonData.get("name") + "%");
        }
        return couponMapper.selective(jsonData);
    }


    /**
     * 生成优惠码
     *
     * @return 可使用优惠码
     */
    @Override
    public String generateCode() {
        String code = getRandomNum(8);
        while (findByCode(code) != null) {
            code = getRandomNum(8);
        }
        return code;
    }

    @Override
    public int insert(Coupon coupon) {
        coupon.setAddTime(LocalDateTime.now());
        coupon.setUpdateTime(LocalDateTime.now());
        return couponMapper.insert(coupon);
    }

    @Override
    public int updateById(Coupon coupon) {
        coupon.setUpdateTime(LocalDateTime.now());
        return couponMapper.updateById(coupon);
    }

    @Override
    public int deleteById(int id) {
        return couponMapper.deleteById(id);
    }

    private String getRandomNum(Integer num) {
        String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        base += "0123456789";

        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < num; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
    public Coupon findByCode(String code) {
        JsonData jsonData =new JsonData();
        jsonData.put("code",code);
        jsonData.put("type",CouponConstant.TYPE_CODE);
        jsonData.put("status",CouponConstant.STATUS_NORMAL);
        jsonData.put("deleted",0);
        List<Coupon> couponList =  couponMapper.selective(jsonData);
        if(couponList.size() > 1){
            throw new RuntimeException("");
        }
        else if(couponList.size() == 0){
            return null;
        }
        else {
            return couponList.get(0);
        }
    }
}
