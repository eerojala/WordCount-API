package com.eerojala.wordcount.api.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class MultipartFileUtil {
    public String getFileContent(MultipartFile file) throws IOException {
        return new String(file.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }
}
