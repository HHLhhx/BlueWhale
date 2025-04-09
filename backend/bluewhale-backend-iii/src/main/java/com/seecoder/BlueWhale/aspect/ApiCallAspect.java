package com.seecoder.BlueWhale.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import static com.seecoder.BlueWhale.util.RedisConstants.API_CALL_COUNT;

@Aspect
@Component
public class ApiCallAspect {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // 拦截 controller 包下所有类的所有方法
    @Before("execution(* com.seecoder.BlueWhale.controller.*.*(..))")
    public void countApiCalls(JoinPoint joinPoint) {
        // 获取类名和方法名，生成 Redis 的键
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        String key = API_CALL_COUNT + className + ":" + methodName;

        // 记录每个时间戳的调用次数
        long timestamp = System.currentTimeMillis();
        redisTemplate.opsForHash().increment(key, String.valueOf(timestamp), 1);
    }

}
