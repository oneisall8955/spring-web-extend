package com.oneisall.spring.web.extend;


import com.oneisall.spring.web.extend.common.properties.SignProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * 启动
 *
 * @author liuzhicong
 */
@EnableConfigurationProperties(SignProperties.class)
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
