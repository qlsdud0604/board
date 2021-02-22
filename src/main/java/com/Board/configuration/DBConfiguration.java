package com.Board.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;

@Configuration   // "@Configuration"이 지정된 클래스를 자바 기반의 설정 파일로 인식
@PropertySource("classpath:/application.properties")   // 해당 클래스에서 참조할 properties 파일의 위치를 지정
public class DBConfiguration {

    @Autowired   // 빈으로 등록된 객체를 클래스에 주입하는 데 사용
    private ApplicationContext applicationContext;

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    public HikariConfig hikariConfig() {   // 히카리CP 객체를 생성
        return new HikariConfig();
    }

    @Bean
    public DataSource dataSource() {   // 데이터 소스 객체를 생성 (데이터 소스는 커넥션 풀을 지원하기 위한 인터페이스)
        return new HikariDataSource(hikariConfig());
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {   // SqlSessionFactory 객체를 생성
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource());

        return factoryBean.getObject();
    }

    @Bean
    public SqlSessionTemplate sqlSession() throws Exception {   // sqlSession 객체를 생성
        return new SqlSessionTemplate(sqlSessionFactory());
    }
}

/**
 * 1. "ApplicationContext"는 스프링 컨테이너 중 하나, 스프링 컨테이너는 빈의 생성과 사용, 관계, 생명 주기 등을 관리함
 * 2. "@Bean"은 "Configuration"클래스의 메소드 레벨에만 지정이 가능 "@Bean"이 지정된 객체는 컨테이너에 의해 관리되는 빈으로 등록됨
 * 3. "@ConfigurationProperties"는 "@PropertySource"에 지정된 파일에서 prefix 에 해당하는 설정을 읽어 들여 해당 메소드에 매핑함
 * 4. "SqlSessionFactory"는 데이터베이스의 커넥션과 SQL 실행에 대한 모든 것을 갖는 중요한 역할을 함
 * 5. "SqlSessionFactoryBean"은 마이바티스와 스프링의 연동 모듈로 사용됨
 * 6. "SqlSessionTemplate"은 "SqlSessionFactory"를 통해 생성되고, SQL 의 실행에 필요한 모든 메소드를 갖는 객체
 */