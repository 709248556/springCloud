package com.example.user.dao;

import com.example.common.entity.Comment;
import com.example.common.util.JsonData;

import java.util.List;

public interface CommentMapper {
    List<Comment> selective(JsonData jsonData);
}
