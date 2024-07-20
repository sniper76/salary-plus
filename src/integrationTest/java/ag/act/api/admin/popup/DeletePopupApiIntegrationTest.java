package ag.act.api.admin.popup;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Popup;
import ag.act.entity.User;
import ag.act.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

public class DeletePopupApiIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/admin/popups/{popupId}";

    private String jwt;
    private Popup popup;
    private Long popupId;

    @BeforeEach
    void setUp() {
        itUtil.init();
        final User user = itUtil.createAdminUser();
        jwt = itUtil.createJwt(user.getId());
    }

    @Nested
    class WhenDeletePush {
        @Nested
        class WhenReserved {
            @BeforeEach
            void setUp() {
                popup = itUtil.createPopup(someAlphanumericString(10));
                popupId = popup.getId();
                popup.setTargetStartDatetime(LocalDateTime.now().plusYears(1));
                itUtil.updatePopup(popup);
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                callApi(popup.getId());
                assertDeletedPopupInDatabase(popupId);
            }
        }

        @Nested
        class WhenAlreadyShowing {
            @BeforeEach
            void setUp() {
                popup = itUtil.createPopup(someAlphanumericString(10));
                popupId = popup.getId();
                popup.setTargetStartDatetime(LocalDateTime.now().minusYears(1));
                itUtil.updatePopup(popup);
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                callApi(popup.getId());

                Popup foundPopup = itUtil.findPopup(popupId).orElseThrow(
                    () -> new RuntimeException("Popup not found.")
                );

                assertThat(foundPopup.getStatus(), is(Status.DELETED_BY_ADMIN));
            }
        }

        private void callApi(Long popupId) throws Exception {
            MvcResult response = mockMvc
                .perform(
                    delete(TARGET_API, popupId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isOk())
                .andReturn();

            final ag.act.model.SimpleStringResponse result = objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                ag.act.model.SimpleStringResponse.class
            );

            assertThat(result.getStatus(), is("ok"));
        }

        private void assertDeletedPopupInDatabase(Long popupId) {
            itUtil.findPopup(popupId).ifPresent(popup -> {
                throw new RuntimeException("Popup should be deleted.");
            });
        }
    }

    @Nested
    class WhenFailToDeletePush {
        @Nested
        class AndNotFoundPush {

            @Test
            void shouldReturnBadRequestException() throws Exception {
                popup = itUtil.createPopup(someAlphanumericString(10));
                popupId = popup.getId();

                callApi(someLong(), 404, "해당 팝업이 존재하지 않습니다.");
                assertPopupExistingInDatabase(popupId);
            }
        }

        private void callApi(Long pushId, int httpStatus, String message) throws Exception {
            MvcResult response = mockMvc
                .perform(
                    delete(TARGET_API, pushId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().is(httpStatus))
                .andReturn();

            itUtil.assertErrorResponse(response, httpStatus, message);
        }

        private void assertPopupExistingInDatabase(Long popupId) {
            if (itUtil.findPopup(popupId).isEmpty()) {
                throw new RuntimeException("Popup should be existing in database.");
            }
        }
    }
}
