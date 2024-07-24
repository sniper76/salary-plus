package ag.act.api.admin.popup;

import ag.act.entity.Popup;
import ag.act.entity.StockGroup;
import ag.act.entity.User;
import ag.act.enums.AppLinkType;
import ag.act.enums.BoardCategory;
import ag.act.enums.popup.PopupDisplayTargetType;
import ag.act.enums.push.PushTargetType;
import ag.act.model.Status;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;

import static ag.act.TestUtil.replacePlaceholders;
import static ag.act.TestUtil.someStockCode;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomStrings.someString;
import static shiver.me.timbers.data.random.RandomThings.someThing;

public class GetPopupExclusiveApiIntegrationTest extends PopupIntegrationTest {

    private static final String TARGET_API = "/api/popup/exclusive?displayTargetType={displayTargetType}";
    private static final String TARGET_STOCK_API = "/api/popup/exclusive?displayTargetType={displayTargetType}&stockCode={stockCode}";

    private String jwt;
    private Popup popup;
    private PopupDisplayTargetType displayTargetType;
    private PushTargetType stockTargetType;
    private String stockCode;
    private User user;
    private Long stockGroupId;

    @BeforeEach
    void setUp() {
        itUtil.init();
        user = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());

        displayTargetType = someEnum(PopupDisplayTargetType.class);
    }

    @AfterEach
    void tearDown() {
        itUtil.findAllPopups()
            .forEach(it -> {
                it.setStatus(Status.DELETED);
                itUtil.updatePopup(it);
            });
    }

    private Popup createPopup(
        AppLinkType appLinkType, PopupDisplayTargetType popupDisplayTargetType, PushTargetType stockTargetType, String stockCode,
        Long stockGroupId
    ) {
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime endDate = now.plusDays(10);

        Popup popup1 = itUtil.createPopup(now, endDate);

        popup1.setStockGroupId(stockGroupId);
        popup1.setStockCode(stockCode);
        popup1.setLinkType(appLinkType);
        popup1.setStockTargetType(stockTargetType);
        popup1.setDisplayTargetType(popupDisplayTargetType);
        return itUtil.updatePopup(popup1);
    }

    private Popup createPopup(
        AppLinkType appLinkType,
        PopupDisplayTargetType popupDisplayTargetType,
        PushTargetType stockTargetType,
        String stockCode,
        Long stockGroupId,
        BoardCategory boardCategory
    ) {
        Popup popup1 = createPopup(appLinkType, popupDisplayTargetType, stockTargetType, stockCode, stockGroupId);
        popup1.setBoardCategory(boardCategory);
        return itUtil.updatePopup(popup1);
    }

    private ag.act.model.PopupDetailsDataResponse callApiAndGetResult() throws Exception {
        final String targetApiUri = stockCode == null
            ? replacePlaceholders(TARGET_API, displayTargetType)
            : replacePlaceholders(TARGET_STOCK_API, displayTargetType, stockCode);

        MvcResult result = mockMvc
            .perform(
                get(targetApiUri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(status().isOk())
            .andReturn();

        return objectMapperUtil.toResponse(
            result.getResponse().getContentAsString(),
            ag.act.model.PopupDetailsDataResponse.class
        );
    }

    @Nested
    class WhenStockTargetTypeIsAll extends DefaultTestCases {
        @BeforeEach
        void setUp() {
            stockTargetType = PushTargetType.ALL;
            stockCode = null;
        }
    }

    @Nested
    class WhenStockTargetTypeIsStock extends DefaultTestCases {
        @BeforeEach
        void setUp() {
            stockTargetType = PushTargetType.STOCK;
            stockCode = someStockCode();

            itUtil.createStock(stockCode);
            itUtil.createUserHoldingStock(stockCode, user);
        }
    }

    @Nested
    class WhenStockTargetTypeIsStockGroup extends DefaultTestCases {
        @BeforeEach
        void setUp() {
            stockTargetType = PushTargetType.STOCK_GROUP;
            stockCode = null;
            final String stockCodeForStockGroup = someStockCode();

            itUtil.createStock(stockCodeForStockGroup);
            itUtil.createUserHoldingStock(stockCodeForStockGroup, user);
            final StockGroup stockGroup = itUtil.createStockGroup(someString(5));
            itUtil.createStockGroupMapping(stockCodeForStockGroup, stockGroup.getId());
            stockGroupId = stockGroup.getId();
        }
    }

    class DefaultTestCases {

        @Nested
        class WhenGetPopupLinkTypeIsNone {
            @BeforeEach
            void setUp() {
                popup = createPopup(AppLinkType.NONE, displayTargetType, stockTargetType, stockCode, stockGroupId);
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                final ag.act.model.PopupDetailsDataResponse result = callApiAndGetResult();

                assertPopupDetailResponse(result.getData(), popup);
            }
        }

        @Nested
        class WhenGetPopupLinkTypeIsStockHome {
            @BeforeEach
            void setUp() {
                createPopup(AppLinkType.NONE, displayTargetType, stockTargetType, stockCode, stockGroupId);
                popup = createPopup(AppLinkType.STOCK_HOME, displayTargetType, stockTargetType, stockCode, stockGroupId);
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                final ag.act.model.PopupDetailsDataResponse result = callApiAndGetResult();

                assertPopupDetailResponse(result.getData(), popup);
            }
        }

        @Nested
        class WhenGetPopupLinkTypeIsNotification {
            @BeforeEach
            void setUp() {
                createPopup(AppLinkType.NONE, displayTargetType, stockTargetType, stockCode, stockGroupId);
                createPopup(AppLinkType.STOCK_HOME, displayTargetType, stockTargetType, stockCode, stockGroupId);
                popup = createPopup(AppLinkType.NOTIFICATION, displayTargetType, stockTargetType, stockCode, stockGroupId);
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                final ag.act.model.PopupDetailsDataResponse result = callApiAndGetResult();

                assertPopupDetailResponse(result.getData(), popup);
            }
        }

        @Nested
        class WhenGetPopupLinkTypeIsLinkAndDigitalDelegation {
            @BeforeEach
            void setUp() {
                createPopup(AppLinkType.NONE, displayTargetType, stockTargetType, stockCode, stockGroupId);
                createPopup(AppLinkType.STOCK_HOME, displayTargetType, stockTargetType, stockCode, stockGroupId);
                createPopup(AppLinkType.NOTIFICATION, displayTargetType, stockTargetType, stockCode, stockGroupId);
                createPopup(AppLinkType.LINK, displayTargetType, stockTargetType, stockCode, stockGroupId,
                    someThing(
                        BoardCategory.SURVEYS,
                        BoardCategory.CO_HOLDING_ARRANGEMENTS,
                        BoardCategory.ETC,
                        BoardCategory.SOLIDARITY_LEADER_LETTERS,
                        BoardCategory.DAILY_ACT,
                        BoardCategory.STOCKHOLDER_ACTION,
                        BoardCategory.STOCK_ANALYSIS_DATA
                    )
                );
                popup = createPopup(
                    AppLinkType.LINK, displayTargetType, stockTargetType, stockCode, stockGroupId, BoardCategory.DIGITAL_DELEGATION
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                final ag.act.model.PopupDetailsDataResponse result = callApiAndGetResult();

                assertPopupDetailResponse(result.getData(), popup);
            }
        }
    }
}
