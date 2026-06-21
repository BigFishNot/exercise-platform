package com.exercise.campus.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis Mapper 扫描：统一扫描 com.exercise.mappers
 */
@Configuration
@MapperScan("com.exercise.mappers")
public class MybatisConfig {
}