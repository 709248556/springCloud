package com.example.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    public void set(String key, Object object, Long time) {
        if (time <= 0) {
            //TODO 抛出异常
        }else{
            if (object instanceof String) {
                setString(key, object);
            }else if (object instanceof Set){
                setSet(key, object);
            }else {
                // TODO 抛出异常
            }
            // 设置有效期 以秒为单位
            stringRedisTemplate.expire(key, time, TimeUnit.SECONDS);
        }
    }

    public void setString(String key, Object object) {
        // 如果是String 类型
        String value = (String) object;
        stringRedisTemplate.opsForValue().set(key, value);
    }

    public void setSet(String key, Object object) {
        Set<String> value = (Set<String>) object;
        for (String oj : value) {
            stringRedisTemplate.opsForSet().add(key, oj);
        }
    }

    public String getString(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    public void expire(String key, Long time) {
        if (time <= 0) {
            //TODO 抛出异常
        }
        // 更新有效时间 以秒为单位
        stringRedisTemplate.expire(key, time, TimeUnit.SECONDS);
    }

    public void del(String key) {
        stringRedisTemplate.delete(key);
    }

    public Boolean hasKey(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    public int getUserId(String token){
            return this.getUser(token).getId();
    }
    public User getUser(String token){
            String userJson = this.getString(token);
            return JSON.parseObject(userJson,User.class);
    }
}

