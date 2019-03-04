package com.example.common.hystrix;

import com.example.common.entity.Comment;
import com.example.common.entity.User;
import com.example.common.enums.UserClientEnum;
import com.example.common.feign.UserClient;
import com.example.common.response.RestResponse;
import com.example.common.util.JsonData;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserFallback implements UserClient {
    @Override
    public RestResponse<List<Comment>> getCommentList(int goodsId,int commentType) {
        return new RestResponse(UserClientEnum.GET_COMMENTLIST);
    }

    @Override
    public RestResponse<Integer> getCollectCountNum(int goodsId, int userId,int collectType) {
        return new RestResponse(UserClientEnum.GET_COLLECTCOUNTNUM);
    }

    @Override
    public RestResponse<List<User>> getUser(int userId) {
        return new RestResponse(UserClientEnum.GET_USER);
    }
}
