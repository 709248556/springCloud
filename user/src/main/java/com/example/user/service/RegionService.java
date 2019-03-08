package com.example.user.service;

import com.example.common.entity.Region;
import com.example.common.util.JsonData;
import com.example.user.dao.RegionMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

public interface RegionService {

    List<Region> getAll();

    Region findById(JsonData jsonData);

    List<Region> selective(JsonData jsonData);
}
