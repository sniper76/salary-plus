package ag.act.api.stockboardgrouppost.digitaldocument;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.TestUtil;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Solidarity;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentItem;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.DigitalAnswerType;
import ag.act.enums.DigitalDocumentType;
import ag.act.util.KoreanDateTimeUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;

@SuppressWarnings("VariableDeclarationUsageDistance")
class CheckDigitalDocumentUserDataApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/users/digital-document/{digitalDocumentId}";

    private String jwt;
    private Stock stock;
    private User user;
    private User acceptUser;
    private User postWriteUser;
    private DigitalDocument digitalDocument;
    private List<DigitalDocumentItem> digitalDocumentItemList;
    private MockMultipartFile signImageFile;
    private MockMultipartFile idCardImageFile;
    private MockMultipartFile bankAccountFile1;
    private MockMultipartFile bankAccountFile2;

    @BeforeEach
    void setUp() {
        itUtil.init();
        postWriteUser = itUtil.createUser();
        user = itUtil.createUser();
        acceptUser = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());
        stock = itUtil.createStock();

        final Solidarity solidarity = itUtil.createSolidarity(stock.getCode());
        itUtil.createSolidarityLeader(solidarity, acceptUser.getId());
        itUtil.createUserHoldingStock(stock.getCode(), user);
    }

    private void defaultSetUp() {
        LocalDate referenceDate = KoreanDateTimeUtil.getTodayLocalDate();

        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime targetStartDate = now.minusDays(3);
        final LocalDateTime targetEndDate = now.plusDays(1);

        Board board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.DIGITAL_DELEGATION);
        Post post = itUtil.createPost(board, postWriteUser.getId());
        digitalDocument = itUtil.createDigitalDocument(
            post, stock, acceptUser, DigitalDocumentType.DIGITAL_PROXY,
            targetStartDate, targetEndDate, referenceDate
        );
        digitalDocumentItemList = itUtil.createDigitalDocumentItemList(digitalDocument);
        post.setDigitalDocument(digitalDocument);

        signImageFile = TestUtil.createMockMultipartFile("signImage");
        idCardImageFile = TestUtil.createMockMultipartFile("idCardImage");
        bankAccountFile1 = TestUtil.createMockMultipartFile("bankAccountImages");
        bankAccountFile2 = TestUtil.createMockMultipartFile("bankAccountImages");

        itUtil.createMyDataSummary(user, stock.getCode(), referenceDate);
    }

    private String genInvalidAnswerDataSize() {
        String answers = "";
        for (int k = 0; k < digitalDocumentItemList.size() - 1; k++) {
            DigitalDocumentItem item = digitalDocumentItemList.get(k);
            if (item.getDefaultSelectValue() == null) {
                continue;
            }
            if (k > 1) {
                answers += ",";
            }
            answers += item.getId() + ":" + someEnum(DigitalAnswerType.class).name();
        }
        return answers;
    }

    private String genInvalidAnswerDataNotExistSplit() {
        String answers = "";
        for (int k = 0; k < digitalDocumentItemList.size(); k++) {
            DigitalDocumentItem item = digitalDocumentItemList.get(k);
            if (item.getDefaultSelectValue() == null) {
                continue;
            }
            if (k > 1) {
                answers += ",";
            }
            answers += someEnum(DigitalAnswerType.class).name();
        }
        return answers;
    }

    private String genInvalidAnswerItemCount() {
        String answers = "";
        for (int k = 0; k < digitalDocumentItemList.size(); k++) {
            DigitalDocumentItem item = digitalDocumentItemList.get(k);
            if (item.getDefaultSelectValue() == null) {
                continue;
            }
            if (k > 1) {
                answers += ",";
            }
            answers += (item.getId() * k) + ":" + someEnum(DigitalAnswerType.class).name();
        }
        return answers;
    }

    @Nested
    class WhenInvalidAnswerDataSize {

        @BeforeEach
        void setUp() {
            defaultSetUp();
        }

        @DisplayName("Should return 400 response code when call " + TARGET_API)
        @Test
        void shouldReturnError() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    multipart(TARGET_API, digitalDocument.getId())
                        .file(signImageFile)
                        .file(idCardImageFile)
                        .file(bankAccountFile1)
                        .file(bankAccountFile2)
                        .param("answerData", genInvalidAnswerDataSize())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
                        .headers(headers(jwt(jwt)))
                )
                .andExpect(status().isBadRequest())
                .andReturn();

            itUtil.assertErrorResponse(response, 400, "선택된 찬성/반대/기권 정보 갯수와 전자문서 의안 갯수가 일치하지 않습니다.");
        }
    }

    @Nested
    class WhenInvalidAnswerDataNotExistSplit {

        @BeforeEach
        void setUp() {
            defaultSetUp();
        }

        @DisplayName("Should return 400 response code when call " + TARGET_API)
        @Test
        void shouldReturnError() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    multipart(TARGET_API, digitalDocument.getId())
                        .file(signImageFile)
                        .file(idCardImageFile)
                        .file(bankAccountFile1)
                        .file(bankAccountFile2)
                        .param("answerData", genInvalidAnswerDataNotExistSplit())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
                        .headers(headers(jwt(jwt)))
                )
                .andExpect(status().isBadRequest())
                .andReturn();

            itUtil.assertErrorResponse(response, 400, "찬성/반대/기권 정보를 확인하세요.");
        }
    }

    @Nested
    class WhenInvalidAnswerItemCount {

        @BeforeEach
        void setUp() {
            defaultSetUp();
        }

        @DisplayName("Should return 400 response code when call " + TARGET_API)
        @Test
        void shouldReturnError() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    multipart(TARGET_API, digitalDocument.getId())
                        .file(signImageFile)
                        .file(idCardImageFile)
                        .file(bankAccountFile1)
                        .file(bankAccountFile2)
                        .param("answerData", genInvalidAnswerItemCount())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
                        .headers(headers(jwt(jwt)))
                )
                .andExpect(status().isBadRequest())
                .andReturn();

            itUtil.assertErrorResponse(response, 400, "선택된 찬성/반대/기권 정보 갯수가 일치하지 않습니다.");
        }
    }
}
