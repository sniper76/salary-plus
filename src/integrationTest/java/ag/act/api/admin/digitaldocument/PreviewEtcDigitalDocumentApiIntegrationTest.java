package ag.act.api.admin.digitaldocument;

import ag.act.AbstractCommonIntegrationTest;
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
import java.util.Locale;

import static ag.act.itutil.PdfTestHelper.assertPdfTextEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PreviewEtcDigitalDocumentApiIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/admin/digital-document/preview";
    private ag.act.model.PreviewDigitalDocumentRequest request;
    private String jwt;
    @Value("classpath:/digitaldocument/previewoutput/etc.pdf")
    private File expectedPdfFile;

    @BeforeEach
    void setUp() {
        itUtil.init();
        Locale.setDefault(Locale.US);
        User user = itUtil.createAdminUser();
        jwt = itUtil.createJwt(user.getId());
    }

    @Nested
    class WhenPreviewSuccess {
        @BeforeEach
        void setUp() {
            request = genRequest();
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final MvcResult response = callApi(status().isOk());

            byte[] actual = response.getResponse().getContentAsByteArray();

            //PdfTestHelper.generatePdfFile(actual, "etc.pdf");
            //전자문서의 내용이 변경되면 테스트 코드에서 비교하는 pdf 파일을 위 코드 주석을 이용해서 만들어 주고 테스트해야 한다.
            assertPdfTextEquals(actual, expectedPdfFile);
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
        class WhenTitleNotIncluded {
            @BeforeEach
            void setUp() {
                request = genRequest();
                request.setTitle(null);
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequest() throws Exception {
                MvcResult response = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(response, 400, "문서 제목을 확인하세요.");
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

                itUtil.assertErrorResponse(response, 400, "문서 내용을 확인하세요.");
            }
        }
    }

    private ag.act.model.PreviewDigitalDocumentRequest genRequest() {
        return new ag.act.model.PreviewDigitalDocumentRequest()
            .type(DigitalDocumentType.ETC_DOCUMENT.name())
            .companyName("test company")
            .title("Title")
            .content("<p>Contents With Strict <br /> Valid HTML tags</p><p>second line</p>");
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
