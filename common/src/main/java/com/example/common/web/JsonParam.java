package com.example.common.web;

import com.example.common.util.JsonData;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonParam {
    String value();

    boolean required() default true;

    String defaultValue() default "";
}
