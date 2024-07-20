package ag.act.api.admin.digitaldocument;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.CorporateUser;
import ag.act.entity.User;
import ag.act.enums.DigitalAnswerType;
import ag.act.enums.DigitalDocumentType;
import ag.act.enums.DigitalDocumentVersion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.io.File;
import java.time.Instant;
import java.util.List;

import static ag.act.itutil.PdfTestHelper.assertPdfTextEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PreviewProxyDigitalDocumentApiIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/admin/digital-document/preview";
    private ag.act.model.PreviewDigitalDocumentRequest request;
    private String jwt;
    @Value("classpath:/digitaldocument/previewoutput/digital-proxy.pdf")
    private File expectedPdfFile;
    @Value("classpath:/digitaldocument/previewoutput/digital-proxy-v2.pdf")
    private File expectedPdfV2File;
    @Value("classpath:/digitaldocument/previewoutput/digital-proxy-corporate-user.pdf")
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
            @Nested
            class WhenVersion2 {
                @BeforeEach
                void setUp() {
                    final User acceptUser = itUtil.createUser();
                    request = genRequest();
                    request.setVersion(DigitalDocumentVersion.V2.name());
                    request.setAcceptUserId(acceptUser.getId());
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnSuccess() throws Exception {
                    final MvcResult response = callApi(status().isOk());

                    byte[] actual = response.getResponse().getContentAsByteArray();

                    //PdfTestHelper.generatePdfFile(actual, "digital-proxy-v2.pdf");
                    //전자문서의 내용이 변경되면 테스트 코드에서 비교하는 pdf 파일을 위 코드 주석을 이용해서 만들어 주고 테스트해야 한다.
                    assertPdfTextEquals(actual, expectedPdfV2File);
                }
            }

            @Nested
            class WhenVersion1 {
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

                    //PdfTestHelper.generatePdfFile(actual, "digital-proxy.pdf");
                    //전자문서의 내용이 변경되면 테스트 코드에서 비교하는 pdf 파일을 위 코드 주석을 이용해서 만들어 주고 테스트해야 한다.
                    assertPdfTextEquals(actual, expectedPdfFile);
                }
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

                //PdfTestHelper.generatePdfFile(actual, "digital-proxy-corporate-user.pdf");
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
        class WhenShareholderMeetingTypeNotIncluded {
            @BeforeEach
            void setUp() {
                request = genRequest();
                request.setShareholderMeetingType(null);
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequest() throws Exception {
                MvcResult response = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(response, 400, "주총구분을 확인하세요.");
            }
        }

        @Nested
        class WhenShareholderMeetingNameNotIncluded {
            @BeforeEach
            void setUp() {
                request = genRequest();
                request.setShareholderMeetingName(null);
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequest() throws Exception {
                MvcResult response = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(response, 400, "주총명을 확인하세요.");
            }
        }

        @Nested
        class WhenShareholderMeetingDateNotIncluded {
            @BeforeEach
            void setUp() {
                request = genRequest();
                request.setShareholderMeetingDate(null);
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequest() throws Exception {
                MvcResult response = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(response, 400, "주총일자를 확인하세요.");
            }
        }


        @Nested
        class WhenDesignatedAgentNamesNotIncluded {
            @BeforeEach
            void setUp() {
                request = genRequest();
                request.setDesignatedAgentNames(null);
            }

            @DisplayName("Should return 400 response code when call " + TARGET_API)
            @Test
            void shouldReturnBadRequest() throws Exception {
                MvcResult response = callApi(status().isBadRequest());

                itUtil.assertErrorResponse(response, 400, "수임인지정대리인을 확인하세요.");
            }
        }
    }

    private ag.act.model.PreviewDigitalDocumentRequest genRequest() {
        return new ag.act.model.PreviewDigitalDocumentRequest()
            .type(DigitalDocumentType.DIGITAL_PROXY.name())
            .companyName("test company")
            .shareholderMeetingDate(
                Instant.parse("2023-11-01T14:59:59.00Z")
            )
            .shareholderMeetingType("(주주총회 종류)")
            .shareholderMeetingName("(주주총회 이름)")
            .designatedAgentNames("(수임인 지정 대리인 목록)")
            .childItems(getItemList());
    }

    private List<ag.act.model.DigitalDocumentItemRequest> getItemList() {
        return List.of(new ag.act.model.DigitalDocumentItemRequest()
            .title("제1안")
            .content("제1안 내용")
            .defaultSelectValue(null)
            .leaderDescription(null)
            .childItems(
                List.of(
                    new ag.act.model.DigitalDocumentItemRequest()
                        .title("제1-1안")
                        .content("제1-1안 내용")
                        .defaultSelectValue(DigitalAnswerType.APPROVAL.name())
                        .leaderDescription("설명 제1-1안 내용"),
                    new ag.act.model.DigitalDocumentItemRequest()
                        .title("제1-2안")
                        .content("제1-2안 내용")
                        .defaultSelectValue(DigitalAnswerType.APPROVAL.name())
                        .leaderDescription("설명 제1-2안 내용")
                )
            ));
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
