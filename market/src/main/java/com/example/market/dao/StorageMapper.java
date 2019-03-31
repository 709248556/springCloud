package com.example.market.dao;

import com.example.common.entity.Storage;
import org.apache.ibatis.annotations.Param;

public interface StorageMapper {
    Storage findByKey(@Param("key")String key, @Param("deleted")int deleted);
    int insert(Storage storageInfo);
}
