package com.Board.service;

import com.Board.domain.BoardDTO;
import com.Board.paging.Criteria;

import java.util.List;

public interface BoardService {

    public boolean registerBoard(BoardDTO params);

    public BoardDTO getBoardDetail(Long idx);

    public boolean deleteBoard(Long idx);

    public List<BoardDTO> getBoardList(Criteria criteria);

    public boolean cntPlus(Long idx);
}
