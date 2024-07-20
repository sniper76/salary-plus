package ag.act.api.stockboardgrouppost;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.converter.DateTimeConverter;
import ag.act.entity.Board;
import ag.act.entity.CorporateUser;
import ag.act.entity.DigitalProxy;
import ag.act.entity.FileContent;
import ag.act.entity.Post;
import ag.act.entity.PostImage;
import ag.act.entity.PostUserProfile;
import ag.act.entity.Stock;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.DigitalDocumentType;
import ag.act.model.BoardCategoryResponse;
import ag.act.model.DigitalDocumentAcceptUserResponse;
import ag.act.model.DigitalDocumentItemResponse;
import ag.act.model.DigitalDocumentUserResponse;
import ag.act.model.HolderListReadAndCopyDigitalDocumentResponse;
import ag.act.model.PostDataResponse;
import ag.act.model.PostResponse;
import ag.act.model.SimpleImageResponse;
import ag.act.model.StockResponse;
import ag.act.model.UserDigitalDocumentResponse;
import ag.act.model.UserProfileResponse;
import ag.act.util.DigitalDocumentItemTreeGenerator;
import ag.act.util.ImageUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static ag.act.TestUtil.assertTime;
import static ag.act.TestUtil.someLocalDateTimeInTheFuture;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@SuppressWarnings("checkstyle:LineLength")
class GetBoardGroupPostDetailDigitalDocumentApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}";

    @Autowired
    private DigitalDocumentItemTreeGenerator digitalDocumentItemTreeGenerator;

    private String jwt;
    private Stock stock;
    private Board board;
    private Post post;
    private User postWriter;
    private User postViewer;
    private User acceptUser;
    private DigitalProxy digitalProxy;
    private DigitalDocument digitalDocument;
    private String corporateNo;

    @BeforeEach
    void setUp() {
        itUtil.init();

        stock = itUtil.createStock();
        postWriter = itUtil.createUserWithAddress();
        itUtil.createUserHoldingStock(stock.getCode(), postWriter);

        postViewer = itUtil.createUserWithAddress();
        jwt = itUtil.createJwt(postViewer.getId());

        acceptUser = itUtil.createUserWithAddress();
        board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.DIGITAL_DELEGATION);
        itUtil.createSolidarity(stock.getCode());
        itUtil.createStockAcceptorUser(stock.getCode(), acceptUser);
    }

    private MvcResult callApi(ResultMatcher resultMatcher) throws Exception {
        return mockMvc
            .perform(
                get(TARGET_API, stock.getCode(), board.getGroup(), post.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(resultMatcher)
            .andReturn();
    }

    private PostDataResponse getResponse(MvcResult response) throws JsonProcessingException, UnsupportedEncodingException {
        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            PostDataResponse.class
        );
    }

    @Nested
    class WhenGetModuSignDigitalProxy {
        @BeforeEach
        void setUp() {
            itUtil.createUserHoldingStock(stock.getCode(), postViewer);
            post = itUtil.createPost(board, postWriter.getId());
            digitalProxy = itUtil.createDigitalProxy(post);
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final PostDataResponse result = getResponse(callApi(status().isOk()));
            assertResponse(result);
        }

        private void assertResponse(PostDataResponse result) {
            PostResponse postResponse = result.getData();

            assertThat(postResponse.getPoll(), is(nullValue()));
            assertThat(postResponse.getDigitalProxy().getId(), is(digitalProxy.getId()));
            assertThat(postResponse.getDigitalDocument(), is(nullValue()));
        }
    }

    @Nested
    class WhenGetModuSignDigitalProxyAfterCreatingDigitalProxyByApi {
        @BeforeEach
        void setUp() throws Exception {
            final ag.act.model.PostDetailsDataResponse postCreateResponse = itUtil.callPostApi(
                mockMvc,
                itUtil.createJwt(itUtil.createAdminUser().getId()),
                objectMapperUtil.toRequestBody(itUtil.generateDigitalProxyCreatePostRequest(board)),
                ag.act.model.PostDetailsDataResponse.class,
                "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts",
                stock.getCode(),
                board.getGroup().name()
            );

            itUtil.createUserHoldingStock(stock.getCode(), postViewer);
            post = itUtil.findPostNoneNull(postCreateResponse.getData().getId());
            digitalProxy = post.getDigitalProxy();
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final PostDataResponse result = getResponse(callApi(status().isOk()));
            assertResponse(result);
        }

        private void assertResponse(PostDataResponse result) {
            PostResponse postResponse = result.getData();

            assertThat(postResponse.getPoll(), is(nullValue()));
            assertThat(postResponse.getDigitalProxy().getId(), is(digitalProxy.getId()));
            assertThat(postResponse.getDigitalDocument(), is(nullValue()));
        }
    }

    @Nested
    class WhenGetDigitalDocument {
        @BeforeEach
        void setUp() {
            post = itUtil.createPost(board, postWriter.getId());
            digitalDocument = itUtil.createDigitalDocument(post, stock, acceptUser, DigitalDocumentType.DIGITAL_PROXY);
            post.setDigitalDocument(digitalDocument);
            itUtil.updatePost(post);
            itUtil.createStockReferenceDate(stock.getCode(), digitalDocument.getStockReferenceDate());
            itUtil.createUserHoldingStock(stock.getCode(), postViewer);
            itUtil.createUserHoldingStockOnReferenceDate(stock.getCode(), postViewer.getId(), digitalDocument.getStockReferenceDate());
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final PostDataResponse result = getResponse(callApi(status().isOk()));
            assertResponse(result);
        }

        private void assertResponse(PostDataResponse result) {
            PostResponse postResponse = result.getData();

            assertThat(postResponse.getPoll(), is(nullValue()));
            assertThat(postResponse.getDigitalProxy(), is(nullValue()));
            assertThat(postResponse.getDigitalDocument().getId(), is(digitalDocument.getId()));
            assertDigitalDocumentDetails(postResponse);
        }
    }

    @Nested
    class WhenGetDigitalProxyDigitalDocument {

        @Nested
        class GetItSuccessfully {

            @BeforeEach
            void setUp() {
                post = itUtil.createPost(board, postWriter.getId());
                digitalDocument = itUtil.createDigitalDocument(post, stock, acceptUser, DigitalDocumentType.DIGITAL_PROXY);
                itUtil.createDigitalDocumentItemList(digitalDocument);
                post.setDigitalDocument(digitalDocument);
                itUtil.updatePost(post);
                itUtil.createStockReferenceDate(stock.getCode(), digitalDocument.getStockReferenceDate());
                itUtil.createUserHoldingStockOnReferenceDate(stock.getCode(), postViewer.getId(), digitalDocument.getStockReferenceDate());
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                final PostDataResponse result = getResponse(callApi(status().isOk()));
                assertResponse(result);
            }

        }

        @Nested
        class AndUserDoesNotHaveTheStockNow {

            @BeforeEach
            void setUp() {
                stock = itUtil.createStock();

                postWriter = itUtil.createUserWithAddress();
                itUtil.createUserHoldingStock(stock.getCode(), postWriter);

                postViewer = itUtil.createUserWithAddress();
                jwt = itUtil.createJwt(postViewer.getId());

                acceptUser = itUtil.createUserWithAddress();
                board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.DIGITAL_DELEGATION);
                itUtil.createSolidarity(stock.getCode());
                itUtil.createStockAcceptorUser(stock.getCode(), acceptUser);
            }

            @Nested
            class ButUserHadTheStockOnReferenceDateOfDigitalDocumentBefore {
                @BeforeEach
                void setUp() {
                    final LocalDate referenceDate = LocalDate.now().minusDays(someIntegerBetween(10, 50));
                    final LocalDateTime targetStartDate = someLocalDateTimeInTheFuture();

                    post = itUtil.createPost(board, postWriter.getId());
                    digitalDocument = itUtil.createDigitalDocument(
                        post,
                        stock,
                        acceptUser,
                        DigitalDocumentType.DIGITAL_PROXY,
                        targetStartDate,
                        targetStartDate.plusDays(someIntegerBetween(1, 10)),
                        referenceDate
                    );
                    itUtil.createDigitalDocumentItemList(digitalDocument);
                    post.setDigitalDocument(digitalDocument);
                    itUtil.updatePost(post);
                    itUtil.createUserHoldingStockOnReferenceDate(stock.getCode(), postViewer.getId(), referenceDate);
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnSuccess() throws Exception {
                    final PostDataResponse result = getResponse(callApi(status().isOk()));
                    assertResponse(result);
                }
            }
        }

        @Nested
        class WhenNotFoundAcceptUser {

            @BeforeEach
            void setUp() {
                acceptUser = null; // acceptUser가 없는 경우

                post = itUtil.createPost(board, postWriter.getId());
                digitalDocument = itUtil.createDigitalDocument(post, stock, acceptUser, DigitalDocumentType.DIGITAL_PROXY);
                itUtil.createDigitalDocumentItemList(digitalDocument);
                post.setDigitalDocument(digitalDocument);
                itUtil.updatePost(post);
                itUtil.createStockReferenceDate(stock.getCode(), digitalDocument.getStockReferenceDate());
                itUtil.createUserHoldingStockOnReferenceDate(stock.getCode(), postViewer.getId(), digitalDocument.getStockReferenceDate());
            }

            @Test
            void shouldReturnException() throws Exception {
                final MvcResult response = callApi(status().isInternalServerError());

                itUtil.assertErrorResponse(response, 500, "전자문서 수임인 정보가 없습니다.");
            }
        }

        @Nested
        class WhenAcceptUserIsCorporateUser {

            @BeforeEach
            void setUp() {
                corporateNo = someString(10);
                acceptUser = itUtil.createUser();
                itUtil.createStockAcceptorUser(stock.getCode(), acceptUser);
                final CorporateUser corporateUser = itUtil.createCorporateUser(corporateNo, someString(10));
                corporateUser.setUserId(acceptUser.getId());
                itUtil.updateCorporateUser(corporateUser);

                post = itUtil.createPost(board, postWriter.getId());
                digitalDocument = itUtil.createDigitalDocument(post, stock, acceptUser, DigitalDocumentType.DIGITAL_PROXY);
                itUtil.createDigitalDocumentItemList(digitalDocument);
                post.setDigitalDocument(digitalDocument);
                itUtil.updatePost(post);
                itUtil.createStockReferenceDate(stock.getCode(), digitalDocument.getStockReferenceDate());
                itUtil.createUserHoldingStockOnReferenceDate(stock.getCode(), postViewer.getId(), digitalDocument.getStockReferenceDate());
            }

            @Test
            void shouldReturnException() throws Exception {
                final PostDataResponse result = getResponse(callApi(status().isOk()));
                assertResponse(result);
            }
        }

        private void assertResponse(PostDataResponse result) {
            PostResponse postResponse = result.getData();

            final UserDigitalDocumentResponse actualDigitalDocument = postResponse.getDigitalDocument();

            assertThat(postResponse.getPoll(), is(nullValue()));
            assertThat(postResponse.getDigitalProxy(), is(nullValue()));
            assertThat(actualDigitalDocument.getId(), is(digitalDocument.getId()));

            assertAcceptUser(actualDigitalDocument);
            assertDigitalDocumentDetails(postResponse);

            final List<DigitalDocumentItemResponse> actualItems = actualDigitalDocument.getItems();
            final List<DigitalDocumentItemResponse> expectedItems = getExpectedItemTree();
            assertThat(actualItems.size(), is(expectedItems.size()));

            final DigitalDocumentItemResponse actualParentItem = actualItems.get(0);
            final DigitalDocumentItemResponse expectedParentItem = expectedItems.get(0);
            assertParent(actualParentItem, expectedParentItem);
            assertChild(actualParentItem.getChildItems().get(0), expectedParentItem.getChildItems().get(0));
            assertChild(actualParentItem.getChildItems().get(1), expectedParentItem.getChildItems().get(1));
        }

        private void assertChild(DigitalDocumentItemResponse actualItem, DigitalDocumentItemResponse expectedItem) {
            assertThat(actualItem.getTitle(), is(expectedItem.getTitle()));
            assertThat(actualItem.getContent(), is(expectedItem.getContent()));
            assertThat(actualItem.getDefaultSelectValue(), is(expectedItem.getDefaultSelectValue()));
            assertThat(actualItem.getLeaderDescription(), is(expectedItem.getLeaderDescription()));
        }

        private void assertParent(DigitalDocumentItemResponse actualParentItem, DigitalDocumentItemResponse expectedParentItem) {
            assertThat(actualParentItem.getTitle(), is(expectedParentItem.getTitle()));
            assertThat(actualParentItem.getContent(), is(expectedParentItem.getContent()));
            assertThat(actualParentItem.getDefaultSelectValue(), is(expectedParentItem.getDefaultSelectValue()));
            assertThat(actualParentItem.getLeaderDescription(), is(expectedParentItem.getLeaderDescription()));
            assertThat(actualParentItem.getChildItems().size(), is(expectedParentItem.getChildItems().size()));
        }
    }

    @Nested
    class WhenGetJointOwnershipDigitalDocument {

        @Nested
        class Success {
            @BeforeEach
            void setUp() {
                post = itUtil.createPost(board, postWriter.getId());
                digitalDocument = itUtil.createDigitalDocument(
                    post, stock, acceptUser, DigitalDocumentType.JOINT_OWNERSHIP_DOCUMENT
                );
                post.setDigitalDocument(digitalDocument);
                itUtil.updatePost(post);
                itUtil.createStockReferenceDate(stock.getCode(), digitalDocument.getStockReferenceDate());
                itUtil.createUserHoldingStock(stock.getCode(), postViewer);
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                final PostDataResponse result = getResponse(callApi(status().isOk()));
                assertResponse(result);
            }

            private void assertResponse(PostDataResponse result) {
                PostResponse postResponse = result.getData();

                assertThat(postResponse.getPoll(), is(nullValue()));
                assertThat(postResponse.getDigitalProxy(), is(nullValue()));
                assertThat(postResponse.getDigitalDocument().getId(), is(digitalDocument.getId()));

                assertDigitalDocumentDetails(postResponse);
            }
        }

        @Nested
        class WhenNotFoundAcceptUser {

            @BeforeEach
            void setUp() {
                acceptUser = null; // acceptUser가 없는 경우

                post = itUtil.createPost(board, postWriter.getId());
                digitalDocument = itUtil.createDigitalDocument(
                    post, stock, acceptUser, DigitalDocumentType.JOINT_OWNERSHIP_DOCUMENT
                );
                post.setDigitalDocument(digitalDocument);
                itUtil.updatePost(post);
                itUtil.createStockReferenceDate(stock.getCode(), digitalDocument.getStockReferenceDate());
                itUtil.createUserHoldingStock(stock.getCode(), postViewer);
            }

            @Test
            void shouldReturnException() throws Exception {
                final MvcResult response = callApi(status().isInternalServerError());

                itUtil.assertErrorResponse(response, 500, "전자문서 수임인 정보가 없습니다.");
            }
        }

        @Nested
        class WhenNotFoundUserHoldingStock {
            @BeforeEach
            void setUp() {
                post = itUtil.createPost(board, postWriter.getId());
                digitalDocument = itUtil.createDigitalDocument(
                    post, stock, acceptUser, DigitalDocumentType.JOINT_OWNERSHIP_DOCUMENT
                );
                post.setDigitalDocument(digitalDocument);
                itUtil.updatePost(post);
                itUtil.createStockReferenceDate(stock.getCode(), digitalDocument.getStockReferenceDate());
            }

            @Test
            void shouldReturnException() throws Exception {
                final MvcResult response = callApi(status().isForbidden());

                itUtil.assertErrorResponse(response, 403, "보유하고 있지 않은 주식입니다.");
            }
        }
    }

    @Nested
    class WhenGetEtcDigitalDocument {

        @BeforeEach
        void setUp() {
            post = itUtil.createPost(board, postWriter.getId());
            digitalDocument = itUtil.createDigitalDocument(post, stock, acceptUser, DigitalDocumentType.ETC_DOCUMENT);
            post.setDigitalDocument(digitalDocument);
            itUtil.updatePost(post);

            itUtil.createUserHoldingStock(stock.getCode(), postViewer);
        }

        @DisplayName("Should return 200 response code when call " + TARGET_API)
        @Test
        void shouldReturnSuccess() throws Exception {
            final PostDataResponse result = getResponse(callApi(status().isOk()));
            assertResponse(result);
        }

        private void assertResponse(PostDataResponse result) {
            PostResponse postResponse = result.getData();

            assertThat(postResponse.getPoll(), is(nullValue()));
            assertThat(postResponse.getDigitalProxy(), is(nullValue()));
            assertThat(postResponse.getDigitalDocument().getId(), is(digitalDocument.getId()));

            assertDigitalDocumentDetails(postResponse);
        }
    }

    @Nested
    class WhenGetHolderListReadAndCopyDigitalDocument {

        private final BoardCategory boardCategory = BoardCategory.HOLDER_LIST_READ_AND_COPY;
        private FileContent fileContent;
        private DigitalDocumentUser digitalDocumentUser;
        private PostUserProfile postUserProfile;

        @BeforeEach
        void setUp() {
            board = itUtil.createBoard(stock, BoardGroup.ANALYSIS, boardCategory);
        }

        @Nested
        class GetItSuccessfully {

            @BeforeEach
            void setUp() {
                itUtil.createUserHoldingStock(stock.getCode(), postViewer);
            }

            @Nested
            class AndPostViewerIsDigitalDocumentUser {
                @BeforeEach
                void setUp() {
                    post = itUtil.createPost(board, postViewer.getId());
                    postUserProfile = post.getPostUserProfile();

                    final PostImage postImage = itUtil.createPostImage(post.getId());
                    fileContent = itUtil.getFileContent(postImage.getImageId());

                    digitalDocument = itUtil.createDigitalDocument(post, stock, null, DigitalDocumentType.HOLDER_LIST_READ_AND_COPY_DOCUMENT);
                    post.setDigitalDocument(digitalDocument);
                    itUtil.updatePost(post);

                    digitalDocumentUser = itUtil.createDigitalDocumentUser(digitalDocument, postViewer, stock, someString(10));
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnSuccess() throws Exception {
                    final PostDataResponse result = getResponse(callApi(status().isOk()));
                    assertResponse(result);
                }
            }

            @Nested
            class AndPostViewerIsNotDigitalDocumentUser {
                @BeforeEach
                void setUp() {
                    post = itUtil.createPost(board, postWriter.getId());
                    postUserProfile = post.getPostUserProfile();

                    final PostImage postImage = itUtil.createPostImage(post.getId());
                    fileContent = itUtil.getFileContent(postImage.getImageId());

                    digitalDocument = itUtil.createDigitalDocument(post, stock, null, DigitalDocumentType.HOLDER_LIST_READ_AND_COPY_DOCUMENT);
                    post.setDigitalDocument(digitalDocument);
                    itUtil.updatePost(post);

                    digitalDocumentUser = itUtil.createDigitalDocumentUser(digitalDocument, postWriter, stock, someString(10));
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnSuccess() throws Exception {
                    final PostDataResponse result = getResponse(callApi(status().isOk()));
                    assertResponse(result);
                }
            }

            @Nested
            class AndPostViewerIsAdminAndNotDigitalDocumentUser {
                @BeforeEach
                void setUp() {
                    post = itUtil.createPost(board, postWriter.getId());
                    postUserProfile = post.getPostUserProfile();

                    final PostImage postImage = itUtil.createPostImage(post.getId());
                    fileContent = itUtil.getFileContent(postImage.getImageId());

                    digitalDocument = itUtil.createDigitalDocument(post, stock, null, DigitalDocumentType.HOLDER_LIST_READ_AND_COPY_DOCUMENT);
                    post.setDigitalDocument(digitalDocument);
                    itUtil.updatePost(post);

                    digitalDocumentUser = itUtil.createDigitalDocumentUser(digitalDocument, postWriter, stock, someString(10));

                    postViewer = itUtil.createAdminUser();
                    itUtil.createUserHoldingStock(stock.getCode(), postViewer);
                    jwt = itUtil.createJwt(postViewer.getId());
                }

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnSuccess() throws Exception {
                    final PostDataResponse result = getResponse(callApi(status().isOk()));
                    assertResponse(result);
                }
            }
        }

        @Nested
        class FailToGetIt {

            @BeforeEach
            void setUp() {
                post = itUtil.createPost(board, postWriter.getId());
                postUserProfile = post.getPostUserProfile();

                final PostImage postImage = itUtil.createPostImage(post.getId());
                fileContent = itUtil.getFileContent(postImage.getImageId());

                digitalDocument = itUtil.createDigitalDocument(post, stock, null, DigitalDocumentType.HOLDER_LIST_READ_AND_COPY_DOCUMENT);
                post.setDigitalDocument(digitalDocument);
                itUtil.updatePost(post);
            }

            @Nested
            class AndPostViewerHoldingStockNotFound {

                @Test
                void shouldReturnException() throws Exception {
                    final MvcResult response = callApi(status().isForbidden());

                    itUtil.assertErrorResponse(response, 403, "보유하고 있지 않은 주식입니다.");
                }
            }

            @Nested
            class AndPdfPathNotFound {

                @BeforeEach
                void setUp() {
                    itUtil.createUserHoldingStock(stock.getCode(), postViewer);
                    digitalDocumentUser = itUtil.createDigitalDocumentUser(digitalDocument, postViewer, stock);
                }

                @Test
                void shouldReturnException() throws Exception {
                    final MvcResult response = callApi(status().isBadRequest());

                    itUtil.assertErrorResponse(response, 400, "주주명부 열람/등사 청구 파일의 경로를 알 수 없습니다. 고객센터에 문의해주세요.");
                }
            }
        }

        private void assertResponse(PostDataResponse result) {
            PostResponse postResponse = result.getData();

            assertThat(postResponse.getPoll(), is(nullValue()));
            assertThat(postResponse.getDigitalProxy(), is(nullValue()));
            assertThat(postResponse.getDigitalDocument(), is(nullValue()));

            final HolderListReadAndCopyDigitalDocumentResponse holderListReadAndCopyDigitalDocumentResponse = postResponse.getHolderListReadAndCopyDigitalDocument();
            if (postViewer.getId() != digitalDocumentUser.getUserId()) {
                assertThat(holderListReadAndCopyDigitalDocumentResponse, is(nullValue()));
            } else {
                final String fileName = FilenameUtils.getName(digitalDocumentUser.getPdfPath());
                assertThat(holderListReadAndCopyDigitalDocumentResponse.getDigitalDocumentId(), is(digitalDocument.getId()));
                assertThat(holderListReadAndCopyDigitalDocumentResponse.getFileName(), is(fileName));
                assertThat(holderListReadAndCopyDigitalDocumentResponse.getUserId(), is(digitalDocumentUser.getUserId()));
            }

            final UserProfileResponse userProfileResponse = postResponse.getUserProfile();
            assertThat(userProfileResponse.getNickname(), is(postUserProfile.getNickname()));
            assertThat(userProfileResponse.getProfileImageUrl(), is(postUserProfile.getProfileImageUrl()));
            assertThat(userProfileResponse.getIndividualStockCountLabel(), is(postUserProfile.getIndividualStockCountLabel()));
            assertThat(userProfileResponse.getTotalAssetLabel(), is(postUserProfile.getTotalAssetLabel()));

            assertThat(postResponse.getId(), is(post.getId()));
            assertThat(postResponse.getTitle(), is(post.getTitle()));
            assertThat(postResponse.getContent(), is(post.getContent()));
            assertThat(postResponse.getStatus(), is(post.getStatus()));
            assertThat(postResponse.getIsActive(), is(post.isActive()));
            assertThat(postResponse.getIsExclusiveToHolders(), is(post.getIsExclusiveToHolders()));
            assertThat(postResponse.getBoardGroup(), is(board.getGroup().name()));

            final BoardCategoryResponse boardCategoryResponse = postResponse.getBoardCategory();
            assertThat(boardCategoryResponse.getName(), is(boardCategory.name()));
            assertThat(boardCategoryResponse.getDisplayName(), is(boardCategory.getDisplayName()));
            assertThat(boardCategoryResponse.getIsExclusiveToHolders(), is(false));
            assertThat(boardCategoryResponse.getIsGroup(), is(false));

            final StockResponse stockResponse = postResponse.getStock();
            assertThat(stockResponse.getCode(), is(stock.getCode()));
            assertThat(stockResponse.getName(), is(stock.getName()));

            List<SimpleImageResponse> postImageList = postResponse.getPostImageList();
            assertThat(postImageList.size(), is(1));
            assertThat(postImageList.get(0).getImageId(), is(fileContent.getId()));
            assertThat(postImageList.get(0).getImageUrl(), is(ImageUtil.getFullPath(s3Environment.getBaseUrl(), fileContent.getFilename())));
        }
    }

    private void assertDigitalDocumentDetails(PostResponse postResponse) {
        assertThat(postResponse.getDigitalDocument().getStock().getCode(), is(digitalDocument.getStockCode()));
        assertThat(
            postResponse.getDigitalDocument().getStock().getReferenceDate(),
            is(digitalDocument.getStockReferenceDate())
        );
        final DigitalDocumentUserResponse currentUserResponse = postResponse.getDigitalDocument().getUser();
        assertThat(currentUserResponse.getId(), is(postViewer.getId()));

        if (digitalDocument.getType() != DigitalDocumentType.ETC_DOCUMENT) {
            assertAcceptUser(postResponse.getDigitalDocument());
        } else {
            assertThat(postResponse.getDigitalDocument().getAcceptUser(), nullValue());
        }

        if (digitalDocument.getType() == DigitalDocumentType.JOINT_OWNERSHIP_DOCUMENT) {
            assertThat(currentUserResponse.getAddress(), is(postViewer.getAddress()));
            assertThat(currentUserResponse.getAddress(), notNullValue());
            assertThat(currentUserResponse.getAddressDetail(), is(postViewer.getAddressDetail()));
            assertThat(currentUserResponse.getAddressDetail(), notNullValue());
            assertThat(currentUserResponse.getZipcode(), is(postViewer.getZipcode()));
            assertThat(currentUserResponse.getZipcode(), notNullValue());
        } else {
            assertThat(currentUserResponse.getAddress(), nullValue());
            assertThat(currentUserResponse.getAddressDetail(), nullValue());
            assertThat(currentUserResponse.getZipcode(), nullValue());
        }

        assertThat(postResponse.getDigitalDocument().getJoinUserCount(), is(digitalDocument.getJoinUserCount()));
        assertThat(postResponse.getDigitalDocument().getJoinStockSum(), is(digitalDocument.getJoinStockSum()));
        assertTime(postResponse.getDigitalDocument().getTargetStartDate(), digitalDocument.getTargetStartDate());
        assertTime(postResponse.getDigitalDocument().getTargetEndDate(), digitalDocument.getTargetEndDate());
        assertThat(postResponse.getDigitalDocument().getAnswerStatus(), is(nullValue()));
        assertThat(postResponse.getTitle(), is(post.getTitle()));
        assertThat(postResponse.getContent(), is(post.getContent()));
        assertThat(postResponse.getBoardGroup(), is(post.getBoard().getGroup().name()));
        assertThat(postResponse.getBoardCategory().getName(), is(post.getBoard().getCategory().name()));
    }

    private List<DigitalDocumentItemResponse> getExpectedItemTree() {
        return digitalDocumentItemTreeGenerator.buildTree(
            itUtil.findDigitalDocumentItemsByDigitalDocumentId(digitalDocument.getId()));
    }

    private void assertAcceptUser(UserDigitalDocumentResponse actualDigitalDocument) {
        final DigitalDocumentAcceptUserResponse actualAcceptUser = actualDigitalDocument.getAcceptUser();

        assertThat(actualAcceptUser.getId(), is(acceptUser.getId()));
        assertThat(actualAcceptUser.getName(), is(acceptUser.getName()));
        assertThat(actualAcceptUser.getPhoneNumber(), is(itUtil.decrypt(acceptUser.getHashedPhoneNumber())));
        assertThat(actualAcceptUser.getBirthDate(), is(DateTimeConverter.convert(acceptUser.getBirthDate())));

        assertThat(actualAcceptUser.getCorporateNo(), is(corporateNo));
    }
}
