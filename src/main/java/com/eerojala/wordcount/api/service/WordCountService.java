package com.eerojala.wordcount.api.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class WordCountService {
    public List<Map.Entry<String, Integer>> getMostCommonWords(MultipartFile file, int k) throws IOException {
        String content = getContent(file);
        var words = splitWords(content);
        var wordCountMap = mapCountPerWord(words);

        return getMostCommonWords(wordCountMap, k);
    }

    private String getContent(MultipartFile file) throws IOException {
        return new String(file.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    private String[] splitWords(String content) {
        return StringUtils.deleteAny(content, ",.'!?:;\"")
                .toLowerCase()
                .trim()
                .replaceAll("\\s+", " ")
                .split(" ");
    }
    private Map<String, Integer> mapCountPerWord(String[] words) {
        return Arrays.stream(words).collect(Collectors.groupingBy(Function.identity(), Collectors.summingInt(e -> 1)));
    }

    private List<Map.Entry<String, Integer>> getMostCommonWords(Map<String, Integer> wordCountMap, int k) {
        return wordCountMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(k)
                .toList();
    }
}
