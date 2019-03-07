package com.example.zuul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"com.example.common"})
@ComponentScan(basePackages = {"com.example.zuul"})
@SpringBootApplication
@EnableZuulProxy
public class ZuulApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(ZuulApplication.class, args);
	}

	//部署
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(ZuulApplication.class);
	}
}

