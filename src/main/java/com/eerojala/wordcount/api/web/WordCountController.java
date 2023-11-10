package com.eerojala.wordcount.api.web;

import com.eerojala.wordcount.api.dto.InputDto;
import com.eerojala.wordcount.api.service.WordCountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class WordCountController {
    @Autowired
    private WordCountService service;

    @PostMapping("/wordcount")
    public List<Map.Entry<String, Integer>> countWords(@Valid @RequestBody InputDto inputDto) {
        return service.getMostCommonWords(inputDto.getContent(), inputDto.getAmount());
    }
}
