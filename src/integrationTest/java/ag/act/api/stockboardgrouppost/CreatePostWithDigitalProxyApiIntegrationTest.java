package ag.act.api.stockboardgrouppost;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.RoleType;
import ag.act.model.CreateDigitalProxyRequest;
import ag.act.model.CreatePostRequest;
import ag.act.model.DigitalProxyResponse;
import ag.act.model.PostDetailsDataResponse;
import ag.act.model.PostDetailsResponse;
import ag.act.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.function.Consumer;
import java.util.stream.Stream;

import static ag.act.TestUtil.assertTime;
import static ag.act.TestUtil.someBoardCategoryExceptDebate;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomTimes.someTimeYesterday;

class CreatePostWithDigitalProxyApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts";

    private CreatePostRequest request;
    private String jwt;
    private Stock stock;
    private Board board;
    private User user;

    @BeforeEach
    void setUp() {
        itUtil.init();
        user = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());
        stock = itUtil.createStock();
        itUtil.createSolidarity(stock.getCode());
        itUtil.createUserHoldingStock(stock.getCode(), user);
    }

    private CreatePostRequest genRequest() {
        return itUtil.generateDigitalProxyCreatePostRequest(board);
    }

    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                post(TARGET_API, stock.getCode(), board.getGroup())
                    .content(objectMapperUtil.toRequestBody(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    private void callApiWithErrorMessage(ResultMatcher resultMatcher, String expectedErrorMessage) throws Exception {
        MvcResult response = callApi(resultMatcher);

        itUtil.assertErrorResponse(response, 400, expectedErrorMessage);
    }

    @Nested
    class WhenUserIsAdmin {
        @BeforeEach
        void setUp() {
            user = itUtil.createUserRole(user, RoleType.ADMIN);
        }

        @Nested
        class AndCreatePostAndDigitalProxy {

            @BeforeEach
            void setUp() {
                board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.DIGITAL_DELEGATION);
                request = genRequest();
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                MvcResult response = callApi(status().isOk());

                final PostDetailsDataResponse result = objectMapperUtil.toResponse(
                    response.getResponse().getContentAsString(),
                    PostDetailsDataResponse.class
                );

                assertResponse(result);
            }

            private void assertResponse(PostDetailsDataResponse result) {
                final PostDetailsResponse createUpdateResponse = result.getData();

                assertThat(createUpdateResponse.getId(), is(notNullValue()));
                assertThat(createUpdateResponse.getBoardId(), is(board.getId()));
                assertThat(createUpdateResponse.getTitle(), is(request.getTitle()));
                assertThat(createUpdateResponse.getContent(), is(request.getContent()));
                assertThat(createUpdateResponse.getStatus(), is(
                    (request.getIsActive() == null || request.getIsActive())
                        ? Status.ACTIVE
                        : Status.INACTIVE_BY_ADMIN
                ));
                assertThat(createUpdateResponse.getLikeCount(), anyOf(nullValue(), is(0L)));
                assertThat(createUpdateResponse.getCommentCount(), anyOf(nullValue(), is(0L)));
                assertThat(createUpdateResponse.getViewCount(), anyOf(nullValue(), is(0L)));
                assertThat(createUpdateResponse.getLiked(), is(false));
                assertThat(createUpdateResponse.getDeletedAt(), is(nullValue()));

                if (request.getIsAnonymous()) {
                    assertThat(createUpdateResponse.getUserProfile().getNickname(), is("익명"));
                } else {
                    assertThat(createUpdateResponse.getUserProfile().getNickname(), is(user.getNickname()));
                    assertThat(createUpdateResponse.getUserProfile().getProfileImageUrl(), is(user.getProfileImageUrl()));
                    assertThat(createUpdateResponse.getUserProfile().getIndividualStockCountLabel(), nullValue());
                    assertThat(createUpdateResponse.getUserProfile().getTotalAssetLabel(), nullValue());
                }
                assertThat(createUpdateResponse.getUserProfile().getUserIp(), is("127.0"));

                final CreateDigitalProxyRequest requestDigitalProxy = request.getDigitalProxy();
                final DigitalProxyResponse responseDigitalProxy = createUpdateResponse.getDigitalProxy();
                assertThat(responseDigitalProxy.getId(), is(notNullValue()));
                assertThat(responseDigitalProxy.getTemplateName(), is(requestDigitalProxy.getTemplateName()));
                assertThat(responseDigitalProxy.getTemplateId(), is(requestDigitalProxy.getTemplateId()));
                assertThat(responseDigitalProxy.getTemplateRole(), is(requestDigitalProxy.getTemplateRole()));
                assertThat(responseDigitalProxy.getStatus(), is(Status.ACTIVE.name()));
                assertTime(responseDigitalProxy.getTargetStartDate(), requestDigitalProxy.getTargetStartDate());
                assertTime(responseDigitalProxy.getTargetEndDate(), requestDigitalProxy.getTargetEndDate());
                assertTime(responseDigitalProxy.getUpdatedAt(), is(notNullValue()));
                assertTime(responseDigitalProxy.getCreatedAt(), is(notNullValue()));

                final Post post = itUtil.findPostNoneNull(createUpdateResponse.getId());
                final Long expectedProxyId = post.getDigitalProxy().getId();
                assertThat(responseDigitalProxy.getId(), is(expectedProxyId));
            }
        }

        @Nested
        class AndDigitalProxyTargetEndDateIsPast {

            @BeforeEach
            void setUp() {
                board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.DIGITAL_DELEGATION);
                request = genRequest();
                request.getDigitalProxy().setTargetEndDate(someTimeYesterday().toInstant());
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequest() throws Exception {
                callApiWithErrorMessage(
                    status().isBadRequest(),
                    "의결권위임 종료일은 현재 시간 이후로 설정해주세요."
                );
            }
        }

        @Nested
        class AndRequiredParameterIsMissing {

            @BeforeEach
            void setUp() {
                stock = itUtil.createStock();
                itUtil.createSolidarity(stock.getCode());
                itUtil.createUserHoldingStock(stock.getCode(), user);
                board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.DIGITAL_DELEGATION);
            }

            private static Stream<Arguments> valueProvider() {
                return Stream.of(
                    Arguments.of(
                        (Consumer<CreateDigitalProxyRequest>) (digitalProxy) -> digitalProxy.setTemplateId(null),
                        "Template Id 을 확인해주세요."
                    ),
                    Arguments.of(
                        (Consumer<CreateDigitalProxyRequest>) (digitalProxy) -> digitalProxy.setTemplateName(null),
                        "Template Name 을 확인해주세요."
                    ),
                    Arguments.of(
                        (Consumer<CreateDigitalProxyRequest>) (digitalProxy) -> digitalProxy.setTemplateRole(null),
                        "Template Role 을 확인해주세요."
                    )
                );
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @ParameterizedTest(name = "{index} => updater=''{0}'', expectedErrorMessage=''{1}''")
            @MethodSource("valueProvider")
            void shouldReturnBadRequest(Consumer<CreateDigitalProxyRequest> updater, String expectedErrorMessage) throws Exception {
                // Given
                request = genRequest();
                updater.accept(request.getDigitalProxy());

                // When / Then
                callApiWithErrorMessage(status().isBadRequest(), expectedErrorMessage);
            }
        }

    }

    @Nested
    class WhenUserIsNotAdmin {

        @BeforeEach
        void setUp() {
            board = itUtil.createBoard(stock);
        }

        @Nested
        class AndTryToCreatePostInNotDebateBoardCategory {

            @BeforeEach
            void setUp() {
                board.setCategory(someBoardCategoryExceptDebate());
                board = itUtil.updateBoard(board);
                request = genRequest();
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequest() throws Exception {
                callApiWithErrorMessage(
                    status().isBadRequest(),
                    "%s 게시판에 글쓰기 권한이 없습니다.".formatted(board.getCategory().getDisplayName())
                );
            }
        }

        @Nested
        class AndTryToCreatePostWithDigitalProxyInDebateBoardCategory {

            @BeforeEach
            void setUp() {
                board.setCategory(BoardCategory.DEBATE);
                board = itUtil.updateBoard(board);
                request = genRequest();
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequest() throws Exception {
                callApiWithErrorMessage(
                    status().isBadRequest(),
                    "%s 게시판에 의결권위임을 생성할 권한이 없습니다.".formatted(board.getCategory().getDisplayName())
                );
            }
        }
    }
}
