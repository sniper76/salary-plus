package ag.act.api.stockboardgrouppost.digitaldocument;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Solidarity;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStock;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.DigitalDocumentType;
import ag.act.enums.digitaldocument.AttachOptionType;
import ag.act.util.KoreanDateTimeUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static ag.act.TestUtil.createMockMultipartFile;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserCreateDigitalDocumentEtcDocumentApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/users/digital-document/{digitalDocumentId}";

    private String jwt;
    private Stock stock;
    private User user;
    private User postWriteUser;
    private DigitalDocument digitalDocument;
    private MockMultipartFile signImageFile;
    private MockMultipartFile idCardImage;
    private MockMultipartFile bankAccountFile1;
    private MockMultipartFile bankAccountFile2;
    private ag.act.model.JsonAttachOption jsonAttachOption;
    private UserHoldingStock userHoldingStock;

    @BeforeEach
    void setUp() {
        itUtil.init();
        postWriteUser = itUtil.createUser();
        user = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());
        stock = itUtil.createStock();

        final User solidarityLeader = itUtil.createUser();
        final Solidarity solidarity = itUtil.createSolidarity(stock.getCode());
        itUtil.createSolidarityLeader(solidarity, solidarityLeader.getId());
        userHoldingStock = itUtil.createUserHoldingStock(stock.getCode(), user);
    }

    private void defaultSetup() {
        final LocalDate referenceDate = KoreanDateTimeUtil.getTodayLocalDate();
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime targetStartDate = now.minusDays(3);
        final LocalDateTime targetEndDate = now.plusDays(1);

        final Board board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.ETC);
        final Post post = itUtil.createPost(board, postWriteUser.getId());
        digitalDocument = itUtil.createDigitalDocument(
            post, stock, null, DigitalDocumentType.ETC_DOCUMENT,
            targetStartDate, targetEndDate, referenceDate, jsonAttachOption
        );
        post.setDigitalDocument(digitalDocument);
        itUtil.createMyDataSummary(user, stock.getCode(), referenceDate);
    }

    @Nested
    class WhenCreateEtc {

        @BeforeEach
        void setUp() {
            jsonAttachOption = new ag.act.model.JsonAttachOption()
                .idCardImage(AttachOptionType.REQUIRED.name())
                .signImage(AttachOptionType.REQUIRED.name())
                .bankAccountImage(AttachOptionType.NONE.name())
                .hectoEncryptedBankAccountPdf(AttachOptionType.NONE.name());
            defaultSetup();

            signImageFile = createMockMultipartFile("signImage");
            idCardImage = createMockMultipartFile("idCardImage");
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    multipart(TARGET_API, digitalDocument.getId())
                        .file(signImageFile)
                        .file(idCardImage)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isOk())
                .andReturn();

            assertResult(response);
        }
    }

    @Nested
    class WhenCreateEtcError {

        @BeforeEach
        void setUp() {
            jsonAttachOption = new ag.act.model.JsonAttachOption()
                .idCardImage(AttachOptionType.REQUIRED.name())
                .signImage(AttachOptionType.REQUIRED.name())
                .bankAccountImage(AttachOptionType.NONE.name())
                .hectoEncryptedBankAccountPdf(AttachOptionType.NONE.name());

            defaultSetup();

            signImageFile = createMockMultipartFile("signImage");
        }

        @DisplayName("Should return 400 response code when call " + TARGET_API)
        @Test
        void shouldReturnError() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    multipart(TARGET_API, digitalDocument.getId())
                        .file(signImageFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isBadRequest())
                .andReturn();

            itUtil.assertErrorResponseContainsString(response, 400, "전자문서 필수 파일 정보가 없습니다.");
        }
    }

    @Nested
    class WhenCreateEtcBankNoneError {

        @BeforeEach
        void setUp() {
            jsonAttachOption = new ag.act.model.JsonAttachOption()
                .idCardImage(AttachOptionType.REQUIRED.name())
                .signImage(AttachOptionType.REQUIRED.name())
                .bankAccountImage(AttachOptionType.NONE.name())
                .hectoEncryptedBankAccountPdf(AttachOptionType.NONE.name());

            defaultSetup();

            signImageFile = createMockMultipartFile("signImage");
            idCardImage = createMockMultipartFile("idCardImage");
            bankAccountFile1 = createMockMultipartFile("bankAccountImages");
            bankAccountFile2 = createMockMultipartFile("bankAccountImages");
        }

        @DisplayName("Should return 400 response code when call " + TARGET_API)
        @Test
        void shouldReturnError() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    multipart(TARGET_API, digitalDocument.getId())
                        .file(signImageFile)
                        .file(idCardImage)
                        .file(bankAccountFile1)
                        .file(bankAccountFile2)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isBadRequest())
                .andReturn();

            itUtil.assertErrorResponseContainsString(response, 400, "잔고증명서는 필수 파일이 아닙니다.");
        }
    }

    @Nested
    class WhenIdCardImageIsNone {

        @BeforeEach
        void setUp() {
            jsonAttachOption = new ag.act.model.JsonAttachOption()
                .signImage(AttachOptionType.REQUIRED.name())
                .idCardImage(AttachOptionType.NONE.name())
                .bankAccountImage(AttachOptionType.NONE.name())
                .hectoEncryptedBankAccountPdf(AttachOptionType.NONE.name());
        }

        @Nested
        class AndUploadIdCardImage {

            @BeforeEach
            void setUp() {

                defaultSetup();

                signImageFile = createMockMultipartFile("signImage");
                idCardImage = createMockMultipartFile("idCardImage");
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnError() throws Exception {
                MvcResult response = mockMvc
                    .perform(
                        multipart(TARGET_API, digitalDocument.getId())
                            .file(signImageFile)
                            .file(idCardImage)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + jwt)
                    )
                    .andExpect(status().isBadRequest())
                    .andReturn();

                itUtil.assertErrorResponseContainsString(response, 400, "신분증 이미지는 필수 파일이 아닙니다.");
            }
        }

        @Nested
        class AndNotUploadIdCardImage {

            @BeforeEach
            void setUp() {

                defaultSetup();

                signImageFile = createMockMultipartFile("signImage");
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldResultSuccess() throws Exception {
                MvcResult response = mockMvc
                    .perform(
                        multipart(TARGET_API, digitalDocument.getId())
                            .file(signImageFile)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + jwt)
                    )
                    .andExpect(status().isOk())
                    .andReturn();

                assertResult(response);
            }
        }
    }

    private void assertResult(MvcResult response) throws JsonProcessingException, UnsupportedEncodingException {
        final ag.act.model.UserDigitalDocumentResponse result = objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            ag.act.model.UserDigitalDocumentResponse.class
        );

        final DigitalDocument digitalDocumentFromDatabase = itUtil.findDigitalDocument(digitalDocument.getId());
        final ag.act.model.JsonAttachOption jsonAttachOptionDatabase = digitalDocumentFromDatabase.getJsonAttachOption();
        final DigitalDocumentUser digitalDocumentUser =
            itUtil.findDigitalDocumentByDigitalDocumentIdAndUserId(digitalDocumentFromDatabase.getId(), user.getId())
                .orElseThrow();

        assertThat(result.getId(), is(digitalDocumentFromDatabase.getId()));
        assertThat(result.getAnswerStatus(), is(digitalDocumentUser.getDigitalDocumentAnswerStatus().name()));
        assertThat(result.getJoinUserCount(), is(digitalDocumentFromDatabase.getJoinUserCount()));
        assertThat(result.getJoinStockSum(), is(digitalDocumentFromDatabase.getJoinStockSum()));
        assertThat(result.getDigitalDocumentType(), is(digitalDocumentFromDatabase.getType().name()));
        assertThat(result.getUser().getId(), is(user.getId()));
        assertThat(result.getStock().getCode(), is(stock.getCode()));
        assertThat(result.getAcceptUser(), nullValue());
        assertThat(digitalDocument.getType(), is(digitalDocument.getType()));
        assertThat(digitalDocumentFromDatabase.getTitle(), is(digitalDocument.getTitle()));
        assertThat(digitalDocumentFromDatabase.getContent(), is(digitalDocument.getContent()));
        assertThat(jsonAttachOptionDatabase.getSignImage(), is(jsonAttachOption.getSignImage()));
        assertThat(jsonAttachOptionDatabase.getIdCardImage(), is(jsonAttachOption.getIdCardImage()));
        assertThat(jsonAttachOptionDatabase.getBankAccountImage(), is(jsonAttachOption.getBankAccountImage()));
        assertThat(jsonAttachOptionDatabase.getHectoEncryptedBankAccountPdf(), is(jsonAttachOption.getHectoEncryptedBankAccountPdf()));

        assertThat(digitalDocumentUser.getPurchasePrice(), is(userHoldingStock.getPurchasePrice()));
    }
}
