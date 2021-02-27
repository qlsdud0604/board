package com.Board.controller;

import com.Board.domain.BoardDTO;
import com.Board.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class BoardController {

    @Autowired
    BoardService boardService;

    @GetMapping(value = "/board/write.do")
    public String openBoardWriter(@RequestParam(value = "idx", required = false) Long idx, Model model) {
        if (idx == null) {
            model.addAttribute("board", new BoardDTO());
        } else {
            BoardDTO board = boardService.getBoardDetail(idx);
            if (board == null) {
                return "redirect:/board/list.do";
            }
            model.addAttribute("board", board);
        }

        return "board/write";
    }

    @PostMapping(value = "/board/register.do")
    public String registerBoard(final BoardDTO params) {
        try {
            boolean isRegistered = boardService.registerBoard(params);

            if (isRegistered == false) {

            }
        } catch (DataAccessException e) {

        } catch (Exception e) {

        }
        return "redirect:/board/list.do";
    }

    @GetMapping(value = "/board/list.do")
    public String openBoardList(Model model) {
        List<BoardDTO> boardList = boardService.getBoardList();
        model.addAttribute("boardList", boardList);

        return "board/list";
    }

    @GetMapping(value = "/board/view.do")
    public String openBoardDetail(@RequestParam(value = "idx", required = false) Long idx, Model model) {
        if (idx == null) {
            return "redirect:/board/list.do";
        }

        BoardDTO board = boardService.getBoardDetail(idx);

        if (board == null || "Y".equals(board.getDeleteYn())) {

            return "redirect:/board/list.do";
        }
        model.addAttribute("board", board);
        boardService.cntPlus(idx);

        return "board/view";
    }

    @PostMapping(value = "/board/delete.do")
    public String deleteBoard(@RequestParam(value = "idx", required = false) Long idx) {
        if (idx == null) {

            return "redirect:/board/list.do";
        }

        try {
            boolean isDeleted = boardService.deleteBoard(idx);

            if (isDeleted == false) {

            }
        } catch (DataAccessException e) {

        } catch (Exception e) {

        }
        return "redirect:/board/list.do";
    }
}

/**
 * 1. 클래스 상단에 "@Controller"를 작성 함으로써, 해당 클래스가 UI를 담당하는 컨트롤러 클래스임을 명시
 * 2. 파라미터 Model은 데이터를 뷰로 전달하는 데 사용
 * 3. addAttribute() 메소드를 이용해서 화면(html)으로 데이터 전달 가능
 * 4. "@RequestParam"은 뷰(화면)에서 전달받은 파라미터를 처리하는데 사용
 */
