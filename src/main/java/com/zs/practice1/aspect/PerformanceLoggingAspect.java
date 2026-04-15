package com.zs.practice1.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Before;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;


/**
 * The type Performance logging aspect.
 */
@Aspect
@Component
public class PerformanceLoggingAspect {
    private static final Logger log= LoggerFactory.getLogger(PerformanceLoggingAspect.class);

    /**
     * Controller aspect.
     */
    @Pointcut("execution(* com.zs.practice1.controller..*(..))")
    public void controllerAspect(){}

    /**
     * Log endpoint.
     *
     * @param joinPoint the join point
     */
    @Before("controllerAspect()")
    public void logEndpoint(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            log.debug("{} endpoint was called", request.getRequestURI());
        } else {
            log.debug("Endpoint method {} was called", joinPoint.getSignature().getName());
        }
    }

    /**
     * Measure execution time object.
     *
     * @param joinPoint the join point
     * @return the object
     * @throws Throwable the throwable
     */
    @Around("controllerAspect()")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime=System.currentTimeMillis();
        try{
            return joinPoint.proceed();
        } finally{
            long endTime=System.currentTimeMillis();
            long executionTime=endTime-startTime;

            log.info("Method [{}] executed in {} ms",
                    joinPoint.getSignature().getName(),
                    executionTime);
        }
    }
}
