package com.example.user.dao;

import com.example.common.entity.Region;
import com.example.common.util.JsonData;
import com.fasterxml.jackson.databind.annotation.JsonAppend;

import java.util.List;

public interface RegionMapper {
    List<Region> selective(JsonData jsonData);
}
