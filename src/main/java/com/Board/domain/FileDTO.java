package com.Board.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileDTO extends CommonDTO{

    private Long idx;   // 파일 번호

    private Long boardIdx;   // 게시글 번호

    private String originalName;   // 원본 파일명

    private String saveName;   // 저장 파일명

    private long size;   // 파일 크기
}
