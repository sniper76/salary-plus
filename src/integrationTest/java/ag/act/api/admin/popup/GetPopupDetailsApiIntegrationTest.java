package ag.act.api.admin.popup;

import ag.act.entity.Popup;
import ag.act.entity.Stock;
import ag.act.entity.StockGroup;
import ag.act.entity.User;
import ag.act.enums.AppLinkType;
import ag.act.enums.popup.PopupDisplayTargetType;
import ag.act.enums.push.PushTargetType;
import ag.act.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static ag.act.TestUtil.someLocalDateTimeInTheFuture;
import static ag.act.TestUtil.someLocalDateTimeInThePast;
import static ag.act.TestUtil.someStockCode;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

public class GetPopupDetailsApiIntegrationTest extends PopupIntegrationTest {

    private static final String TARGET_API = "/api/admin/popups/{popupId}";

    private String jwt;
    private Popup popup;
    private String popupTitle;
    private Long popupId;

    @BeforeEach
    void setUp() {
        itUtil.init();
        final User adminUser = itUtil.createAdminUser();
        jwt = itUtil.createJwt(adminUser.getId());

        popupTitle = someAlphanumericString(30);
    }

    private ag.act.model.PopupDetailsDataResponse callApiAndGetResult() throws Exception {
        MvcResult result = mockMvc
            .perform(
                get(TARGET_API, popupId)
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
    class WhenGetPopupDetails {
        @Nested
        class AndNormal {
            @BeforeEach
            void setUp() {
                popup = itUtil.createPopup(popupTitle);
                popupId = popup.getId();
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                final ag.act.model.PopupDetailsDataResponse result = callApiAndGetResult();

                assertPopupDetailResponse(result.getData(), popup);
            }
        }

        @Nested
        class AndStock {
            private Stock stock;

            @BeforeEach
            void setUp() {
                final String stockCode = someStockCode();
                stock = itUtil.createStock(stockCode);
                popup = itUtil.createPopup(
                    popupTitle, PopupDisplayTargetType.STOCK_HOME, PushTargetType.STOCK, AppLinkType.STOCK_HOME,
                    stockCode, null, someLocalDateTimeInThePast(), someLocalDateTimeInTheFuture()
                );
                popupId = popup.getId();
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                final ag.act.model.PopupDetailsDataResponse result = callApiAndGetResult();

                final ag.act.model.PopupDetailsResponse resultData = result.getData();
                assertPopupDetailResponse(resultData, popup);

                assertThat(resultData.getStockName(), is(stock.getName()));
            }
        }

        @Nested
        class AndStockGroup {
            private Stock stock;
            private StockGroup stockGroup;

            @BeforeEach
            void setUp() {
                final String stockCode = someStockCode();
                stock = itUtil.createStock(stockCode);
                stockGroup = itUtil.createStockGroup(someString(10));
                itUtil.createStockGroupMapping(stockCode, stockGroup.getId());

                popup = itUtil.createPopup(
                    popupTitle, PopupDisplayTargetType.STOCK_HOME, PushTargetType.STOCK, AppLinkType.STOCK_HOME,
                    stockCode, stockGroup.getId(), someLocalDateTimeInThePast(), someLocalDateTimeInTheFuture()
                );
                popupId = popup.getId();
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                final ag.act.model.PopupDetailsDataResponse result = callApiAndGetResult();

                final ag.act.model.PopupDetailsResponse resultData = result.getData();
                assertPopupDetailResponse(resultData, popup);

                assertThat(resultData.getStockName(), is(stock.getName()));
                assertThat(resultData.getStockGroupName(), is(stockGroup.getName()));
            }
        }

        @Nested
        class AndNotActive {
            @BeforeEach
            void setUp() {
                popup = itUtil.createPopup(popupTitle);
                popup.setStatus(Status.DELETED_BY_ADMIN);
                itUtil.updatePopup(popup);
                popupId = popup.getId();
            }

            @DisplayName("Should return 404 response code when call " + TARGET_API)
            @Test
            void shouldReturnNotFound() throws Exception {
                callApiToFail("해당 팝업을 찾을 수 없습니다.");
            }

            private void callApiToFail(String expectedErrorMessage) throws Exception {
                MvcResult result = mockMvc
                    .perform(
                        get(TARGET_API, popupId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .headers(headers(jwt(jwt)))
                    )
                    .andExpect(status().isNotFound())
                    .andReturn();

                itUtil.assertErrorResponse(result, 404, expectedErrorMessage);
            }
        }
    }
}
