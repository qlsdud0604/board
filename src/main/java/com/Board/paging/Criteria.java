package com.Board.paging;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Criteria {
    private int currentPageNo;   // 현재 페이지 번호

    private int recordsPerPage;   // 페이지당 출력할 데이터 개수

    private int pageSize;   // 화면 하단에 출력할 페이지 사이즈

    private String searchKeyword;   // 검색 키워드

    private String searchType;   // 검색 유형

    public Criteria() {
        this.currentPageNo = 1;
        this.recordsPerPage = 10;
        this.pageSize = 10;
    }
}
