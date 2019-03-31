package com.example.market.controller;

import com.example.common.response.RestResponse;
import com.example.market.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class StorageController {

    @Autowired
    private StorageService storageService;

    @PostMapping("storage/create")
    public Object create(@RequestParam("file") MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String url = storageService.store(file, file.getSize(), file.getContentType(), originalFilename);
        Map<String, Object> data = new HashMap<>();
        data.put("url", url);
        return new RestResponse<>(data);
    }
}
