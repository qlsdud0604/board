package com.Board.service;

import com.Board.domain.BoardDTO;
import com.Board.domain.FileDTO;
import com.Board.mapper.BoardMapper;
import com.Board.mapper.FileMapper;
import com.Board.paging.Criteria;
import com.Board.paging.PaginationInfo;
import com.Board.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

@Service
public class BoardServiceImpl implements BoardService {

    @Autowired
    private BoardMapper boardMapper;

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private FileUtils fileUtils;

    @Override
    public boolean registerBoard(BoardDTO params) {
        int queryResult = 0;

        if (params.getIdx() == null) {
            queryResult = boardMapper.insertBoard(params);
        } else {
            queryResult = boardMapper.updateBoard(params);

            if ("Y".equals(params.getChangeYn())) {
                fileMapper.deleteFile(params.getIdx());

                if (CollectionUtils.isEmpty(params.getFileIdxs()) == false) {
                    fileMapper.undeleteFile(params.getFileIdxs());
                }
            }
        }
        return (queryResult > 0);
    }

    @Override
    public boolean registerBoard(BoardDTO params, MultipartFile[] files) {
        int queryResult = 1;

        if (registerBoard(params) == false)
            return false;

        List<FileDTO> fileList = fileUtils.uploadFiles(files, params.getIdx());

        if (CollectionUtils.isEmpty(fileList) == false) {
            queryResult = fileMapper.insertFile(fileList);

            if (queryResult < 1)
                queryResult = 0;
        }
        return (queryResult > 0);
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

        return (queryResult == 1) ? true : false;
    }

    @Override
    public List<BoardDTO> getBoardList(BoardDTO params) {
        List<BoardDTO> boardList = Collections.emptyList();

        int boardTotalCount = boardMapper.selectBoardTotalCount(params);

        PaginationInfo paginationInfo = new PaginationInfo(params);
        paginationInfo.setTotalRecordCount(boardTotalCount);

        params.setPaginationInfo(paginationInfo);

        if (boardTotalCount > 0) {
            boardList = boardMapper.selectBoardList(params);
        }

        return boardList;
    }

    @Override
    public boolean cntPlus(Long idx) {
        return boardMapper.cntPlus(idx);
    }

    @Override
    public List<FileDTO> getFileList(Long boardIdx) {
        int fileTotalCount = fileMapper.selectFileTotalCount(boardIdx);

        if (fileTotalCount < 1)
            return Collections.emptyList();

        return fileMapper.selectFileList(boardIdx);
    }
}

/**
 * 1. "@Service"를 클래스 상단에 작성해 줌으로써 해당 클래스가 비즈니스 로직을 담당하는 서비스 클래스임을 명시
 */