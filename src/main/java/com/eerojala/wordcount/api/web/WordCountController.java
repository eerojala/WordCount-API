package com.eerojala.wordcount.api.web;

import com.eerojala.wordcount.api.dto.FileAndAmountDto;
import com.eerojala.wordcount.api.model.WordCount;
import com.eerojala.wordcount.api.service.WordCountService;
import com.eerojala.wordcount.api.util.MultipartFileUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class WordCountController {
    @Autowired
    private WordCountService service;

    @Autowired
    private MultipartFileUtil fileUtil;

    @PostMapping("/wordcount")
    public List<WordCount> countWords(@Valid FileAndAmountDto dto) throws IOException {
        String content = fileUtil.getFileContent(dto.getFile());
        return service.countMostCommonWords(content, dto.getAmount());
    }
}
