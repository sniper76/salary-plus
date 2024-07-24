package ag.act.api.admin.user;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.User;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomLongs.someLongBetween;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class UserSolidarityLeaderConfidentialAgreementDocumentDownloadIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/admin/users/{userId}/download-document/solidarity-leader-confidential-agreement";

    private String jwt;
    private User user;
    private String userName;
    private Long userId;
    private String pdfFileContent;

    @BeforeEach
    void setUp() {
        itUtil.init();

        jwt = itUtil.createJwt(itUtil.someAdminUser().getId());

        userName = someAlphanumericString(10);
        user = createUserWithSolidarityLeaderConfidentialAgreementSigned(userName);
        userId = user.getId();

        pdfFileContent = someString(50);
        final InputStream inputStream = new ByteArrayInputStream(pdfFileContent.getBytes(StandardCharsets.UTF_8));

        final String pdfFilePath = "contents/solidarity-leader-confidential-agreement-sign/%s/비밀유지서약서(%s).pdf".formatted(userId, userName);
        given(itUtil.getS3ServiceMockBean().findObjectFromPrivateBucket(pdfFilePath))
            .willReturn(Optional.of(inputStream));
    }

    private User createUserWithSolidarityLeaderConfidentialAgreementSigned(String userName) {
        User user = itUtil.createUser();
        user.setName(userName);
        user.setIsSolidarityLeaderConfidentialAgreementSigned(true);

        return itUtil.updateUser(user);
    }

    @Nested
    class WhenUserIsSolidarityLeaderConfidentialAgreementSigned {

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldDownloadPdfFile() throws Exception {
            final MvcResult response = callApi(userId, status().isOk());

            final String actual = response.getResponse().getContentAsString();

            assertThat(actual, is(pdfFileContent));
        }
    }

    @Nested
    class WhenUserNotFound {

        @DisplayName("Should return 404 response code when call " + TARGET_API)
        @Test
        void shouldReturnNotFound() throws Exception {
            final Long notFoundUserId = someLongBetween(1000L, 999999L);

            final MvcResult response = callApi(notFoundUserId, status().isNotFound());

            itUtil.assertErrorResponse(response, 404, "회원을 찾을 수 없습니다.");
        }
    }

    @Nested
    class WhenUserIsNotSolidarityLeaderConfidentialAgreementSigned {

        @BeforeEach
        void setUp() {
            user.setIsSolidarityLeaderConfidentialAgreementSigned(false);
            itUtil.updateUser(user);
        }

        @DisplayName("Should return 400 response code when call " + TARGET_API)
        @Test
        void shouldReturnBadRequest() throws Exception {
            final MvcResult response = callApi(userId, status().isBadRequest());

            itUtil.assertErrorResponse(response, 400, "회원이 비밀유지서약서를 서명하지 않았습니다.");
        }
    }

    @Nested
    class WhenNotFoundFileInS3 {

        @BeforeEach
        void setUp() {
            given(itUtil.getS3ServiceMockBean().findObjectFromPrivateBucket(anyString()))
                .willReturn(Optional.empty());
        }

        @DisplayName("Should return 404 response code when call " + TARGET_API)
        @Test
        void shouldReturnNotFound() throws Exception {
            final MvcResult response = callApi(userId, status().isNotFound());

            itUtil.assertErrorResponse(
                response,
                404,
                "%s(%s) 회원의 비밀유지서약서를 찾을 수 없습니다.".formatted(userName, userId)
            );
        }
    }

    @NotNull
    private MvcResult callApi(Long userId, ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                get(TARGET_API, userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_PDF, MediaType.ALL)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(resultMatcher)
            .andReturn();
    }
}
