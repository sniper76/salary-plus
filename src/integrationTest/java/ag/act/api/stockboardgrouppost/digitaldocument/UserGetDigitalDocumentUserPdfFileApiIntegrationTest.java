package ag.act.api.stockboardgrouppost.digitaldocument;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.dto.DigitalDocumentUserAnswerDto;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentItem;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.DigitalDocumentAnswerStatus;
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
import java.util.Optional;

import static ag.act.TestUtil.someFilename;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("VariableDeclarationUsageDistance")
class UserGetDigitalDocumentUserPdfFileApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/users/digital-document/{digitalDocumentId}";

    private String jwt;
    private Stock stock;
    private User user;
    private User acceptUser;
    private User postWriteUser;
    private DigitalDocument digitalDocument;
    private List<DigitalDocumentItem> digitalDocumentItemList;

    @BeforeEach
    void setUp() {
        itUtil.init();
        postWriteUser = itUtil.createUser();
        user = itUtil.createUser();
        acceptUser = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());
        stock = itUtil.createStock();

        itUtil.createSolidarity(stock.getCode());
        itUtil.createStockAcceptorUser(stock.getCode(), acceptUser);
        itUtil.createUserHoldingStock(stock.getCode(), user);
    }

    private String getAnswerData() {
        String string = "";
        for (int k = 0; k < digitalDocumentItemList.size(); k++) {
            DigitalDocumentItem item = digitalDocumentItemList.get(k);
            if (item.getDefaultSelectValue() == null) {
                continue;
            }
            if (k > 1) {
                string += ",";
            }
            string += item.getId() + ":" + item.getDefaultSelectValue();
        }
        return string;
    }

    @Nested
    class WhenCreateDigitalDocumentUserDataAllFilesReturnPdfPath {
        private MockMultipartFile signImageFile;
        private MockMultipartFile idCardImageFile;
        private MockMultipartFile bankAccountFile1;
        private MockMultipartFile bankAccountFile2;
        private String answerData;

        @BeforeEach
        void setUp() {
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
            digitalDocument.setDigitalDocumentItemList(digitalDocumentItemList);
            post.setDigitalDocument(digitalDocument);

            final String originalSignImageFilename = someFilename();
            final String originalIdCardImageFilename = someFilename();
            final String originalBankAccountFilename1 = someFilename();
            final String originalBankAccountFilename2 = someFilename();

            signImageFile = new MockMultipartFile(
                "signImage",
                originalSignImageFilename,
                MediaType.IMAGE_PNG_VALUE,
                "test image".getBytes()
            );
            idCardImageFile = new MockMultipartFile(
                "idCardImage",
                originalIdCardImageFilename,
                MediaType.IMAGE_PNG_VALUE,
                "test image".getBytes()
            );
            bankAccountFile1 = new MockMultipartFile(
                "bankAccountImages",
                originalBankAccountFilename1,
                MediaType.IMAGE_PNG_VALUE,
                "test image".getBytes()
            );
            bankAccountFile2 = new MockMultipartFile(
                "bankAccountImages",
                originalBankAccountFilename2,
                MediaType.IMAGE_PNG_VALUE,
                "test image".getBytes()
            );

            answerData = getAnswerData();

            itUtil.createMyDataSummary(user, stock.getCode(), referenceDate);
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    multipart(TARGET_API, digitalDocument.getId())
                        .file(signImageFile)
                        .file(idCardImageFile)
                        .file(bankAccountFile1)
                        .file(bankAccountFile2)
                        .param("answerData", answerData)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isOk())
                .andReturn();

            final ag.act.model.UserDigitalDocumentResponse result = objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                ag.act.model.UserDigitalDocumentResponse.class
            );

            List<DigitalDocumentItem> lastItemList = digitalDocumentItemList.stream()
                .filter(element -> element.getDefaultSelectValue() != null)
                .toList();
            List<DigitalDocumentUserAnswerDto> answerDtoList = itUtil.findUserAnswerList(digitalDocument.getId(), user.getId());

            Optional<DigitalDocumentUser> optionalDigitalDocumentUser = itUtil.findDigitalDocumentByDigitalDocumentIdAndUserId(
                digitalDocument.getId(), user.getId()
            );

            assertThat(result.getId(), is(digitalDocument.getId()));
            assertThat(result.getUser().getId(), is(user.getId()));
            assertThat(result.getStock().getCode(), is(stock.getCode()));
            assertThat(result.getAcceptUser().getId(), is(acceptUser.getId()));
            assertThat(lastItemList.size(), is(answerDtoList.size()));
            assertThat(optionalDigitalDocumentUser.get().getHashedPhoneNumber(), is(notNullValue()));
            assertThat(optionalDigitalDocumentUser.get().getDigitalDocumentAnswerStatus(), is(DigitalDocumentAnswerStatus.SAVE));
        }
    }
}
