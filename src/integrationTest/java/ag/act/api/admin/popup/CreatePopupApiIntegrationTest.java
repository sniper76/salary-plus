package ag.act.api.admin.popup;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Popup;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.enums.AppLinkType;
import ag.act.enums.BoardCategory;
import ag.act.enums.popup.PopupDisplayTargetType;
import ag.act.enums.push.PushTargetType;
import ag.act.model.PopupDataResponse;
import ag.act.model.PopupDetailsResponse;
import ag.act.model.PopupRequest;
import ag.act.util.AppLinkUrlGenerator;
import ag.act.util.KoreanDateTimeUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomLongs.somePositiveLong;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class CreatePopupApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/popups";
    @Autowired
    private AppLinkUrlGenerator appLinkUrlGenerator;
    private PopupRequest request;
    private String adminJwt;
    private String stockCode;
    private Instant now;
    private Instant targetStartDatetime;
    private Instant targetEndDatetime;
    private Long postId;
    private String linkUrl;
    private BoardCategory boardCategory;

    @BeforeEach
    void setUp() {
        itUtil.init();
        final User user = itUtil.createAdminUser();
        adminJwt = itUtil.createJwt(user.getId());
        stockCode = someAlphanumericString(6);
        final Stock stock = itUtil.createStock(stockCode);
        final Board board = itUtil.createBoard(stock);
        final Post post = itUtil.createPost(board, user.getId());
        postId = post.getId();
        boardCategory = board.getCategory();

        linkUrl = appLinkUrlGenerator.generateBoardGroupPostLinkUrl(post);

        now = KoreanDateTimeUtil.getNowInKoreanTime().toInstant();
        targetStartDatetime = now.plus(1, ChronoUnit.DAYS);
        targetEndDatetime = now.plus(3, ChronoUnit.DAYS);
    }

    private PopupRequest genRequestDocument(
        PopupDisplayTargetType displayTargetType, PushTargetType stockTargetType, AppLinkType linkType,
        String stockCode, Long stockGroupId, String linkTitle, Long postId
    ) {
        return new PopupRequest()
            .title(someString(10))
            .content(someAlphanumericString(300))
            .displayTargetType(displayTargetType.name())
            .stockTargetType(stockTargetType.name())
            .stockCode(stockCode)
            .stockGroupId(stockGroupId)
            .linkType(linkType.name())
            .linkTitle(linkTitle)
            .postId(postId)
            .targetStartDatetime(targetStartDatetime)
            .targetEndDatetime(targetEndDatetime);
    }

    private MvcResult callApi(PopupRequest request, ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                post(TARGET_API)
                    .content(objectMapperUtil.toJson(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(adminJwt)))
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    private void assertResponse(PopupDataResponse result) {
        final PopupDetailsResponse resultData = result.getData();
        assertThat(resultData, notNullValue());
        assertThat(resultData.getDisplayTargetType(), is(request.getDisplayTargetType()));
        assertThat(resultData.getStockTargetType(), is(request.getStockTargetType()));
        assertThat(resultData.getLinkType(), is(request.getLinkType()));
        assertThat(resultData.getTargetStartDatetime(), is(request.getTargetStartDatetime()));
        assertThat(resultData.getTargetEndDatetime(), is(request.getTargetEndDatetime()));
    }

    private PopupDataResponse getResponse(MvcResult response) throws JsonProcessingException, UnsupportedEncodingException {
        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            PopupDataResponse.class
        );
    }

    @Nested
    class WhenCreateSuccess {
        @Nested
        class AndDisplayTargetTypeIsMainHome {
            @BeforeEach
            void setUp() {
                request = genRequestDocument(
                    PopupDisplayTargetType.MAIN_HOME,
                    PushTargetType.STOCK_GROUP,
                    AppLinkType.STOCK_HOME,
                    null,
                    null,
                    null,
                    null
                );
            }

            @Nested
            class AndStockTargetTypeIsStockGroup {
                @BeforeEach
                void setUp() {
                    request = request.stockGroupId(somePositiveLong());
                }

                @Nested
                class AndLinkTypeIsStockHome {
                    @DisplayName("Should return 200 response code when call " + TARGET_API)
                    @Test
                    void shouldReturnSuccess() throws Exception {
                        final MvcResult response = callApi(request, status().isBadRequest());

                        itUtil.assertErrorResponse(response, HttpStatus.BAD_REQUEST.value(), "타켓이 그룹이거나 종목전체 일때 링크(종목홈)을 선택할 수 없습니다.");
                    }
                }
            }

            @Nested
            class AndStockTargetTypeIsAll {
                @Nested
                class AndLinkTypeIsStockHome {
                    @BeforeEach
                    void setUp() {
                        request = request.stockTargetType(PushTargetType.ALL.name());
                    }

                    @DisplayName("Should return 200 response code when call " + TARGET_API)
                    @Test
                    void shouldReturnSuccess() throws Exception {
                        final MvcResult response = callApi(request, status().isBadRequest());

                        itUtil.assertErrorResponse(response, HttpStatus.BAD_REQUEST.value(), "타켓이 그룹이거나 종목전체 일때 링크(종목홈)을 선택할 수 없습니다.");
                    }
                }
            }

            @Nested
            class AndStockTargetTypeIsStock {
                @BeforeEach
                void setUp() {
                    request = request.stockCode(stockCode)
                        .stockTargetType(PushTargetType.STOCK.name())
                        .linkType(AppLinkType.LINK.name());
                }

                @Nested
                class AndLinkTypeIsLink {
                    @BeforeEach
                    void setUp() {
                        request = request.linkTitle(someString(10))
                            .postId(postId);
                    }

                    @DisplayName("Should return 200 response code when call " + TARGET_API)
                    @Test
                    void shouldReturnSuccess() throws Exception {
                        final MvcResult response = callApi(request, status().isOk());
                        final PopupDataResponse dataResponse = getResponse(response);
                        assertResponse(dataResponse);

                        final PopupDetailsResponse popupDetailsResponse = dataResponse.getData();
                        assertThat(popupDetailsResponse.getLinkUrl(), is(linkUrl));
                        assertThat(popupDetailsResponse.getPostId(), is(postId));

                        final Popup databasePopup = itUtil.findPopup(popupDetailsResponse.getId()).orElseThrow();
                        assertThat(databasePopup.getBoardCategory(), is(boardCategory));
                    }
                }

                @Nested
                class AndLinkTypeIsNotification {
                    @BeforeEach
                    void setUp() {
                        request = request.linkType(AppLinkType.NOTIFICATION.name())
                            .linkTitle(null)
                            .postId(null);
                    }

                    @DisplayName("Should return 200 response code when call " + TARGET_API)
                    @Test
                    void shouldReturnSuccess() throws Exception {
                        final MvcResult response = callApi(request, status().isOk());
                        assertResponse(getResponse(response));
                    }
                }

                @Nested
                class AndLinkTypeIsMainHome {
                    @BeforeEach
                    void setUp() {
                        request = request.linkType(AppLinkType.MAIN_HOME.name())
                            .linkTitle(null)
                            .postId(null);
                    }

                    @DisplayName("Should return 200 response code when call " + TARGET_API)
                    @Test
                    void shouldReturnSuccess() throws Exception {
                        final MvcResult response = callApi(request, status().isOk());
                        assertResponse(getResponse(response));
                    }
                }

                @Nested
                class AndLinkTypeIsNewHome {
                    @BeforeEach
                    void setUp() {
                        request = request.linkType(AppLinkType.NEWS_HOME.name())
                            .linkTitle(null)
                            .postId(null);
                    }

                    @DisplayName("Should return 200 response code when call " + TARGET_API)
                    @Test
                    void shouldReturnSuccess() throws Exception {
                        final MvcResult response = callApi(request, status().isOk());
                        assertResponse(getResponse(response));
                    }
                }

                @Nested
                class AndLinkTypeIsStockHome {
                    @BeforeEach
                    void setUp() {
                        request = request.linkType(AppLinkType.STOCK_HOME.name())
                            .linkTitle(null)
                            .postId(null);
                    }

                    @DisplayName("Should return 200 response code when call " + TARGET_API)
                    @Test
                    void shouldReturnSuccess() throws Exception {
                        final MvcResult response = callApi(request, status().isOk());
                        assertResponse(getResponse(response));
                    }
                }

                @Nested
                class AndLinkTypeIsNone {
                    @BeforeEach
                    void setUp() {
                        request = request.linkType(AppLinkType.NONE.name())
                            .linkTitle(null)
                            .postId(null);
                    }

                    @DisplayName("Should return 200 response code when call " + TARGET_API)
                    @Test
                    void shouldReturnSuccess() throws Exception {
                        final MvcResult response = callApi(request, status().isOk());
                        assertResponse(getResponse(response));
                    }
                }
            }
        }
    }
}
