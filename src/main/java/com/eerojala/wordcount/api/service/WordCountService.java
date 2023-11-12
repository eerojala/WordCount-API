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
    /**
     * Counts k most common words from given file, and returns them in a List of WordCount objects in descending order.
     *
     * @param file must not be blank
     * @param k must be 1 or higher
     * @return
     * @throws IOException caused by file reading.
     */
    public List<WordCount> countMostCommonWords(MultipartFile file, int k) throws IOException {
        if (k < 1) {
            throw new IllegalArgumentException("k must be 1 or higher");
        }

        String content = getContent(file);

        if (content.isBlank()) {
            throw new IllegalArgumentException("Given file cannot be blank");
        }

        var words = splitWords(content);
        var wordCountMap = mapCountPerWord(words);

        return countMostCommonWords(wordCountMap, k);
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

    private List<WordCount> countMostCommonWords(Map<String, Integer> wordCountMap, int k) {
        return wordCountMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(k)
                .map(entry -> new WordCount(entry.getKey(), entry.getValue()))
                .toList();
    }
}
