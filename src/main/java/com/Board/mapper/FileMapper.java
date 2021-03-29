package com.Board.mapper;

import com.Board.domain.FileDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FileMapper {
    public int insertFile(List<FileDTO> fileList);   // 파일 정보를 저장하는 INSERT 쿼리를 호출

    public FileDTO selectFileDetail(Long idx);   // 파일 번호(idx)에 해당하는 파일의 상세 정보 조회

    public int deleteFile(Long boardIdx);   // 파일을 삭제

    public List<FileDTO> selectFileList(Long boardIdx);   // 특정 게시글에 포함된 파일 목록을 조회하는 SELECT 쿼리를 호출

    public int selectFileTotalCount(Long boardIdx);   // 특정 게시글에 포함된 파일 개수를 조회하는 SELECT 쿼리를 호출

    public int undeleteFile(List<Long> idxs);
}
