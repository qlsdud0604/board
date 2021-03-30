package com.Board.controller;

import com.Board.constant.Method;
import com.Board.domain.BoardDTO;
import com.Board.domain.FileDTO;
import com.Board.service.BoardService;
import com.Board.util.UiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Controller
public class BoardController extends UiUtils {

    @Autowired
    BoardService boardService;

    @GetMapping(value = "/board/write.do")
    public String openBoardWrite(@ModelAttribute("params") BoardDTO params, @RequestParam(value = "idx", required = false) Long idx, Model model) {
        if (idx == null) {
            model.addAttribute("board", new BoardDTO());
        } else {
            BoardDTO board = boardService.getBoardDetail(idx);

            if (board == null || "Y".equals(board.getDeleteYn())) {
                return showMessageWithRedirect("없는 게시글이거나 이미 삭제된 게시글입니다.", "/board/list.do", Method.GET, null, model);
            }
            model.addAttribute("board", board);

            List<FileDTO> fileList = boardService.getFileList(idx);

            model.addAttribute("fileList", fileList);
        }
        return "board/write";
    }

    @PostMapping(value = "/board/register.do")
    public String registerBoard(@ModelAttribute("params") final BoardDTO params, final MultipartFile[] files, Model model) {

        Map<String, Object> pagingParams = getPagingParams(params);

        try {
            boolean isRegistered = boardService.registerBoard(params, files);

            if (isRegistered == false) {
                return showMessageWithRedirect("게시글 등록에 실패하였습니다.", "/board/list.do", Method.GET, pagingParams, model);
            }
        } catch (DataAccessException e) {
            return showMessageWithRedirect("데이터베이스 처리 과정에 문제가 발생하였습니다.", "/board/list.do", Method.GET, pagingParams, model);
        } catch (Exception e) {
            return showMessageWithRedirect("시스템에 문제가 발생하였습니다.", "/board/list.do", Method.GET, pagingParams, model);
        }
        return showMessageWithRedirect("게시글 등록이 완료되었습니다.", "/board/list.do", Method.GET, pagingParams, model);
    }

    @GetMapping(value = "/board/list.do")
    public String openBoardList(@ModelAttribute("params") BoardDTO params, Model model) {
        List<BoardDTO> boardList = boardService.getBoardList(params);
        model.addAttribute("boardList", boardList);

        return "board/list";
    }

    @GetMapping(value = "/board/view.do")
    public String openBoardDetail(@ModelAttribute("params") BoardDTO params, @RequestParam(value = "idx", required = false) Long idx, Model model) {

        if (idx == null) {
            return showMessageWithRedirect("올바르지 않은 접근입니다.", "/board/list.do", Method.GET, null, model);
        }

        BoardDTO board = boardService.getBoardDetail(idx);

        if (board == null || "Y".equals(board.getDeleteYn())) {

            return showMessageWithRedirect("없는 게시글이거나 이미 삭제된 게시글입니다.", "/board/list.do", Method.GET, null, model);
        }
        model.addAttribute("board", board);

        boardService.cntPlus(idx);

        return "board/view";
    }

    @PostMapping(value = "/board/delete.do")
    public String deleteBoard(@ModelAttribute("params") BoardDTO params, @RequestParam(value = "idx", required = false) Long idx, Model model) {
        if (idx == null) {
            return showMessageWithRedirect("올바르지 않은 접근입니다.", "/board/list.do", Method.GET, null, model);
        }
        Map<String, Object> pagingParams = getPagingParams(params);

        try {
            boolean isDeleted = boardService.deleteBoard(idx);

            if (isDeleted == false) {
                return showMessageWithRedirect("게시글 삭제에 실패하였습니다.", "/board/list.do", Method.GET, pagingParams, model);
            }
        } catch (DataAccessException e) {
            return showMessageWithRedirect("데이터베이스 처리 과정에 문제가 발생하였습니다.", "/board/list.do", Method.GET, pagingParams, model);
        } catch (Exception e) {
            return showMessageWithRedirect("시스템에 문제가 발생하였습니다.", "/board/list.do", Method.GET, pagingParams, model);
        }
        return showMessageWithRedirect("게시글 삭제가 완료되었습니다.", "/board/list.do", Method.GET, pagingParams, model);
    }
}

/**
 * 1. 클래스 상단에 "@Controller"를 작성 함으로써, 해당 클래스가 UI를 담당하는 컨트롤러 클래스임을 명시
 * 2. 파라미터 Model은 데이터를 뷰로 전달하는 데 사용
 * 3. addAttribute() 메소드를 이용해서 화면(html)으로 데이터 전달 가능
 * 4. "@RequestParam"은 뷰(화면)에서 전달받은 파라미터를 처리하는데 사용
 */
