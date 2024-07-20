package ag.act.api.admin.popup;


import ag.act.entity.Popup;
import ag.act.entity.User;
import ag.act.enums.popup.PopupSearchType;
import ag.act.enums.popup.PopupStatus;
import ag.act.model.PopupDetailsResponse;
import ag.act.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;

import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class GetPopupListApiIntegrationTest extends PopupIntegrationTest {

    private static final String TARGET_API = "/api/admin/popups";

    private String jwt;
    private String popupStatus;
    private String searchKeyword;
    private String searchType;


    @BeforeEach
    void setUp() {
        final String stockCode = someStockCode();

        itUtil.init();
        dbCleaner.clean();

        final User user = itUtil.createAdminUser();
        jwt = itUtil.createJwt(user.getId());
        itUtil.createStock(stockCode);
        itUtil.createStockGroup(someString(20));
    }

    private ag.act.model.GetPopupDataResponse callApi() throws Exception {
        MvcResult response = mockMvc
            .perform(
                get(TARGET_API)
                    .param("popupStatus", popupStatus)
                    .param("searchKeyword", searchKeyword)
                    .param("searchType", searchType)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(status().isOk())
            .andReturn();

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            ag.act.model.GetPopupDataResponse.class
        );
    }

    @SuppressWarnings("UnusedReturnValue")
    private Popup createDeletedByAdminPopup() {
        Popup popup = itUtil.createPopup(someAlphanumericString(10));
        popup.setStatus(Status.DELETED_BY_ADMIN);
        return itUtil.updatePopup(popup);
    }

    private Popup createWillStartPopup() {
        return itUtil.createPopup(LocalDateTime.now().plusMinutes(30), LocalDateTime.now().plusMinutes(60));
    }

    private Popup createOnGoingPopup() {
        return itUtil.createPopup(LocalDateTime.now().minusMinutes(30), LocalDateTime.now().plusMinutes(30));
    }

    private Popup createCompletedPopup() {
        return itUtil.createPopup(LocalDateTime.now().minusMinutes(60), LocalDateTime.now().minusMinutes(30));
    }

    @Nested
    class WhenSearchTypeIsTitle {

        @BeforeEach
        void setUp() {
            searchType = PopupSearchType.TITLE.name();
        }

        @Nested
        class AndSearchKeywordProvided {

            private Popup expectedPopup;

            @BeforeEach
            void setUp() {
                searchKeyword = "TEST_POPUP_TITLE";

                expectedPopup = itUtil.createPopup(someString(10) + searchKeyword + someString(10));
                itUtil.createPopup(someAlphanumericString(15));
                itUtil.createPopup(someAlphanumericString(20));
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                assertResponse(callApi());
            }

            private void assertResponse(ag.act.model.GetPopupDataResponse result) {

                final List<PopupDetailsResponse> popupDetailsResponses = result.getData();

                assertThat(popupDetailsResponses.size(), is(1));
                assertPopupDetailResponse(popupDetailsResponses.get(0), expectedPopup);
            }
        }

        @Nested
        class AndSearchKeywordIsEmpty {

            private List<Popup> expectedPopups;

            @BeforeEach
            void setUp() {
                searchKeyword = "";

                expectedPopups = List.of(
                    itUtil.createPopup(someAlphanumericString(10)),
                    itUtil.createPopup(someAlphanumericString(15)),
                    itUtil.createPopup(someAlphanumericString(20)),
                    itUtil.createPopup(someAlphanumericString(30))
                );
                createDeletedByAdminPopup();
                createDeletedByAdminPopup();
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnAllPushesInCreatedAtDesc() throws Exception {
                assertResponse(callApi());
            }

            private void assertResponse(ag.act.model.GetPopupDataResponse result) {

                final List<ag.act.model.PopupDetailsResponse> popupListItemResponseList = result.getData();

                assertThat(popupListItemResponseList.size(), is(4));

                assertPopupDetailResponse(popupListItemResponseList.get(0), expectedPopups.get(3));
                assertPopupDetailResponse(popupListItemResponseList.get(1), expectedPopups.get(2));
                assertPopupDetailResponse(popupListItemResponseList.get(2), expectedPopups.get(1));
                assertPopupDetailResponse(popupListItemResponseList.get(3), expectedPopups.get(0));
            }
        }
    }

    @Nested
    class WhenPopupStatusProvided {
        @Nested
        class AndPopupStatusIsReady {
            private Popup expectedPopup;

            @BeforeEach
            void setUp() {
                popupStatus = PopupStatus.READY.name();

                expectedPopup = createWillStartPopup();

                createOnGoingPopup();
                createOnGoingPopup();
                createOnGoingPopup();
                createCompletedPopup();
                createCompletedPopup();
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                assertResponse(callApi());
            }

            private void assertResponse(ag.act.model.GetPopupDataResponse result) {

                final List<PopupDetailsResponse> popupDetailsResponses = result.getData();

                assertThat(popupDetailsResponses.size(), is(1));
                assertPopupDetailResponse(popupDetailsResponses.get(0), expectedPopup);
            }
        }

        @Nested
        class AndPopupStatusIsProcessing {
            private Popup expectedPopup;

            @BeforeEach
            void setUp() {
                popupStatus = PopupStatus.PROCESSING.name();

                expectedPopup = createOnGoingPopup();

                createWillStartPopup();
                createWillStartPopup();
                createWillStartPopup();
                createCompletedPopup();
                createCompletedPopup();
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                assertResponse(callApi());
            }

            private void assertResponse(ag.act.model.GetPopupDataResponse result) {

                final List<PopupDetailsResponse> popupDetailsResponses = result.getData();

                assertThat(popupDetailsResponses.size(), is(1));
                assertPopupDetailResponse(popupDetailsResponses.get(0), expectedPopup);
            }
        }

        @Nested
        class AndPopupStatusIsComplete {
            private Popup expectedPopup;

            @BeforeEach
            void setUp() {
                popupStatus = PopupStatus.COMPLETE.name();

                expectedPopup = createCompletedPopup();

                createWillStartPopup();
                createWillStartPopup();
                createWillStartPopup();
                createOnGoingPopup();
                createOnGoingPopup();
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                assertResponse(callApi());
            }

            private void assertResponse(ag.act.model.GetPopupDataResponse result) {

                final List<PopupDetailsResponse> popupDetailsResponses = result.getData();

                assertThat(popupDetailsResponses.size(), is(1));
                assertPopupDetailResponse(popupDetailsResponses.get(0), expectedPopup);
            }
        }

        @Nested
        class AndSearchTypeIsTitle {

            @BeforeEach
            void setUp() {
                searchType = PopupSearchType.TITLE.name();
            }

            @Nested
            class AndSearchKeywordProvided {
                private Popup expectedPopup;

                @BeforeEach
                void setUp() {
                    searchKeyword = "TEST_POPUP_TITLE";
                    popupStatus = PopupStatus.READY.name();

                    expectedPopup = itUtil.createPopup(
                        searchKeyword,
                        LocalDateTime.now().plusMinutes(30),
                        LocalDateTime.now().plusMinutes(60)
                    );

                    itUtil.createPopup(
                        searchKeyword,
                        LocalDateTime.now().minusMinutes(30),
                        LocalDateTime.now().plusMinutes(30)
                    );
                    createWillStartPopup();
                    createWillStartPopup();
                    createOnGoingPopup();
                    createOnGoingPopup();
                    createCompletedPopup();
                    createCompletedPopup();
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnSuccess() throws Exception {
                    assertResponse(callApi());
                }

                private void assertResponse(ag.act.model.GetPopupDataResponse result) {

                    final List<PopupDetailsResponse> popupDetailsResponses = result.getData();

                    assertThat(popupDetailsResponses.size(), is(1));
                    assertPopupDetailResponse(popupDetailsResponses.get(0), expectedPopup);
                }
            }

            @Nested
            class AndSearchKeywordIsEmpty {
                private Popup expectedPopup;

                @BeforeEach
                void setUp() {
                    searchKeyword = "";
                    popupStatus = PopupStatus.READY.name();

                    expectedPopup = itUtil.createPopup(
                        someAlphanumericString(10),
                        LocalDateTime.now().plusMinutes(30),
                        LocalDateTime.now().plusMinutes(60)
                    );

                    createOnGoingPopup();
                    createOnGoingPopup();
                    createOnGoingPopup();
                    createCompletedPopup();
                    createCompletedPopup();
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnSuccess() throws Exception {
                    assertResponse(callApi());
                }

                private void assertResponse(ag.act.model.GetPopupDataResponse result) {

                    final List<PopupDetailsResponse> popupDetailsResponses = result.getData();

                    assertThat(popupDetailsResponses.size(), is(1));
                    assertPopupDetailResponse(popupDetailsResponses.get(0), expectedPopup);
                }
            }
        }
    }

    @Nested
    class WhenWithoutAnySearchCondition {

        @Nested
        class AndThereArePopups {

            private List<Popup> expectedPopups;

            @BeforeEach
            void setUp() {
                expectedPopups = List.of(
                    itUtil.createPopup(someAlphanumericString(10)),
                    createWillStartPopup(),
                    createOnGoingPopup(),
                    createCompletedPopup()
                );
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                assertResponse(callApi());
            }

            private void assertResponse(ag.act.model.GetPopupDataResponse result) {

                final List<PopupDetailsResponse> popupDetailsResponses = result.getData();

                assertThat(popupDetailsResponses.size(), is(4));
                assertPopupDetailResponse(popupDetailsResponses.get(0), expectedPopups.get(3));
                assertPopupDetailResponse(popupDetailsResponses.get(1), expectedPopups.get(2));
                assertPopupDetailResponse(popupDetailsResponses.get(2), expectedPopups.get(1));
                assertPopupDetailResponse(popupDetailsResponses.get(3), expectedPopups.get(0));
            }
        }
    }
}
