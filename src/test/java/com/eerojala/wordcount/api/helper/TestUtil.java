package com.eerojala.wordcount.api.helper;

import jakarta.validation.constraints.Null;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestUtil {
    /**
     * Reads content from a file with given name. Will fail the test if an exception occurs during file read.
     * @param fileName File must be located in src/test/resources or in any of it's sub-folders
     * @return
     */
    public static String readStringFromFile(String fileName) {
        if (fileName == null) {
            throw new NullPointerException("Given file name cannot be null");
        }

        return new String(readBytesFromFile(fileName), StandardCharsets.UTF_8);
    }

    private static byte[] readBytesFromFile(String fileName) {
        var inputStream = TestUtil.class.getClassLoader().getResourceAsStream(fileName);

        try {
            return StreamUtils.copyToByteArray(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail();

            return null;
        }
    }

    /**
     * Creates a MockMultipartFile with content from a file with given file name.
     * The file name is also set to the created MockMultipartFile.
     * Will fail the test if an exception occurs during file read.
     * @param fileName File must be located in src/test/resources or in any of it's sub-folders
     * @return
     */
    public static MockMultipartFile createMockMultipartFileFromFile(String fileName) {
        var fileContent = readBytesFromFile(fileName);

        return createMockMultipartFile(fileName, fileContent);
    }

    /**
     * Creates a MockMultipartFile with given String content and gives it the file name "test.txt".
     * @param content
     * @return
     */
    public static MockMultipartFile createMockMultipartFile(String content) {
        return createMockMultipartFile("test.txt", content.getBytes());
    }

    /**
     * Creates a MockMultipartFile with given String content and gives it the given file name.
     * NOTE: DOES NOT READ FROM A FILE WITH GIVEN FILE NAME!!
     * @param fileName
     * @param content
     * @return
     */
    public static MockMultipartFile createMockMultipartFile(String fileName, String content) {
        return createMockMultipartFile(fileName, content.getBytes());
    }

    private static MockMultipartFile createMockMultipartFile(String originalFileName, byte[] content) {
        return new MockMultipartFile(
                "file",
                originalFileName,
                MediaType.TEXT_PLAIN_VALUE,
                content);
    }

    /**
     * Asserts that given executable causes an IllegalArgumentException with given error message.
     * @param executable
     * @param errorMsg
     */
    public static void assertIllegalArgumentException(Executable executable, String errorMsg) {
        assertException(IllegalArgumentException.class, executable, errorMsg);
    }

    /**
     * Asserts that given executable causes an NullPointerException with given error message.
     * @param executable
     * @param errorMsg
     */
    public static void assertNullPointerException(Executable executable, String errorMsg) {
        assertException(NullPointerException.class, executable, errorMsg);
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
