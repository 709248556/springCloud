package com.example.market;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignFormatterRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;

@ComponentScan(basePackages = {"com.example.common"})
@ComponentScan(basePackages = {"com.example.market"})
@EnableFeignClients(basePackages = {"com.example.common.feign"})
@MapperScan("com.example.market.dao")
@SpringBootApplication
public class MarketApplication {

	public static void main(String[] args) {
		SpringApplication.run(MarketApplication.class, args);
	}

	@Bean
	public FeignFormatterRegistrar localDateFeignFormatterRegistrar() {
		return new FeignFormatterRegistrar() {
			@Override
			public void registerFormatters(FormatterRegistry formatterRegistry) {
				DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
				registrar.setUseIsoFormat(true);
				registrar.registerFormatters(formatterRegistry);
			}
		};
	}

}
