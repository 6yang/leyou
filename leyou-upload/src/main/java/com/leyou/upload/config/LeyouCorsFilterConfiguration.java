package com.leyou.upload.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class LeyouCorsFilterConfiguration {

    @Bean
    public CorsFilter corsFilter(){
        //初始化对象
        CorsConfiguration configuration = new CorsConfiguration();
        //添加允许跨域的域名，可以设置多个 ， 如果允许设置cookie 一定不可以设置为*
        configuration.addAllowedOrigin("http://manage.leyou.com");
        //允许携带cookie
        configuration.setAllowCredentials(true);
        //允许所有请求方法跨域访问
        configuration.addAllowedMethod("*");
        //允许所有头信息跨域访问
        configuration.addAllowedHeader("*");
        //初始化配置源对象
        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
        //拦截所有请求，校验是否允许跨域
        configurationSource.registerCorsConfiguration("/**",configuration);
        return new CorsFilter(configurationSource);
    }
}
