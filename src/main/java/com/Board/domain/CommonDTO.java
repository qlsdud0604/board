package com.Board.domain;

import com.Board.paging.Criteria;
import com.Board.paging.PaginationInfo;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommonDTO extends Criteria {

    private PaginationInfo paginationInfo;   // 페이징 정보

    private String deleteYn;   // 삭제 여부

    private LocalDateTime insertTime;   // 등록일

    private LocalDateTime updateTime;   // 수정일

    private LocalDateTime deleteTime;   // 삭제일
}
