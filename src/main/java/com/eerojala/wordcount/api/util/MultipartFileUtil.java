package com.eerojala.wordcount.api.util;

import com.eerojala.wordcount.api.exception.FileReadException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

@Component
public class MultipartFileUtil {
    private static final String NULL_POINTER_MSG = "Given file cannot be null";

    /**
     * Reads the content from the given file and returns it as UTF-8 encoded String
     * @param file Not null
     * @return
     * @throws FileReadException if something went wrong during file read.
     */
    public String readFileContent(MultipartFile file) {
        if (file == null) {
            throw new NullPointerException(NULL_POINTER_MSG);
        }

        try {
            return new String(file.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new FileReadException("Error while reading file");
        }
    }

    /**
     * Returns if given file is a .txt file.
     * @param file Not null
     * @return
     */
    public boolean isValidFileType(MultipartFile file) {
        if (file == null) {
            throw new NullPointerException(NULL_POINTER_MSG);
        }

        String fileName = file.getOriginalFilename();

        return fileName != null && fileName.toLowerCase().endsWith(".txt");
    }
}
