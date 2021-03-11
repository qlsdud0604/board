package com.Board;

import com.Board.domain.CommentDTO;
import com.Board.service.CommentService;

import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CommentTests {

    @Autowired
    private CommentService commentService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void registerComments() {
        int number = 20;


        CommentDTO params = new CommentDTO();

        params.setBoardIdx((long) 100);
        params.setContent("21번 댓글 추가");
        params.setWriter("21번 작성자");

        commentService.registerComment(params);

    }

    @Test
    public void deleteComment() {
        commentService.deleteComment((long) 10);

        getCommentList();
    }

    @Test
    public void getCommentList() {
        CommentDTO params = new CommentDTO();
        params.setBoardIdx((long) 100);

        commentService.getCommentList(params);
    }
}
