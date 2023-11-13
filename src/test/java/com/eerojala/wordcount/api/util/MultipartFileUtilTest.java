package com.eerojala.wordcount.api.util;

import com.eerojala.wordcount.api.exception.FileReadException;
import com.eerojala.wordcount.api.helper.TestUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MultipartFileUtilTest {
    @Autowired
    private MultipartFileUtil util;

    @Test
    void testReadFileContentReadsContentFromTxtFileSuccessfully() {
        String content = "hello world";
        var file = TestUtil.createMockMultipartFile(content);
        assertEquals(content, util.readFileContent(file));
    }

    @Test
    void testReadFileContentReadsUtf8Characters() {
        String content = "zażółć ääliö 日本語";
        var file = TestUtil.createMockMultipartFile(content);
        assertEquals(content, util.readFileContent(file));
    }

    @Test
    void testReadFileContentThrowsNullPointerExceptionIfGivenFileIsNull() {
        TestUtil.assertNullPointerException(() -> util.readFileContent(null), "Given file cannot be null");
    }

    @Test
    void testReadFileThrowsFileReadExceptionIfFileReadFails() throws IOException {
        var fileMock = Mockito.mock(MultipartFile.class);
        Mockito.when(fileMock.getInputStream()).thenThrow(new NullPointerException("test exception"));
        TestUtil.assertException(FileReadException.class, () -> util.readFileContent(fileMock),
                "Error while reading file");
    }

    @Test
    void testIsFileTypeValidReturnsTrueIfIsTxtFile() {
        var file = TestUtil.createMockMultipartFile("test.txt", "hello");
        assertTrue(util.isValidFileType(file));
    }

    @Test
    void testIsFileTypeValidReturnsTrueIfTextFileCaseInsensitive() {
        var file = TestUtil.createMockMultipartFile("test.TxT", "world");
        assertTrue(util.isValidFileType(file));
    }

    @Test
    void testIsFileTypeValidReturnsFalseIfNotTxtFile() {
        var file = TestUtil.createMockMultipartFile("test.jpg", "foo");
        assertFalse(util.isValidFileType(file));
    }

    @Test
    void testIsFileTypeValidReturnsFileIfFileExtensionMissing() {
        var file = TestUtil.createMockMultipartFile("test", "bar");
        assertFalse(util.isValidFileType(file));
    }

    @Test
    void testIsFileTypeValidReturnsFileIfFileNameMissing() {
        var file = TestUtil.createMockMultipartFile(null, "abc");
        assertFalse(util.isValidFileType(file));
    }

    @Test
    void testIsFileTypeValidThrowsNullPointerExceptionIfGivenFileIsNull() {
        TestUtil.assertNullPointerException(() -> util.isValidFileType(null), "Given file cannot be null");
    }
}
