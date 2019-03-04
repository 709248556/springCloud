package com.example.user.controller;

import com.example.common.entity.Comment;
import com.example.common.enums.RestEnum;
import com.example.common.exception.IllegalParamsException;
import com.example.common.response.RestResponse;
import com.example.common.util.JsonData;
import com.example.user.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@Slf4j
@RestController
public class CommentController {
    @Autowired
    private CommentService commentService;

    @GetMapping("/getCommentList")
    public RestResponse<List<Comment>> getCommentList(JsonData jsonData){
        List<Comment> commentsList = commentService.selective(jsonData);
        return new RestResponse(commentsList);
    }
}
