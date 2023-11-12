package com.eerojala.wordcount.api.util;

import com.eerojala.wordcount.api.helper.TestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class MultipartFileUtilTest {
    @Autowired
    private MultipartFileUtil util;

    @Test
    void testGetFileContentGetsContentFromFileSuccessfully() throws Exception {
        String content = "hello world";
        var file = TestUtil.createMockMultipartFile(content);
        assertEquals(content, util.getFileContent(file));
    }
}
