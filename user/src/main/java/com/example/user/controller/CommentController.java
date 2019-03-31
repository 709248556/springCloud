package com.example.user.controller;

import com.example.common.annotation.RequiresPermissionsDesc;
import com.example.common.entity.Comment;
import com.example.common.enums.RestEnum;
import com.example.common.exception.IllegalParamsException;
import com.example.common.response.RestResponse;
import com.example.common.util.JsonData;
import com.example.user.service.CommentService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @RequiresPermissions("admin:comment:list")
    @RequiresPermissionsDesc(menu={"商品管理" , "评论管理"}, button="查询")
    @GetMapping("comment/list")
    public RestResponse list(JsonData jsonData) {
        jsonData.put("notType",2); // type=2 是订单商品回复，这里过滤
        if(jsonData.containsKey("valueId")) jsonData.put("commentType",0);
        List<Comment> brandList = commentService.selective(jsonData);
        long total = PageInfo.of(brandList).getTotal();
        Map<String, Object> data = new HashMap<>();
        data.put("total", total);
        data.put("items", brandList);
        return new RestResponse(data);
    }

    @PostMapping("comment/delete")
    public Object delete(@RequestBody Comment comment) {
        Integer id = comment.getId();
        if (id == null) {
            return new RestResponse<>(RestEnum.BADARGUMENT);
        }
        if(commentService.deleteById(id) == 0) return new RestResponse<>(RestEnum.SERIOUS);
        return new RestResponse<>();
    }
}
