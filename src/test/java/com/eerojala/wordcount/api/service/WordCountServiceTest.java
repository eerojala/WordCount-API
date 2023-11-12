package com.eerojala.wordcount.api.service;

import com.eerojala.wordcount.api.model.WordCount;
import com.eerojala.wordcount.api.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class WordCountServiceTest {
    @Autowired
    private WordCountService service;

    @Test
    void testCountMostCommonWordsReturnsMostCommonWordsInDescendingOrder() throws Exception {
        var mockFile = TestUtil.createMockMultipartFileFromFile("sample_text.txt");
        var hello = new WordCount("hello", 6);
        var world = new WordCount("world", 5);
        var foo = new WordCount("foo", 4);
        var bar = new WordCount("bar", 3);
        var fo = new WordCount("fo", 2);
        var o = new WordCount("o", 1);

        var expected = List.of(hello, world, foo, bar, fo, o);
        assertEquals(expected, service.countMostCommonWords(mockFile, 6));
    }

    @Test
    void testCountMostCommonWordsReturnsGivenAmountOfWords() throws Exception {
        var mockFile = TestUtil.createMockMultipartFile("three two one three two three");
        var three = new WordCount("three", 3);
        var two = new WordCount("two", 2);
        var one = new WordCount("one", 1);

        assertEquals(List.of(three, two, one), service.countMostCommonWords(mockFile, 3));
        assertEquals(List.of(three, two), service.countMostCommonWords(mockFile, 2));
        assertEquals(List.of(three), service.countMostCommonWords(mockFile, 1));
    }

    @Test
    void testCountMostCommonWordsWorksIfLessWordsThanGivenAmount() throws Exception {
        var mockFile = TestUtil.createMockMultipartFile("a b c b c c");
        var c = new WordCount("c", 3);
        var b = new WordCount("b", 2);
        var a = new WordCount("a", 1);

        assertEquals(List.of(c, b, a), service.countMostCommonWords(mockFile, 4));
    }

    @Test
    void testCountMostCommonWordsWorksIfThereAreMultipleWordsWithSameAmount() throws Exception {
        var mockFile = TestUtil.createMockMultipartFile("world foo bar bar foo hello");
        var foo = new WordCount("foo", 2);
        var bar = new WordCount("bar", 2);
        var hello = new WordCount("hello", 1);
        var world = new WordCount("world", 1);

        var result = service.countMostCommonWords(mockFile, 4);
        assertEquals(4, result.size());

        assertTrue(wordCountAtIndexEitherOr(result, 0, foo, bar));
        assertTrue(wordCountAtIndexEitherOr(result, 1, foo, bar));
        assertNotEquals(result.get(0), result.get(1));

        assertTrue(wordCountAtIndexEitherOr(result, 2, hello, world));
        assertTrue(wordCountAtIndexEitherOr(result, 3, world, hello));
        assertNotEquals(result.get(2), result.get(3));
    }

    private boolean wordCountAtIndexEitherOr(List<WordCount> list, int i, WordCount a, WordCount b) {
        var wordCountAtIndex = list.get(i);

        return wordCountAtIndex.equals(a) || wordCountAtIndex.equals(b);
    }

    @Test
    void testCountMostCommonWordsWorksWithNumbers() throws Exception {
        var mockFile = TestUtil.createMockMultipartFileFromFile("numbers.txt");
        var one = new WordCount("1", 11);
        var two = new WordCount("2", 10);
        var three = new WordCount("3", 9);
        var four = new WordCount("4", 8);
        var five = new WordCount("5", 7);
        var six = new WordCount("6", 6);
        var seven = new WordCount("7", 5);
        var eight = new WordCount("8", 4);
        var nine = new WordCount("9", 3);
        var zero = new WordCount("0", 2);
        var ten = new WordCount("10", 1);

        var expected = List.of(one, two, three, four, five, six, seven, eight, nine, zero, ten);
        assertEquals(expected, service.countMostCommonWords(mockFile, 11));
    }

    @Test
    void testCountMostCommonWordsIgnoresSpecialCharacters() throws Exception {
        var mockFile = TestUtil.createMockMultipartFileFromFile("special_characters.txt");
        var test = new WordCount("test", 14);

        var expected = List.of(test);
        assertEquals(expected, service.countMostCommonWords(mockFile, 1));
    }

    @Test
    void testCountMostCommonWordsWorksWithUTF8Characters() throws Exception {
        var mockFile = TestUtil.createMockMultipartFileFromFile("utf-8.txt");
        var finnish = new WordCount("ääliö", 3);
        var japanese = new WordCount("日本語", 2);
        var polish = new WordCount("zażółć", 1);

        var expected = List.of(finnish, japanese, polish);
        assertEquals(expected, service.countMostCommonWords(mockFile, 3));
    }

    @Test
    void testCountMostCommonWordsLowerCasesAllWords() throws Exception {
        var mockFile = TestUtil.createMockMultipartFile("tEst Test test teSt tesT");
        var test = new WordCount("test", 5);

        var expected = List.of(test);
        assertEquals(expected, service.countMostCommonWords(mockFile, 1));
    }

    @Test
    void testCountMostCommonWordsRemovesExtraWhitespace() throws Exception {
        var mockFile = TestUtil.createMockMultipartFileFromFile("whitespace.txt");
        var foo = new WordCount("foo", 3);
        var bar = new WordCount("bar", 2);

        var expected = List.of(foo, bar);
        assertEquals(expected, service.countMostCommonWords(mockFile, 2));
    }

    @Test
    void testCountMostCommonWordsThrowsIllegalArgumentExceptionWithBlankFile() {
        var mockFile = TestUtil.createMockMultipartFile(" ");
        TestUtil.assertIllegalArgumentException(() ->  service.countMostCommonWords(mockFile, 1),
                "Given file cannot be blank");
    }

    @Test
    void testCountMostCommonWordsThrowsIllegalArgumentExceptionWithKTooSmall() {
        var mockFile = TestUtil.createMockMultipartFile("test");
        TestUtil.assertIllegalArgumentException(() -> service.countMostCommonWords(mockFile, 0),
                "k must be 1 or higher");
    }
}
