package com.learnSpringBoot.eCom.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    String uploadImage(String path, MultipartFile image) throws IOException;
}
