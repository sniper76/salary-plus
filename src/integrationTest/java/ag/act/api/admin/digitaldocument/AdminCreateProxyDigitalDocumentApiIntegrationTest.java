package ag.act.api.admin.digitaldocument;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.FileContent;
import ag.act.entity.Post;
import ag.act.entity.PostImage;
import ag.act.entity.PostUserProfile;
import ag.act.entity.Solidarity;
import ag.act.entity.SolidarityLeader;
import ag.act.entity.Stock;
import ag.act.entity.StockAcceptorUser;
import ag.act.entity.StockReferenceDate;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStock;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentItem;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.DigitalAnswerType;
import ag.act.enums.DigitalDocumentType;
import ag.act.enums.DigitalDocumentVersion;
import ag.act.enums.digitaldocument.AttachOptionType;
import ag.act.model.CreateDigitalDocumentRequest;
import ag.act.model.DigitalDocumentItemRequest;
import ag.act.model.DigitalDocumentItemResponse;
import ag.act.model.JsonAttachOption;
import ag.act.util.DigitalDocumentItemTreeGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Stream;

import static ag.act.TestUtil.someLocalDateTimeInTheFutureDaysBetween;
import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class AdminCreateProxyDigitalDocumentApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/stocks/{stockCode}/board-groups/{boardGroup}/posts";

    @Autowired
    private DigitalDocumentItemTreeGenerator digitalDocumentItemTreeGenerator;
    private List<Long> imageIds;
    private ag.act.model.CreatePostRequest request;
    private List<DigitalDocumentItemRequest> digitalDocumentItemList;
    private String jwt;
    private LocalDate referenceDate;
    private User acceptUser;
    private StockReferenceDate stockReferenceDate;
    private Stock stock;
    private Board board;
    private SolidarityLeader solidarityLeader;
    private Solidarity solidarity;
    private Instant targetStartDate;
    private Instant targetEndDate;
    private Instant now;
    private String stockCode;
    private Long acceptUserId;

    private static void assertUserProfile(PostUserProfile postUserProfile) {
        assertThat(postUserProfile, is(notNullValue()));
        assertThat(postUserProfile.getNickname(), is(notNullValue()));
        assertThat(postUserProfile.getProfileImageUrl(), is(notNullValue()));
        assertThat(postUserProfile.getIndividualStockCountLabel(), nullValue());
    }

    @BeforeEach
    void setUp() {
        itUtil.init();
        User user = itUtil.createAdminUser();
        jwt = itUtil.createJwt(user.getId());
        stockCode = someStockCode();
        stock = itUtil.createStock(stockCode);
        board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.DIGITAL_DELEGATION);

        referenceDate = someLocalDateTimeInTheFutureDaysBetween(5, 10).toLocalDate();
        stockReferenceDate = itUtil.createStockReferenceDate(stock.getCode(), referenceDate);

        solidarity = itUtil.createSolidarity(stock.getCode());
        UserHoldingStock userHoldingStock = itUtil.createUserHoldingStock(stock.getCode(), user);
        itUtil.updateUserHoldingStock(userHoldingStock);

        now = Instant.now();
        targetStartDate = now.minus(3, ChronoUnit.DAYS);
        targetEndDate = now.plus(3, ChronoUnit.DAYS);

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

    private ag.act.model.CreatePostRequest genRequestDocument() {
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
            .acceptUserId(acceptUserId)
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

    private void assertDigitalDocument(DigitalDocument digitalDocument, List<DigitalDocumentItem> itemList) {
        assertThat(digitalDocument, notNullValue());
        assertThat(digitalDocument.getStockCode(), is(stock.getCode()));
        assertThat(digitalDocument.getType().name(), is(request.getDigitalDocument().getType()));
        assertThat(digitalDocument.getAcceptUserId(), is(acceptUserId));
        assertThat(digitalDocument.getStockReferenceDate(), is(referenceDate));
        assertThat(digitalDocument.getVersion(), is(DigitalDocumentVersion.V1));

        final List<DigitalDocumentItemResponse> actualItemTree = digitalDocumentItemTreeGenerator.buildTree(itemList);

        assertThat(actualItemTree.size(), is(digitalDocumentItemList.size()));

        final DigitalDocumentItemResponse actualParentItem = actualItemTree.get(0);
        final DigitalDocumentItemRequest expectedParentItem = digitalDocumentItemList.get(0);
        assertParent(actualParentItem, expectedParentItem);
        assertChild(actualParentItem.getChildItems().get(0), expectedParentItem.getChildItems().get(0));
        assertChild(actualParentItem.getChildItems().get(1), expectedParentItem.getChildItems().get(1));

        assertThat(digitalDocument.getJsonAttachOption(), notNullValue());
        assertThat(digitalDocument.getJsonAttachOption().getSignImage(), is(AttachOptionType.REQUIRED.name()));
        JsonAttachOption attachOptions = request.getDigitalDocument().getAttachOptions();
        assertThat(digitalDocument.getJsonAttachOption().getIdCardImage(), is(attachOptions.getIdCardImage()));
        assertThat(digitalDocument.getJsonAttachOption().getBankAccountImage(), is(attachOptions.getBankAccountImage()));
        assertThat(digitalDocument.getJsonAttachOption().getHectoEncryptedBankAccountPdf(),
            is(attachOptions.getHectoEncryptedBankAccountPdf()));
    }

    private void assertChild(DigitalDocumentItemResponse actualItem, DigitalDocumentItemRequest expectedItem) {
        assertThat(actualItem.getTitle(), is(expectedItem.getTitle()));
        assertThat(actualItem.getContent(), is(expectedItem.getContent()));
        assertThat(actualItem.getDefaultSelectValue(), is(expectedItem.getDefaultSelectValue()));
        assertThat(actualItem.getLeaderDescription(), is(expectedItem.getLeaderDescription()));
    }

    private void assertParent(DigitalDocumentItemResponse actualParentItem, DigitalDocumentItemRequest expectedParentItem) {
        assertThat(actualParentItem.getTitle(), is(expectedParentItem.getTitle()));
        assertThat(actualParentItem.getContent(), is(expectedParentItem.getContent()));
        assertThat(actualParentItem.getDefaultSelectValue(), is(expectedParentItem.getDefaultSelectValue()));
        assertThat(actualParentItem.getLeaderDescription(), is(expectedParentItem.getLeaderDescription()));
        assertThat(actualParentItem.getChildItems().size(), is(expectedParentItem.getChildItems().size()));
    }

    private void assertResponse(ag.act.model.PostDetailsDataResponse result, Post afterPost) {
        assertThat(result.getData(), notNullValue());

        final PostUserProfile postUserProfile = afterPost.getPostUserProfile();
        final DigitalDocument digitalDocument = afterPost.getDigitalDocument();
        final List<DigitalDocumentItem> itemList = itUtil.findDigitalDocumentItemsByDigitalDocumentId(
            digitalDocument.getId()
        );

        assertUserProfile(postUserProfile);

        assertDigitalDocument(digitalDocument, itemList);
    }

    @Nested
    class WhenCreateSuccess {

        @BeforeEach
        void setUp() {
            acceptUser = itUtil.createUser();
            acceptUserId = acceptUser.getId();
            itUtil.createStockAcceptorUser(stockCode, acceptUser);
            imageIds = Stream.of(itUtil.createImage(), itUtil.createImage()).map(FileContent::getId).toList();

            request = genRequestDocument();
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    post(TARGET_API, stockCode, board.getGroup().name())
                        .content(objectMapperUtil.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isOk())
                .andReturn();

            final ag.act.model.PostDetailsDataResponse result = objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                ag.act.model.PostDetailsDataResponse.class
            );

            final Post afterPost = itUtil.findOnePostByBoardId(board.getId()).orElseThrow();

            assertResponse(result, afterPost);

            final List<PostImage> postImagesByPostId = itUtil.findPostImagesByPostId(afterPost.getId());
            assertThat(postImagesByPostId.get(0).getImageId(), is(imageIds.get(0)));
            assertThat(postImagesByPostId.get(1).getImageId(), is(imageIds.get(1)));
        }
    }

    @Nested
    class WhenCreateWithAttachOptionSuccess {

        @BeforeEach
        void setUp() {
            acceptUser = itUtil.createUser();
            acceptUserId = acceptUser.getId();
            itUtil.createStockAcceptorUser(stockCode, acceptUser);

            imageIds = List.of();
            request = genRequestDocument();
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    post(TARGET_API, stockCode, board.getGroup().name())
                        .content(objectMapperUtil.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isOk())
                .andReturn();

            final ag.act.model.PostDetailsDataResponse result = objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                ag.act.model.PostDetailsDataResponse.class
            );

            final Post afterPost = itUtil.findOnePostByBoardId(board.getId()).orElseThrow();

            assertResponse(result, afterPost);

            final StockAcceptorUser stockAcceptorUser = itUtil.findStockAcceptorUser(stockCode).orElseThrow();
            assertThat(stockAcceptorUser.getUserId(), is(acceptUserId));
        }
    }

    @Nested
    class WhenCreateNotMatchAcceptor {

        @BeforeEach
        void setUp() {
            acceptUser = itUtil.createUser();
            acceptUserId = acceptUser.getId();

            imageIds = List.of();
            request = genRequestDocument();
        }

        @DisplayName("Should return 400 response code when call " + TARGET_API)
        @Test
        void shouldReturnError() throws Exception {
            MvcResult response = mockMvc
                .perform(
                    post(TARGET_API, stockCode, board.getGroup().name())
                        .content(objectMapperUtil.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt)
                )
                .andExpect(status().isBadRequest())
                .andReturn();

            itUtil.assertErrorResponse(response, 400, "전자문서 수임인 정보가 없습니다.");
        }
    }
}
