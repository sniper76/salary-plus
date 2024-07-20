package ag.act.api.admin.popup;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.converter.DateTimeConverter;
import ag.act.entity.Board;
import ag.act.entity.Popup;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.StockGroup;
import ag.act.entity.User;
import ag.act.enums.AppLinkType;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;
import static shiver.me.timbers.data.random.RandomThings.someThing;

class UpdatePopupApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/popups/{popupId}";
    @Autowired
    private AppLinkUrlGenerator appLinkUrlGenerator;
    private PopupRequest request;
    private String adminJwt;
    private String stockCode;
    private LocalDateTime now;
    private LocalDateTime targetStartDatetime;
    private LocalDateTime targetEndDatetime;
    private Long changePostId;
    private String changeLinkUrl;
    private Long popupId;
    private Stock stock;
    private StockGroup stockGroup;

    @BeforeEach
    void setUp() {
        itUtil.init();
        final User user = itUtil.createAdminUser();
        adminJwt = itUtil.createJwt(user.getId());
        stockCode = someAlphanumericString(6);
        stock = itUtil.createStock(stockCode);
        stockGroup = itUtil.createStockGroup(someString(10));
        itUtil.createStockGroupMapping(stockCode, stockGroup.getId());
        final Board board = itUtil.createBoard(stock);
        final Post post = itUtil.createPost(board, user.getId());
        final Post changePost = itUtil.createPost(board, user.getId());
        changePostId = changePost.getId();

        changeLinkUrl = getLinkUrl(changePost);

        now = KoreanDateTimeUtil.getNowInKoreanTime().toLocalDateTime();
        targetStartDatetime = now.plus(1, ChronoUnit.DAYS);
        targetEndDatetime = now.plus(3, ChronoUnit.DAYS);
    }

    private String getLinkUrl(Post post) {
        return appLinkUrlGenerator.generateBoardGroupPostLinkUrl(post);
    }

    private PopupRequest genRequestDocument(Popup popup) {
        return new PopupRequest()
            .id(popup.getId())
            .title(popup.getTitle())
            .content(popup.getContent())
            .displayTargetType(popup.getDisplayTargetType().name())
            .stockTargetType(popup.getStockTargetType().name())
            .stockCode(popup.getStockCode())
            .stockGroupId(popup.getStockGroupId())
            .linkType(popup.getLinkType().name())
            .linkTitle(popup.getLinkTitle())
            .postId(popup.getPostId())
            .targetStartDatetime(DateTimeConverter.convert(popup.getTargetStartDatetime()))
            .targetEndDatetime(DateTimeConverter.convert(popup.getTargetEndDatetime()));
    }

    private MvcResult callApi(Long popupId, PopupRequest request, ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                patch(TARGET_API, popupId)
                    .content(objectMapperUtil.toJson(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + adminJwt)
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    private void assertResponse(PopupDataResponse result) {
        final PopupDetailsResponse resultData = result.getData();
        assertThat(resultData, notNullValue());
        assertThat(resultData.getId(), is(request.getId()));
        assertThat(resultData.getTitle(), is(request.getTitle()));
        assertThat(resultData.getContent(), is(request.getContent()));
        assertThat(resultData.getStockCode(), is(request.getStockCode()));
        assertThat(resultData.getStockGroupId(), is(request.getStockGroupId()));
        assertThat(resultData.getDisplayTargetType(), is(request.getDisplayTargetType()));
        assertThat(resultData.getStockTargetType(), is(request.getStockTargetType()));
        assertThat(resultData.getLinkType(), is(request.getLinkType()));
        assertThat(resultData.getLinkTitle(), is(request.getLinkTitle()));
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
    class WhenUpdate {
        private Popup popup;

        @Nested
        class AndSuccess {
            private AppLinkType linkType;
            private PushTargetType stockTargetType;
            private String stockCode;
            private Long stockGroupId;

            @BeforeEach
            void setUp() {
                popup = itUtil.createPopup(someString(10), targetStartDatetime, targetEndDatetime);
                popupId = popup.getId();
            }

            @Nested
            class AndStockTargetType {
                @BeforeEach
                void setUp() {
                    stockTargetType = someEnum(PushTargetType.class);

                    switch (stockTargetType) {
                        case STOCK -> {
                            stockCode = stock.getCode();

                            linkType = someEnum(AppLinkType.class);
                        }
                        case STOCK_GROUP -> {
                            stockGroupId = stockGroup.getId();

                            linkType = someThing(
                                AppLinkType.LINK,
                                AppLinkType.NOTIFICATION,
                                AppLinkType.MAIN_HOME,
                                AppLinkType.NEWS_HOME,
                                AppLinkType.NONE
                            );
                        }
                        default -> {
                            stockCode = null;
                            stockGroupId = null;

                            linkType = someThing(
                                AppLinkType.LINK,
                                AppLinkType.NOTIFICATION,
                                AppLinkType.MAIN_HOME,
                                AppLinkType.NEWS_HOME,
                                AppLinkType.NONE
                            );
                        }
                    }

                    request = genRequestDocument(popup)
                        .stockTargetType(stockTargetType.name())
                        .stockCode(stockCode)
                        .stockGroupId(stockGroupId)
                        .linkType(linkType.name())
                    ;

                    switch (linkType) {
                        case LINK -> {
                            request.linkTitle(someString(5));
                            request.postId(changePostId);
                        }
                        case NONE -> {
                            request.linkTitle(null);
                            request.postId(null);
                        }
                        default -> {
                            request.linkTitle(someString(5));
                            request.postId(null);
                        }
                    }
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnSuccess() throws Exception {
                    final MvcResult response = callApi(popupId, request, status().isOk());
                    final PopupDataResponse popupDataResponse = getResponse(response);
                    assertResponse(popupDataResponse);

                    final PopupDetailsResponse popupDetailsResponse = popupDataResponse.getData();
                    if (linkType == AppLinkType.LINK) {
                        assertThat(popupDetailsResponse.getLinkUrl(), is(changeLinkUrl));
                        assertThat(popupDetailsResponse.getPostId(), is(changePostId));
                    }
                }
            }
        }
    }
}
