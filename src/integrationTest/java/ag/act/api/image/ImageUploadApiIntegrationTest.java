package ag.act.api.image;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.User;
import ag.act.enums.FileContentType;
import ag.act.enums.FileType;
import ag.act.model.FileContentResponse;
import ag.act.model.ImageUploadResponse;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import static ag.act.TestUtil.createMockMultipartFile;
import static ag.act.TestUtil.someFilename;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

class ImageUploadApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/images";

    private MockMultipartFile file;
    private String jwt;
    private String originalFilename;

    @BeforeEach
    void setUp() {
        itUtil.init();
        final User user = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());
        originalFilename = someFilename();

        file = createMockMultipartFile(
            "file",
            originalFilename,
            MediaType.IMAGE_PNG_VALUE
        );
    }

    @Nested
    class WhenImageUploadSuccess {

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {

            final ResultMatcher resultMatcher = status().isOk();
            final MvcResult response = callApi(resultMatcher);
            final ImageUploadResponse result = itUtil.getResult(response, ImageUploadResponse.class);

            assertResponse(result);
        }

        private void assertResponse(ImageUploadResponse result) {
            final FileContentResponse fileContentResponse = result.getData();
            assertThat(fileContentResponse.getId(), notNullValue());
            assertThat(fileContentResponse.getUrl(), startsWith(s3Environment.getBaseUrl()));
            assertThat(fileContentResponse.getFileContentType(), is(FileContentType.DEFAULT.name()));
            assertThat(fileContentResponse.getFileType(), is(FileType.IMAGE.name()));
            assertThat(fileContentResponse.getOriginalFilename(), is(originalFilename));
        }
    }

    @Nested
    class WhenMediaTypeInvalid {

        @BeforeEach
        void setUp() {

            file = createMockMultipartFile(
                "file",
                someAlphanumericString(10)
            );
        }

        @DisplayName("Should return 400 response code when call " + TARGET_API)
        @Test
        void shouldReturnBadRequest() throws Exception {

            final MvcResult response = callApi(status().isBadRequest());

            itUtil.assertErrorResponse(response, 400, "지원하지 않는 이미지 형식입니다.");
        }
    }

    @NotNull
    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc.perform(
                multipart(TARGET_API)
                    .file(file)
                    .headers(headers(jwt(jwt))))
            .andExpect(resultMatcher)
            .andReturn();
    }
}
