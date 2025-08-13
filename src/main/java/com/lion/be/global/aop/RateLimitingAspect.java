package com.lion.be.global.aop;

import com.lion.be.auth.domain.UserPrincipal;
import com.lion.be.chat.domain.dto.ChatMessageRequest;
import com.lion.be.global.exception.CustomException;
import com.lion.be.global.exception.ErrorCode;
import com.lion.be.global.service.RateLimitingService;
import com.lion.be.user.domain.entity.User;
import com.lion.be.user.service.UserReadService;
import io.github.bucket4j.Bucket;
import java.security.Principal;
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
    private final UserReadService userReadService;

    // === 피드 생성 제한을 처리하는 Advice ===
    @Around("@annotation(com.lion.be.global.aop.CheckRateLimitFeed)")
    public Object checkFeedRateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        UserPrincipal userPrincipal = findUserPrincipal(joinPoint.getArgs());
        if (userPrincipal == null) {
            // 인증 정보가 없으면 로직을 통과
            return joinPoint.proceed();
        }

        Bucket bucket = rateLimitingService.resolveFeedBucket(userPrincipal.getId());
        if (bucket.tryConsume(1)) {
            return joinPoint.proceed();
        } else {
            throw new CustomException(ErrorCode.TOO_MANY_API_REQUEST_EXCEPTION);
        }
    }

    // === 피드 댓글 생성 제한을 처리하는 Advice ===
    @Around("@annotation(com.lion.be.global.aop.CheckRateLimitFeedComment)")
    public Object checkFeedCommentRateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        UserPrincipal userPrincipal = findUserPrincipal(joinPoint.getArgs());
        Long feedId = findArgumentByName(joinPoint, "feedId", Long.class);

        if (userPrincipal == null || feedId == null) {
            // 필요한 정보가 없으면 로직을 통과 (혹은 예외 처리)
            return joinPoint.proceed();
        }

        Bucket bucket = rateLimitingService.resolveFeedCommentBucket(feedId, userPrincipal.getId());
        if (bucket.tryConsume(1)) {
            return joinPoint.proceed();
        } else {
            throw new CustomException(ErrorCode.TOO_MANY_API_REQUEST_EXCEPTION);
        }
    }

    // === 채팅 메시지 제한을 처리하는 Advice 추가 ===
    @Around("@annotation(com.lion.be.global.aop.CheckRateLimitChat)")
    public Object checkChatRateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        // 1. 메소드 인자에서 필요한 정보 추출
        UserPrincipal userPrincipal = findUserPrincipal(joinPoint.getArgs());
        // @DestinationVariable("roomId") 값을 이름으로 찾기
        Long roomId = findArgumentByName(joinPoint, "roomId", Long.class);

        if (userPrincipal == null || roomId == null) {
            // 필수 정보가 없으면 로직을 통과시킴 (또는 예외 발생)
            return joinPoint.proceed();
        }

        Long userId = userPrincipal.getId();

        // 2. 버킷을 가져와서 토큰 소비 시도
        Bucket bucket = rateLimitingService.resolveChatBucket(roomId, userId);
        if (bucket.tryConsume(1)) {
            return joinPoint.proceed(); // 성공 시 메소드 실행
        } else {
            // 실패 시 예외 발생. 클라이언트는 이 에러를 받고 재시도 UI를 보여줄 수 있음
            throw new CustomException(ErrorCode.TOO_MANY_API_REQUEST_EXCEPTION);
        }
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