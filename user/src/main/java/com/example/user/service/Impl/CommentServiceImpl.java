package com.example.user.service.Impl;

import com.example.common.constants.PageContstant;
import com.example.common.entity.Comment;
import com.example.common.util.JsonData;
import com.example.user.dao.CommentMapper;
import com.example.user.service.CommentService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("commentService")
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Override
    public List<Comment> selective(JsonData jsonData) {
        PageHelper.startPage(PageContstant.COMMENT_OFFSET,PageContstant.COMENT_LIMIT);
        return commentMapper.selective(jsonData);
    }
}
