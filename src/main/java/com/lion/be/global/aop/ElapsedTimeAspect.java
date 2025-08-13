package com.lion.be.global.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class ElapsedTimeAspect {
    @Around("@annotation(com.lion.be.global.aop.ElapsedTime)")
    public Object checkElapsedTime(ProceedingJoinPoint joinPoint) throws Throwable{
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        String httpMethod = request.getMethod();
        String requestURI = request.getRequestURI();

        long startTime = System.currentTimeMillis();
        Object returnObject = joinPoint.proceed();
        long elapsedTime = System.currentTimeMillis() - startTime;
        log.info("{} {} Elasped Time: {} ms", httpMethod, requestURI, elapsedTime);

        return returnObject;
    }
}
