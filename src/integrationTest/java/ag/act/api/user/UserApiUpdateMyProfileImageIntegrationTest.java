package ag.act.api.user;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.FileContent;
import ag.act.entity.User;
import ag.act.model.SimpleImageDataResponse;
import ag.act.model.SimpleImageResponse;
import ag.act.util.ImageUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomLongs.somePositiveLong;

class UserApiUpdateMyProfileImageIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/users/my-profile-image";

    private String jwt;
    private User user;
    private ag.act.model.UpdateMyProfileImageRequest request;
    private FileContent newProfileImage;

    @BeforeEach
    void setUp() {
        itUtil.init();
        user = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());
        newProfileImage = itUtil.createImage();
        request = genRequest();
    }

    private ag.act.model.UpdateMyProfileImageRequest genRequest() {
        ag.act.model.UpdateMyProfileImageRequest request = new ag.act.model.UpdateMyProfileImageRequest();
        request.setImageId(newProfileImage.getId());
        return request;
    }

    @Nested
    class WhenSuccess {

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    patch(TARGET_API)
                        .content(objectMapperUtil.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .headers(headers(jwt(jwt)))
                )
                .andExpect(status().isOk())
                .andReturn();

            final SimpleImageDataResponse result = objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                SimpleImageDataResponse.class
            );

            assertResponse(result);
            assertResponseFromDatabase();
        }
    }

    @Nested
    class WhenProfileImageIdIsMissing {

        @BeforeEach
        void setUp() {
            request = genRequest();
            request.setImageId(null);
        }

        @DisplayName("Should return 400 response code when call " + TARGET_API)
        @Test
        void shouldReturnBadRequest() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    patch(TARGET_API)
                        .content(objectMapperUtil.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .headers(headers(jwt(jwt)))
                )
                .andExpect(status().isBadRequest())
                .andReturn();

            itUtil.assertErrorResponse(response, 400, "프로파일 이미지 정보를 확인해주세요.");
        }
    }

    @Nested
    class WhenProfileImageNotFound {

        @BeforeEach
        void setUp() {
            request = genRequest();
            request.setImageId(somePositiveLong());
        }

        @DisplayName("Should return 404 response code when call " + TARGET_API)
        @Test
        void shouldReturnNotFoundException() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    patch(TARGET_API)
                        .content(objectMapperUtil.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .headers(headers(jwt(jwt)))
                )
                .andExpect(status().isNotFound())
                .andReturn();

            itUtil.assertErrorResponse(response, 404, "이미지를 찾을 수 없습니다.");
        }
    }

    private void assertResponseFromDatabase() {
        final User userFromDatabase = itUtil.findUser(user.getId());
        assertThat(
            userFromDatabase.getProfileImageUrl(),
            is(ImageUtil.getFullPath(s3Environment.getBaseUrl(), newProfileImage.getFilename()))
        );
    }

    private void assertResponse(SimpleImageDataResponse result) {
        final SimpleImageResponse simpleImageResponse = result.getData();
        assertThat(simpleImageResponse.getImageId(), is(newProfileImage.getId()));
        assertThat(
            simpleImageResponse.getImageUrl(),
            is(ImageUtil.getFullPath(s3Environment.getBaseUrl(), newProfileImage.getFilename()))
        );
    }
}
