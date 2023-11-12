package com.eerojala.wordcount.api.helper;

import org.junit.jupiter.api.function.Executable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestUtil {
    /**
     * Reads content from a file with given name.
     * @param fileName File must be located in src/test/resources or in any of it's subfolders
     * @return
     * @throws Exception
     */
    public static String readStringFromFile(String fileName) throws Exception {
        var inputStream = TestUtil.class.getClassLoader().getResourceAsStream(fileName);

        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }

    /**
     * Creates a MockMultipartFile with given String content
     * @param content
     * @return
     */
    public static MockMultipartFile createMockMultipartFile(String content) {
        return createMockMultipartFile(content.getBytes());
    }

    private static MockMultipartFile createMockMultipartFile(byte[] content) {
        return new MockMultipartFile(
                "file",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                content);
    }

    /**
     * Assers that given executable causes an IllegalArgumentException with given error message
     * @param executable
     * @param errorMsg
     */
    public static void assertIllegalArgumentException(Executable executable, String errorMsg) {
        assertException(IllegalArgumentException.class, executable, errorMsg);
    }

    /**
     * Asserts that given executable causes an Exception of given class with given error message.
     * @param exceptionClass
     * @param executable
     * @param errorMsg
     * @param <T>
     */
    public static <T extends Exception> void assertException(Class<T> exceptionClass, Executable executable,
                                                             String errorMsg) {
        Exception exception = assertThrows(exceptionClass, executable);
        assertEquals(errorMsg, exception.getMessage());
    }
}
