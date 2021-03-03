package com.Board.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component   // 개발자가 직접 정의한 클래스를 빈으로 등록하기 위한 어노테이션
@Aspect   // AOP 기능을 하는 클래스에 지정하는 어노테이션
@Slf4j
public class LoggerAspect {

    @Around("execution(* com.Board..controller.*Controller.*(..)) or execution(* com.Board..service.*Impl.*(..)) or execution(* com.Board..mapper.*Mapper.*(..))")
    public Object printLog(ProceedingJoinPoint joinPoint) throws Throwable {

        String type = "";
        String name = joinPoint.getSignature().getDeclaringTypeName();   // "getSignature()" 메소드는 실행되는 대상 객체의 메소드에 대한 정보를 가지고 옴

        if (name.contains("Controller") == true)
            type = "Controller ===> ";

        else if (name.contains("Service") == true)
            type = "ServiceImpl ===> ";

        else if (name.contains("Mapper") == true)
            type = "Mapper ===> ";

        log.debug(type + name + "." + joinPoint.getSignature().getName() + "()");

        return joinPoint.proceed();
    }
}

/**
 * 1. AOP는 여러 개의 핵심 비즈니스 로직 외에 공통으로 처리되어야 하는 코드를 별도로 분리해서 하나의 단위로 묶는 모듈화의 개념
 * 2. AOP에서 관점은 "핵심적인 관점"과 "부가적인 관점"으로 나눌 수 있음
 * 3. 핵심적인 관점을 핵심 비즈니스 로직을 의미, 부가적인 관점은 공통으로 처리되어야 하는 코드를 의미
 */
