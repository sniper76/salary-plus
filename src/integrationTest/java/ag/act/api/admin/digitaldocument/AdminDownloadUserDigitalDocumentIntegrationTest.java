package ag.act.api.admin.digitaldocument;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomLongs.someLongBetween;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class AdminDownloadUserDigitalDocumentIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/users/{userId}/digital-document/{digitalDocumentId}/download-document";
    private static final String userNotFoundMessage = "전자 문서 유저를 찾을 수 없습니다. digitalDocumentId: %d, userId: %d";
    private Stock stock;
    private String jwt;
    private Long digitalDocumentId;
    private User user;
    private Long userId;

    @BeforeEach
    void setUp() {
        itUtil.init();
        final User adminUser = itUtil.createAdminUser();
        jwt = itUtil.createJwt(adminUser.getId());
        user = itUtil.createUserWithAddress();
        userId = user.getId();
        stock = itUtil.createStock();
    }

    @Nested
    class WhenDigitalDocumentExists {

        @BeforeEach
        void setUp() {
            final Board board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.DIGITAL_DELEGATION);
            final DigitalDocument digitalDocument = createDigitalDocument(board, DigitalDocumentType.DIGITAL_PROXY, user);
            createDigitalDocumentUser(user, digitalDocument);

            digitalDocumentId = digitalDocument.getId();
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            callApi(status().isOk());
        }
    }

    @Nested
    class WhenUserIsAcceptor {

        private User acceptorUser;

        @BeforeEach
        void setUp() {
            acceptorUser = itUtil.createAcceptorUser();
            jwt = itUtil.createJwt(acceptorUser.getId());

            final Board board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.DIGITAL_DELEGATION);
            final DigitalDocument digitalDocument = createDigitalDocument(board, DigitalDocumentType.DIGITAL_PROXY, acceptorUser);
            createDigitalDocumentUser(user, digitalDocument);

            digitalDocumentId = digitalDocument.getId();
        }

        @Nested
        class AndUserDoesNotHaveTheStock {

            @DisplayName("Should return 403 response code when call " + TARGET_API)
            @Test
            void shouldReturnForbidden() throws Exception {
                MvcResult response = callApi(status().isForbidden());

                itUtil.assertErrorResponse(response, 403, "보유하고 있지 않은 주식입니다.");
            }
        }

        @Nested
        class AndUserHasTheStock {
            @BeforeEach
            void setUp() {
                itUtil.createUserHoldingStock(stock.getCode(), acceptorUser);
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnDigitalDocumentUsers() throws Exception {
                callApi(status().isOk());
            }
        }
    }

    @Nested
    class WhenDigitalDocumentIsInvalid {
        @BeforeEach
        void setUp() {
            User anotherUser = itUtil.createUserWithAddress();
            userId = anotherUser.getId();

            final Board board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.DIGITAL_DELEGATION);
            final DigitalDocument digitalDocument = createDigitalDocument(board, DigitalDocumentType.DIGITAL_PROXY, user);
            createDigitalDocumentUserWithEmptyPdfPath(anotherUser, digitalDocument);

            digitalDocumentId = digitalDocument.getId();
        }

        @DisplayName("Should return 400 response code when call " + TARGET_API)
        @Test
        void shouldReturnBadRequest() throws Exception {
            MvcResult response = callApi(status().isBadRequest());

            itUtil.assertErrorResponse(response, 400, "전자문서 PDF 파일 정보가 없습니다.");
        }
    }

    @Nested
    class WhenUserNotExist {
        @BeforeEach
        void setUp() {
            userId = someLongBetween(10L, 100L);
            digitalDocumentId = someLongBetween(100L, 1000L);
        }

        @DisplayName("Should return 500 response code when call " + TARGET_API)
        @Test
        void shouldReturnInternalServerError() throws Exception {
            MvcResult response = callApi(status().isInternalServerError());

            itUtil.assertErrorResponse(response, 500, String.format(userNotFoundMessage, digitalDocumentId, userId));
        }
    }

    @Nested
    class WhenDigitalDocumentIsHolderListReadAndCopy {

        @BeforeEach
        void setUp() {
            final Board board = itUtil.createBoard(stock, BoardGroup.ANALYSIS, BoardCategory.HOLDER_LIST_READ_AND_COPY);
            final DigitalDocument digitalDocument = createDigitalDocument(board, DigitalDocumentType.HOLDER_LIST_READ_AND_COPY_DOCUMENT, user);
            createDigitalDocumentUser(user, digitalDocument);

            digitalDocumentId = digitalDocument.getId();
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            callApi(status().isOk());
        }
    }

    private DigitalDocumentUser createDigitalDocumentUser(User user, DigitalDocument document) {
        final String pdfPath = "%s/%s".formatted(user.getId(), someString(10));
        itUtil.createUserHoldingStock(stock.getCode(), user);
        return itUtil.createDigitalDocumentUser(document, user, stock, pdfPath, DigitalDocumentAnswerStatus.COMPLETE);
    }

    private DigitalDocumentUser createDigitalDocumentUserWithEmptyPdfPath(User user, DigitalDocument document) {
        itUtil.createUserHoldingStock(stock.getCode(), user);
        return itUtil.createDigitalDocumentUser(document, user, stock, "", DigitalDocumentAnswerStatus.COMPLETE);
    }

    private DigitalDocument createDigitalDocument(Board board, DigitalDocumentType digitalDocumentType, User user) {
        final User postWriteUser = itUtil.createAdminUser();
        final LocalDateTime targetStartDate = LocalDateTime.now().minusDays(3);
        final LocalDateTime targetEndDate = LocalDateTime.now().plusDays(1);

        final Post post = itUtil.createPost(board, postWriteUser.getId());
        final DigitalDocument digitalDocument = itUtil.createDigitalDocument(
            post,
            stock,
            user,
            digitalDocumentType,
            targetStartDate,
            targetEndDate,
            KoreanDateTimeUtil.getTodayLocalDate()
        );
        post.setDigitalDocument(digitalDocument);
        itUtil.updatePost(post);

        return digitalDocument;
    }

    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc.perform(
                get(TARGET_API, userId, digitalDocumentId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt))
            .andExpect(resultMatcher)
            .andReturn();
    }
}
