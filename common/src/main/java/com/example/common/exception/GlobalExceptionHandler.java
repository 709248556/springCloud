package com.example.common.exception;

import com.example.common.enums.RestEnum;
import com.example.common.response.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(value = Throwable.class)
    public RestResponse handler(HttpServletRequest req, Throwable throwable) throws Exception {
        log.error(throwable.getMessage(), throwable);
        log.error(req.getRequestURL().toString() + " encounter exception or error");
        RestResponse restResponse = new RestResponse();
        // TODO 判断属于哪一个异常
        if (throwable instanceof IllegalParamsException) {
            IllegalParamsException illegalParamsException = (IllegalParamsException)throwable;
            //TODO 异常信息写进restResponse
            restResponse.setErrno(illegalParamsException.getCode());
            restResponse.setErrmsg(illegalParamsException.getMsg());
        }
        if (throwable instanceof UnLoginExceptionHandler) {
            UnLoginExceptionHandler unLoginExceptionHandler = (UnLoginExceptionHandler)throwable;
            //TODO 异常信息写进restResponse
            restResponse.setErrno(unLoginExceptionHandler.getCode());
            restResponse.setErrmsg(unLoginExceptionHandler.getMsg());
        }
        else {
            log.info("出现错误");
            restResponse.error(RestEnum.SERIOUS);
        }

        return restResponse;
    }



}
