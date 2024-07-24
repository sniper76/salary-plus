package ag.act.api.admin.digitaldocument;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentDownload;
import ag.act.enums.DigitalDocumentType;
import ag.act.enums.FileType;
import ag.act.enums.digitaldocument.ZipFileStatus;
import ag.act.model.DigitalDocumentZipFileCallbackRequest;
import ag.act.util.UUIDUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.UUID;

import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class CallbackDigitalDocumentZipFileResultApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/callback/digital-document-download/{fileType}";

    private List<MockedStatic<?>> statics;
    @Mock
    private UUID uuid;
    private User user;
    private Long digitalDocumentId;
    private DigitalDocumentZipFileCallbackRequest request;
    private String uuidString;

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(UUIDUtil.class));
        uuidString = someString(20);
        given(UUIDUtil.randomUUID()).willReturn(uuid);
        given(uuid.toString()).willReturn(uuidString);

        itUtil.init();
        user = itUtil.createUser(); // NOT ADMIN
        final DigitalDocument digitalDocument = mockDigitalProxyDocument();
        digitalDocumentId = digitalDocument.getId();

        DigitalDocumentDownload digitalDocumentDownload = itUtil.createDigitalDocumentDownload(digitalDocumentId, user.getId(), true);
        request = new ag.act.model.DigitalDocumentZipFileCallbackRequest()
            .digitalDocumentDownloadId(digitalDocumentDownload.getId())
            .zipFilePath(someAlphanumericString(20));
    }

    private DigitalDocument mockDigitalProxyDocument() {
        final Stock stock = itUtil.createStock(someStockCode());
        final Board board = itUtil.createBoard(stock);
        final Post post = itUtil.createPost(board, user.getId());
        final User acceptUser = itUtil.createUser();

        DigitalDocument digitalDocument = itUtil.createDigitalDocument(post, stock, acceptUser);
        digitalDocument.setType(DigitalDocumentType.DIGITAL_PROXY);
        return itUtil.updateDigitalDocument(digitalDocument);
    }

    @Nested
    class WhenZipFileCallbackIsSuccess {

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    post(TARGET_API, FileType.DIGITAL_DOCUMENT.name())
                        .content(objectMapperUtil.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .headers(batchXApiKey())
                )
                .andExpect(status().isOk())
                .andReturn();

            final ag.act.model.SimpleStringResponse result = objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                ag.act.model.SimpleStringResponse.class
            );

            assertThat(result.getStatus(), is("ok"));
            assertDigitalDocumentDownloadInDatabase();
        }

        private void assertDigitalDocumentDownloadInDatabase() {

            final List<DigitalDocumentDownload> digitalDocumentDownloadList = itUtil.findAllDigitalDocumentDownload(digitalDocumentId);

            assertThat(digitalDocumentDownloadList.size(), is(1));

            assertThat(digitalDocumentDownloadList.get(0).getDigitalDocumentId(), is(digitalDocumentId));
            assertThat(digitalDocumentDownloadList.get(0).getRequestUserId(), is(user.getId()));
            assertThat(digitalDocumentDownloadList.get(0).getIsLatest(), is(true));
            assertThat(digitalDocumentDownloadList.get(0).getZipFileStatus(), is(ZipFileStatus.COMPLETE));
            assertThat(digitalDocumentDownloadList.get(0).getZipFilePath(), is(request.getZipFilePath()));
            assertThat(digitalDocumentDownloadList.get(0).getZipFileKey(), is(uuidString));
            assertThat(digitalDocumentDownloadList.get(0).getDownloadCount(), is(0));
        }
    }
}
