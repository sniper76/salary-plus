package ag.act.api.admin.upload;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.User;
import ag.act.enums.FileContentType;
import ag.act.enums.FileType;
import ag.act.model.FileContentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;

import static ag.act.TestUtil.createMockMultipartFile;
import static ag.act.TestUtil.someFilename;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;

class AdminFileUploadApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/images/{fileContentType}";

    private MockMultipartFile file;
    private String jwt;
    private String originalFilename;
    private String fileContentType;
    private String description;

    @BeforeEach
    void setUp() {
        itUtil.init();
        final User user = itUtil.createAdminUser();
        jwt = itUtil.createJwt(user.getId());
        description = someAlphanumericString(20);
    }

    @Nested
    class WhenUploadDefaultProfileImage {

        @BeforeEach
        void setUp() {

            fileContentType = FileContentType.DEFAULT_PROFILE.name();
            originalFilename = someFilename();

            file = genMockFile(originalFilename, MediaType.IMAGE_PNG_VALUE);
        }

        @Nested
        class AndImageUploadSuccess {

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {

                MvcResult response = mockMvc.perform(
                        multipart(TARGET_API, fileContentType)
                            .file(file)
                            .param("description", description)
                            .header(AUTHORIZATION, "Bearer " + jwt))
                    .andExpect(status().isOk())
                    .andReturn();

                final ag.act.model.ImageUploadResponse result = objectMapperUtil.toResponse(
                    response.getResponse().getContentAsString(),
                    ag.act.model.ImageUploadResponse.class
                );

                assertResponse(result);
            }

            private void assertResponse(ag.act.model.ImageUploadResponse result) {
                final FileContentResponse fileContentResponse = result.getData();
                assertThat(fileContentResponse.getId(), notNullValue());
                assertThat(fileContentResponse.getUrl(), startsWith(s3Environment.getBaseUrl()));
                assertThat(fileContentResponse.getFileContentType(), is(fileContentType));
                assertThat(fileContentResponse.getFileType(), is(FileType.IMAGE.name()));
                assertThat(fileContentResponse.getOriginalFilename(), is(originalFilename));
                assertThat(fileContentResponse.getDescription(), is(description));
            }
        }
    }

    @Nested
    class WhenMediaTypeInvalid {
        @BeforeEach
        void setUp() {
            fileContentType = someEnum(FileContentType.class).name();
            file = genMockFile(originalFilename, someAlphanumericString(10));
        }

        @DisplayName("Should return 400 response code when call " + TARGET_API)
        @Test
        void shouldReturnBadRequest() throws Exception {

            MvcResult response = mockMvc.perform(
                    multipart(TARGET_API, fileContentType)
                        .file(file)
                        .param("description", description)
                        .header(AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().isBadRequest())
                .andReturn();

            itUtil.assertErrorResponse(response, 400, "지원하지 않는 이미지 형식입니다.");
        }
    }

    @Nested
    class WhenContentTypeInvalid {

        private String wrongFileContentType;

        @BeforeEach
        void setUp() {
            wrongFileContentType = someAlphanumericString(10);
            file = genMockFile(originalFilename, MediaType.IMAGE_JPEG_VALUE);
        }


        @DisplayName("Should return 400 response code when call " + TARGET_API)
        @Test
        void shouldReturnBadRequest() throws Exception {

            MvcResult response = mockMvc.perform(
                    multipart(TARGET_API, wrongFileContentType)
                        .file(file)
                        .param("description", description)
                        .header(AUTHORIZATION, "Bearer " + jwt))
                .andExpect(status().isBadRequest())
                .andReturn();

            itUtil.assertErrorResponse(response, 400, "지원하지 않는 FileContentType '%s' 타입입니다.".formatted(wrongFileContentType));
        }
    }

    private MockMultipartFile genMockFile(String originalFilename, String mediaType) {
        return createMockMultipartFile("file", originalFilename, mediaType);
    }
}
