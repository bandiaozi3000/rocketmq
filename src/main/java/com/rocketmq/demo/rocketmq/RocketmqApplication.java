package com.rocketmq.demo.rocketmq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.rocketmq.demo.rocketmq.dao")
public class RocketmqApplication {

	public static void main(String[] args) {
		SpringApplication.run(RocketmqApplication.class, args);
	}

}
