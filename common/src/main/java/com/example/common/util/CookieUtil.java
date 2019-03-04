package com.example.common.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class CookieUtil {
    /**
     * 添加Cookie值（切记，为防止XSS劫持Cookie攻击，在向客户端返回Cookie值时记得设置HttpOnly）.
     * @param response .
     * @param name cookie的名称 .
     * @param value cookie的值 .
     * @param maxAge cookie存放的时间(以秒为单位,假如存放三天,即3*24*60*60; 如果值为0, cookie将随浏览器关闭而清除).
     */
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        if (maxAge > 0) {
            cookie.setMaxAge(maxAge);
        }
        response.addCookie(cookie);
    }

    /**
     * 添加Cookie值,cookie有效时间，关闭浏览器，cookie失效
     * @param response
     * @param name
     * @param value
     */
    public static void addCookie(HttpServletResponse response, String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    /**
     * 根据某一Cookie名获取Cookie的值.
     * @param request .
     * @param name Cookie的名称 .
     * @return Cookie值.
     */
    public static String getCookieByName(HttpServletRequest request, String name) {
        Map<String, Cookie> cookieMap = CookieUtil.readCookieMap(request);
        if (cookieMap.containsKey(name)) {
            Cookie cookie = cookieMap.get(name);
            return cookie.getValue();
        } else {
            return null;
        }
    }

    /**
     * 从request中读取所有Cookie值,放入Map中.
     * @param request .
     * @return cookieMap.
     */
    private static Map<String, Cookie> readCookieMap(HttpServletRequest request) {
        Map<String, Cookie> cookieMap = new HashMap<String, Cookie>();
        Cookie[] cookies = request.getCookies();
        if (null != cookies) {
            for (int num = 0; num < cookies.length; num++) {
                cookieMap.put(cookies[num].getName(), cookies[num]);
            }
        }
        return cookieMap;
    }
}
