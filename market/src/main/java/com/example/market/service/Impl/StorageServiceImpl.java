package com.example.market.service.Impl;

import com.example.common.entity.Storage;
import com.example.common.util.CharUtil;
import com.example.market.dao.StorageMapper;
import com.example.market.service.StorageService;
import com.example.market.systemConfig.FastDFSFile;
import com.example.market.util.FastDFSClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Date;

@Service("storageService")
@Slf4j
public class StorageServiceImpl implements StorageService {
    @Autowired
    private StorageMapper storageMapper;

    @Override
    public String store(MultipartFile multipartFile, long contentLength, String contentType, String fileName) {
        String key = generateKey(fileName);
        String url = null;
        try {
            url = saveFile(multipartFile);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file " + fileName, e);
        }
        Storage storageInfo = new Storage();
        storageInfo.setName(fileName);
        storageInfo.setSize((int) contentLength);
        storageInfo.setType(contentType);
        storageInfo.setKey(key);
        storageInfo.setUrl(url);
        storageInfo.setAddTime(LocalDateTime.now());
        storageInfo.setUpdateTime(LocalDateTime.now());
        storageMapper.insert(storageInfo);

        return url;
    }

    private String generateKey(String originalFilename) {
        int index = originalFilename.lastIndexOf('.');
        String suffix = originalFilename.substring(index);

        String key = null;
        Storage storageInfo = null;

        do {
            key = CharUtil.getRandomString(20) + suffix;
            storageInfo = storageMapper.findByKey(key, 0);
        }
        while (storageInfo != null);

        return key;
    }

    //上传文件并返回文件地址
    public String saveFile(MultipartFile multipartFile) throws IOException {
        String[] fileAbsolutePath = {};
        String fileName = multipartFile.getOriginalFilename();
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        byte[] file_buff = null;
        InputStream inputStream = multipartFile.getInputStream();
        if (inputStream != null) {
            int len1 = inputStream.available();
            file_buff = new byte[len1];
            inputStream.read(file_buff);
        }
        inputStream.close();
        FastDFSFile file = new FastDFSFile(fileName, file_buff, ext);
        try {
            fileAbsolutePath = FastDFSClient.upload(file);  //upload to fastdfs
        } catch (Exception e) {
            log.error("upload file Exception!", e);
        }
        if (fileAbsolutePath == null) {
            log.error("upload file failed,please upload again!");
        }
        String path = FastDFSClient.getTrackerUrl() + fileAbsolutePath[0] + "/" + fileAbsolutePath[1];
        return path;
    }
}

