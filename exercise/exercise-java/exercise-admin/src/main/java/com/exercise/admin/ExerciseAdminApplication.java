package com.exercise.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 管理端启动类
 * 扫描范围覆盖 com.exercise（包含 common 模块），Mapper 扫描由 common 的 MybatisConfig 提供
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.exercise"})
public class ExerciseAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExerciseAdminApplication.class, args);
    }
}