package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * 
 *
 * @param
 * @return 
 * @description TODO
 * @author huiwang45@iflytek.com
 * @date 2019/11/23 16:26
 */
@SpringBootApplication
@EnableDiscoveryClient
// 导入tk包(通用mapper)
@MapperScan("com.leyou.item.mapper")
public class LeyouItemApplication {
    public static void main(String[] args) {
        SpringApplication.run(LeyouItemApplication.class);
    }
}
