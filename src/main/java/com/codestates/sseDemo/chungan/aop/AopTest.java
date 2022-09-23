package com.codestates.sseDemo.chungan.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
@Slf4j
public class AopTest {

    @Around("execution(public * com.codestates.sseDemo.chungan.alarm.controller..*(..)) && args(userId, ..)")
    public Object execute(ProceedingJoinPoint joinPoint, final Long userId) throws Throwable {

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        log.info("싷행!!, header host: {}", request.getHeader("Host"));
        log.info("싷행!!, header port: {}", request.getLocalPort());

        log.info("실행!!, userId: {}", userId);
        return joinPoint.proceed();
    }
}
