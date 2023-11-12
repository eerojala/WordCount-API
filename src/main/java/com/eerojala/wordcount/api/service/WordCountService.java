package com.eerojala.wordcount.api.service;

import com.eerojala.wordcount.api.model.WordCount;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class WordCountService {
    /**
     * Counts k most common words from given string, and returns them in a List of WordCount objects in descending order.
     *
     * NOTE TO SELF:
     * @Cachable works only if the method is called from another Spring Bean, not if called internally.
     * Also requires a config class with annotation @EnableCaching
     *
     * @param content Non-blank
     * @param k 1 or larger
     * @return
     */
    @Cacheable("results")
    public List<WordCount> countMostCommonWords(String content, int k) {
        if (content.isBlank()) {
            throw new IllegalArgumentException("Given content cannot be blank");
        }

        if (k < 1) {
            throw new IllegalArgumentException("k must be 1 or higher");
        }

        var words = splitWords(content);
        var wordCountMap = mapCountPerWord(words);

        return countMostCommonWords(wordCountMap, k);
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
