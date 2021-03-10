package com.Board.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentDTO extends CommonDTO {
    private Long idx;

    private Long boardIdx;

    private String content;

    private String writer;
}
