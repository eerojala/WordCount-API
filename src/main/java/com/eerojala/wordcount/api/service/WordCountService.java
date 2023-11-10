package com.eerojala.wordcount.api.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class WordCountService {
    public List<Map.Entry<String, Integer>> getMostCommonWords(String content, int k) {
        var words = getWords(content);
        var wordCountMap = Arrays.stream(words)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.summingInt(e -> 1)));

        return wordCountMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(k)
                .toList();
    }

    private String[] getWords(String content) {
        return StringUtils.deleteAny(content, ",.'!?:;\"")
                .toLowerCase()
                .trim()
                .replaceAll("\\s+", " ")
                .split(" ");
    }
}
