package ag.act.api.digitaldocument;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.DigitalDocumentAnswerStatus;
import ag.act.enums.DigitalDocumentType;
import ag.act.model.GetPostDigitalDocumentResponse;
import ag.act.model.Paging;
import ag.act.model.PostDigitalDocumentResponse;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static ag.act.TestUtil.someLocalDateTimeInTheFutureDaysBetween;
import static ag.act.TestUtil.someStockCode;
import static ag.act.TestUtil.toMultiValueMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someString;
import static shiver.me.timbers.data.random.RandomThings.someThing;

class DigitalDocumentPostsIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/posts/digital-document/{digitalDocumentType}";

    private String jwt;

    private DigitalDocument digitalDocumentEnd;
    private DigitalDocument digitalDocumentProcessing;
    private DigitalDocument digitalDocumentStandby;
    private User accessUser;

    @BeforeEach
    void setUp() {
        itUtil.init();
        accessUser = itUtil.createUser();
        jwt = itUtil.createJwt(accessUser.getId());
    }

    private DigitalDocument createDigitalDocument(User currentUser, LocalDateTime startDate, LocalDateTime endDate) {
        final User user = itUtil.createUser();
        final String stockCode = someStockCode();
        final Stock stock = itUtil.createStock(stockCode);
        final Board board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.DIGITAL_DELEGATION);
        final Post post = itUtil.createPost(board, user.getId());
        final LocalDate referenceDate = someLocalDateTimeInTheFutureDaysBetween(5, 10).toLocalDate();

        final DigitalDocument digitalDocument = itUtil.createDigitalDocument(
            post, stock, user, DigitalDocumentType.DIGITAL_PROXY, startDate, endDate, referenceDate
        );
        board.setStock(stock);
        post.setBoard(board);
        digitalDocument.setPost(post);

        itUtil.createUserHoldingStockOnReferenceDate(stock.getCode(), currentUser.getId(), referenceDate);
        itUtil.createDigitalDocumentItemList(digitalDocument);

        return digitalDocument;
    }

    @Nested
    class WhenGetPostsError {

        @DisplayName("Should return 400 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            MvcResult response = callApi(
                Map.of(),
                someThing(DigitalDocumentType.JOINT_OWNERSHIP_DOCUMENT, DigitalDocumentType.ETC_DOCUMENT),
                status().isBadRequest()
            );

            itUtil.assertErrorResponse(response, 400, "전자문서 의결권위임만 조회가 가능합니다.");
        }
    }

    @Nested
    class WhenGetPosts {

        @BeforeEach
        void setUp() {
            LocalDateTime now = LocalDateTime.now();
            digitalDocumentEnd = createDigitalDocument(accessUser, now.minusDays(5), now.minusDays(1));//종료
            digitalDocumentProcessing = createDigitalDocument(accessUser, now.minusDays(5), now.plusHours(10));//진행중
            digitalDocumentStandby = createDigitalDocument(accessUser, now.plusHours(10), now.plusDays(3));//대기중

            final Stock stock = digitalDocumentProcessing.getPost().getBoard().getStock();
            final String pdfPath = someString(10);
            itUtil.createDigitalDocumentUser(digitalDocumentProcessing, accessUser, stock, pdfPath, DigitalDocumentAnswerStatus.COMPLETE);
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnPostListInPage1() throws Exception {
            final Map<String, Object> params = Map.of(
                "page", PAGE_1.toString(),
                "size", SIZE.toString()
            );

            final GetPostDigitalDocumentResponse firstResult = callApiAndResult(params);
            final Paging paging1 = firstResult.getPaging();
            assertThat(paging1.getTotalElements(), is(3L));

            assertThat(firstResult.getData(), is(notNullValue()));
            assertThat(firstResult.getData().size(), is(SIZE));

            final List<PostDigitalDocumentResponse> resultData1 = firstResult.getData();

            assertThat(resultData1.get(0).getTitle(), is(digitalDocumentProcessing.getPost().getTitle()));
            assertThat(resultData1.get(0).getDigitalDocument().getAnswerStatus(), is(DigitalDocumentAnswerStatus.COMPLETE.name()));

            assertThat(resultData1.get(1).getTitle(), is(digitalDocumentStandby.getPost().getTitle()));
            assertThat(resultData1.get(1).getDigitalDocument().getAnswerStatus(), is(nullValue()));
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnPostListInPage2() throws Exception {
            final int expectedResultSize = 1;
            final Map<String, Object> params = Map.of(
                "page", PAGE_2.toString(),
                "size", SIZE.toString()
            );
            final GetPostDigitalDocumentResponse secondResult = callApiAndResult(params);
            final Paging paging = secondResult.getPaging();
            assertThat(paging.getTotalElements(), is(3L));

            assertThat(secondResult.getData(), is(notNullValue()));
            assertThat(secondResult.getData().size(), is(expectedResultSize));

            final List<PostDigitalDocumentResponse> resultData = secondResult.getData();

            assertThat(resultData.get(0).getTitle(), is(digitalDocumentEnd.getPost().getTitle()));
            assertThat(resultData.get(0).getDigitalDocument().getAnswerStatus(), is(nullValue()));
        }

        private GetPostDigitalDocumentResponse callApiAndResult(Map<String, Object> param) throws Exception {
            final MvcResult response = callApi(param, DigitalDocumentType.DIGITAL_PROXY, status().isOk());

            return itUtil.getResult(response, GetPostDigitalDocumentResponse.class);
        }
    }

    @NotNull
    private MvcResult callApi(Map<String, Object> param, DigitalDocumentType type, ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                get(TARGET_API, type)
                    .params(toMultiValueMap(param))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(resultMatcher)
            .andReturn();
    }
}
