package ag.act.api.admin.acceptor;


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
import ag.act.enums.admin.PostSearchType;
import ag.act.model.GetPostDigitalDocumentDataResponse;
import ag.act.model.Paging;
import ag.act.model.PostResponse;
import ag.act.model.UserDigitalDocumentResponse;
import org.hamcrest.Matcher;
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

import static ag.act.TestUtil.assertTime;
import static ag.act.TestUtil.someLocalDateTimeInTheFutureDaysBetween;
import static ag.act.TestUtil.someStockCode;
import static ag.act.TestUtil.toMultiValueMap;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;
import static shiver.me.timbers.data.random.RandomThings.someThing;

class GetDigitalDocumentPostsOfAcceptorIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/acceptors/digital-document/{digitalDocumentType}";

    private String jwt;
    private DigitalDocument digitalDocument1;
    private DigitalDocument digitalDocument2;
    private DigitalDocument digitalDocument3;
    private User acceptorUser;
    private Stock stock;
    private Board digitalDelegationBoard;

    @BeforeEach
    void setUp() {
        itUtil.init();
        acceptorUser = itUtil.createAcceptorUser();
        jwt = itUtil.createJwt(acceptorUser.getId());

        stock = itUtil.createStock(someStockCode());
        digitalDelegationBoard = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.DIGITAL_DELEGATION);
        createWrongDigitalDocumentsOnAnotherBoard();
    }

    private void createWrongDigitalDocumentsOnAnotherBoard() {
        Board anotherBoard = itUtil.createBoard(stock, BoardGroup.ACTION, someThing(BoardCategory.CO_HOLDING_ARRANGEMENTS, BoardCategory.ETC));

        LocalDateTime now = LocalDateTime.now();
        createDigitalDocument(itUtil.createUser(), now.minusDays(5), now.minusDays(1), anotherBoard, someString(5));
        createDigitalDocument(itUtil.createUser(), now.minusDays(6), now.minusDays(2), anotherBoard, someString(7));
        createDigitalDocument(itUtil.createUser(), now.minusDays(7), now.minusDays(3), anotherBoard, someString(9));
        createDigitalDocument(itUtil.createUser(), now.minusDays(8), now.minusDays(4), anotherBoard, someString(11));
    }

    private DigitalDocument createDigitalDocument(User acceptorUser, LocalDateTime startDate, LocalDateTime endDate, Board board, String title) {
        final User postWriter = itUtil.createUser();
        final Post post = itUtil.createPost(board, postWriter.getId(), title, false);
        final LocalDate referenceDate = someLocalDateTimeInTheFutureDaysBetween(5, 10).toLocalDate();

        final DigitalDocument digitalDocument = itUtil.createDigitalDocument(
            post, stock, acceptorUser, DigitalDocumentType.DIGITAL_PROXY, startDate, endDate, referenceDate
        );
        post.setBoard(board);
        digitalDocument.setPost(post);

        itUtil.createDigitalDocumentItemList(digitalDocument);

        return digitalDocument;
    }

    private void assertDigitalDocument(
        UserDigitalDocumentResponse digitalDocumentResponse,
        DigitalDocument digitalDocument,
        Matcher<Object> answerStatusMatcher
    ) {
        assertThat(digitalDocumentResponse.getAnswerStatus(), answerStatusMatcher);
        assertThat(digitalDocumentResponse.getStock().getCode(), is(stock.getCode()));
        assertThat(digitalDocumentResponse.getDigitalDocumentType(), is(DigitalDocumentType.DIGITAL_PROXY.name()));
        assertThat(digitalDocumentResponse.getAcceptUser().getId(), is(acceptorUser.getId()));
        assertTime(digitalDocumentResponse.getTargetStartDate(), digitalDocument.getTargetStartDate());
        assertTime(digitalDocumentResponse.getTargetEndDate(), digitalDocument.getTargetEndDate());
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

        private String searchKeyword;

        @BeforeEach
        void setUp() {
            LocalDateTime now = LocalDateTime.now();
            searchKeyword = someAlphanumericString(5);
            final String title = someString(5) + searchKeyword + someString(5);

            digitalDocument1 = createDigitalDocument(acceptorUser, now.minusDays(5), now.minusDays(1), digitalDelegationBoard, someString(20));
            digitalDocument2 = createDigitalDocument(acceptorUser, now.minusDays(5), now.plusHours(10), digitalDelegationBoard, title);
            digitalDocument3 = createDigitalDocument(acceptorUser, now.plusHours(10), now.plusDays(3), digitalDelegationBoard, someString(30));

            final Stock stock = digitalDocument2.getPost().getBoard().getStock();
            final String pdfPath = someString(10);
            itUtil.createDigitalDocumentUser(digitalDocument2, acceptorUser, stock, pdfPath, DigitalDocumentAnswerStatus.COMPLETE);
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnPostListInPage1() throws Exception {
            final Map<String, Object> params = Map.of(
                "page", PAGE_1.toString(),
                "size", SIZE.toString()
            );

            final GetPostDigitalDocumentDataResponse firstResult = callApiAndResult(params);
            final Paging paging1 = firstResult.getPaging();
            assertThat(paging1.getTotalElements(), is(3L));

            assertThat(firstResult.getData(), is(notNullValue()));
            assertThat(firstResult.getData().size(), is(SIZE));

            final List<PostResponse> postResponses = firstResult.getData();

            assertThat(postResponses.get(0).getTitle(), is(digitalDocument3.getPost().getTitle()));
            assertDigitalDocument(postResponses.get(0).getDigitalDocument(), digitalDocument3, nullValue());

            assertThat(postResponses.get(1).getTitle(), is(digitalDocument2.getPost().getTitle()));
            assertDigitalDocument(postResponses.get(1).getDigitalDocument(), digitalDocument2, is(DigitalDocumentAnswerStatus.COMPLETE.name()));
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnPostListInPage2() throws Exception {
            final int expectedResultSize = 1;
            final Map<String, Object> params = Map.of(
                "page", PAGE_2.toString(),
                "size", SIZE.toString()
            );
            final GetPostDigitalDocumentDataResponse secondResult = callApiAndResult(params);
            final Paging paging = secondResult.getPaging();
            assertThat(paging.getTotalElements(), is(3L));

            assertThat(secondResult.getData(), is(notNullValue()));
            assertThat(secondResult.getData().size(), is(expectedResultSize));

            final List<PostResponse> postResponses = secondResult.getData();

            assertThat(postResponses.get(0).getTitle(), is(digitalDocument1.getPost().getTitle()));
            assertDigitalDocument(postResponses.get(0).getDigitalDocument(), digitalDocument1, nullValue());
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnPostListWithSearchKeyword() throws Exception {
            final int expectedResultSize = 1;
            final Map<String, Object> params = Map.of(
                "searchType", PostSearchType.TITLE.name(),
                "searchKeyword", searchKeyword,
                "page", PAGE_1.toString(),
                "size", SIZE.toString()
            );
            final GetPostDigitalDocumentDataResponse secondResult = callApiAndResult(params);
            final Paging paging = secondResult.getPaging();
            assertThat(paging.getTotalElements(), is(1L));

            assertThat(secondResult.getData(), is(notNullValue()));
            assertThat(secondResult.getData().size(), is(expectedResultSize));

            final List<PostResponse> postResponses = secondResult.getData();

            assertThat(postResponses.get(0).getTitle(), is(digitalDocument2.getPost().getTitle()));
            assertDigitalDocument(postResponses.get(0).getDigitalDocument(), digitalDocument2, is(DigitalDocumentAnswerStatus.COMPLETE.name()));
        }

        @Nested
        class WhenUserIsNotAcceptor {

            @BeforeEach
            void setUp() {
                final User adminUser = itUtil.createAdminUser();
                jwt = itUtil.createJwt(adminUser.getId());
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                MvcResult response = callApi(
                    Map.of(),
                    DigitalDocumentType.DIGITAL_PROXY,
                    status().isBadRequest()
                );

                itUtil.assertErrorResponse(response, 400, "수임인만 사용 가능한 기능입니다.");
            }
        }

        private GetPostDigitalDocumentDataResponse callApiAndResult(Map<String, Object> param) throws Exception {
            final MvcResult response = callApi(param, DigitalDocumentType.DIGITAL_PROXY, status().isOk());

            return itUtil.getResult(response, GetPostDigitalDocumentDataResponse.class);
        }
    }

    @NotNull
    private MvcResult callApi(
        Map<String, Object> param,
        DigitalDocumentType type,
        ResultMatcher resultMatcher
    ) throws Exception {
        return mockMvc
            .perform(
                get(TARGET_API, type)
                    .params(toMultiValueMap(param))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(resultMatcher)
            .andReturn();
    }
}
