package com.example.zuul.filter;

import com.example.common.constants.TokenConstant;
import com.example.common.enums.RestEnum;
import com.example.common.exception.UnLoginExceptionHandler;
import com.example.common.response.RestResponse;
import com.example.common.util.CookieUtil;
import com.example.common.util.RedisUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.example.zuul.Constants.FilterConstants.PRE_DECORATION_FILTER_ORDER;
import static com.example.zuul.Constants.FilterConstants.PRE_TYPE;
import com.alibaba.fastjson.JSON;


@Component
public class TokenFilter extends ZuulFilter {
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return PRE_DECORATION_FILTER_ORDER - 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
        HttpServletResponse response = RequestContext.getCurrentContext().getResponse();
        String requestUrl = request.getRequestURL().toString();
        String url = requestUrl.substring(requestUrl.lastIndexOf("/") + 1);
        if (url.equals("login") || url.equals("login_by_weixin")) {
            return null;
        }
        String tokenValue;
        if (request.getServerPort() == 8089) {
            tokenValue = request.getHeader(TokenConstant.TOKEN);//小程序
        } else {
            tokenValue = CookieUtil.getCookieByName(request, TokenConstant.TOKEN);//页面
        }
        //有token
        if (StringUtils.isNotBlank(tokenValue)) {
            //登出情况
            if (url.equals("logout")) {
                CookieUtil.addCookie(response, TokenConstant.TOKEN, null, 0);
                redisUtil.del(tokenValue);
                //TODO 抛出异常，跳到首页
            }
            if (!redisUtil.hasKey(tokenValue)) {
                //TODO 抛出异常，跳到登陆页面，提示
            }
            redisUtil.expire(tokenValue, TokenConstant.TIME);
        } else {
            //TODO 抛出异常，跳到登陆页面，提示
//            RequestContext ctx = RequestContext.getCurrentContext();
//            RestResponse restResponse = new RestResponse();
//            ctx.setSendZuulResponse(false);//不需要进行路由，也就是不会调用api服务提供者
//            //返回内容给客户端
//            ctx.setResponseBody(JSON.toJSONString(restResponse.error(RestEnum.UNLOGIN)));// 返回错误内容
        }
        return null;
    }
}
