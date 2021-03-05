package com.Board.mapper;

import com.Board.domain.BoardDTO;
import com.Board.paging.Criteria;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BoardMapper {
    public int insertBoard(BoardDTO params);   // 게시글을 생성하는 INSERT 쿼리를 호출하는 메소드

    public BoardDTO selectBoardDetail(Long idx);   // 하나의 게시글을 조회하는 SELECT 쿼리를 호출하는 메소드

    public int updateBoard(BoardDTO params);   // 게시글을 수정하는 UPDATE 쿼리를 호출하는 메소드

    public int deleteBoard(Long idx);   // 게시글을 삭제하는 DELETE 쿼리를 호출하는 메소드

    public List<BoardDTO> selectBoardList(BoardDTO params);   // 게시글 목록을 조회하는 SELECT 쿼리를 호출하는 메소드

    public int selectBoardTotalCount(BoardDTO params);   // 삭제 여부가 'N'으로 지정된 게시글의 개수를 조회하는 SELECT 쿼리를 호출하는 메소드

    public boolean cntPlus(Long idx);   // 게시글의 조회수를 카운트 하는 UPDATE 쿼리를 호출하는 메소드
}

/**
 * 1. 위 인터페이스는 데이터베이스와 통신 역할을 하는 인터페이스임
 * 2. 마이바티스 인터페이스에서는 "@Mapper"만 지정해주면 XML Mapper 에서 메소드의 이름과 일치하는 SQL 문을 찾아 실행해줌
 */
