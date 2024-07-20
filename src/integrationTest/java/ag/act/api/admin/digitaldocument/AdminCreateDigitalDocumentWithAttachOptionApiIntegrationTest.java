package ag.act.api.admin.digitaldocument;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.PostUserProfile;
import ag.act.entity.Role;
import ag.act.entity.Solidarity;
import ag.act.entity.Stock;
import ag.act.entity.StockAcceptorUser;
import ag.act.entity.StockReferenceDate;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStock;
import ag.act.entity.UserRole;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentItem;
import ag.act.entity.digitaldocument.DigitalDocumentNumber;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.DigitalAnswerType;
import ag.act.enums.DigitalDocumentType;
import ag.act.enums.DigitalDocumentVersion;
import ag.act.enums.RoleType;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

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

class AdminCreateDigitalDocumentWithAttachOptionApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/stocks/{stockCode}/board-groups/{boardGroup}/posts";

    @Autowired
    private DigitalDocumentItemTreeGenerator digitalDocumentItemTreeGenerator;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private ag.act.model.CreatePostRequest request;
    private List<DigitalDocumentItemRequest> digitalDocumentItemList;
    private String jwt;
    private LocalDate referenceDate;
    private StockReferenceDate stockReferenceDate;
    private Stock stock;
    private Board board;
    private Solidarity solidarity;
    private String stockCode;
    private User acceptUser;

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

        digitalDocumentItemList = getItemList();
    }

    private ag.act.model.CreatePostRequest genRequestDocument() {
        ag.act.model.CreatePostRequest request = new ag.act.model.CreatePostRequest();
        request.setBoardCategory(board.getCategory().name());
        request.setTitle(someString(10));
        request.setContent(someAlphanumericString(300));
        request.setIsActive(Boolean.TRUE);

        final Instant endDate = Instant.now().plus(Period.ofDays(5));
        final Instant startDate = endDate.minus(Period.ofDays(1));

        request.digitalDocument(new CreateDigitalDocumentRequest()
            .type(DigitalDocumentType.DIGITAL_PROXY.name())
            .version(DigitalDocumentVersion.V1.name())
            .companyName(someString(10))
            .acceptUserId(acceptUser.getId())
            .stockReferenceDateId(stockReferenceDate.getId())
            .targetStartDate(startDate)
            .targetEndDate(endDate)
            .shareholderMeetingDate(startDate)
            .shareholderMeetingType(someString(10))
            .shareholderMeetingName(someString(10))
            .designatedAgentNames(someString(10))
            .title(someString(10))
            .content(someAlphanumericString(50))
            .attachOptions(new ag.act.model.JsonAttachOption()
                .signImage(AttachOptionType.REQUIRED.name())
                .idCardImage(AttachOptionType.OPTIONAL.name())
                .bankAccountImage(AttachOptionType.OPTIONAL.name())
                .hectoEncryptedBankAccountPdf(AttachOptionType.OPTIONAL.name())
            )
            .childItems(getItemList())
        );

        return request;
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

    private void assertAcceptorUserFromDatabase() {
        final User acceptUserFromDatabase = itUtil.findUser(acceptUser.getId());
        final StockAcceptorUser stockAcceptorUser = itUtil.findStockAcceptorUser(stockCode).orElseThrow();
        assertThat(stockAcceptorUser.getUserId(), is(acceptUser.getId()));

        final List<RoleType> userRoleTypes = acceptUserFromDatabase.getRoles().stream().map(UserRole::getRole).map(Role::getType).toList();
        assertThat(userRoleTypes, is(List.of(RoleType.ACCEPTOR_USER)));

        final String expectedPassword = itUtil.decrypt(acceptUserFromDatabase.getHashedPhoneNumber());
        assertThat(passwordEncoder.matches(expectedPassword, acceptUserFromDatabase.getPassword()), is(true));
        assertThat(acceptUserFromDatabase.getIsChangePasswordRequired(), is(true));
    }

    private void assertPostFromDatabase() {
        final Post afterPost = itUtil.findOnePostByBoardId(board.getId()).orElseThrow();
        final PostUserProfile postUserProfile = afterPost.getPostUserProfile();
        final DigitalDocument digitalDocument = afterPost.getDigitalDocument();
        final List<DigitalDocumentItem> itemList = itUtil.findDigitalDocumentItemsByDigitalDocumentId(
            digitalDocument.getId()
        );

        assertThat(postUserProfile, is(notNullValue()));
        assertThat(postUserProfile.getNickname(), is(notNullValue()));
        assertThat(postUserProfile.getProfileImageUrl(), is(notNullValue()));
        assertThat(postUserProfile.getIndividualStockCountLabel(), nullValue());

        assertThat(digitalDocument, is(notNullValue()));
        assertThat(digitalDocument.getStockCode(), is(stock.getCode()));
        assertThat(digitalDocument.getType().name(), is(request.getDigitalDocument().getType()));
        assertThat(digitalDocument.getAcceptUserId(), is(acceptUser.getId()));
        assertThat(digitalDocument.getStockReferenceDate(), is(referenceDate));

        assertDigitalDocumentItemTree(itemList);

        assertThat(digitalDocument.getJsonAttachOption().getSignImage(), is(AttachOptionType.REQUIRED.name()));
        JsonAttachOption attachOptions = request.getDigitalDocument().getAttachOptions();
        assertThat(digitalDocument.getJsonAttachOption().getIdCardImage(), is(attachOptions.getIdCardImage()));
        assertThat(digitalDocument.getJsonAttachOption().getBankAccountImage(), is(attachOptions.getBankAccountImage()));
        assertThat(digitalDocument.getJsonAttachOption().getHectoEncryptedBankAccountPdf(),
            is(attachOptions.getHectoEncryptedBankAccountPdf()));

        final DigitalDocumentNumber databaseDigitalDocumentNumber = itUtil.findByDigitalDocumentId(
            digitalDocument.getId()).orElseThrow();
        assertThat(digitalDocument.getId(), is(databaseDigitalDocumentNumber.getDigitalDocumentId()));
        assertThat(databaseDigitalDocumentNumber.getLastIssuedNumber(), is(0L));
    }

    private void assertDigitalDocumentItemTree(List<DigitalDocumentItem> itemList) {

        final List<DigitalDocumentItemResponse> actualItemTree = digitalDocumentItemTreeGenerator.buildTree(itemList);

        assertThat(actualItemTree.size(), is(digitalDocumentItemList.size()));

        final DigitalDocumentItemResponse actualParentItem = actualItemTree.get(0);
        final DigitalDocumentItemRequest expectedParentItem = digitalDocumentItemList.get(0);
        assertParent(actualParentItem, expectedParentItem);
        assertChild(actualParentItem.getChildItems().get(0), expectedParentItem.getChildItems().get(0));
        assertChild(actualParentItem.getChildItems().get(1), expectedParentItem.getChildItems().get(1));
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

    @Nested
    class WhenCreateSuccess {


        @BeforeEach
        void setUp() {
            acceptUser = itUtil.createUser();
            acceptUser.setIsChangePasswordRequired(Boolean.TRUE);
            itUtil.updateUser(acceptUser);
            itUtil.createUserRole(acceptUser, RoleType.ACCEPTOR_USER);
            itUtil.createStockAcceptorUser(stockCode, acceptUser);

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

            final ag.act.model.PostDetailsDataResponse result = itUtil.getResult(response, ag.act.model.PostDetailsDataResponse.class);

            assertThat(result.getData(), notNullValue());

            assertPostFromDatabase();
            assertAcceptorUserFromDatabase();
        }
    }
}
