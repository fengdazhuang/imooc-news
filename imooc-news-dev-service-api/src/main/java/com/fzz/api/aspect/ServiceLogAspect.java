package com.fzz.api.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class ServiceLogAspect {

    final static Logger logger=LoggerFactory.getLogger(ServiceLogAspect.class);

    @Around("execution(* com.fzz.*.service.impl..*.*(..))")
    public Object recordTimeOfService(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.info("===开始执行："+joinPoint.getTarget().getClass()+"=="+joinPoint.getSignature().getName());

        long start=System.currentTimeMillis();
        long end=System.currentTimeMillis();
        long costTime=end-start;
        Object result=joinPoint.proceed();

        if(costTime>3000){
            logger.error("执行花费了：{}",costTime);
        }else if(costTime>2000){
            logger.warn("执行花费了：{}",costTime);
        }else{
            logger.info("执行花费了：{}",costTime);
        }

        return result;

    }
}
