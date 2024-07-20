package ag.act.api.admin.digitaldocument;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.FileContent;
import ag.act.entity.Post;
import ag.act.entity.PostImage;
import ag.act.entity.Solidarity;
import ag.act.entity.Stock;
import ag.act.entity.StockReferenceDate;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStock;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.DigitalAnswerType;
import ag.act.enums.DigitalDocumentType;
import ag.act.enums.DigitalDocumentVersion;
import ag.act.enums.digitaldocument.AttachOptionType;
import ag.act.model.CreateDigitalDocumentRequest;
import ag.act.model.CreatePostRequest;
import ag.act.model.DigitalDocumentItemRequest;
import ag.act.model.UpdatePostRequest;
import ag.act.model.UpdatePostRequestDigitalDocument;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Stream;

import static ag.act.TestUtil.assertTime;
import static ag.act.TestUtil.someLocalDateTimeInTheFutureDaysBetween;
import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class UpdateDigitalDocumentApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/stocks/{stockCode}/board-groups/{boardGroupName}/posts";
    private static final String TARGET_API_UPDATE = "/api/admin/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}";

    private CreatePostRequest requestForCreate;
    private UpdatePostRequest requestForUpdate;
    private List<DigitalDocumentItemRequest> digitalDocumentItemList;
    private String jwt;
    private LocalDate referenceDateUpdate;
    private StockReferenceDate stockReferenceDate;
    private StockReferenceDate stockReferenceDateUpdate;
    private Stock stock;
    private Board board;
    private User acceptUser;
    private Instant targetStartDate;
    private Instant targetEndDate;
    private Instant targetEndDateUpdate;
    private Instant now;
    private List<Long> imageIds;
    private String stockCode;
    private String contentUpdateString;
    private String titleUpdateString;
    private Instant targetStartDateUpdate;

    @BeforeEach
    void setUp() {
        itUtil.init();
        User user = itUtil.createAdminUser();
        jwt = itUtil.createJwt(user.getId());
        stockCode = someStockCode();
        stock = itUtil.createStock(stockCode);
        board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.DIGITAL_DELEGATION);

        LocalDate referenceDate = someLocalDateTimeInTheFutureDaysBetween(5, 10).toLocalDate();
        referenceDateUpdate = someLocalDateTimeInTheFutureDaysBetween(5, 10).toLocalDate();
        stockReferenceDate = itUtil.createStockReferenceDate(stock.getCode(), referenceDate);
        stockReferenceDateUpdate = itUtil.createStockReferenceDate(stock.getCode(), referenceDateUpdate);

        Solidarity solidarity = itUtil.createSolidarity(stock.getCode());
        UserHoldingStock userHoldingStock = itUtil.createUserHoldingStock(stock.getCode(), user);
        itUtil.updateUserHoldingStock(userHoldingStock);

        acceptUser = itUtil.createUser();
        itUtil.createStockAcceptorUser(stockCode, acceptUser);

        digitalDocumentItemList = getItemList();
    }

    private List<DigitalDocumentItemRequest> getItemList() {
        return List.of(new DigitalDocumentItemRequest()
            .title("제1안")
            .content("제1안 내용")
            .defaultSelectValue(null)
            .leaderDescription(null)
            .childItems(
                List.of(
                    new DigitalDocumentItemRequest()
                        .title("제1-1안")
                        .content("제1-1안 내용")
                        .defaultSelectValue(DigitalAnswerType.APPROVAL.name())
                        .leaderDescription("설명 제1-1안 내용"),
                    new DigitalDocumentItemRequest()
                        .title("제1-2안")
                        .content("제1-2안 내용")
                        .defaultSelectValue(DigitalAnswerType.APPROVAL.name())
                        .leaderDescription("설명 제1-2안 내용")
                )
            ));
    }

    private ag.act.model.CreatePostRequest getRequestCreate() {
        ag.act.model.CreatePostRequest request = new ag.act.model.CreatePostRequest();
        request.setBoardCategory(board.getCategory().name());
        request.setTitle(someString(10));
        request.setContent(someAlphanumericString(300));
        request.setIsActive(Boolean.TRUE);
        request.setImageIds(imageIds);

        request.digitalDocument(new CreateDigitalDocumentRequest()
            .type(DigitalDocumentType.DIGITAL_PROXY.name())
            .version(DigitalDocumentVersion.V1.name())
            .companyName(someString(10))
            .acceptUserId(acceptUser.getId())
            .stockReferenceDateId(stockReferenceDate.getId())
            .targetStartDate(targetStartDate)
            .targetEndDate(targetEndDate)
            .shareholderMeetingDate(now)
            .shareholderMeetingType(someString(10))
            .shareholderMeetingName(someString(10))
            .designatedAgentNames(someString(10))
            .title(someString(10))
            .content(someAlphanumericString(50))
            .attachOptions(new ag.act.model.JsonAttachOption()
                .idCardImage(AttachOptionType.REQUIRED.name())
                .signImage(AttachOptionType.REQUIRED.name())
                .bankAccountImage(AttachOptionType.NONE.name())
                .hectoEncryptedBankAccountPdf(AttachOptionType.NONE.name())
            )
            .childItems(digitalDocumentItemList)
        );

        return request;
    }

    private UpdatePostRequest getRequestUpdate() {
        contentUpdateString = someAlphanumericString(10);
        titleUpdateString = someAlphanumericString(20);
        return new UpdatePostRequest()
            .imageIds(imageIds)
            .content("renamecontent")
            .title("renametitle")
            .digitalDocument(
                new UpdatePostRequestDigitalDocument()
                    .targetStartDate(targetStartDateUpdate)
                    .targetEndDate(targetEndDateUpdate)
                    .stockReferenceDateId(stockReferenceDateUpdate.getId())
                    .title(titleUpdateString)
                    .content(contentUpdateString)
            );
    }

    private void failToUpdateDigitalDocument(Post post, String message) throws Exception {
        MvcResult response = callApi(post, status().isBadRequest());

        itUtil.assertErrorResponse(response, 400, message);
    }

    private void updateDigitalDocument(Post post) throws Exception {
        MvcResult response = callApi(post, status().isOk());

        assertResponse(response);
    }

    private MvcResult callApi(Post post, ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                patch(TARGET_API_UPDATE, post.getBoard().getStockCode(), post.getBoard().getGroup().name(), post.getId())
                    .content(objectMapperUtil.toJson(requestForUpdate))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    private void createDigitalDocument() throws Exception {
        MvcResult response = mockMvc
            .perform(
                post(TARGET_API, stockCode, board.getGroup().name())
                    .content(objectMapperUtil.toJson(requestForCreate))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(status().isOk())
            .andReturn();

        assertResponse(response);
    }

    private void assertResponse(MvcResult responseUpdate) throws JsonProcessingException, UnsupportedEncodingException {
        final ag.act.model.PostDetailsDataResponse resultUpdate = objectMapperUtil.toResponse(
            responseUpdate.getResponse().getContentAsString(),
            ag.act.model.PostDetailsDataResponse.class
        );

        assertThat(resultUpdate.getData(), is(notNullValue()));
    }

    @Nested
    class WhenUpdateSuccess {

        @BeforeEach
        void setUp() {
            now = Instant.now();
            targetStartDate = now.plus(1, ChronoUnit.DAYS);
            targetEndDate = now.plus(5, ChronoUnit.DAYS);

            targetStartDateUpdate = now.plus(2, ChronoUnit.DAYS);
            targetEndDateUpdate = now.plus(8, ChronoUnit.DAYS);

            final FileContent image1 = itUtil.createImage();
            final FileContent image2 = itUtil.createImage();
            imageIds = Stream.of(image1, image2).map(FileContent::getId).toList();

            requestForCreate = getRequestCreate();
            requestForUpdate = getRequestUpdate();
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            createDigitalDocument();

            Post afterPost = itUtil.findOnePostByBoardId(board.getId()).orElseThrow();
            final DigitalDocument digitalDocument = afterPost.getDigitalDocument();

            updateDigitalDocument(afterPost);

            afterPost = itUtil.findOnePostByBoardId(board.getId()).orElseThrow();
            final DigitalDocument digitalDocumentAfter = itUtil.findByDigitalDocument(digitalDocument.getId())
                .orElseThrow();

            assertThat(digitalDocument, is(notNullValue()));
            assertThat(digitalDocumentAfter.getTitle(), is(titleUpdateString));
            assertThat(digitalDocumentAfter.getContent(), is(contentUpdateString));
            assertThat(digitalDocumentAfter.getStockCode(), is(stock.getCode()));
            assertThat(digitalDocumentAfter.getType().name(), is(requestForCreate.getDigitalDocument().getType()));

            assertTime(digitalDocumentAfter.getStockReferenceDate(), referenceDateUpdate);
            assertTime(digitalDocumentAfter.getTargetStartDate(), targetStartDateUpdate);
            assertTime(digitalDocumentAfter.getTargetEndDate(), targetEndDateUpdate);
            assertThat(afterPost.getTitle(), is(requestForUpdate.getTitle()));
            assertThat(afterPost.getContent(), is(requestForUpdate.getContent()));

            final List<PostImage> postImagesByPostId = itUtil.findPostImagesByPostId(afterPost.getId());
            assertThat(postImagesByPostId.get(0).getImageId(), is(imageIds.get(0)));
            assertThat(postImagesByPostId.get(1).getImageId(), is(imageIds.get(1)));
        }
    }

    @Nested
    class WhenErrorTargetEndDate {

        @BeforeEach
        void setUp() {
            now = Instant.now();
            targetStartDate = now.minus(3, ChronoUnit.DAYS);
            targetEndDate = now.plus(3, ChronoUnit.DAYS);

            targetEndDateUpdate = now.minus(1, ChronoUnit.DAYS);

            requestForCreate = getRequestCreate();
            requestForUpdate = getRequestUpdate();
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnError() throws Exception {
            createDigitalDocument();

            Post afterPost = itUtil.findOnePostByBoardId(board.getId()).orElseThrow();
            // final DigitalDocument digitalDocument = afterPost.getDigitalDocument();

            failToUpdateDigitalDocument(afterPost, "종료일은 현재일 이후로 수정 가능합니다.");
        }
    }

    @Nested
    class WhenErrorTargetStartDateIsBeforeToday {

        @BeforeEach
        void setUp() {
            now = Instant.now();
            targetStartDate = now.minus(3, ChronoUnit.DAYS);
            targetEndDate = now.plus(3, ChronoUnit.DAYS);

            targetEndDateUpdate = now.plus(5, ChronoUnit.DAYS);

            requestForCreate = getRequestCreate();
            requestForUpdate = getRequestUpdate();
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnError() throws Exception {
            createDigitalDocument();

            Post afterPost = itUtil.findOnePostByBoardId(board.getId()).orElseThrow();

            failToUpdateDigitalDocument(afterPost, "기준일은 시작일 이전에 변경 가능합니다.");
        }
    }
}
