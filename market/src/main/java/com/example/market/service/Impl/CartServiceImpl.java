package com.example.market.service.Impl;

import com.example.common.entity.Cart;
import com.example.common.util.JsonData;
import com.example.market.dao.CartMapper;
import com.example.market.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("cartService")
public class CartServiceImpl implements CartService {
    @Autowired
    private CartMapper cartMapper;
    @Override
    public List<Cart> selective(JsonData jsonData) {
        return cartMapper.selective(jsonData);
    }

    @Override
    public int insertive(JsonData jsonData) {
        return cartMapper.insertive(jsonData);
    }

    @Override
    public int updative(JsonData jsonData) {
        return cartMapper.updative(jsonData);
    }
}
