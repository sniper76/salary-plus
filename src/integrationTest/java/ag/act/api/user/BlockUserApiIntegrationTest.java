package ag.act.api.user;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.User;
import ag.act.enums.AppPreferenceType;
import ag.act.model.SimpleStringResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BlockUserApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/users/me/blocked-users";

    private User user;
    private User targetUser;
    private String jwt;

    @BeforeEach
    void setUp() {
        itUtil.init();
        user = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());
        given(appPreferenceCache.getValue(AppPreferenceType.BLOCK_EXCEPT_USER_IDS))
            .willReturn(Collections.EMPTY_LIST);
    }

    @Nested
    class WhenNormal {

        @BeforeEach
        void setUp() {
            targetUser = itUtil.createUser();
        }

        @Test
        void shouldReturnSuccess() throws Exception {
            SimpleStringResponse simpleStringResponse = callApiAndGetResult(status().isOk());

            assertResponse(simpleStringResponse);
        }

        private void assertResponse(ag.act.model.SimpleStringResponse response) {
            assertThat(response.getStatus(), is("ok"));
        }
    }

    @Nested
    class WhenBlockUserSelf {

        @BeforeEach
        void setUp() {
            targetUser = user;
        }

        @Test
        void shouldReturn400Response() throws Exception {
            callApiAndGetResult(status().isBadRequest());
        }
    }

    @Nested
    class WhenDuplicatedBlockUser {

        @BeforeEach
        void setUp() {
            targetUser = itUtil.createUser();
            itUtil.createBlockedUser(user.getId(), targetUser.getId());
        }

        @Test
        void shouldReturn400Response() throws Exception {
            callApiAndGetResult(status().isBadRequest());
        }
    }

    @Nested
    class WhenBlockExceptUser {

        @BeforeEach
        void setUp() {
            targetUser = itUtil.createUser();
            given(appPreferenceCache.getValue(AppPreferenceType.BLOCK_EXCEPT_USER_IDS))
                .willReturn(List.of(targetUser.getId()));
        }

        @Test
        void shouldReturn400Response() throws Exception {
            callApiAndGetResult(status().isBadRequest());
        }
    }

    private ag.act.model.SimpleStringResponse callApiAndGetResult(ResultMatcher resultMatcher) throws Exception {
        MvcResult response = mockMvc
            .perform(
                post(TARGET_API)
                    .content(objectMapperUtil.toRequestBody(generateRequest(targetUser.getId())))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(resultMatcher)
            .andReturn();

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            ag.act.model.SimpleStringResponse.class
        );
    }

    private ag.act.model.CreateBlockUserRequest generateRequest(Long targetUserId) {
        return new ag.act.model.CreateBlockUserRequest().targetUserId(targetUserId);
    }
}
