package com.example.common.util;

import com.alibaba.fastjson.JSON;
import com.example.common.constants.TokenConstant;
import com.example.common.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Slf4j
@Component
public class JsonData {

    private HttpServletRequest request;

    Map map = null;

    public JsonData() {
        Map map = new HashMap();
        this.map = map;
    }

    public JsonData(HttpServletRequest request) {
        this.request = request;
        Map returnMap = new HashMap();
        Map properties = request.getParameterMap();
        Iterator entries = properties.entrySet().iterator();
        Map.Entry entry;
        String name = "";

        while (entries.hasNext()) {
            entry = (Map.Entry) entries.next();
            name = (String) entry.getKey();
            Object valueObj = entry.getValue();
            String value = "";
            if (null == valueObj) {
                value = "";
            } else if (valueObj instanceof List) {
                log.info("List类型");
                return;
            } else if (valueObj instanceof String[]) {
                String[] values = (String[]) valueObj;
                for (int i = 0; i < values.length; i++) {
                    value = value + values[i] + ",";
                }
                value = value.substring(0, value.length() - 1);

                value = value.replace((char) 12288, ' ');
                value = value.trim();

            } else {
                value = valueObj.toString();

                value = value.replace((char) 12288, ' ');
                value = value.trim();
            }
            returnMap.put(name, value);

        }
        if (StringUtils.isNotBlank(request.getHeader(TokenConstant.TOKEN))) {
            returnMap.put(TokenConstant.TOKEN, request.getHeader(TokenConstant.TOKEN));
        }
        map = returnMap;
    }

    public Object get(Object key) {
        Object obj = null;
        if (map.get(key) instanceof Object[]) {
            Object[] arr = (Object[]) map.get(key);
            obj = request == null ? arr : (request.getParameter((String) key) == null ? arr : arr[0]);
        } else {
            obj = map.get(key);
        }
        return obj;
    }

    @SuppressWarnings("unchecked")
    public Object put(Object key, Object value) {
        return map.put(key, value);
    }

    public Object remove(Object key) {
        return map.remove(key);
    }

    public void clear() {
        map.clear();
    }

    public boolean containsKey(Object key) {
        // TODO Auto-generated method stub
        return map.containsKey(key);
    }

    public boolean containsValue(Object value) {
        // TODO Auto-generated method stub
        return map.containsValue(value);
    }

    public Set entrySet() {
        // TODO Auto-generated method stub
        return map.entrySet();
    }

    public boolean isEmpty() {
        // TODO Auto-generated method stub
        return map.isEmpty();
    }

    public Set keySet() {
        // TODO Auto-generated method stub
        return map.keySet();
    }

    @SuppressWarnings("unchecked")
    public void putAll(Map t) {
        // TODO Auto-generated method stub
        map.putAll(t);
    }

    public int size() {
        // TODO Auto-generated method stub
        return map.size();
    }

    public Collection values() {
        // TODO Auto-generated method stub
        return map.values();
    }
}
