package com.Board.service;

import com.Board.domain.CommentDTO;

import java.util.List;

public interface CommentService {

    public boolean registerComment(CommentDTO params);

    public boolean deleteComment(Long idx);

    public List<CommentDTO> getCommentList(CommentDTO params);
}
