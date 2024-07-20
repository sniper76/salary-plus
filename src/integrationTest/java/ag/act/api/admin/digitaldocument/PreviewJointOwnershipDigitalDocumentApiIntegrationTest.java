package ag.act.api.admin.digitaldocument;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.CorporateUser;
import ag.act.entity.User;
import ag.act.enums.DigitalDocumentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.io.File;

import static ag.act.itutil.PdfTestHelper.assertPdfTextEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PreviewJointOwnershipDigitalDocumentApiIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/admin/digital-document/preview";
    private ag.act.model.PreviewDigitalDocumentRequest request;
    private String jwt;
    @Value("classpath:/digitaldocument/previewoutput/joint-ownership.pdf")
    private File expectedPdfFile;
    @Value("classpath:/digitaldocument/previewoutput/joint-ownership-corporate-user.pdf")
    private File expectedPdfFileForCorporateUser;

    @BeforeEach
    void setUp() {
        itUtil.init();
        User user = itUtil.createAdminUser();
        jwt = itUtil.createJwt(user.getId());
    }

    @Nested
    class WhenPreviewSuccess {

        @Nested
        class WhenAcceptorNormalUser {
            @BeforeEach
            void setUp() {
                final User acceptUser = itUtil.createUser();
                request = genRequest();
                request.setAcceptUserId(acceptUser.getId());
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                final MvcResult response = callApi(status().isOk());

                byte[] actual = response.getResponse().getContentAsByteArray();

                //PdfTestHelper.generatePdfFile(actual, "joint-ownership.pdf");
                //전자문서의 내용이 변경되면 테스트 코드에서 비교하는 pdf 파일을 위 코드 주석을 이용해서 만들어 주고 테스트해야 한다.
                assertPdfTextEquals(actual, expectedPdfFile);
            }
        }

        @Nested
        class WhenAcceptorCorporateUser {
            @BeforeEach
            void setUp() {
                final String corporateNo = "123456-1234567";
                final String corporateName = "테스트법인";
                final User acceptUser = itUtil.createUser();
                final CorporateUser corporateUser = itUtil.createCorporateUser(corporateNo, corporateName);
                corporateUser.setUserId(acceptUser.getId());
                itUtil.updateCorporateUser(corporateUser);

                request = genRequest();
                request.setAcceptUserId(acceptUser.getId());
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                final MvcResult response = callApi(status().isOk());

                byte[] actual = response.getResponse().getContentAsByteArray();

                //PdfTestHelper.generatePdfFile(actual, "joint-ownership-corporate-user.pdf");
                //전자문서의 내용이 변경되면 테스트 코드에서 비교하는 pdf 파일을 위 코드 주석을 이용해서 만들어 주고 테스트해야 한다.
                assertPdfTextEquals(actual, expectedPdfFileForCorporateUser);
            }
        }
    }

    @Nested
    class FailToPreview {
        @Nested
        class WhenTypeNotIncluded {
            @BeforeEach
            void setUp() {
                request = genRequest();
                request.setType(null);
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequest() throws Exception {
                MvcResult response = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(response, 400, "문서 구분을 확인해주세요.");
            }
        }

        @Nested
        class WhenCompanyNameNotIncluded {
            @BeforeEach
            void setUp() {
                request = genRequest();
                request.setCompanyName(null);
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequest() throws Exception {
                MvcResult response = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(response, 400, "회사명을 확인해주세요.");
            }
        }

        @Nested
        class WhenContentNotIncluded {
            @BeforeEach
            void setUp() {
                request = genRequest();
                request.setContent(null);
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequest() throws Exception {
                MvcResult response = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(response, 400, "공동보유확인서 내용을 확인하세요.");
            }
        }

        @Nested
        class WhenCompanyRegistrationNumberNotIncluded {
            @BeforeEach
            void setUp() {
                request = genRequest();
                request.setCompanyRegistrationNumber(null);
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequest() throws Exception {
                MvcResult response = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(response, 400, "법인등록번호를 확인하세요.");
            }
        }
    }

    private ag.act.model.PreviewDigitalDocumentRequest genRequest() {
        return new ag.act.model.PreviewDigitalDocumentRequest()
            .type(DigitalDocumentType.JOINT_OWNERSHIP_DOCUMENT.name())
            .companyName("test company")
            .content("공동보유확인서 내용이 들어갑니다.")
            .companyRegistrationNumber("123-45-67890");
    }

    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                post(TARGET_API)
                    .content(objectMapperUtil.toJson(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(resultMatcher)
            .andReturn();
    }
}
