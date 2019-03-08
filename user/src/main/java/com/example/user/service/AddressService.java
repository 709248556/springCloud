package com.example.user.service;

import com.example.common.entity.Address;
import com.example.common.util.JsonData;

import java.util.List;

public interface AddressService {
    List<Address> selective(JsonData jsonData);
    int updative(JsonData jsonData);
    int insertive(JsonData jsonData);
    int deletive(JsonData jsonData);
}
