package com.exercise.web.biz;

import com.exercise.campus.component.LoginContextHolder;
import com.exercise.campus.service.AiEncourageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * AI 鼓励 Web Biz
 */
@Component
public class AiEncourageWebBiz {

    @Autowired
    private AiEncourageService aiEncourageService;

    public String generate() {
        String userId = LoginContextHolder.requireUserId();
        return aiEncourageService.generate(userId);
    }
}