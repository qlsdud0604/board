package com.Board.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BoardDTO extends CommonDTO {
    private Long idx;   // 번호 (PK)

    private String title;   // 제목

    private String content;   // 내용

    private String writer;   // 작성자

    private int viewCnt;   // 조회 수

    private String noticeYn;   // 공지 여부

    private String secretYn;   // 비밀 여부

    private String changeYn;   // 파일 변경 여부

    private List<Long> fileIdxs;   // 파일 인덱스 리스트

}

/**
 * 1. 위 클래스는 MySQL 에 저장된 게시판 테이블의 구조화 역할을 하는 클래스임
 */