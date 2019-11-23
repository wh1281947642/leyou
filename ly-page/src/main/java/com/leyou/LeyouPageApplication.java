package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Author: cuzz
 * @Date: 2018/11/15 12:01
 * @Description:
 */
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
public class LeyouPageApplication {
    public static void main(String[] args) {
        SpringApplication.run(LeyouPageApplication.class, args);
    }
}
