package com.zs.practice1.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * The type Business logging aspect.
 */
@Aspect
@Component
public class BusinessLoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(BusinessLoggingAspect.class);

    /**
     * Service method.
     */
    @Pointcut("execution(* com.zs.practice1.service..*(..))")
    public void serviceMethod(){}

    /**
     * Logging before.
     *
     * @param joinPoint the join point
     */
    @Before("serviceMethod()")
    public void LoggingBefore(JoinPoint joinPoint){
        Logger log = LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringType());
        log.info("Request received in [{}.{}] with arguments: {}",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs()));
    }

    /**
     * Logging after returning.
     *
     * @param joinPoint the join point
     */
    @AfterReturning(value = "serviceMethod()", returning = "result")
    public void LoggingAfterReturning(JoinPoint joinPoint){
        Logger log = LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringType());
        log.info("Successfully executed [{}.{}]",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName());
    }

    /**
     * Logging after exception.
     *
     * @param joinPoint the join point
     * @param ex        the ex
     */
    @AfterThrowing(value = "serviceMethod()" , throwing = "ex")
    public void LoggingAfterException(JoinPoint joinPoint, Throwable ex){
        Logger log = LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringType());
        log.error("Exception in [{}.{}] with cause = {}",
                joinPoint.getSignature().getDeclaringType().getSimpleName(),
                joinPoint.getSignature().getName(),
                ex.getMessage() != null ? ex.getMessage() : "NULL");
    }

}
