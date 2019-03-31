package com.example.market.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface StorageService {
    /**
     * 存储一个文件对象
     *
     * @param file   文件
     * @param contentLength 文件长度
     * @param contentType   文件类型
     * @param fileName      文件索引名
     */
    String store(MultipartFile file, long contentLength, String contentType, String fileName);
}
