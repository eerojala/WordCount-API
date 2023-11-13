package com.eerojala.wordcount.api.web;

import com.eerojala.wordcount.api.exception.FileReadException;
import com.eerojala.wordcount.api.helper.TestUtil;
import com.eerojala.wordcount.api.model.WordCount;
import com.eerojala.wordcount.api.service.WordCountService;
import com.eerojala.wordcount.api.util.MultipartFileUtil;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@SpringBootTest
public class WordCountControllerTest {
    private static final String ENDPOINT = "/wordcount";
    private static final String ERROR_MSG_AMOUNT_TOO_LOW = "amount: 'must be greater than 0'";
    private static final Integer BAR_AMOUNT = 1;
    private static final Integer FOO_AMOUNT = 2;
    private static final String BAR_WORD = "bar";
    private static final String FOO_WORD = "foo";

    @Autowired
    private WebApplicationContext ctx;

    @MockBean
    private WordCountService wordCountServiceMock;

    @MockBean
    private MultipartFileUtil fileUtilMock;

    private MockMvc mvc;

    @BeforeEach
    void beforeEach() {
        mvc = MockMvcBuilders.webAppContextSetup(ctx).build();
    }

    @Test
    void testSucceedsWithValidFileAndAmount() throws Exception {
        mockIsValidFileType(true);
        mockReadFileContent();
        mockGetMostCommonWords();
        // NOTE: File size does not matter for MockMvc, see the commented out test further below for details
        var bigFile = TestUtil.createMockMultipartFileFromFile("big.txt");
        Integer maxValidAmount = Integer.MAX_VALUE - 1;
        var request = createMockRequest(bigFile, maxValidAmount.toString());
        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].word").value(FOO_WORD))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].amount").value(FOO_AMOUNT.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].word").value(BAR_WORD))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].amount").value(BAR_AMOUNT.toString()));

    }

    private void mockIsValidFileType(boolean isValid) {
        Mockito.when(fileUtilMock.isValidFileType(Mockito.any(MultipartFile.class))).thenReturn(isValid);
    }

    private void mockReadFileContent() {
        Mockito.when(fileUtilMock.readFileContent(Mockito.any(MultipartFile.class))).thenReturn("test");
    }

    private void mockGetMostCommonWords() {
        var foo = new WordCount(FOO_WORD, FOO_AMOUNT);
        var bar = new WordCount(BAR_WORD, BAR_AMOUNT);
        var mockReturnList = List.of(foo, bar);
        Mockito.when(wordCountServiceMock.countMostCommonWords(Mockito.anyString(), Mockito.anyInt()))
                .thenReturn(mockReturnList);
    }

    /**
     * Creates a mock request with valid file and given amount
     * @param amount
     */
    private MockHttpServletRequestBuilder createMockRequest(String amount) {
        var file = TestUtil.createMockMultipartFile("test");

        return createMockRequest(file, amount);
    }

    private MockHttpServletRequestBuilder createMockRequest(MockMultipartFile file, String amount) {
        var request = MockMvcRequestBuilders.multipart(ENDPOINT);

        if (file != null) {
            request = request.file(file);
        }

        if (amount != null) {
            request = (MockMultipartHttpServletRequestBuilder) request.param("amount", amount);
        }

        return request;
    }

    @Test
    void testFailsWithNullFile() throws Exception {
        var request = createMockRequest(null,"1");
        sendRequestExpectError(request, "file: 'must not be null'");
    }

    private void sendRequestExpectError(MockHttpServletRequestBuilder request, String errorMsg) {
        try {
            mvc.perform(request)
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.error").value(errorMsg));
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail();
        }
    }

    @Test
    void testFailsWithStringAsFile()  {
        var request = createMockRequest(null, "10").param("file", "file");
        var errorMsg = "file: 'Failed to convert value of type 'java.lang.String' to required type " +
                "'org.springframework.web.multipart.MultipartFile'; Cannot convert value of type 'java.lang.String' " +
                "to required type 'org.springframework.web.multipart.MultipartFile': no matching editors or " +
                "conversion strategy found'";
        sendRequestExpectError(request, errorMsg);
    }

    /*
     * NOTE: Cannot test upload limits with MockMvc because the file size checks are done in Tomcat which is not used
     * in the mock environment.
     *
     * If you want to test this you need to start a real server environment.
     *
     * See here: https://stackoverflow.com/a/68086006
     *
     */
/*    @Test
    void testFailsWithTooBigFile() throws Exception {
        var tooBigFile = TestUtil.createMockMultipartFileFromFile("too_big.txt");
        var request = createMockRequest(tooBigFile, "100");
        sendRequestExpectError(request, "dsada");
    }*/

    @Test
    void testFailsWithNullAmount() {
        var request = createMockRequest(null);
        sendRequestExpectError(request, "amount: 'must not be null'");
    }

    @Test
    void testFailsWithAmountZero() {
        assertAmountTooLow("0");
    }

    private void assertAmountTooLow(String amount) {
        var request = createMockRequest(amount);
        sendRequestExpectError(request, ERROR_MSG_AMOUNT_TOO_LOW);
    }

    @Test
    void testFailsWithAmountBelowZero()  {
        assertAmountTooLow("" + Integer.MIN_VALUE);
    }

    @Test
    void testFailsWithAmountAboveMax()  {
        String aboveMax = "2147483648";
        assertIntegerConversionError(aboveMax);
    }

    private void assertIntegerConversionError(String amount) {
        var request = createMockRequest(amount);
        String errorMsg =
                "amount: 'Failed to convert value of type 'java.lang.String' to required type 'java.lang.Integer'; " +
                "For input string: \"" +
                amount +
                "\"'";
        sendRequestExpectError(request, errorMsg);
    }

    @Test
    void testFailsWithAmountBelowMin() {
        String belowMin = "-2147483649";
        assertIntegerConversionError(belowMin);
    }

    @Test
    void testFailsWithNonIntegerAmount() {
        String nan = "1'";
        assertIntegerConversionError(nan);
    }

    @Test
    void testFailsIfFileTypeIsNotTxt() {
        mockIsValidFileType(false);
        var request = createMockRequest("1000");
        sendRequestExpectError(request, "Given file must be a .txt file");
    }

    @Test
    void testFailsIfExceptionOccursDuringFileRead() {
        String errorMsg = "Error while reading file - test";
        mockIsValidFileType(true);
        mockReadFileContentToThrowException(errorMsg);

        var request = createMockRequest("10000");
        sendRequestExpectError(request, errorMsg);
    }

    private void mockReadFileContentToThrowException(String errorMsg) {
        Mockito.when(fileUtilMock.readFileContent(Mockito.any(MultipartFile.class)))
                .thenThrow(new FileReadException(errorMsg));
    }

    @Test
    void testFailsIfServiceMethodThrowsException() {
        mockIsValidFileType(true);
        mockReadFileContent();
        String errorMsg = "Test Exception!";
        mockGetMostCommonWordsToThrowException(errorMsg);
        var request = createMockRequest("100");
        sendRequestExpectError(request, errorMsg);
    }

    private void mockGetMostCommonWordsToThrowException(String errorMsg) {
        Mockito.when(wordCountServiceMock.countMostCommonWords(Mockito.anyString(), Mockito.anyInt()))
                .thenThrow(new IllegalArgumentException(errorMsg));
    }
}
