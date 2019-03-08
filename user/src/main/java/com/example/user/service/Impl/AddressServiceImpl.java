package com.example.user.service.Impl;

import com.example.common.entity.Address;
import com.example.common.util.JsonData;
import com.example.user.dao.AddressMapper;
import com.example.user.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("addressService")
public class AddressServiceImpl implements AddressService{

    @Autowired
    private AddressMapper addressMapper;

    @Override
    public List<Address> selective(JsonData jsonData) {
        return addressMapper.selective(jsonData);
    }

    @Override
    public int updative(JsonData jsonData) {
        return addressMapper.updative(jsonData);
    }

    @Override
    public int insertive(JsonData jsonData) {
        return addressMapper.insertive(jsonData);
    }

    @Override
    public int deletive(JsonData jsonData) {
        return addressMapper.deletive(jsonData);
    }
}
