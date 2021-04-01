package com.Board.util;

import com.Board.domain.FileDTO;
import com.Board.exception.AttachFileException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class FileUtils {

    private final String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));   // 오늘 날짜

    private final String uploadPath = Paths.get("C:", "develop", "upload", today).toString();   // 업로드 경로

    private final String getRandomString() {   // 서버에 생성할 파일명을 처리할 랜덤 문자열 반환
        return UUID.randomUUID().toString().replace("-", "");
    }

    public List<FileDTO> uploadFiles(MultipartFile[] files, Long boardIdx) {   // 서버에 첨부 파일을 생성하고, 업로드 파일 목록 반환

        List<FileDTO> fileList = new ArrayList<>();

        File dir = new File(uploadPath);
        if (dir.exists() == false)
            dir.mkdirs();

        for (MultipartFile file : files) {
            if (file.getSize() < 1) {
                continue;
            }
            try {
                final String extension = FilenameUtils.getExtension(file.getOriginalFilename());   // 파일 확장자

                final String saveName = getRandomString() + "." + extension;   // 서버에 저장할 파일명

                File target = new File(uploadPath, saveName);   // 업로드 경로에 saveName과 동일한 이름을 가진 파일 생성
                file.transferTo(target);

                /** 파일 정보 저장 */
                FileDTO fileDTO = new FileDTO();
                fileDTO.setBoardIdx(boardIdx);
                fileDTO.setOriginalName(file.getOriginalFilename());
                fileDTO.setSaveName(saveName);
                fileDTO.setSize(file.getSize());

                fileList.add(fileDTO);   // 파일 정보 추가
            } catch (IOException e) {
                throw new AttachFileException("[" + file.getOriginalFilename() + "] failed to save file...");
            } catch (Exception e) {
                throw new AttachFileException("[" + file.getOriginalFilename() + "] failed to save file...");
            }
        }
        return fileList;
    }
}

/**
 * 1. "@Component"는 "@Bean"과 달리 개발자가 직접 작성한 클래스를 스프링 컨테이너에 등록하는 게 사용
 * 2. Path.get 메소드를 이용하면 파리미터로 전달한 여러 개의 문다열을 하나로 연결해서 OS에 해당하는 패턴으로 경로를 리턴해 줌
 */
