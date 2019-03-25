package com.example.user.config;

import com.example.common.enums.AuthEnum;
import com.example.common.enums.RestEnum;
import com.example.common.response.RestResponse;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@Order( value = Ordered.HIGHEST_PRECEDENCE )
public class ShiroExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    @ResponseBody
    public RestResponse unauthenticatedHandler(AuthenticationException e) {
        e.printStackTrace();
        return new RestResponse(RestEnum.UNLOGIN);
    }

    @ExceptionHandler(AuthorizationException.class)
    @ResponseBody
    public Object unauthorizedHandler(AuthorizationException e) {
        e.printStackTrace();
        return new RestResponse(AuthEnum.UNAUTHZ);
    }

}
