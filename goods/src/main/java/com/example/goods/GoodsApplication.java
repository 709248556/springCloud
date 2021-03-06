package com.example.goods;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.Properties;

@ComponentScan(basePackages = {"com.example.common"})
@ComponentScan(basePackages = {"com.example.goods"})
@EnableFeignClients(basePackages = {"com.example.common.feign"})
@MapperScan("com.example.goods.dao")
@SpringBootApplication
public class GoodsApplication {

	public static void main(String[] args) {
		SpringApplication.run(GoodsApplication.class, args);
	}

}
