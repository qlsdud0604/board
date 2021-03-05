package com.Board.paging;

public class PaginationInfo {

    private Criteria criteria;   // 페이징 계산에 필요한 파라미터들이 담긴 클래스

    private int totalRecordCount;   // 전체 데이터 개수

    private int totalPageCount;   // 전체 페이지 개수

    private int firstPage;   // 페이지 리스트의 첫 페이지 번호

    private int lastPage;   // 페이지 리스트의 마지막 페이지 번호

    private int firstRecordIndex;   // SQl의 조건절에 사용되는 첫 RNUM

    private int lastRecordIndex;   // SQl의 조건절에 사용되는 마지막 RNUM

    private boolean hasPreviousPage;   // 이전 페이지 존재 여부

    private boolean hasNextPage;   // 다음 페이지 존재 여부


    public PaginationInfo(Criteria criteria) {
        if (criteria.getCurrentPageNo() < 1)
            criteria.setCurrentPageNo(1);

        if (criteria.getRecordsPerPage() < 1 || criteria.getRecordsPerPage() > 100)
            criteria.setRecordsPerPage(10);

        if (criteria.getPageSize() < 5 || criteria.getPageSize() > 20)
            criteria.setPageSize(10);

        this.criteria = criteria;
    }

    public void setTotalRecordCount(int totalRecordCount) {
        this.totalRecordCount = totalRecordCount;

        if (totalRecordCount > 0)
            calculation();
    }

    private void calculation() {
        totalPageCount = ((totalRecordCount - 1) / criteria.getRecordsPerPage()) + 1;

        if (criteria.getCurrentPageNo() > totalPageCount)
            criteria.setCurrentPageNo(totalPageCount);

        firstPage = ((criteria.getCurrentPageNo() - 1) / criteria.getPageSize()) * criteria.getPageSize() + 1;

        lastPage = firstPage + criteria.getPageSize() - 1;

        if (lastPage > totalPageCount)
            lastPage = totalPageCount;

        firstRecordIndex = (criteria.getCurrentPageNo() - 1) * criteria.getRecordsPerPage();

        lastRecordIndex = criteria.getCurrentPageNo() * criteria.getRecordsPerPage();

        hasPreviousPage = firstPage != 1;

        hasNextPage = (lastPage * criteria.getRecordsPerPage()) < totalPageCount;
    }
}

/**
 * 위 클래스는 페이징 정보를 계산하기 위한 용도의 클래스
 */
