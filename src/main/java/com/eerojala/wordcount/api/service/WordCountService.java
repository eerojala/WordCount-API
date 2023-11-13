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
     * Also requires a config class with annotation @EnableCaching + giving the cache a name (at least some times).
     *
     * @param content Non-blank that has at least one unicode letter/numeric
     * @param k 1 or larger
     * @return
     */
    @Cacheable("results")
    public List<WordCount> countMostCommonWords(String content, int k) {
        if (content == null) {
            throw new NullPointerException("Given content cannot be null");
        }

        if (content.isBlank()) {
            throw new IllegalArgumentException("Given content cannot be blank");
        }

        if (k < 1) {
            throw new IllegalArgumentException("k must be 1 or higher");
        }

        var cleanedContent = cleanContent(content);

        if (cleanedContent.isBlank()) {
            throw new IllegalArgumentException("Given content needs to contain at least one unicode letter or numeric");
        }

        var words = cleanedContent.split(" ");
        var wordCountMap = mapCountPerWord(words);

        return getMostCommonWords(wordCountMap, k);
    }

    private String cleanContent(String content) {
        var temp = content
                /*
                 * \p{L} = Any unicode letter, \p{N} = Any numeric
                 * i.e. remove characters which are not letters, numerics, whitespace, carriage returns or newlines
                 */
                .replaceAll("[^\\p{L}\\p{N}\r\n ]", "")
                .trim()
                .toLowerCase();

        return StringUtils.normalizeSpace(temp);
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
