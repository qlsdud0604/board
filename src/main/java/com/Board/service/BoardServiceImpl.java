package com.Board.service;

import com.Board.domain.BoardDTO;
import com.Board.mapper.BoardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class BoardServiceImpl implements BoardService {

    @Autowired
    private BoardMapper boardMapper;


    @Override
    public boolean registerBoard(BoardDTO params) {
        int queryResult = 0;

        if (params.getIdx() == null) {
            queryResult = boardMapper.insertBoard(params);
        } else {
            queryResult = boardMapper.updateBoard(params);
        }

        return (queryResult == 1) ? true : false;
    }

    @Override
    public BoardDTO getBoardDetail(Long idx) {
        return boardMapper.selectBoardDetail(idx);
    }

    @Override
    public boolean deleteBoard(Long idx) {
        int queryResult = 0;

        BoardDTO board = boardMapper.selectBoardDetail(idx);

        if (board != null && "N".equals(board.getDeleteYn())) {
            queryResult = boardMapper.deleteBoard(idx);
        }

        return (queryResult != 1) ? true : false;
    }

    @Override
    public List<BoardDTO> getBoardList() {
        List<BoardDTO> boardList = Collections.emptyList();

        int boardTotalCount = boardMapper.selectBoardTotalCount();

        if (boardTotalCount > 0) {
            boardList = boardMapper.selectBoardList();
        }

        return boardList;
    }
}

/**
 * 1. "@Service"를 클래스 상단에 작성해 줌으로써 해당 클래스가 비즈니스 로직을 담당하는 서비스 클래스임을 명시
 */