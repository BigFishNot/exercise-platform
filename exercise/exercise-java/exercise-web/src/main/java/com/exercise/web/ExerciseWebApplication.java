package com.exercise.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 用户端启动类
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.exercise"})
public class ExerciseWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExerciseWebApplication.class, args);
    }
}