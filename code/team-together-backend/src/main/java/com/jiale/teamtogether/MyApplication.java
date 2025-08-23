package com.jiale.teamtogether;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启动类
 *
 */
@SpringBootApplication
@MapperScan("com.jiale.teamtogether.mapper")        // 如果mapper.java 文件中没有使用mapper注解需要添加
@EnableScheduling
@Slf4j
public class MyApplication {

    public static void main(String[] args) {

        SpringApplication.run(MyApplication.class, args);
        log.info("TeamTogether 服务器启动");
    }
}

