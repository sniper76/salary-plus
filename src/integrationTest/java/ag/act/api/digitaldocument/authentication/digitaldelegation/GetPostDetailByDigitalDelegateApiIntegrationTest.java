package ag.act.api.digitaldocument.authentication.digitaldelegation;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.DigitalDocumentType;
import ag.act.model.PostDetailsDataResponse;
import ag.act.model.PostDetailsResponse;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDate;

import static ag.act.TestUtil.someLocalDateTimeInThePastDaysBetween;
import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GetPostDetailByDigitalDelegateApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/stocks/{stockCode}/board-groups/{boardGroup}/posts/{postId}";

    private String stockCode;
    private String jwt;
    private User user;
    private Board board;
    private Stock stock;
    private Long postId;
    private Long digitalDocumentId;
    private LocalDate referenceDate;

    @BeforeEach
    void setUp() {
        user = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());

        stockCode = someStockCode();
        stock = itUtil.createStock(stockCode);
        board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.DIGITAL_DELEGATION);
        referenceDate = someLocalDateTimeInThePastDaysBetween(5, 10).toLocalDate();
        itUtil.createMyDataSummary(user, stockCode, referenceDate);
        itUtil.createStockReferenceDate(stock.getCode(), referenceDate);

        final DigitalDocument digitalDocument = mockDigitalProxyDocument();
        digitalDocumentId = digitalDocument.getId();
        postId = digitalDocument.getPostId();
    }

    private DigitalDocument mockDigitalProxyDocument() {
        User acceptUser = itUtil.createAcceptorUser();
        itUtil.createStockAcceptorUser(stock.getCode(), acceptUser);
        final Post post = itUtil.createPost(board, user.getId());

        DigitalDocument digitalDocument = itUtil.createDigitalDocument(post, stock, acceptUser);
        digitalDocument.setType(DigitalDocumentType.DIGITAL_PROXY);
        digitalDocument.setStockReferenceDate(referenceDate);
        return itUtil.updateDigitalDocument(digitalDocument);
    }

    private PostDetailsDataResponse getPostDetailsDataResponse() throws Exception {
        final MvcResult response = callApi(status().isOk());

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            PostDetailsDataResponse.class
        );
    }

    private void assertDocumentResponse(PostDetailsDataResponse result) {
        final PostDetailsResponse createResponse = result.getData();

        assertThat(createResponse.getPoll(), is(nullValue()));
        assertThat(createResponse.getDigitalProxy(), is(nullValue()));
        assertThat(createResponse.getDigitalDocument(), is(notNullValue()));

        assertThat(createResponse.getId(), is(postId));
        assertThat(createResponse.getId(), is(notNullValue()));
        assertThat(createResponse.getBoardId(), is(board.getId()));

        ag.act.model.DigitalDocumentResponse digitalDocumentResponse = createResponse.getDigitalDocument();
        assertThat(digitalDocumentResponse, is(notNullValue()));
        assertThat(digitalDocumentResponse.getId(), is(digitalDocumentId));
    }

    @NotNull
    private MvcResult callApi(ResultMatcher matcher) throws Exception {
        return mockMvc
            .perform(
                get(TARGET_API, stockCode, board.getGroup().name(), postId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(matcher)
            .andReturn();
    }

    @Nested
    class WhenUserHasUserHoldingStockOnReferenceDate {

        @BeforeEach
        void setUp() {
            itUtil.createUserHoldingStockOnReferenceDate(stockCode, user.getId(), referenceDate);
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            assertDocumentResponse(getPostDetailsDataResponse());
        }
    }

    @Nested
    class WhenUserDoesNotHaveUserHoldingStockOnReference {

        @DisplayName("Should return 403 response code when call " + TARGET_API)
        @Test
        void shouldReturnError() throws Exception {
            final MvcResult response = callApi(status().isForbidden());

            itUtil.assertErrorResponse(response, 403, "보유하고 있지 않은 주식입니다.");
        }
    }
}
