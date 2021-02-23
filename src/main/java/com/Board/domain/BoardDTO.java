package com.Board.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BoardDTO {
    private Long idx;   // 번호 (PK)

    private String title;   // 제목

    private String content;   // 내용

    private String writer;   // 작성자

    private int viewCnt;   // 조회 수

    private String noticeYn;   // 공지 여부

    private String secretYn;   // 비밀 여부

    private LocalDateTime insertTime;   // 등록일

    private LocalDateTime updateTime;   // 수정일

    private LocalDateTime deleteTime;   // 삭제일
}

/**
 * 1. 위 클래스는 MySQL 에 저장된 게시판 테이블의 구조화 역할을 하는 클래스임
 */