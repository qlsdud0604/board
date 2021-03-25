package com.Board.service;

import com.Board.domain.BoardDTO;
import com.Board.domain.FileDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BoardService {

    public boolean registerBoard(BoardDTO params);

    public boolean registerBoard(BoardDTO params, MultipartFile[] files);

    public BoardDTO getBoardDetail(Long idx);

    public boolean deleteBoard(Long idx);

    public List<BoardDTO> getBoardList(BoardDTO params);

    public boolean cntPlus(Long idx);

    public List<FileDTO> getFileList(Long boardIdx);
}
