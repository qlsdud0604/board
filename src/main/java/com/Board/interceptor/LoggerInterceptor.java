package com.Board.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class LoggerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.debug("");
        log.debug("==================== BEGIN ====================");
        log.debug("Request URI ===> " + request.getRequestURI());

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.debug("==================== END ======================");
        log.debug("");
    }
}

/**
 * 1. 스프링에서 인터셉터는 "HandlerInterceptor" 인터페이스를 상속받아 구현할 수 있음
 * 2. "preHandle" 메소드는 컨트롤러의 메소드에 매핑된 특정 URI를 호출했을 때 컨트롤러에 접근하기 전에 실행되는 메소드
 * 3. "postHandle" 메소드는 컨트롤러를 경유한 다음 화면으로 결과를 전달하기 전에 실행되는 메소드
 */
