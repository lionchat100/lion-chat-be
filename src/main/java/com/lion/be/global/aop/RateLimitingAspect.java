package com.lion.be.global.aop;

import com.lion.be.auth.domain.UserPrincipal;
import com.lion.be.chat.message.domain.dto.ChatMessageRequest;
import com.lion.be.global.exception.CustomException;
import com.lion.be.global.exception.ErrorCode;
import com.lion.be.global.service.RateLimitingService;
import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitingAspect {

    private final RateLimitingService rateLimitingService;

    @Around("@annotation(com.lion.be.global.aop.CheckRateLimitFeed)")
    public Object checkFeedRateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        UserPrincipal userPrincipal = findUserPrincipal(joinPoint.getArgs());
        if (userPrincipal == null) {
            return joinPoint.proceed();
        }

        Bucket bucketPerSecond = rateLimitingService.resolveFeedBucketPerSecond(userPrincipal.getId());
        Bucket bucketPerMinute = rateLimitingService.resolveFeedBucketPerMinute(userPrincipal.getId());

        if (!bucketPerSecond.tryConsume(1)) {
            // 초당 제한에 대한 커스텀 예외
            throw new CustomException(ErrorCode.TOO_MANY_REQUESTS_PER_SECONDS_EXCEPTION);
        }

        if (!bucketPerMinute.tryConsume(1)) {
            // 분당 제한에 대한 커스텀 예외
            throw new CustomException(ErrorCode.TOO_MANY_REQUESTS_PER_MINUTE_EXCEPTION); // 새로운 예외 코드
        }

        return joinPoint.proceed();
    }

    @Around("@annotation(com.lion.be.global.aop.CheckRateLimitFeedComment)")
    public Object checkFeedCommentRateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        UserPrincipal userPrincipal = findUserPrincipal(joinPoint.getArgs());
        Long feedId = findArgumentByName(joinPoint, "feedId", Long.class);

        if (userPrincipal == null || feedId == null) {
            return joinPoint.proceed();
        }

        Bucket bucketPerSecond = rateLimitingService.resolveFeedCommentBucketPerSecond(feedId, userPrincipal.getId());
        Bucket bucketPerMinute = rateLimitingService.resolveFeedCommentBucketPerMinute(feedId, userPrincipal.getId());

        if (!bucketPerSecond.tryConsume(1)) {
            throw new CustomException(ErrorCode.TOO_MANY_REQUESTS_PER_SECONDS_EXCEPTION);
        }

        if (!bucketPerMinute.tryConsume(1)) {
            throw new CustomException(ErrorCode.TOO_MANY_REQUESTS_PER_MINUTE_EXCEPTION);
        }

        return joinPoint.proceed();
    }

    @Around("@annotation(com.lion.be.global.aop.CheckRateLimitChat)")
    public Object checkChatRateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        UserPrincipal userPrincipal = findUserPrincipal(joinPoint.getArgs());

        Long roomId = null;
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof ChatMessageRequest) {
                roomId = ((ChatMessageRequest) arg).chatRoomId();
                break;
            }
        }

        if (roomId == null) {
            roomId = findArgumentByName(joinPoint, "roomId", Long.class);
        }

        if (userPrincipal == null || roomId == null) {
            return joinPoint.proceed();
        }

        Long userId = userPrincipal.getId();

        Bucket burstBucket = rateLimitingService.resolveChatBucketBurst(roomId, userId);
        Bucket sustainedBucket = rateLimitingService.resolveChatBucketSustained(roomId, userId);

        if (!sustainedBucket.tryConsume(1)) {
            throw new CustomException(ErrorCode.TOO_MANY_REQUESTS_PER_SECONDS_EXCEPTION);
        }

        if (!burstBucket.tryConsume(1)) {
            throw new CustomException(ErrorCode.TOO_MANY_REQUESTS_PER_MINUTE_EXCEPTION);
        }

        return joinPoint.proceed();
    }

    private <T> T findArgumentByName(ProceedingJoinPoint joinPoint, String name, Class<T> returnType) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < parameterNames.length; i++) {
            if (parameterNames[i].equals(name) && returnType.isInstance(args[i])) {
                return returnType.cast(args[i]);
            }
        }
        return null;
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