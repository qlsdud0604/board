package com.Board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {

    @GetMapping(value = "/message")
    public Map<Integer, Object> testByResponseBody() {

        Map<Integer, Object> members = new HashMap<>();

        for (int i = 1; i <= 20; i++) {
            Map<String, Object> member = new HashMap<>();

            member.put("idx", i);
            member.put("nickname", i + "길동");
            member.put("height", i + 20);
            member.put("weight", i + 30);
            members.put(i, member);
        }
        return members;
    }
}

/**
 * 1. 메소드에 "@ResponseBody"가 붙게 되면 스프링의 메시지 컨버터에 의해 리턴 타임에 해당하는 데이터 자체를 리턴함
 * 2. 클래스에 "@RestController"가 붙게 되면 클래스에 선언된 모든 메소드는 "@ResponseBody"가 적용됨
 */
