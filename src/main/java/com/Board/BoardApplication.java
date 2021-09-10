package com.Board;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BoardApplication {

    public static void main(String[] args) {
        SpringApplication.run(BoardApplication.class, args);
    }

}

/**
 * 1. main 메서드는 SpringApplication.run 메서드를 호출해서 웹 애플리케이션을 실행하는 역할을 함
 * 2. "@SpringBootApplication"은 다음 3가지 애너테이션으로 구성
 * 3. "@EnableAutoConfiguration" : 다양한 설정들의 일부가 자동으로 완료됨
 * 4. "@ComponentScan" : 자동으로 컴포넌트 클래스를 검색하고 스프링 애플리케이션 콘텍스트에 빈으로 등록함
 * 5. "@Configuration" : 해당 애너테이션이 선언된 클래스는 자바 기반의 설정 파일로 인식함
 */