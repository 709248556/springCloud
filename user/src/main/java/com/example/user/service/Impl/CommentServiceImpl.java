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
//        PageHelper.startPage(PageContstant.COMMENT_OFFSET,PageContstant.COMENT_LIMIT);
        if (jsonData.containsKey("page") && jsonData.containsKey("limit"))
            PageHelper.startPage(Integer.valueOf(jsonData.get("page").toString()), Integer.valueOf(jsonData.get("limit").toString()));
        if (jsonData.containsKey("name")) {
            jsonData.put("name", "%" + jsonData.get("name") + "%");
        }
        return commentMapper.selective(jsonData);
    }

    @Override
    public int insert(Comment comment) {
        return commentMapper.insert(comment);
    }

    @Override
    public int deleteById(int id) {
        return commentMapper.deleteById(id);
    }
}
