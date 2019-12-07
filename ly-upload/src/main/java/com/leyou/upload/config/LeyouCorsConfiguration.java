package com.leyou.upload.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * <p>
 * <code>CustomerStateWorkerOrderService</code>
 * </p>
 *  处理跨域问题
 * @author huiwang45@iflytek.com
 * @description
 * @date 2019/11/23 17:57
 */
@Configuration
public class LeyouCorsConfiguration {
    @Bean
    public CorsFilter corsFilter() {
        //初始化cors配置对象
        CorsConfiguration configuration = new CorsConfiguration();
        //1) 允许的域,不要写*，否则cookie就无法使用了
        configuration.addAllowedOrigin("http://www.leyou.com");
        configuration.addAllowedOrigin("http://manage.leyou.com");
        configuration.addAllowedOrigin("http://api.leyou.com");
        configuration.addAllowedOrigin("http://image.leyou.com");
        //2) 是否发送Cookie信息
        configuration.setAllowCredentials(true);
        //3) 允许所有的请求方法
        configuration.addAllowedMethod("*");
       /* configuration.addAllowedMethod("OPTIONS");
        configuration.addAllowedMethod("HEAD");
        configuration.addAllowedMethod("GET");
        configuration.addAllowedMethod("PUT");
        configuration.addAllowedMethod("POST");
        configuration.addAllowedMethod("DELETE");
        configuration.addAllowedMethod("PATCH");*/
        //4）允许携带任何头信息
        configuration.addAllowedHeader("*");
        //5）有效时长 (使用默认)
        //configuration.setMaxAge(3600L);

        //初始化cors配置源对象
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration("/**", configuration);

        //3.返回新的CorsFilter.
        return new CorsFilter(configSource);
    }
}
