package com.Board;

import com.Board.domain.BoardDTO;
import com.Board.mapper.BoardMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.CollectionUtils;

import java.util.List;

@SpringBootTest
public class MapperTests {

    @Autowired
    private BoardMapper boardMapper;

    @Test
    public void testMultipleInsert() {
        for (int i = 2; i <= 50; i++) {
            BoardDTO params = new BoardDTO();
            params.setTitle(i + "번 게시글 제목");
            params.setContent(i + "번 게시글 내용");
            params.setWriter(i + "번 게시글 작성자");
            boardMapper.insertBoard(params);
        }
    }

    @Test
    public void testOfSelectDetail() {
        BoardDTO board = boardMapper.selectBoardDetail((long) 1);
        try {
            String boardJson = new ObjectMapper().writeValueAsString(board);


            System.out.println("=========================");
            System.out.println(boardJson);
            System.out.println("=========================");

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testOfUpdate() {
        BoardDTO params = new BoardDTO();
        params.setTitle("1번 게시글 제목을 수정");
        params.setContent("1번 게시글 내용을 수정");
        params.setWriter("1번 게시글 작성자를 수정");
        params.setIdx((long) 1);

        int result = boardMapper.updateBoard(params);

        if (result == 1) {
            BoardDTO board = boardMapper.selectBoardDetail((long) 1);

            try {
                String boardJson = new ObjectMapper().writeValueAsString(board);

                System.out.println("=========================");
                System.out.println(boardJson);
                System.out.println("=========================");
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testOfDelete() {
        int result = boardMapper.deleteBoard((long) 1);

        if (result == 1) {
            BoardDTO params = boardMapper.selectBoardDetail((long) 1);
            try {
                String boardJson = new ObjectMapper().writeValueAsString(params);

                System.out.println("=========================");
                System.out.println(boardJson);
                System.out.println("=========================");
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

}
