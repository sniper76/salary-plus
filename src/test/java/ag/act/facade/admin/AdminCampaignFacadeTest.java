package ag.act.facade.admin;

import ag.act.configuration.security.ActUserProvider;
import ag.act.converter.CampaignDetailsResponseConverter;
import ag.act.core.holder.RequestContextHolder;
import ag.act.dto.campaign.CampaignDetailsResponseSourceDto;
import ag.act.dto.campaign.SimpleCampaignPostDto;
import ag.act.dto.post.CreatePostRequestDto;
import ag.act.entity.Campaign;
import ag.act.entity.CampaignStockMapping;
import ag.act.entity.Post;
import ag.act.entity.StockGroup;
import ag.act.entity.User;
import ag.act.enums.ClientType;
import ag.act.exception.BadRequestException;
import ag.act.facade.admin.campaign.AdminCampaignFacade;
import ag.act.facade.admin.campaign.CampaignDownloadFacade;
import ag.act.facade.post.PostFacade;
import ag.act.model.CampaignDetailsResponse;
import ag.act.model.PostDetailsResponse;
import ag.act.model.Status;
import ag.act.service.digitaldocument.campaign.CampaignService;
import ag.act.service.digitaldocument.campaign.CampaignStockMappingService;
import ag.act.service.post.PostService;
import ag.act.service.stock.StockGroupMappingService;
import ag.act.service.stock.StockGroupService;
import ag.act.service.stockboardgrouppost.StockBoardGroupPostCreateService;
import ag.act.validator.document.CampaignValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;

import static ag.act.TestUtil.assertException;
import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mockStatic;
import static shiver.me.timbers.data.random.RandomBooleans.someBoolean;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class AdminCampaignFacadeTest {

    @InjectMocks
    private AdminCampaignFacade facade;
    @Mock
    private StockBoardGroupPostCreateService stockBoardGroupPostCreateService;
    @Mock
    private PostFacade postFacade;
    @Mock
    private PostService postService;
    @Mock
    private StockGroupMappingService stockGroupMappingService;
    @Mock
    private CampaignValidator campaignValidator;
    @Mock
    private CampaignService campaignService;
    @Mock
    private CampaignStockMappingService campaignStockMappingService;
    @Mock
    private StockGroupService stockGroupService;
    @Mock
    private CampaignDetailsResponseConverter campaignDetailsResponseConverter;
    @Mock
    private CampaignDownloadFacade campaignDownloadFacade;
    @Mock
    private User user;

    private List<MockedStatic<?>> statics;

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(ActUserProvider.class), mockStatic(RequestContextHolder.class));

        given(ActUserProvider.getNoneNull()).willReturn(user);
        given(RequestContextHolder.getClientType()).willReturn(someEnum(ClientType.class));
    }

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @Nested
    class CreateCampaign {
        @Mock
        private ag.act.model.CreateCampaignRequest createCampaignRequest;
        @Mock
        private ag.act.model.CreatePostRequest createPostRequest;
        @Mock
        private ag.act.model.PostDetailsDataResponse postDetailsDataResponse;
        private CampaignDetailsResponse actualResponse;
        @Mock
        private CampaignDetailsResponse expectedResponse;
        private Long stockGroupId;
        private Long campaignId;

        @BeforeEach
        void setUp() {
            stockGroupId = someLong();

            given(createCampaignRequest.getStockGroupId()).willReturn(stockGroupId);
        }

        @Nested
        class WhenRequestValidationFailed {

            @Test
            void shouldThrowException() {

                // Given
                final String message = someString(5);
                final RuntimeException exception = new RuntimeException(message);

                willThrow(exception).given(campaignValidator).validate(createCampaignRequest);

                // When // Then
                assertException(
                    RuntimeException.class,
                    () -> facade.createCampaign(createCampaignRequest),
                    message
                );
            }
        }

        @Nested
        class WhenStockGroupHasNoStocks {

            @Test
            void shouldThrowException() {

                // Given
                given(stockGroupMappingService.getAllStockCodes(stockGroupId)).willReturn(List.of());

                // When // Then
                assertException(
                    BadRequestException.class,
                    () -> facade.createCampaign(createCampaignRequest),
                    "해당 종목그룹에 매핑된 종목이 없습니다."
                );
            }
        }

        @Nested
        class WhenCreateCampaignSuccessfully {

            @Captor
            private ArgumentCaptor<CreatePostRequestDto> createPostRequestDtoCaptor;
            @Captor
            private ArgumentCaptor<Campaign> campaignArgumentCaptor;
            private String firstStockCode;
            @Mock
            private Campaign savedCampaign;
            @Mock
            private PostDetailsResponse postDetailsResponse;
            @Mock
            private StockGroup stockGroup;
            @Mock
            private Post sourcePost;
            private String campaignTitle;
            private Long postId;
            private List<String> stockCodes;

            @BeforeEach
            void setUp() {
                postId = someLong();
                firstStockCode = someStockCode();
                campaignTitle = someString(15);
                campaignId = someLong();
                final String title = someString(10);
                final List<CampaignStockMapping> createdCampaignStockMappings = List.of();
                final String postTitle = someString(20);
                final String stockGroupName = someString(30);
                stockCodes = List.of(firstStockCode, someStockCode(), someStockCode());

                willDoNothing().given(campaignValidator).validate(createCampaignRequest);
                given(createCampaignRequest.getCreatePostRequest()).willReturn(createPostRequest);
                given(createCampaignRequest.getTitle()).willReturn(campaignTitle);
                given(createPostRequest.getTitle()).willReturn(title);
                given(stockBoardGroupPostCreateService.createBoardGroupPost(any(CreatePostRequestDto.class)))
                    .willReturn(postDetailsDataResponse);
                given(postDetailsDataResponse.getData()).willReturn(postDetailsResponse);
                given(postDetailsResponse.getId()).willReturn(postId);
                given(postDetailsResponse.getTitle()).willReturn(postTitle);
                given(stockGroupMappingService.getAllStockCodes(stockGroupId)).willReturn(stockCodes);
                given(campaignService.save(any(Campaign.class))).willReturn(savedCampaign);
                given(savedCampaign.getId()).willReturn(campaignId);
                given(savedCampaign.getTitle()).willReturn(campaignTitle);
                given(campaignStockMappingService.createMappings(campaignId, stockCodes))
                    .willReturn(createdCampaignStockMappings);
                given(postFacade.duplicatePosts(postId, stockCodes)).willReturn(stockCodes.size());
                given(stockGroupService.findById(stockGroupId)).willReturn(Optional.of(stockGroup));
                given(campaignService.getSourcePostOf(savedCampaign)).willReturn(sourcePost);
                given(stockGroup.getName()).willReturn(stockGroupName);
                given(campaignDetailsResponseConverter.convert(
                    CampaignDetailsResponseSourceDto.builder()
                        .campaign(savedCampaign)
                        .stockGroup(stockGroup)
                        .sourcePost(sourcePost)
                        .build()
                )).willReturn(expectedResponse);

                actualResponse = facade.createCampaign(createCampaignRequest);
            }

            @Test
            void shouldCreateFirstPostWithCreatePostRequest() {
                then(stockBoardGroupPostCreateService).should().createBoardGroupPost(createPostRequestDtoCaptor.capture());

                final CreatePostRequestDto captorValue = createPostRequestDtoCaptor.getValue();
                assertThat(captorValue.getCreatePostRequest(), is(createPostRequest));
            }

            @Test
            void shouldCreateFirstPostWithFirstStockCode() {
                then(stockBoardGroupPostCreateService).should().createBoardGroupPost(createPostRequestDtoCaptor.capture());

                final CreatePostRequestDto captorValue = createPostRequestDtoCaptor.getValue();
                assertThat(captorValue.getStockCode(), is(firstStockCode));
            }

            @Test
            void shouldCreateCampaignWithTitle() {
                final Campaign captorValue = verifyCampaignServiceAndReturnCampaignParameter();

                assertThat(captorValue.getTitle(), is(campaignTitle));
            }

            @Test
            void shouldCreateCampaignWithStockGroupId() {
                final Campaign captorValue = verifyCampaignServiceAndReturnCampaignParameter();

                assertThat(captorValue.getSourceStockGroupId(), is(stockGroupId));
            }

            @Test
            void shouldCreateCampaignWithPostId() {
                final Campaign captorValue = verifyCampaignServiceAndReturnCampaignParameter();

                assertThat(captorValue.getSourcePostId(), is(postId));
            }

            @Test
            void shouldCreateCampaignWithActiveStatus() {
                final Campaign captorValue = verifyCampaignServiceAndReturnCampaignParameter();

                assertThat(captorValue.getStatus(), is(Status.ACTIVE));
            }

            @Test
            void shouldCreateCampaignStockMappings() {
                then(campaignStockMappingService).should().createMappings(campaignId, stockCodes);
            }

            @Test
            void shouldDuplicatePosts() {
                then(postFacade).should().duplicatePosts(postId, stockCodes);
            }

            @Test
            void shouldReturnCampaignDetails() {
                assertThat(actualResponse, is(expectedResponse));
            }

            private Campaign verifyCampaignServiceAndReturnCampaignParameter() {
                then(campaignService).should().save(campaignArgumentCaptor.capture());
                return campaignArgumentCaptor.getValue();
            }
        }
    }

    @Nested
    class GetCampaignDetails {
        private long campaignId;
        private long sourceStockGroupId;
        @Mock
        private Campaign campaign;
        @Mock
        private StockGroup sourceStockGroup;
        @Mock
        private Post sourcePost;
        @Mock
        private CampaignDetailsResponse expectedResponse;

        @Nested
        class FoundCampaign {
            @Mock
            private List<SimpleCampaignPostDto> simpleCampaignPosts;

            @Test
            void shouldReturnCampaignDetails() {
                // Given
                campaignId = someLong();
                sourceStockGroupId = someLong();
                long sourcePostId = someLong();
                String campaignTitle = someString(10);
                String sourcePostTitle = someString(10);

                given(campaign.getTitle()).willReturn(campaignTitle);
                given(campaign.getSourceStockGroupId()).willReturn(sourceStockGroupId);
                given(campaign.getSourcePostId()).willReturn(sourcePostId);

                given(campaignService.getSourcePostOf(campaign)).willReturn(sourcePost);
                given(campaignService.getCampaign(campaignId)).willReturn(campaign);
                given(stockGroupService.findById(sourceStockGroupId)).willReturn(Optional.of(sourceStockGroup));
                given(campaignService.getSimpleCampaignPostDtos(sourcePost)).willReturn(simpleCampaignPosts);
                given(sourcePost.getId()).willReturn(sourcePostId);
                given(sourcePost.getTitle()).willReturn(sourcePostTitle);

                given(campaignDetailsResponseConverter.convert(
                    CampaignDetailsResponseSourceDto.builder()
                        .campaign(campaign)
                        .stockGroup(sourceStockGroup)
                        .sourcePost(sourcePost)
                        .simpleCampaignPostDtos(simpleCampaignPosts)
                        .build()
                )).willReturn(expectedResponse);

                // When
                CampaignDetailsResponse actualResponse = facade.getCampaignDetails(campaignId);

                // Then
                assertThat(actualResponse, is(expectedResponse));
            }
        }

        @Nested
        class NotFoundCampaign {
            @Test
            void shouldThrowBadRequest() {
                // Given
                campaignId = someLong();
                given(campaignService.getCampaign(campaignId)).willThrow(new BadRequestException("캠페인이 존재하지 않습니다."));

                // When // Then
                assertException(
                    BadRequestException.class,
                    () -> facade.getCampaignDetails(campaignId),
                    "캠페인이 존재하지 않습니다."
                );
            }
        }

        @Nested
        class NotFoundSourceStockGroup {
            @Test
            void shouldThrowBadRequest() {
                // Given
                campaignId = someLong();
                sourceStockGroupId = someLong();

                given(campaignService.getCampaign(campaignId)).willReturn(campaign);
                given(campaign.getSourceStockGroupId()).willReturn(sourceStockGroupId);
                given(stockGroupService.findById(sourceStockGroupId)).willReturn(Optional.empty());

                // When // Then
                assertException(
                    BadRequestException.class,
                    () -> facade.getCampaignDetails(campaignId),
                    "종목그룹이 존재하지 않습니다."
                );
            }
        }
    }

    @Nested
    class CreateDigitalDocumentZipFile {

        private Long campaignId;
        private Long postId;
        @Mock
        private Campaign campaign;

        @Nested
        class WhenCampaignHaveNoPost {

            @BeforeEach
            void setUp() {
                campaignId = someLong();
                postId = someLong();

                given(campaignService.getCampaign(campaignId)).willReturn(campaign);
                given(campaign.getSourcePostId()).willReturn(postId);
                given(campaignService.getSourcePostOf(campaign)).willThrow(new BadRequestException("캠페인의 게시글이 존재하지 않습니다."));
            }

            @Test
            void shouldThrowBadRequestException() {
                assertException(
                    BadRequestException.class,
                    () -> facade.createDigitalDocumentZipFile(campaignId, someBoolean()),
                    "캠페인의 게시글이 존재하지 않습니다."
                );
            }
        }

        @Nested
        class WhenCampaignHaveNoDigitalDocuments {

            @Mock
            private Post post;

            @BeforeEach
            void setUp() {
                campaignId = someLong();
                postId = someLong();

                given(campaignService.getCampaign(campaignId)).willReturn(campaign);
                given(campaign.getSourcePostId()).willReturn(postId);
                given(postFacade.findPostById(postId)).willReturn(Optional.of(post));
                given(campaignService.findAllDigitalDocumentIdsBySourcePostId(postId))
                    .willReturn(List.of());
            }

            @Test
            void shouldFindCampaign() {
                assertException(
                    BadRequestException.class,
                    () -> facade.createDigitalDocumentZipFile(campaignId, someBoolean()),
                    "캠페인의 전자문서가 존재하지 않습니다."
                );
            }
        }

        @Nested
        class WhenCampaignCreateDigitalDocumentZipFileSuccessfully {

            @Mock
            private Post post;
            private List<Long> digitalDocumentIds;
            private Boolean isSecured;

            @BeforeEach
            void setUp() {
                campaignId = someLong();
                isSecured = someBoolean();
                postId = someLong();
                digitalDocumentIds = List.of(someLong());

                given(campaignService.getCampaign(campaignId)).willReturn(campaign);
                given(campaign.getSourcePostId()).willReturn(postId);
                given(postFacade.findPostById(postId)).willReturn(Optional.of(post));
                given(campaignService.findAllDigitalDocumentIdsBySourcePostId(postId))
                    .willReturn(digitalDocumentIds);
                willDoNothing().given(campaignDownloadFacade)
                    .createDigitalDocumentZipFile(campaignId, digitalDocumentIds, isSecured);

                facade.createDigitalDocumentZipFile(campaignId, isSecured);
            }

            @Test
            void shouldCallCreateDigitalDocumentZipFile() {
                then(campaignDownloadFacade).should()
                    .createDigitalDocumentZipFile(campaignId, digitalDocumentIds, isSecured);
            }
        }
    }
}
