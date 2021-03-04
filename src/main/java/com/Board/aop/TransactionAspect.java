package com.Board.aop;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.interceptor.MatchAlwaysTransactionAttributeSource;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import java.util.Collections;
import java.util.List;

@Configuration
public class TransactionAspect {

    @Autowired
    private TransactionManager transactionManager;

    private static final String EXPRESSION = "execution(* com.Board..service.*Impl.*(..))";   // 포인트컷, 비즈니스 로직을 수행하는 모든 ServiceImpl 클래스의 모든 메소드를 의미

    @Bean
    public TransactionInterceptor transactionAdvice() {
        List<RollbackRuleAttribute> rollbackRules = Collections.singletonList(new RollbackRuleAttribute(Exception.class));   // 롤백을 수행하는 규칙

        RuleBasedTransactionAttribute transactionAttribute = new RuleBasedTransactionAttribute();

        transactionAttribute.setRollbackRules(rollbackRules);
        transactionAttribute.setName("*");   // 트랜잭션의 이름을 설정

        MatchAlwaysTransactionAttributeSource attributeSource = new MatchAlwaysTransactionAttributeSource();
        attributeSource.setTransactionAttribute(transactionAttribute);

        return new TransactionInterceptor(transactionManager, attributeSource);
    }

    @Bean
    public Advisor transactionAdvisor() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();   // AOP의 포인트컷을 설정
        pointcut.setExpression(EXPRESSION);   // EXPRESSION 변수에 지정한 ServiceImpl 클래스의 모든 메소드를 대상으로 설정

        return new DefaultPointcutAdvisor(pointcut, transactionAdvice());
    }
}
