package com.example.user.service;

import com.example.common.entity.Comment;
import com.example.common.util.JsonData;

import java.util.List;

public interface CommentService {
    List<Comment> selective(JsonData jsonData);
}
