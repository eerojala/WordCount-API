package com.eerojala.wordcount.api.service;

import com.eerojala.wordcount.api.model.WordCount;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class WordCountService {
    public List<WordCount> getMostCommonWords(MultipartFile file, int k) throws IOException {
        if (k < 1) {
            throw new IllegalArgumentException("k must be 1 or larger");
        }

        String content = getContent(file);

        if (content.isBlank()) {
            throw new IllegalArgumentException("Given file cannot be blank!");
        }

        var words = splitWords(content);
        var wordCountMap = mapCountPerWord(words);

        return getMostCommonWords(wordCountMap, k);
    }

    private String getContent(MultipartFile file) throws IOException {
        return new String(file.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    private String[] splitWords(String content) {
        return StringUtils.normalizeSpace(content)
                .replaceAll("[^\\p{L}\\p{N} ]", "")
                .trim()
                .toLowerCase()
                .split(" ");
    }
    private Map<String, Integer> mapCountPerWord(String[] words) {
        return Arrays.stream(words).collect(Collectors.groupingBy(Function.identity(), Collectors.summingInt(e -> 1)));
    }

    private List<WordCount> getMostCommonWords(Map<String, Integer> wordCountMap, int k) {
        return wordCountMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(k)
                .map(entry -> new WordCount(entry.getKey(), entry.getValue()))
                .toList();
    }
}
