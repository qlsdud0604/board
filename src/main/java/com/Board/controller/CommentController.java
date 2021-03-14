package com.Board.controller;

import com.Board.adapter.GsonLocalDateTimeAdapter;
import com.Board.domain.CommentDTO;
import com.Board.service.CommentService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class CommentController {

    @Autowired
    private CommentService commentService;

    @RequestMapping(value = {"/comments", "/comments/{idx}"}, method = {RequestMethod.POST, RequestMethod.PATCH})
    public JsonObject registerComment(@PathVariable(value = "idx", required = false) Long idx, @RequestBody final CommentDTO params) {

        JsonObject jsonObject = new JsonObject();

        try {
            boolean isRegistered = commentService.registerComment(params);
            jsonObject.addProperty("result", isRegistered);

        } catch (DataAccessException e) {
            jsonObject.addProperty("message", "데이터베이스 처리 과정에 문제가 발생하였습니다.");
        } catch (Exception e) {
            jsonObject.addProperty("message", "시스템에 문제가 발생하였습니다.");
        }
        return jsonObject;
    }

    @GetMapping(value = "/comments/{boardIdx}")
    public JsonObject getCommentList(@PathVariable("boardIdx") Long boardIdx, @ModelAttribute("params") CommentDTO params) {

        JsonObject jsonObject = new JsonObject();

        List<CommentDTO> commentList = commentService.getCommentList(params);

        if (CollectionUtils.isEmpty(commentList) == false) {
            Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new GsonLocalDateTimeAdapter()).create();

            JsonArray jsonArr = gson.toJsonTree(commentList).getAsJsonArray();   // commentList에 담긴 댓글을 JsonArray 타입으로 변환

            jsonObject.add("commentList", jsonArr);   // JSON 객체에 "commentList"라는 프로퍼티로 추가해서 리턴
        }
        return jsonObject;
    }
}

/**
 * 1. "@PathVariable"은 URI에 파라미터로 전달받을 변수를 지정할 수 있음
 * 2. JsonArray 객체 자체를 리턴하지 않고 JSON 객체에 추가하는 이유는 JSON 객체레 담는 형태로 처리하면 JSON 객체에 여러 가지 타입의 데이터를 추가할 수 있기 때문
 */
