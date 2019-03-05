package com.example.market.service;

import com.example.common.entity.Cart;
import com.example.common.util.JsonData;

import java.util.List;

public interface CartService {
    List<Cart> selective(JsonData jsonData);
    int insertive(JsonData jsonData);
    int updative(JsonData jsonData);
}
