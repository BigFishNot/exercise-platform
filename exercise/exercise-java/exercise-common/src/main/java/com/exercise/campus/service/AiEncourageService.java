package com.exercise.campus.service;

/**
 * AI 鼓励 Service
 */
public interface AiEncourageService {

    /**
     * 生成一句鼓励语
     * @param userId 当前用户
     * @return 鼓励语文本
     */
    String generate(String userId);
}