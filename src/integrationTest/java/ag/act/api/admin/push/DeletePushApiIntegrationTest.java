package ag.act.api.admin.push;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Push;
import ag.act.entity.User;
import ag.act.enums.push.PushSendStatus;
import ag.act.enums.push.PushSendType;
import ag.act.enums.push.PushTargetType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomThings.someThing;

class DeletePushApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/pushes/{pushId}";

    private String jwt;

    @BeforeEach
    void setUp() {
        itUtil.init();
        final User user = itUtil.createAdminUser();
        jwt = itUtil.createJwt(user.getId());
    }

    @Nested
    class WhenDeletePush {

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @RepeatedTest(5)
        void shouldReturnSuccess() throws Exception {
            callApi(stubValidPush().getId());
        }

        private void callApi(Long pushId) throws Exception {
            MvcResult response = mockMvc
                .perform(
                    delete(TARGET_API, pushId)
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
            assertDeletedPushInDatabase(pushId);
        }

        private void assertDeletedPushInDatabase(Long pushId) {
            itUtil.findPush(pushId).map(push -> {
                throw new RuntimeException("Push should be deleted");
            });
        }

        private Push stubValidPush() {
            final Push push = itUtil.createPush(someAlphanumericString(10), PushTargetType.ALL);
            push.setSendType(PushSendType.SCHEDULE);
            push.setSendStatus(PushSendStatus.READY);
            return itUtil.updatePush(push);
        }
    }

    @Nested
    class WhenFailToDeletePush {

        @Nested
        class AndPushIsImmediately {

            @Test
            void shouldReturnBadRequestException() throws Exception {
                final Push push = itUtil.createPush(someAlphanumericString(10), PushTargetType.ALL);
                push.setSendType(PushSendType.IMMEDIATELY);
                itUtil.updatePush(push);

                callApi(push.getId(), 400, "발송 예약된 푸시만 삭제 가능합니다.");
                assertPushExistingInDatabase(push.getId());
            }
        }

        @Nested
        class AndPushIsNotReadyStatus {

            @Test
            void shouldReturnBadRequestException() throws Exception {
                final Push push = itUtil.createPush(someAlphanumericString(10), PushTargetType.ALL);
                push.setSendType(PushSendType.SCHEDULE);
                push.setSendStatus(someThing(PushSendStatus.notReady().toArray(new PushSendStatus[0])));
                itUtil.updatePush(push);

                callApi(push.getId(), 400, "발송대기중인 푸시만 삭제 가능합니다.");
                assertPushExistingInDatabase(push.getId());
            }
        }

        @Nested
        class AndNotFoundPush {

            @Test
            void shouldReturnBadRequestException() throws Exception {
                final Push push = itUtil.createPush(someAlphanumericString(10), PushTargetType.ALL);
                push.setSendType(PushSendType.SCHEDULE);
                push.setSendStatus(someThing(PushSendStatus.notReady().toArray(new PushSendStatus[0])));
                itUtil.updatePush(push);

                callApi(someLong(), 404, "해당 푸시를 찾을 수 없습니다.");
                assertPushExistingInDatabase(push.getId());
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

        private void assertPushExistingInDatabase(Long pushId) {
            if (itUtil.findPush(pushId).isEmpty()) {
                throw new RuntimeException("Push should be existing in database");
            }
        }

    }
}
