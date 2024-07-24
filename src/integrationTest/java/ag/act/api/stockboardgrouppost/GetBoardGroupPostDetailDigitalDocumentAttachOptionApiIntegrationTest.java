package ag.act.api.stockboardgrouppost;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.StockAcceptorUser;
import ag.act.entity.StockAcceptorUserHistory;
import ag.act.entity.User;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.DigitalDocumentType;
import ag.act.enums.digitaldocument.AttachOptionType;
import ag.act.itutil.ITUtil;
import ag.act.model.DigitalDocumentItemResponse;
import ag.act.model.DigitalDocumentUserResponse;
import ag.act.model.JsonAttachOption;
import ag.act.model.PostResponse;
import ag.act.model.Status;
import ag.act.model.UserDigitalDocumentResponse;
import ag.act.util.DigitalDocumentItemTreeGenerator;
import ag.act.util.ObjectMapperUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

import static ag.act.TestUtil.assertTime;
import static ag.act.itutil.authentication.AuthenticationTestUtil.jwt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomThings.someThing;

class GetBoardGroupPostDetailDigitalDocumentAttachOptionApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts/{postId}";

    @Autowired
    private ITUtil itUtil;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapperUtil objectMapperUtil;
    @Autowired
    private DigitalDocumentItemTreeGenerator digitalDocumentItemTreeGenerator;

    private String jwt;
    private Stock stock;
    private Board board;
    private Post post;
    private User user;
    private User acceptUser;
    private DigitalDocument digitalDocument;

    @BeforeEach
    void setUp() {
        itUtil.init();
        user = itUtil.createUserWithAddress();
        jwt = itUtil.createJwt(user.getId());
        stock = itUtil.createStock();

        acceptUser = itUtil.createUserWithAddress();
        board = itUtil.createBoard(stock, BoardGroup.ACTION, BoardCategory.DIGITAL_DELEGATION);
        itUtil.createSolidarity(stock.getCode());
        itUtil.createStockAcceptorUser(stock.getCode(), acceptUser);
        itUtil.createUserHoldingStock(stock.getCode(), user);
    }

    private MvcResult callApi() throws Exception {
        return mockMvc
            .perform(
                get(TARGET_API, stock.getCode(), board.getGroup(), post.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .headers(headers(jwt(jwt)))
            )
            .andExpect(status().isOk())
            .andReturn();
    }

    private ag.act.model.PostDataResponse getResponse(MvcResult response) throws JsonProcessingException, UnsupportedEncodingException {
        return objectMapperUtil.toResponse(
            response.getResponse().getContentAsString(),
            ag.act.model.PostDataResponse.class
        );
    }

    private void assertDigitalDocumentDetails(PostResponse postResponse) {
        assertThat(postResponse.getDigitalDocument().getStock().getCode(), is(digitalDocument.getStockCode()));
        assertThat(
            postResponse.getDigitalDocument().getStock().getReferenceDate(),
            is(digitalDocument.getStockReferenceDate())
        );
        final DigitalDocumentUserResponse currentUserResponse = postResponse.getDigitalDocument().getUser();
        assertThat(currentUserResponse.getId(), is(user.getId()));
        if (digitalDocument.getType() != DigitalDocumentType.ETC_DOCUMENT) {
            assertThat(postResponse.getDigitalDocument().getAcceptUser().getId(), is(acceptUser.getId()));
        }

        if (digitalDocument.getType() == DigitalDocumentType.JOINT_OWNERSHIP_DOCUMENT) {
            assertThat(currentUserResponse.getAddress(), is(user.getAddress()));
            assertThat(currentUserResponse.getAddress(), notNullValue());
            assertThat(currentUserResponse.getAddressDetail(), is(user.getAddressDetail()));
            assertThat(currentUserResponse.getAddressDetail(), notNullValue());
            assertThat(currentUserResponse.getZipcode(), is(user.getZipcode()));
            assertThat(currentUserResponse.getZipcode(), notNullValue());
        } else {
            assertThat(currentUserResponse.getAddress(), nullValue());
            assertThat(currentUserResponse.getAddressDetail(), nullValue());
            assertThat(currentUserResponse.getZipcode(), nullValue());
        }

        assertThat(postResponse.getDigitalDocument().getJoinUserCount(), is(digitalDocument.getJoinUserCount()));
        assertTime(postResponse.getDigitalDocument().getTargetStartDate(), digitalDocument.getTargetStartDate());
        assertTime(postResponse.getDigitalDocument().getTargetEndDate(), digitalDocument.getTargetEndDate());
        assertThat(postResponse.getDigitalDocument().getAnswerStatus(), is(nullValue()));
        assertThat(postResponse.getTitle(), is(post.getTitle()));
        assertThat(postResponse.getContent(), is(post.getContent()));
        assertThat(postResponse.getBoardGroup(), is(post.getBoard().getGroup().name()));
        assertThat(postResponse.getBoardCategory().getName(), is(post.getBoard().getCategory().name()));
    }

    @Nested
    class WhenGetDigitalDocument {

        private JsonAttachOption jsonAttachOption;

        @BeforeEach
        void setUp() {
            post = itUtil.createPost(board, user.getId());
            jsonAttachOption = new JsonAttachOption()
                .idCardImage(AttachOptionType.REQUIRED.name())
                .signImage(AttachOptionType.REQUIRED.name())
                .bankAccountImage(AttachOptionType.NONE.name())
                .hectoEncryptedBankAccountPdf(
                    someThing(null,
                        AttachOptionType.REQUIRED.name(),
                        AttachOptionType.OPTIONAL.name(),
                        AttachOptionType.NONE.name()
                    )
                );

            digitalDocument = itUtil.createDigitalDocument(
                post, stock, acceptUser, DigitalDocumentType.DIGITAL_PROXY,
                jsonAttachOption
            );
            itUtil.createDigitalDocumentItemList(digitalDocument);
            post.setDigitalDocument(digitalDocument);
            itUtil.updatePost(post);
            itUtil.createStockReferenceDate(stock.getCode(), digitalDocument.getStockReferenceDate());
            itUtil.createUserHoldingStockOnReferenceDate(stock.getCode(), user.getId(), digitalDocument.getStockReferenceDate());
            itUtil.createMyDataSummary(user, stock.getCode(), digitalDocument.getStockReferenceDate());
        }

        @Nested
        class WhenHasStockAcceptUser {

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                final ag.act.model.PostDataResponse result = getResponse(callApi());
                final PostResponse data = result.getData();
                assertResponse(data, data.getDigitalDocument());
            }
        }

        @Nested
        class WhenDeleteStockAcceptUser {

            @BeforeEach
            void setUp() {
                itUtil.deleteStockAcceptorUser(stock.getCode(), acceptUser.getId());
                final StockAcceptorUserHistory stockAcceptorUserHistory = itUtil.createStockAcceptorUserHistory(stock.getCode(), acceptUser);
                stockAcceptorUserHistory.setStatus(Status.DELETED_BY_ADMIN);
                itUtil.updateStockAcceptorUserHistory(stockAcceptorUserHistory);
            }

            @DisplayName("Should return 200 response code when call " + TARGET_API)
            @Test
            void shouldReturnSuccess() throws Exception {
                final ag.act.model.PostDataResponse result = getResponse(callApi());
                final PostResponse data = result.getData();
                final UserDigitalDocumentResponse digitalDocumentResponse = data.getDigitalDocument();
                assertResponse(data, digitalDocumentResponse);

                final Optional<StockAcceptorUser> databaseStockAcceptUser = itUtil.findStockAcceptorUser(stock.getCode(), acceptUser.getId());
                assertThat(databaseStockAcceptUser.isPresent(), is(false));
            }
        }

        private void assertResponse(PostResponse postResponse, UserDigitalDocumentResponse actualDigitalDocument) {
            assertThat(postResponse.getPoll(), is(nullValue()));
            assertThat(postResponse.getDigitalProxy(), is(nullValue()));
            assertThat(actualDigitalDocument.getId(), is(digitalDocument.getId()));

            assertDigitalDocumentDetails(postResponse);

            final List<DigitalDocumentItemResponse> actualItems = actualDigitalDocument.getItems();
            final List<DigitalDocumentItemResponse> expectedItems = digitalDocumentItemTreeGenerator.buildTree(
                itUtil.findDigitalDocumentItemsByDigitalDocumentId(digitalDocument.getId()));
            assertThat(actualItems.size(), is(expectedItems.size()));

            final DigitalDocumentItemResponse actualParentItem = actualItems.get(0);
            final DigitalDocumentItemResponse expectedParentItem = expectedItems.get(0);
            assertParent(actualParentItem, expectedParentItem);
            assertChild(actualParentItem.getChildItems().get(0), expectedParentItem.getChildItems().get(0));
            assertChild(actualParentItem.getChildItems().get(1), expectedParentItem.getChildItems().get(1));

            assertThat(
                actualDigitalDocument.getAttachOptions().getSignImage(),
                is(digitalDocument.getJsonAttachOption().getSignImage())
            );
            assertThat(
                actualDigitalDocument.getAttachOptions().getIdCardImage(),
                is(digitalDocument.getJsonAttachOption().getIdCardImage())
            );
            assertThat(
                actualDigitalDocument.getAttachOptions().getBankAccountImage(),
                is(digitalDocument.getJsonAttachOption().getBankAccountImage())
            );
            assertThat(
                actualDigitalDocument.getAttachOptions().getHectoEncryptedBankAccountPdf(),
                is(jsonAttachOption.getHectoEncryptedBankAccountPdf() == null
                    ? AttachOptionType.NONE.name() :
                    jsonAttachOption.getHectoEncryptedBankAccountPdf()
                )
            );

            assertThat(actualDigitalDocument.getAcceptUser().getId(), is(acceptUser.getId()));
            assertThat(actualDigitalDocument.getAcceptUser().getName(), is(acceptUser.getName()));
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
}
