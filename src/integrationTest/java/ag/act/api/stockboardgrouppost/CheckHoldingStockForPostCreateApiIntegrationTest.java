package ag.act.api.stockboardgrouppost;


import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Board;
import ag.act.entity.Post;
import ag.act.entity.Stock;
import ag.act.entity.StockReferenceDate;
import ag.act.entity.User;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.DigitalAnswerType;
import ag.act.enums.DigitalDocumentType;
import ag.act.enums.DigitalDocumentVersion;
import ag.act.enums.RoleType;
import ag.act.enums.SelectionOption;
import ag.act.enums.VoteType;
import ag.act.enums.digitaldocument.AttachOptionType;
import ag.act.model.CreateDigitalDocumentRequest;
import ag.act.model.CreatePollRequest;
import ag.act.model.CreatePostRequest;
import ag.act.model.DigitalDocumentItemRequest;
import ag.act.model.PostDetailsDataResponse;
import ag.act.model.PostDetailsResponse;
import ag.act.model.UserProfileResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static ag.act.TestUtil.someCompanyRegistrationNumber;
import static ag.act.TestUtil.someLocalDateTimeInTheFutureDaysBetween;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;

class CheckHoldingStockForPostCreateApiIntegrationTest extends AbstractCommonIntegrationTest {

    private static final String TARGET_API = "/api/stocks/{stockCode}/board-groups/{boardGroupName}/posts";

    private CreatePostRequest request;
    private String jwt;
    private Stock stock;
    private Board board;
    private User user;

    @BeforeEach
    void setUp() {
        stock = itUtil.createStock();
    }

    private CreatePostRequest genRequest() {
        CreatePostRequest request = new CreatePostRequest();
        request.setBoardCategory(board.getCategory().name());
        request.setTitle(someString(10));
        request.setContent(someAlphanumericString(300));
        request.setIsActive(Boolean.TRUE);
        request.setIsAnonymous(Boolean.FALSE);
        request.setIsExclusiveToHolders(Boolean.FALSE);

        return request;
    }

    private MvcResult callApiWithJwt(ResultMatcher matcher, String jwt, CreatePostRequest request) throws Exception {
        return mockMvc
            .perform(
                post(TARGET_API, stock.getCode(), board.getGroup())
                    .content(objectMapperUtil.toRequestBody(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(matcher)
            .andReturn();
    }

    private void assertResponse(PostDetailsDataResponse result) {
        final PostDetailsResponse postDetailsResponse = result.getData();

        assertThat(postDetailsResponse.getId(), is(notNullValue()));
        assertThat(postDetailsResponse.getBoardId(), is(board.getId()));
        assertThat(postDetailsResponse.getTitle(), is(request.getTitle()));
        assertThat(postDetailsResponse.getContent(), is(request.getContent()));

        final UserProfileResponse createResponseUserProfile = postDetailsResponse.getUserProfile();
        assertThat(createResponseUserProfile.getNickname(), is(user.getNickname()));

        final Long databasePostId = postDetailsResponse.getId();
        final Post databasePost = itUtil.findPost(databasePostId).orElseThrow();
        assertThat(databasePost.getIsExclusiveToHolders(), is(request.getIsExclusiveToHolders()));
    }

    private void createActionRequest(BoardCategory boardCategory) {
        final Instant endDate = Instant.now().plus(Period.ofDays(5));
        final Instant startDate = endDate.minus(Period.ofDays(1));

        final User acceptUser = itUtil.createUser();
        itUtil.createStockAcceptorUser(stock.getCode(), acceptUser);

        final LocalDate referenceDate = someLocalDateTimeInTheFutureDaysBetween(5, 10).toLocalDate();
        final StockReferenceDate stockReferenceDate = itUtil.createStockReferenceDate(stock.getCode(), referenceDate);

        if (boardCategory == BoardCategory.SURVEYS) {
            request.setPolls(List.of(new CreatePollRequest()
                .title(someString(10))
                .targetStartDate(startDate.truncatedTo(ChronoUnit.SECONDS))
                .targetEndDate(endDate.truncatedTo(ChronoUnit.SECONDS))
                .selectionOption(someEnum(SelectionOption.class).name())
                .voteType(someEnum(VoteType.class).name())
                .pollItems(List.of(
                    new ag.act.model.CreatePollItemRequest().text("FIRST_" + someString(10)),
                    new ag.act.model.CreatePollItemRequest().text("SECOND_" + someString(10))
                ))));
        } else if (boardCategory == BoardCategory.DIGITAL_DELEGATION) {
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
                    .signImage(AttachOptionType.OPTIONAL.name())
                    .idCardImage(AttachOptionType.OPTIONAL.name())
                    .bankAccountImage(AttachOptionType.OPTIONAL.name())
                    .hectoEncryptedBankAccountPdf(AttachOptionType.OPTIONAL.name())
                )
                .childItems(getItemList())
            );
        } else if (boardCategory == BoardCategory.CO_HOLDING_ARRANGEMENTS) {
            request.digitalDocument(new CreateDigitalDocumentRequest()
                .type(DigitalDocumentType.JOINT_OWNERSHIP_DOCUMENT.name())
                .version(DigitalDocumentVersion.V1.name())
                .companyName(someString(10))
                .companyRegistrationNumber(someCompanyRegistrationNumber())
                .acceptUserId(acceptUser.getId())
                .targetStartDate(startDate)
                .targetEndDate(endDate)
                .title(someString(10))
                .content(someAlphanumericString(50))
                .attachOptions(new ag.act.model.JsonAttachOption()
                    .signImage(AttachOptionType.REQUIRED.name())
                    .idCardImage(someEnum(AttachOptionType.class).name())
                    .bankAccountImage(someEnum(AttachOptionType.class).name())
                    .hectoEncryptedBankAccountPdf(someEnum(AttachOptionType.class).name())
                )
            );
        } else if (boardCategory == BoardCategory.ETC) {
            request.digitalDocument(new CreateDigitalDocumentRequest()
                .type(DigitalDocumentType.ETC_DOCUMENT.name())
                .version(DigitalDocumentVersion.V1.name())
                .companyName(someString(10))
                .acceptUserId(acceptUser.getId())
                .targetStartDate(startDate)
                .targetEndDate(endDate)
                .title(someString(10))
                .content("<p>%s</p>".formatted(someAlphanumericString(50)))
                .attachOptions(new ag.act.model.JsonAttachOption()
                    .signImage(AttachOptionType.OPTIONAL.name())
                    .idCardImage(AttachOptionType.OPTIONAL.name())
                    .bankAccountImage(AttachOptionType.OPTIONAL.name())
                    .hectoEncryptedBankAccountPdf(AttachOptionType.OPTIONAL.name())
                )
            );
        }
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

    @Nested
    class WhenCreatePost {

        @Nested
        class WhenNormalUser {

            @BeforeEach
            void setUp() {
                user = itUtil.createUser();
                jwt = itUtil.createJwt(user.getId());
            }

            @Nested
            class WhenBoardDebate {
                @BeforeEach
                void setUp() {
                    final BoardGroup boardGroup = BoardGroup.DEBATE;
                    board = itUtil.createBoard(stock, boardGroup, boardGroup.getCategories().get(0));
                    request = genRequest();
                }

                @Nested
                class WhenErrorDoesNotHaveUserHoldingStock {

                    @DisplayName("Should return 403 response code when call " + TARGET_API)
                    @Test
                    void shouldReturnError() throws Exception {
                        MvcResult response = callApiWithJwt(status().isForbidden(), jwt, request);

                        itUtil.assertErrorResponse(response, 403, "보유하고 있지 않은 주식입니다.");
                    }
                }

                @Nested
                class WhenSuccessHasUserHoldingStock {
                    @BeforeEach
                    void setUp() {
                        itUtil.createUserHoldingStock(stock.getCode(), user);
                    }

                    @DisplayName("Should return 200 response code when call " + TARGET_API)
                    @Test
                    void shouldReturnSuccess() throws Exception {
                        MvcResult response = callApiWithJwt(status().isOk(), jwt, request);

                        final PostDetailsDataResponse result = objectMapperUtil.toResponse(
                            response.getResponse().getContentAsString(),
                            PostDetailsDataResponse.class
                        );

                        assertResponse(result);
                    }
                }
            }

            @Nested
            class WhenBoardGlobalCommunity {
                @BeforeEach
                void setUp() {
                    final BoardGroup boardGroup = BoardGroup.GLOBALCOMMUNITY;
                    board = itUtil.createBoard(stock, boardGroup, boardGroup.getCategories().get(0));
                    request = genRequest();
                }

                @Nested
                class WhenErrorDoesNotHaveUserHoldingStock {

                    @DisplayName("Should return 403 response code when call " + TARGET_API)
                    @Test
                    void shouldReturnError() throws Exception {
                        MvcResult response = callApiWithJwt(status().isForbidden(), jwt, request);

                        itUtil.assertErrorResponse(response, 403, "보유하고 있지 않은 주식입니다.");
                    }
                }

                @Nested
                class WhenSuccessHasUserHoldingStock {
                    @BeforeEach
                    void setUp() {
                        itUtil.createUserHoldingStock(stock.getCode(), user);
                    }

                    @DisplayName("Should return 200 response code when call " + TARGET_API)
                    @Test
                    void shouldReturnSuccess() throws Exception {
                        MvcResult response = callApiWithJwt(status().isOk(), jwt, request);

                        final PostDetailsDataResponse result = objectMapperUtil.toResponse(
                            response.getResponse().getContentAsString(),
                            PostDetailsDataResponse.class
                        );

                        assertResponse(result);
                    }
                }
            }
        }

        @Nested
        class WhenAdminUserDoesNotHaveUserHoldingStock {

            @BeforeEach
            void setUp() {
                user = itUtil.createUser();
                jwt = itUtil.createJwt(user.getId());
                itUtil.createUserRole(user, RoleType.ADMIN);
                final BoardGroup boardGroup = someEnum(BoardGroup.class);
                board = itUtil.createBoard(stock, boardGroup, boardGroup.getCategories().get(0));
                request = genRequest();
                if (boardGroup == BoardGroup.ACTION) {
                    createActionRequest(board.getCategory());
                }
            }

            @Nested
            class WhenSuccess {

                @DisplayName("Should return 200 response code when call " + TARGET_API)
                @Test
                void shouldReturnSuccess() throws Exception {
                    MvcResult response = callApiWithJwt(status().isOk(), jwt, request);

                    final PostDetailsDataResponse result = objectMapperUtil.toResponse(
                        response.getResponse().getContentAsString(),
                        PostDetailsDataResponse.class
                    );

                    assertResponse(result);
                }
            }
        }
    }
}
