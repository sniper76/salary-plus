package ag.act.api.admin.digitaldocument;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.FileContent;
import ag.act.entity.Post;
import ag.act.entity.PostImage;
import ag.act.entity.PostUserProfile;
import ag.act.entity.Role;
import ag.act.entity.Solidarity;
import ag.act.entity.Stock;
import ag.act.entity.StockAcceptorUser;
import ag.act.entity.User;
import ag.act.entity.UserHoldingStock;
import ag.act.entity.UserRole;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.DigitalDocumentType;
import ag.act.enums.DigitalDocumentVersion;
import ag.act.enums.RoleType;
import ag.act.enums.digitaldocument.AttachOptionType;
import ag.act.model.CreateDigitalDocumentRequest;
import ag.act.model.JsonAttachOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Stream;

import static ag.act.TestUtil.someCompanyRegistrationNumber;
import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class AdminCreateJointOwnershipDigitalDocumentApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/admin/stocks/{stockCode}/board-groups/{boardGroup}/posts";
    private List<Long> imageIds;
    private ag.act.model.CreatePostRequest request;
    private User user;
    private String jwt;
    private User acceptUser;
    private Stock stock;
    private Board board;
    private Solidarity solidarity;
    private Instant targetStartDate;
    private Instant targetEndDate;
    private String stockCode;

    @BeforeEach
    void setUp() {
        itUtil.init();
        user = itUtil.createAdminUser();
        jwt = itUtil.createJwt(user.getId());
        stockCode = someStockCode();
        stock = itUtil.createStock(stockCode);
        board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.CO_HOLDING_ARRANGEMENTS);

        solidarity = itUtil.createSolidarity(stock.getCode());
        UserHoldingStock userHoldingStock = itUtil.createUserHoldingStock(stock.getCode(), user);
        itUtil.updateUserHoldingStock(userHoldingStock);

        Instant now = Instant.now();
        targetStartDate = now.minus(3, ChronoUnit.DAYS);
        targetEndDate = now.plus(3, ChronoUnit.DAYS);
    }

    private ag.act.model.CreatePostRequest genRequest(Long acceptUserId) {
        ag.act.model.CreatePostRequest request = new ag.act.model.CreatePostRequest();
        request.setBoardCategory(board.getCategory().name());
        request.setTitle(someString(10));
        request.setContent(someAlphanumericString(300));
        request.setIsActive(Boolean.TRUE);
        request.setImageIds(imageIds);

        request.digitalDocument(new CreateDigitalDocumentRequest()
            .type(DigitalDocumentType.JOINT_OWNERSHIP_DOCUMENT.name())
            .version(DigitalDocumentVersion.V1.name())
            .companyName(someString(10))
            .companyRegistrationNumber(someCompanyRegistrationNumber())
            .acceptUserId(acceptUserId)
            .targetStartDate(targetStartDate)
            .targetEndDate(targetEndDate)
            .title(someString(10))
            .content(someAlphanumericString(50))
            .attachOptions(new ag.act.model.JsonAttachOption()
                .signImage(AttachOptionType.REQUIRED.name())
                .idCardImage(someEnum(AttachOptionType.class).name())
                .bankAccountImage(someEnum(AttachOptionType.class).name())
                .hectoEncryptedBankAccountPdf(someEnum(AttachOptionType.class).name())
            )
        );

        return request;
    }

    private List<Long> createFiles() {
        final FileContent image1 = itUtil.createImage();
        final FileContent image2 = itUtil.createImage();
        return Stream.of(image1, image2).map(FileContent::getId).toList();
    }

    @Nested
    class WhenCreateSuccess {

        @BeforeEach
        void setUp() {
            acceptUser = itUtil.createUser();
            itUtil.createStockAcceptorUser(stockCode, acceptUser);

            imageIds = createFiles();
            request = genRequest(acceptUser.getId());
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

            assertThat(result.getData(), notNullValue());

            final Post afterPost = itUtil.findOnePostByBoardId(board.getId()).orElseThrow();

            assertPostFromDatabase(afterPost);
            assertAcceptorUserFromDatabase();
        }

        private void assertAcceptorUserFromDatabase() {
            final User acceptUserFromDatabase = itUtil.findUser(acceptUser.getId());
            final StockAcceptorUser stockAcceptorUser = itUtil.findStockAcceptorUser(stockCode).orElseThrow();
            assertThat(stockAcceptorUser.getUserId(), is(acceptUser.getId()));

            final List<RoleType> userRoleTypes = acceptUserFromDatabase.getRoles().stream().map(UserRole::getRole).map(Role::getType).toList();

            assertThat(userRoleTypes, not(contains(RoleType.ACCEPTOR_USER)));
            assertThat(acceptUserFromDatabase.getPassword(), nullValue());
        }

        private void assertPostFromDatabase(Post afterPost) {
            final PostUserProfile postUserProfile = afterPost.getPostUserProfile();
            final DigitalDocument digitalDocument = afterPost.getDigitalDocument();

            final List<PostImage> postImagesByPostId = itUtil.findPostImagesByPostId(afterPost.getId());
            assertThat(postImagesByPostId.get(0).getImageId(), is(imageIds.get(0)));
            assertThat(postImagesByPostId.get(1).getImageId(), is(imageIds.get(1)));

            assertThat(postUserProfile.getNickname(), is(user.getNickname()));
            assertThat(postUserProfile.getProfileImageUrl(), is(notNullValue()));
            assertThat(postUserProfile.getIndividualStockCountLabel(), nullValue());

            assertThat(digitalDocument.getStockCode(), is(stock.getCode()));
            assertThat(digitalDocument.getType().name(), is(request.getDigitalDocument().getType()));
            assertThat(digitalDocument.getAcceptUserId(), is(acceptUser.getId()));

            JsonAttachOption jsonAttachOptionDatabase = digitalDocument.getJsonAttachOption();
            JsonAttachOption attachOptions = request.getDigitalDocument().getAttachOptions();
            assertThat(jsonAttachOptionDatabase.getSignImage(), is(attachOptions.getSignImage()));
            assertThat(jsonAttachOptionDatabase.getIdCardImage(), is(attachOptions.getIdCardImage()));
            assertThat(jsonAttachOptionDatabase.getBankAccountImage(), is(attachOptions.getBankAccountImage()));
            assertThat(jsonAttachOptionDatabase.getHectoEncryptedBankAccountPdf(), is(attachOptions.getHectoEncryptedBankAccountPdf()));
        }
    }

    @Nested
    class WhenAcceptorNotExists {
        @BeforeEach
        void setUp() {
            acceptUser = itUtil.createUser();

            imageIds = createFiles();
            request = genRequest(acceptUser.getId());
        }

        @DisplayName("Should return 400 response code when call " + TARGET_API)
        @Test
        void shouldReturnBadRequest() throws Exception {
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

            final ag.act.model.ErrorResponse result = objectMapperUtil.toResponse(
                response.getResponse().getContentAsString(),
                ag.act.model.ErrorResponse.class
            );

            assertThat(result.getMessage(), is("전자문서 수임인 정보가 없습니다."));
        }
    }
}
