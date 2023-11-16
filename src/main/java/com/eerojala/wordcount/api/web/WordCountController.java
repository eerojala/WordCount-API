package com.eerojala.wordcount.api.web;

import com.eerojala.wordcount.api.dto.FileAndAmountDto;
import com.eerojala.wordcount.api.model.WordCount;
import com.eerojala.wordcount.api.service.WordCountService;
import com.eerojala.wordcount.api.util.MultipartFileUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class WordCountController {
    @Autowired
    private WordCountService service;

    @Autowired
    private MultipartFileUtil fileUtil;

    @PostMapping("/")
    public List<WordCount> countWords(@Valid FileAndAmountDto dto)  {
        var file = dto.getFile();

        if (!fileUtil.isValidFileType(file)) {
            throw new IllegalArgumentException("Given file must be a .txt file");
        }

        String content = fileUtil.readFileContent(file);

        return service.countMostCommonWords(content, dto.getAmount());
    }
}
