package com.wilson;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling //开启定时任务
public class FreeProxyCrawlerApplication {

	public static void main(String[] args) {
		SpringApplication.run(FreeProxyCrawlerApplication.class, args);
		try {
			Thread.sleep(10000000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

}
