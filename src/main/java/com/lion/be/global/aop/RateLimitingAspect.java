package com.lion.be.global.aop;

import com.lion.be.auth.domain.UserPrincipal;
import com.lion.be.global.exception.CustomException;
import com.lion.be.global.exception.ErrorCode;
import com.lion.be.global.service.RateLimitingService;
import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitingAspect {

    private final RateLimitingService rateLimitingService;

    @Around("@annotation(com.lion.be.global.aop.CheckRateLimit)")
    public Object checkRateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        UserPrincipal userPrincipal = findUserPrincipal(joinPoint.getArgs());
        if (userPrincipal == null) {
            // 인증 정보가 없는 경우, 로직을 그냥 통과시키거나 예외 처리 가능
            return joinPoint.proceed();
        }

        Bucket bucket = rateLimitingService.resolveBucket(userPrincipal.getId());
        if (bucket.tryConsume(1)) {
            return joinPoint.proceed(); // 메소드 실행 계속
        } else {
            // 예외 발생
            throw new CustomException(ErrorCode.TOO_MANY_API_REQUEST_EXCEPTION);
        }
    }

    private UserPrincipal findUserPrincipal(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof UserPrincipal) {
                return (UserPrincipal) arg;
            }
        }
        return null;
    }

}