package ag.act.api.stockboardgrouppost.digitaldocument;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Solidarity;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.DigitalDocumentAnswerStatus;
import ag.act.enums.DigitalDocumentType;
import ag.act.util.KoreanDateTimeUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static ag.act.TestUtil.createMockMultipartFile;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("VariableDeclarationUsageDistance")
class CheckPostWriterDigitalDocumentApiIntegrationTest extends AbstractCommonIntegrationTest {
    private static final String TARGET_API = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts";
    private static final String TARGET_API_SAVE = "/api/users/digital-document/{digitalDocumentId}";

    private String jwt;
    private Stock stock;
    private Board board;
    private User user;
    private User acceptUser;
    private User postWriteUser;
    private MockMultipartFile signImageFile;

    @BeforeEach
    void setUp() {
        itUtil.init();
        postWriteUser = itUtil.createUser();
        user = itUtil.createUser();
        acceptUser = itUtil.createUser();
        jwt = itUtil.createJwt(user.getId());
        stock = itUtil.createStock();

        final Solidarity solidarity = itUtil.createSolidarity(stock.getCode());
        itUtil.createSolidarityLeader(solidarity, acceptUser.getId());
        itUtil.createUserHoldingStock(stock.getCode(), user);
    }

    private void defaultSetUp() {
        LocalDate referenceDate = KoreanDateTimeUtil.getTodayLocalDate();

        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime targetStartDate = now.minusDays(3);
        final LocalDateTime targetEndDate = now.plusDays(1);

        board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.DIGITAL_DELEGATION);
        Post post = itUtil.createPost(board, postWriteUser.getId());
        Post post2 = itUtil.createPost(board, postWriteUser.getId());

        DigitalDocument digitalDocument = itUtil.createDigitalDocument(
            post, stock, acceptUser, DigitalDocumentType.ETC_DOCUMENT, targetStartDate, targetEndDate, referenceDate
        );
        DigitalDocument digitalDocument2 = itUtil.createDigitalDocument(
            post2, stock, acceptUser, DigitalDocumentType.ETC_DOCUMENT, targetStartDate, targetEndDate, referenceDate
        );
        post.setDigitalDocument(digitalDocument);
        post2.setDigitalDocument(digitalDocument2);
        itUtil.updatePost(post);
        itUtil.updatePost(post2);

        itUtil.createMyDataSummary(user, stock.getCode(), referenceDate);
        itUtil.createMyDataSummary(postWriteUser, stock.getCode(), referenceDate);
    }

    private void makeDocumentUserData() {
        signImageFile = createMockMultipartFile("signImage");
    }

    private ag.act.model.GetBoardGroupPostResponse callApiAndGetResult() throws Exception {
        MvcResult response = mockMvc
            .perform(
                get(TARGET_API, stock.getCode(), board.getGroup())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(status().isOk())
            .andReturn();

        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            ag.act.model.GetBoardGroupPostResponse.class
        );
    }

    @Nested
    class WhenGetListDocumentAnswerStatus {

        @BeforeEach
        void setUp() {
            defaultSetUp();
        }

        private void saveDigitalDocument(Long firstPostByDocumentId) throws Exception {
            MvcResult response2 = mockMvc
                .perform(
                    multipart(TARGET_API_SAVE, firstPostByDocumentId)
                        .file(signImageFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
                        .headers(headers(jwt(jwt)))
                )
                .andExpect(status().isOk())
                .andReturn();

            objectMapperUtil.toResponse(
                response2.getResponse().getContentAsString(),
                ag.act.model.UserDigitalDocumentResponse.class
            );
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            // Get posts
            final ag.act.model.GetBoardGroupPostResponse result = callApiAndGetResult();

            List<ag.act.model.PostResponse> postResponses = result.getData();
            assertThat(postResponses.size(), is(2));
            assertThat(postResponses.get(0).getDigitalDocument(), is(notNullValue()));
            assertThat(postResponses.get(1).getDigitalDocument(), is(notNullValue()));
            assertThat(postResponses.get(0).getDigitalDocument().getAnswerStatus(), is(nullValue()));
            assertThat(postResponses.get(1).getDigitalDocument().getAnswerStatus(), is(nullValue()));

            Long firstPostByDocumentId = postResponses.get(0).getDigitalDocument().getId();
            Long secondPostByDocumentId = postResponses.get(1).getDigitalDocument().getId();

            makeDocumentUserData();

            // Save Digital document
            saveDigitalDocument(firstPostByDocumentId);

            // Get posts again
            final ag.act.model.GetBoardGroupPostResponse result3 = callApiAndGetResult();

            DigitalDocument digitalDocumentFirst = itUtil.findDigitalDocument(firstPostByDocumentId);
            DigitalDocument digitalDocumentSecond = itUtil.findDigitalDocument(secondPostByDocumentId);

            DigitalDocumentAnswerStatus statusFirst = itUtil.findDigitalDocumentByDigitalDocumentIdAndUserId(
                    digitalDocumentFirst.getId(), user.getId()
                )
                .map(DigitalDocumentUser::getDigitalDocumentAnswerStatus).orElse(null);

            List<ag.act.model.PostResponse> postResponses3 = result3.getData();
            assertThat(postResponses3.size(), is(2));
            assertThat(postResponses3.get(0).getDigitalDocument(), is(notNullValue()));
            assertThat(postResponses3.get(1).getDigitalDocument(), is(notNullValue()));
            assertThat(postResponses3.get(0).getDigitalDocument().getId(), is(digitalDocumentFirst.getId()));
            assertThat(postResponses3.get(1).getDigitalDocument().getId(), is(digitalDocumentSecond.getId()));
            assertThat(postResponses3.get(0).getDigitalDocument().getAnswerStatus(), is(statusFirst.name()));
            assertThat(postResponses3.get(1).getDigitalDocument().getAnswerStatus(), is(nullValue()));
        }
    }
}
