package com.Board.mapper;

import com.Board.domain.CommentDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

    public int insertComment(CommentDTO params);   // 댓글을 삽입하는 INSERT 쿼리 호출

    public CommentDTO selectCommentDetail(Long idx);   // 댓글 번호에 해당하는 댓글의 상세 내용을 조회

    public int updateComment(CommentDTO params);   // 댓글을 수정하는 UPDATE 쿼리 호출

    public int deleteComment(Long idx);   //  댓글의 삭제여부를 수정하는 UPDATE 쿼리 호출

    public List<CommentDTO> selectCommentList(CommentDTO params);   // 댓글 목록을 조회하는 SELECT 쿼리 호출

    public int selectCommentTotalCount(CommentDTO params);   // 갯글 개수를 조회하는 SELECT 쿼리 호출
}
