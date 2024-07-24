package ag.act.itutil;

import ag.act.TestUtil;
import ag.act.api.home.util.HomeApiIntegrationTestHelper;
import ag.act.configuration.security.ActUserProvider;
import ag.act.configuration.security.CryptoHelper;
import ag.act.configuration.security.TokenProvider;
import ag.act.converter.DecryptColumnConverter;
import ag.act.core.configuration.GlobalBoardManager;
import ag.act.core.infra.S3Environment;
import ag.act.dto.DigitalDocumentUserAnswerDto;
import ag.act.dto.admin.DashboardStatisticsAgeCountDto;
import ag.act.dto.admin.DashboardStatisticsGenderCountDto;
import ag.act.dto.file.UploadFilePathDto;
import ag.act.dto.solidarity.election.SolidarityLeaderElectionStatusGroup;
import ag.act.entity.ActEntity;
import ag.act.entity.AppPreference;
import ag.act.entity.AutomatedAuthorPush;
import ag.act.entity.BatchLog;
import ag.act.entity.BlockedUser;
import ag.act.entity.Board;
import ag.act.entity.Campaign;
import ag.act.entity.CampaignStockMapping;
import ag.act.entity.Comment;
import ag.act.entity.CommentUserProfile;
import ag.act.entity.CorporateUser;
import ag.act.entity.DigitalProxy;
import ag.act.entity.DigitalProxyApproval;
import ag.act.entity.FileContent;
import ag.act.entity.LatestPostTimestamp;
import ag.act.entity.LatestUserPostsView;
import ag.act.entity.MyDataSummary;
import ag.act.entity.NicknameHistory;
import ag.act.entity.Poll;
import ag.act.entity.PollAnswer;
import ag.act.entity.PollItem;
import ag.act.entity.Popup;
import ag.act.entity.Post;
import ag.act.entity.PostImage;
import ag.act.entity.PostUserLike;
import ag.act.entity.PostUserProfile;
import ag.act.entity.PostUserView;
import ag.act.entity.PrivateStock;
import ag.act.entity.Push;
import ag.act.entity.Report;
import ag.act.entity.ReportHistory;
import ag.act.entity.Role;
import ag.act.entity.Solidarity;
import ag.act.entity.SolidarityDailyStatistics;
import ag.act.entity.SolidarityDailySummary;
import ag.act.entity.SolidarityLeader;
import ag.act.entity.SolidarityLeaderApplicant;
import ag.act.entity.Stock;
import ag.act.entity.StockAcceptorUser;
import ag.act.entity.StockAcceptorUserHistory;
import ag.act.entity.StockDartCorporation;
import ag.act.entity.StockGroup;
import ag.act.entity.StockGroupMapping;
import ag.act.entity.StockReferenceDate;
import ag.act.entity.StockSearchTrend;
import ag.act.entity.StopWord;
import ag.act.entity.TestStock;
import ag.act.entity.User;
import ag.act.entity.UserAnonymousCount;
import ag.act.entity.UserBadgeVisibility;
import ag.act.entity.UserHoldingStock;
import ag.act.entity.UserHoldingStockHistoryOnDate;
import ag.act.entity.UserHoldingStockOnReferenceDate;
import ag.act.entity.UserPushAgreement;
import ag.act.entity.UserRole;
import ag.act.entity.UserVerificationHistory;
import ag.act.entity.WebVerification;
import ag.act.entity.admin.DashboardAgeStatistics;
import ag.act.entity.admin.DashboardGenderStatistics;
import ag.act.entity.admin.DashboardStatistics;
import ag.act.entity.admin.DashboardStockStatistics;
import ag.act.entity.admin.StockRanking;
import ag.act.entity.campaign.CampaignDownload;
import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.entity.digitaldocument.DigitalDocumentDownload;
import ag.act.entity.digitaldocument.DigitalDocumentItem;
import ag.act.entity.digitaldocument.DigitalDocumentItemUserAnswer;
import ag.act.entity.digitaldocument.DigitalDocumentNumber;
import ag.act.entity.digitaldocument.DigitalDocumentUser;
import ag.act.entity.digitaldocument.HolderListReadAndCopy;
import ag.act.entity.mydata.JsonMyData;
import ag.act.entity.mydata.JsonMyDataStock;
import ag.act.entity.notification.Notification;
import ag.act.entity.notification.NotificationUserView;
import ag.act.entity.solidarity.election.BlockedSolidarityLeaderApplicant;
import ag.act.entity.solidarity.election.SolidarityLeaderElection;
import ag.act.entity.solidarity.election.SolidarityLeaderElectionPollItemMapping;
import ag.act.enums.ActErrorCode;
import ag.act.enums.AppLinkType;
import ag.act.enums.AppPreferenceType;
import ag.act.enums.AutomatedPushContentType;
import ag.act.enums.AutomatedPushCriteria;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.ClientType;
import ag.act.enums.CommentType;
import ag.act.enums.DigitalAnswerType;
import ag.act.enums.DigitalDocumentAnswerStatus;
import ag.act.enums.DigitalDocumentType;
import ag.act.enums.DigitalDocumentVersion;
import ag.act.enums.MediaType;
import ag.act.enums.PostsViewType;
import ag.act.enums.ReportType;
import ag.act.enums.RoleType;
import ag.act.enums.SelectionOption;
import ag.act.enums.SolidarityLeaderElectionProcedure;
import ag.act.enums.UserBadgeType;
import ag.act.enums.VoteType;
import ag.act.enums.admin.DashboardStatisticsType;
import ag.act.enums.digitaldocument.HolderListReadAndCopyItemType;
import ag.act.enums.digitaldocument.ZipFileStatus;
import ag.act.enums.notification.NotificationCategory;
import ag.act.enums.notification.NotificationType;
import ag.act.enums.popup.PopupDisplayTargetType;
import ag.act.enums.push.PushSendStatus;
import ag.act.enums.push.PushSendType;
import ag.act.enums.push.PushTargetType;
import ag.act.enums.push.PushTopic;
import ag.act.enums.push.UserPushAgreementType;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionAnswerType;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionApplyStatus;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionStatus;
import ag.act.enums.solidarity.election.SolidarityLeaderElectionStatusDetails;
import ag.act.enums.verification.VerificationOperationType;
import ag.act.enums.verification.VerificationType;
import ag.act.enums.virtualboard.VirtualBoardCategory;
import ag.act.enums.virtualboard.VirtualBoardGroup;
import ag.act.exception.BadRequestException;
import ag.act.exception.InternalServerException;
import ag.act.exception.NotFoundException;
import ag.act.itutil.dto.PostUserProfileDto;
import ag.act.model.ErrorResponse;
import ag.act.model.Gender;
import ag.act.model.PostDetailsResponse;
import ag.act.model.PostResponse;
import ag.act.model.ReportStatus;
import ag.act.model.SimpleStringResponse;
import ag.act.model.Status;
import ag.act.repository.AppPreferenceRepository;
import ag.act.repository.AutomatedAuthorPushRepository;
import ag.act.repository.BatchLogRepository;
import ag.act.repository.BlockedUserRepository;
import ag.act.repository.BoardRepository;
import ag.act.repository.CampaignRepository;
import ag.act.repository.CampaignStockMappingRepository;
import ag.act.repository.CommentRepository;
import ag.act.repository.CommentUserProfileRepository;
import ag.act.repository.CorporateUserRepository;
import ag.act.repository.DigitalDocumentDownloadRepository;
import ag.act.repository.DigitalDocumentItemRepository;
import ag.act.repository.DigitalDocumentItemUserAnswerRepository;
import ag.act.repository.DigitalDocumentNumberRepository;
import ag.act.repository.DigitalDocumentRepository;
import ag.act.repository.DigitalDocumentUserRepository;
import ag.act.repository.DigitalProxyRepository;
import ag.act.repository.FileContentRepository;
import ag.act.repository.HolderListReadAndCopyRepository;
import ag.act.repository.LatestPostTimestampRepository;
import ag.act.repository.LatestUserPostsViewRepository;
import ag.act.repository.MyDataSummaryRepository;
import ag.act.repository.NicknameHistoryRepository;
import ag.act.repository.PollAnswerRepository;
import ag.act.repository.PollItemRepository;
import ag.act.repository.PollRepository;
import ag.act.repository.PopupRepository;
import ag.act.repository.PostImageRepository;
import ag.act.repository.PostRepository;
import ag.act.repository.PostUserLikeRepository;
import ag.act.repository.PostUserProfileRepository;
import ag.act.repository.PostUserViewRepository;
import ag.act.repository.PrivateStockRepository;
import ag.act.repository.PushRepository;
import ag.act.repository.ReportRepository;
import ag.act.repository.RoleRepository;
import ag.act.repository.SolidarityDailyStatisticsRepository;
import ag.act.repository.SolidarityDailySummaryRepository;
import ag.act.repository.SolidarityLeaderApplicantRepository;
import ag.act.repository.SolidarityLeaderElectionPollItemMappingRepository;
import ag.act.repository.SolidarityLeaderRepository;
import ag.act.repository.SolidarityRepository;
import ag.act.repository.StockDartCorporationRepository;
import ag.act.repository.StockGroupMappingRepository;
import ag.act.repository.StockGroupRepository;
import ag.act.repository.StockReferenceDateRepository;
import ag.act.repository.StockRepository;
import ag.act.repository.StopWordRepository;
import ag.act.repository.TestStockRepository;
import ag.act.repository.UserAnonymousCountRepository;
import ag.act.repository.UserBadgeVisibilityRepository;
import ag.act.repository.UserHoldingStockHistoryOnDateRepository;
import ag.act.repository.UserHoldingStockOnReferenceDateRepository;
import ag.act.repository.UserHoldingStockRepository;
import ag.act.repository.UserPushAgreementRepository;
import ag.act.repository.UserRepository;
import ag.act.repository.UserRoleRepository;
import ag.act.repository.UserVerificationHistoryRepository;
import ag.act.repository.WebVerificationRepository;
import ag.act.repository.admin.DashboardAgeStatisticsRepository;
import ag.act.repository.admin.DashboardGenderStatisticsRepository;
import ag.act.repository.admin.DashboardStatisticsRepository;
import ag.act.repository.admin.DashboardStockStatisticsRepository;
import ag.act.repository.admin.StockAcceptorUserHistoryRepository;
import ag.act.repository.admin.StockAcceptorUserRepository;
import ag.act.repository.admin.StockRankingRepository;
import ag.act.repository.campaign.CampaignDownloadRepository;
import ag.act.repository.interfaces.DigitalDocumentUserSummary;
import ag.act.repository.interfaces.JoinCount;
import ag.act.repository.notification.NotificationRepository;
import ag.act.repository.notification.NotificationUserViewRepository;
import ag.act.repository.solidarity.election.BlockedSolidarityLeaderApplicantRepository;
import ag.act.repository.solidarity.election.SolidarityLeaderElectionRepository;
import ag.act.repository.stocksearchtrend.StockSearchTrendRepository;
import ag.act.service.aws.S3Service;
import ag.act.service.digitaldocument.DigitalDocumentTemplatePathService;
import ag.act.service.solidarity.election.SolidarityLeaderService;
import ag.act.service.user.AdminUserService;
import ag.act.service.user.UserRoleService;
import ag.act.util.DateTimeUtil;
import ag.act.util.KoreanDateTimeUtil;
import ag.act.util.ObjectMapperUtil;
import ag.act.util.StatusUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mockito.internal.invocation.InterceptedInvocation;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static ag.act.TestUtil.someBirthDay;
import static ag.act.TestUtil.someBoardCategory;
import static ag.act.TestUtil.someBoardCategoryForStocks;
import static ag.act.TestUtil.someCompanyRegistrationNumber;
import static ag.act.TestUtil.someEmail;
import static ag.act.TestUtil.someInstantInTheFuture;
import static ag.act.TestUtil.someLocalDateTimeInTheFuture;
import static ag.act.TestUtil.someLocalDateTimeInThePast;
import static ag.act.TestUtil.somePhoneNumber;
import static ag.act.TestUtil.someSolidarityLeaderElectionApplicationItem;
import static ag.act.TestUtil.someStockCode;
import static ag.act.TestUtil.someWebVerificationCode;
import static ag.act.enums.FileContentType.DEFAULT;
import static ag.act.enums.FileContentType.DEFAULT_PROFILE;
import static ag.act.enums.FileType.IMAGE;
import static ag.act.module.auth.web.WebVerificationBase.VERIFICATION_CODE_DURATION_MINUTES;
import static org.apache.commons.collections4.ListUtils.emptyIfNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWithIgnoringCase;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shiver.me.timbers.data.random.RandomBooleans.someBoolean;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomLongs.someLongBetween;
import static shiver.me.timbers.data.random.RandomLongs.somePositiveLong;
import static shiver.me.timbers.data.random.RandomStrings.someAlphanumericString;
import static shiver.me.timbers.data.random.RandomStrings.someNumericString;
import static shiver.me.timbers.data.random.RandomStrings.someString;
import static shiver.me.timbers.data.random.RandomThings.someThing;

@Component
@SuppressWarnings({"checkstyle:AbbreviationAsWordInName", "UnusedReturnValue", "ResultOfMethodCallIgnored", "LombokGetterMayBeUsed", "unused"})
@Transactional(Transactional.TxType.REQUIRES_NEW)
public class ITUtil {

    private static final int RECENT_NOTIFICATION_TIME_PERIOD_MONTHS = 1;
    private static final int SOLIDARITY_APPLY_DEFAULT_VERSION = 1;
    private static final int SOLIDARITY_APPLY_NEW_VERSION = 2;
    private static final int END_HOURS_IN_UTC = 15;
    private final HomeApiIntegrationTestHelper homeTestHelper;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private GlobalBoardManager globalBoardManager;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostImageRepository postImageRepository;
    @Autowired
    private PollAnswerRepository pollAnswerRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserVerificationHistoryRepository userVerificationHistoryRepository;
    @Autowired
    private CommentUserProfileRepository commentUserProfileRepository;
    @Autowired
    private UserHoldingStockRepository userHoldingStockRepository;
    @Autowired
    private SolidarityRepository solidarityRepository;
    @Autowired
    private FileContentRepository fileContentRepository;
    @Autowired
    private SolidarityLeaderRepository solidarityLeaderRepository;
    @Autowired
    private SolidarityLeaderService solidarityLeaderService;
    @Autowired
    private SolidarityLeaderApplicantRepository solidarityLeaderApplicantRepository;
    @Autowired
    private SolidarityLeaderElectionRepository solidarityLeaderElectionRepository;
    @Autowired
    private StockSearchTrendRepository stockSearchTrendRepository;
    @Autowired
    private S3Environment s3Environment;
    @Autowired
    private CryptoHelper cryptoHelper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private DigitalProxyRepository digitalProxyRepository;
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private BatchLogRepository batchLogRepository;
    @Autowired
    private SolidarityDailySummaryRepository solidarityDailySummaryRepository;
    @Autowired
    private PostUserViewRepository postUserViewRepository;
    @Autowired
    private PushRepository pushRepository;
    @Autowired
    private PopupRepository popupRepository;
    @Autowired
    private PostUserLikeRepository postUserLikeRepository;
    @Autowired
    private MyDataSummaryRepository myDataSummaryRepository;
    @Autowired
    private WebVerificationRepository webVerificationRepository;
    @Autowired
    private PostUserProfileRepository postUserProfileRepository;
    @Autowired
    private StockReferenceDateRepository stockReferenceDateRepository;
    @Autowired
    private DigitalDocumentRepository digitalDocumentRepository;
    @Autowired
    private DigitalDocumentDownloadRepository digitalDocumentDownloadRepository;
    @Autowired
    private CampaignDownloadRepository campaignDownloadRepository;
    @Autowired
    private DigitalDocumentUserRepository digitalDocumentUserRepository;
    @Autowired
    private StockGroupMappingRepository stockGroupMappingRepository;
    @Autowired
    private StockGroupRepository stockGroupRepository;
    @Autowired
    private DigitalDocumentItemRepository digitalDocumentItemRepository;
    @Autowired
    private DigitalDocumentItemUserAnswerRepository digitalDocumentItemUserAnswerRepository;
    @Autowired
    private NicknameHistoryRepository nicknameHistoryRepository;
    @Autowired
    private SolidarityDailyStatisticsRepository solidarityDailyStatisticsRepository;
    @Autowired
    private CampaignStockMappingRepository campaignStockMappingRepository;
    @Autowired
    private CampaignRepository campaignRepository;
    @Autowired
    private DashboardStatisticsRepository dashboardStatisticsRepository;
    @Autowired
    private DashboardAgeStatisticsRepository dashboardAgeStatisticsRepository;
    @Autowired
    private DashboardGenderStatisticsRepository dashboardGenderStatisticsRepository;
    @Autowired
    private DashboardStockStatisticsRepository dashboardStockStatisticsRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private NotificationUserViewRepository notificationUserViewRepository;
    @Autowired
    private ObjectMapperUtil objectMapperUtil;
    @Autowired
    private TestStockRepository testStockRepository;
    @Autowired
    private PrivateStockRepository privateStockRepository;
    @Autowired
    private DecryptColumnConverter decryptColumnConverter;
    @Autowired
    private DataSource dataSource;
    @MockBean
    private TokenProvider tokenProvider;
    @MockBean
    private S3Service s3Service;
    @MockBean
    private DigitalDocumentTemplatePathService digitalDocumentTemplatePathService;
    @MockBean
    private AdminUserService adminUserService;
    @Autowired
    private PollRepository pollRepository;
    @Autowired
    private PollItemRepository pollItemRepository;
    @Autowired
    private LatestPostTimestampRepository latestPostTimestampRepository;
    @Autowired
    private LatestUserPostsViewRepository latestUserPostsViewRepository;
    @Autowired
    private UserHoldingStockOnReferenceDateRepository userHoldingStockOnReferenceDateRepository;
    @Autowired
    private DigitalDocumentNumberRepository digitalDocumentNumberRepository;
    @Autowired
    private AutomatedAuthorPushRepository automatedAuthorPushRepository;
    @Autowired
    private StockDartCorporationRepository stockDartCorporationRepository;
    @Autowired
    private CorporateUserRepository corporateBusinessRepository;
    @Autowired
    private UserPushAgreementRepository userPushAgreementRepository;
    @Autowired
    private UserBadgeVisibilityRepository userBadgeVisibilityRepository;
    @Autowired
    private StockAcceptorUserHistoryRepository stockAcceptorUserHistoryRepository;
    @Autowired
    private StockAcceptorUserRepository stockAcceptorUserRepository;
    @Autowired
    private StockRankingRepository stockRankingRepository;
    @Autowired
    private BlockedUserRepository blockedUserRepository;
    @Autowired
    private StopWordRepository stopWordRepository;
    @Autowired
    private AppPreferenceRepository appPreferenceRepository;
    @Autowired
    private UserHoldingStockHistoryOnDateRepository userHoldingStockHistoryOnDateRepository;
    @Autowired
    private SolidarityLeaderElectionPollItemMappingRepository solidarityLeaderElectionPollItemMappingRepository;
    @Autowired
    private HolderListReadAndCopyRepository holderListReadAndCopyRepository;
    @Autowired
    private UserAnonymousCountRepository userAnonymousCountRepository;
    @Autowired
    private BlockedSolidarityLeaderApplicantRepository blockedSolidarityLeaderApplicantRepository;

    public ITUtil() {
        homeTestHelper = new HomeApiIntegrationTestHelper(this);
    }

    private static LocalDate getEndOfThisYearLocalDate() {
        return LocalDate.of(LocalDate.now().getYear(), 12, 31);
    }

    private void initialMock() {
        given(tokenProvider.createAppToken(any())).willReturn(someString(10));
        given(tokenProvider.createWebToken(any())).willReturn(someString(10));
        given(s3Service.putObject(any(UploadFilePathDto.class), any(InputStream.class))).willReturn(true);
        given(s3Service.readObject(any())).willReturn(InputStream.nullInputStream());
        given(s3Service.removeObjectInRetry(anyString())).willReturn(true);
        given(digitalDocumentTemplatePathService.getPath()).willReturn(
            TestUtil.getDigitalDocumentTemplatePath()
        );
        given(adminUserService.getSuperAdminUserIds())
            .willAnswer(invocationOnMock -> userRepository.findAllByRole(RoleType.SUPER_ADMIN));
    }

    private void initialLoad() {
        loadDefaultProfileImages();
        loadUserRoles();
    }

    private void loadDefaultProfileImages() {
        fileContentRepository.deleteAll();
        createDefaultProfileImage(Gender.M.toString());
        createDefaultProfileImage(Gender.F.toString());
    }

    public void init() {
        initialLoad();
        initialMock();
    }

    private void loadUserRoles() {
        for (RoleType roleType : RoleType.values()) {
            roleRepository.findByType(roleType)
                .orElseGet(() -> {
                    final Role role = new Role();
                    role.setType(roleType);
                    role.setStatus(Status.ACTIVE);
                    return roleRepository.save(role);
                });
        }
    }

    ////////////////////////////
    ////////////////////////////
    ////////////////////////////

    private void createDefaultProfileImage(String description) {
        FileContent fileContent = new FileContent();
        fileContent.setFileContentType(DEFAULT_PROFILE);
        fileContent.setFileType(IMAGE);
        fileContent.setFilename(someAlphanumericString(10));
        fileContent.setOriginalFilename(someAlphanumericString(20));
        fileContent.setMimetype(someEnum(MediaType.class).getTypeName());
        fileContent.setStatus(Status.ACTIVE);
        fileContent.setDescription(description);
        fileContentRepository.save(fileContent);
    }

    public HomeApiIntegrationTestHelper getHomeTestHelper() {
        return homeTestHelper;
    }

    public S3Service getS3ServiceMockBean() {
        return s3Service;
    }

    public String createJwt(Long userId) {
        final String token = "TEST_TOKEN_" + userId.toString();

        given(tokenProvider.parseBearerToken(any()))
            .willAnswer(invocation -> {
                final HttpServletRequest request = invocation.getArgument(0);
                if (request == null) {
                    return null;
                }
                final String authorization = request.getHeader("Authorization");
                if (StringUtils.isBlank(authorization)) {
                    return null;
                }
                return authorization.substring(7);
            });
        given(tokenProvider.validateAndGetUserId(token)).willReturn(userId);
        given(tokenProvider.createAppToken(userId.toString())).willReturn(token);

        return token;
    }

    public User withdrawRequest(User user) {
        user.setStatus(Status.DELETED_BY_USER);
        user.setDeletedAt(LocalDateTime.now());
        user.setLastPinNumberVerifiedAt(null);
        user.setHashedPinNumber(null);
        return updateUser(user);
    }

    public User createUserWithAddress() {
        User user = createUser();
        user.setAddress("ADDRESS_" + someAlphanumericString(10));
        user.setAddressDetail("ADDRESS_DETAIL_" + someAlphanumericString(10) + "(TEST1,TEST2)");
        user.setZipcode(someNumericString(5));
        return updateUser(user);
    }

    public User createUserWithGivenName(String name) {
        User user = createUser();
        user.setName(name);
        return user;
    }

    public User createUserWithGivenNameAndStatus(String name, Status status) {
        User user = createUserWithGivenName(name);
        user.setStatus(status);
        return user;
    }

    public User createUserWithGivenNickname(String nickname) {
        User user = createUser();
        user.setNickname(nickname);
        return user;
    }

    public User createUserWithGivenEmail(String email) {
        User user = createUser();
        user.setEmail(email);
        return user;
    }

    public User createUserWithPhoneNumber(String phoneNumber) throws Exception {
        User user = createUser();
        user.setHashedPhoneNumber(cryptoHelper.encrypt(phoneNumber));
        return user;
    }

    public User changeUserStatus(User user, Status status) {
        user.setStatus(status);
        return user;
    }

    public User createUser() {
        return createUser(someNumericString(6));
    }

    public User createUser(String pinNumber) {
        final User user = createUserBeforePinRegistered();
        return updatePinNumber(user, pinNumber);
    }

    public User createUserBeforePinRegistered() {
        final String nickname = someAlphanumericString(20);
        final User user = createUserAfterVerifyAuthCode();
        final NicknameHistory nicknameHistory = new NicknameHistory();
        nicknameHistory.setNickname(nickname);
        nicknameHistory.setIsFirst(true);
        nicknameHistory.setByAdmin(false);
        nicknameHistory.setStatus(Status.ACTIVE);
        nicknameHistory.setUserId(user.getId());
        nicknameHistoryRepository.saveAndFlush(nicknameHistory);

        user.setStatus(Status.ACTIVE);
        user.setNickname(nickname);
        user.setNicknameHistory(nicknameHistory);
        user.setNicknameHistories(Lists.list(nicknameHistory));
        user.setProfileImageUrl(getDefaultProfileImage());
        return userRepository.save(user);
    }

    public User createUserAfterVerifyAuthCode() {
        return createUserInRetry();
    }

    private User createUserInRetry() {
        Exception exception = null;
        int maxTryCount = 3;

        for (int i = 0; i < maxTryCount; i++) {
            try {
                return createDefaultUser();
            } catch (Exception ex) {
                exception = ex;
            }
        }

        throw new InternalServerException("회원을 생성하는 중에 알 수 없는 오류가 발생하였습니다.", exception);
    }

    @NotNull
    private User createDefaultUser() throws Exception {
        final Gender gender = someEnum(Gender.class);
        final LocalDateTime birthDate = someBirthDay();

        final User user = new User();
        user.setEmail(someEmail());
        user.setHashedPhoneNumber(cryptoHelper.encrypt("010" + someNumericString(8)));
        user.setHashedCI(cryptoHelper.encrypt(someAlphanumericString(83)));
        user.setHashedDI(passwordEncoder.encode(someAlphanumericString(63)));
        user.setGender(gender);
        user.setName(someAlphanumericString(10));
        user.setStatus(Status.PROCESSING);
        user.setBirthDate(birthDate);
        user.setFirstNumberOfIdentification(
            TestUtil.getFirstNumberOfIdentification(birthDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")), gender)
        );

        return userRepository.save(user);
    }

    public User createDeletedUser() {
        final User user = createUser();
        user.setStatus(Status.DELETED_BY_USER);
        user.setDeletedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public User updateUserNickname(User user, String nickname, boolean byAdmin) {
        final NicknameHistory previousNicknameHistory = user.getNicknameHistory();
        previousNicknameHistory.setStatus(byAdmin ? Status.INACTIVE_BY_ADMIN : Status.INACTIVE_BY_USER);
        previousNicknameHistory.setDeletedAt(LocalDateTime.now());
        previousNicknameHistory.setByAdmin(false);
        nicknameHistoryRepository.save(previousNicknameHistory);

        final NicknameHistory nicknameHistory = new NicknameHistory();
        nicknameHistory.setNickname(nickname);
        nicknameHistory.setIsFirst(false);
        nicknameHistory.setByAdmin(byAdmin);
        nicknameHistory.setStatus(Status.ACTIVE);
        nicknameHistory.setUserId(user.getId());
        nicknameHistoryRepository.save(nicknameHistory);

        user.setNickname(nickname);
        user.setNicknameHistory(nicknameHistory);
        return userRepository.save(user);
    }

    public void updateAllNicknameHistoriesCreatedAtWithinTimeRange(User user, long daysAgo) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        final String updateQuery = "update nickname_histories set created_at=? where id=?";

        findAllNicknameHistoriesByUserId(user.getId())
            .forEach(it -> jdbcTemplate.update(
                    updateQuery,
                    new Object[] {
                        it.getCreatedAt().minusDays(daysAgo).format(DateTimeFormatter.ISO_DATE_TIME),
                        it.getId()
                    }
                )
            );
    }

    public String getDefaultProfileImage() {
        return s3Environment.getBaseUrl() + "/test/default_profile.png";
    }


    public User updateUserWithCI(User user, String ci) {
        try {
            user.setHashedCI(cryptoHelper.encrypt(ci));
            return updateUser(user);
        } catch (Exception e) {
            throw new InternalServerException("회원을 수정하는 중에 알 수 없는 오류가 발생하였습니다.", e);
        }
    }

    public User updateUser(User user) {
        try {
            final User savedUser = userRepository.save(user);
            savedUser.setAdmin(isAdmin(savedUser));
            savedUser.setAcceptor(isAcceptorUser(savedUser));
            return savedUser;
        } catch (Exception e) {
            throw new InternalServerException("회원을 수정하는 중에 알 수 없는 오류가 발생하였습니다.", e);
        }
    }

    public User updatePinNumber(User user, String pinNumber) {
        try {
            user.setHashedPinNumber(cryptoHelper.encrypt(pinNumber));
            user.setLastPinNumberVerifiedAt(LocalDateTime.now());
            return userRepository.save(user);
        } catch (Exception e) {
            throw new InternalServerException("핀번호를 업데이트하는 중에 알 수 없는 오류가 발생하였습니다.");
        }
    }

    public Stock createStock() {
        return createStock(someStockCode());
    }

    public Stock createStock(String stockCode) {
        return createStock(stockCode, someAlphanumericString(10));
    }

    public Stock createStock(String stockCode, String stockName) {
        final Stock stock = new Stock();
        stock.setCode(stockCode);
        stock.setName(stockName);
        stock.setTotalIssuedQuantity(someLongBetween(100_000_000L, 9_999_999_999L));
        stock.setSolidarityId(null);
        stock.setStatus(Status.ACTIVE);
        stock.setStandardCode(someAlphanumericString(12));
        stock.setFullName(someAlphanumericString(20));
        stock.setMarketType(someThing("KOSPI", "KOSDAQ", "KONEX"));
        stock.setStockType(someThing("구형우선주", "보통주", "신형우선주", "종류주권"));
        stock.setClosingPrice(someIntegerBetween(1000, 99999));
        stock.setRepresentativePhoneNumber(somePhoneNumber());

        return stockRepository.save(stock);
    }

    public Stock updateStock(Stock stock) {
        return stockRepository.save(stock);
    }

    public TestStock createTestStock() {
        final TestStock testStock = new TestStock();

        final String stockCode = "TEST_STOCK_" + someStockCode();
        testStock.setCode(stockCode);
        testStock.setName(stockCode);

        return testStockRepository.save(testStock);
    }

    public PrivateStock createPrivateStock() {
        final PrivateStock privateStock = new PrivateStock();

        final String stockCode = "PRIVATE_STOCK_" + someStockCode();
        privateStock.setCode(stockCode);
        privateStock.setName(stockCode);
        privateStock.setFullName(stockCode);
        privateStock.setStandardCode(stockCode + "_STANDARD_CODE");
        privateStock.setMarketType(someAlphanumericString(10));
        privateStock.setStockType(someAlphanumericString(15));
        privateStock.setClosingPrice(someIntegerBetween(100, 10_000));
        privateStock.setTotalIssuedQuantity(someLongBetween(1000L, 100_000L));

        return privateStockRepository.save(privateStock);
    }

    public void deletePrivateStocks(List<String> stockCodes) {
        stockCodes.forEach(
            stockCode -> privateStockRepository.findByCode(stockCode)
                .ifPresent(privateStock -> privateStockRepository.delete(privateStock))
        );
    }

    public Solidarity createSolidarity(String stockCode) {
        final Solidarity solidarity = new Solidarity();

        solidarity.setStockCode(stockCode);
        solidarity.setStatus(Status.ACTIVE);

        final Solidarity savedSolidarity = solidarityRepository.save(solidarity);
        Stock stock = findStock(stockCode);
        solidarity.setStock(stock);
        connectStockAndSolidarity(stock, solidarity);

        return savedSolidarity;
    }

    public Solidarity updateSolidarity(Solidarity solidarity) {
        return solidarityRepository.save(solidarity);
    }

    public List<StopWord> findAllStopWords() {
        return stopWordRepository.findAll();
    }

    public StopWord createStopWord(String word) {
        final StopWord stopWord = new StopWord(word.trim(), Status.ACTIVE);
        return stopWordRepository.save(stopWord);
    }

    public StopWord createStopWord(String word, Status status) {
        final StopWord stopWord = new StopWord(word.trim(), status);
        return stopWordRepository.save(stopWord);
    }

    public StopWord updateStopWord(StopWord stopWord) {
        return stopWordRepository.save(stopWord);
    }

    public StopWord updateStopWord(StopWord stopWord, Status status) {
        stopWord.setStatus(status);
        return updateStopWord(stopWord);
    }

    public UserHoldingStock createUserHoldingStock(String stockCode, User user) {
        return createUserHoldingStock(stockCode, user, someLongBetween(1L, 10L));
    }

    public UserHoldingStock createUserHoldingStock(String stockCode, User user, Long quantity) {
        final UserHoldingStock userHoldingStock = new UserHoldingStock();
        userHoldingStock.setUserId(user.getId());
        userHoldingStock.setStockCode(stockCode);
        userHoldingStock.setQuantity(quantity);
        userHoldingStock.setPurchasePrice(someLongBetween(1000L, 10000L));
        userHoldingStock.setDisplayOrder(1);
        userHoldingStock.setStatus(Status.ACTIVE);

        final UserHoldingStock savedUserHoldingStock = userHoldingStockRepository.save(userHoldingStock);
        savedUserHoldingStock.setStock(findStock(stockCode));
        connectUserAndUserHoldingStock(user, savedUserHoldingStock);

        return savedUserHoldingStock;
    }

    public UserHoldingStock createDummyUserHoldingStock(String stockCode, User user) {
        final UserHoldingStock userHoldingStock = new UserHoldingStock();
        userHoldingStock.setUserId(user.getId());
        userHoldingStock.setStockCode(stockCode);

        final UserHoldingStock savedUserHoldingStock = userHoldingStockRepository.save(userHoldingStock);
        savedUserHoldingStock.setStock(findStock(stockCode));
        connectUserAndUserHoldingStock(user, savedUserHoldingStock);

        return savedUserHoldingStock;
    }

    public Optional<UserHoldingStock> findUserHoldingStock(Long userId, String stockCode) {
        Optional<UserHoldingStock> optionalUserHoldingStock = userHoldingStockRepository.findFirstByUserIdAndStockCode(userId, stockCode);
        // init stock entity because of lazy loading
        optionalUserHoldingStock.ifPresent(userHoldingStock -> userHoldingStock.getStock().getName());
        return optionalUserHoldingStock;
    }

    public List<UserHoldingStock> findAllUserHoldingStocks() {
        return userHoldingStockRepository.findAll();
    }

    public boolean hasDummyUserStock(Long userId, String stockCode) {
        return userHoldingStockRepository.existsByUserIdAndStockCodeAndStatus(userId, stockCode, null);
    }

    public boolean hasUserStockOnReferenceDate(Long userId, String stockCode) {
        return userHoldingStockOnReferenceDateRepository.existsByUserIdAndStockCode(userId, stockCode);
    }

    private User connectUserAndUserHoldingStock(User user, UserHoldingStock savedUserHoldingStock) {
        savedUserHoldingStock.setUserId(user.getId());
        final List<UserHoldingStock> userHoldingStocks = Optional.ofNullable(user.getUserHoldingStocks()).orElse(new ArrayList<>());
        userHoldingStocks.add(savedUserHoldingStock);
        user.setUserHoldingStocks(userHoldingStocks);
        return userRepository.save(user);
    }

    public UserHoldingStock updateUserHoldingStock(UserHoldingStock userHoldingStock) {
        return userHoldingStockRepository.save(userHoldingStock);
    }

    public UserHoldingStockOnReferenceDate updateUserHoldingStockOnReferenceDate(
        UserHoldingStockOnReferenceDate userHoldingStockOnReferenceDate
    ) {
        return userHoldingStockOnReferenceDateRepository.save(userHoldingStockOnReferenceDate);
    }

    private Stock connectStockAndSolidarity(Stock stock, Solidarity solidarity) {
        stock.setSolidarity(solidarity);
        stock.setSolidarityId(solidarity.getId());
        solidarity.setStock(stock);

        return stockRepository.save(stock);
    }

    public Stock findStock(String stockCode) {
        final Optional<Stock> stockOptional = stockRepository.findByCode(stockCode);
        return stockOptional.orElseThrow(() -> new RuntimeException("[TEST] Stock을 찾을 수 없습니다."));
    }

    public FileContent createImage() {
        MediaType mediaType = someEnum(MediaType.class);
        String extension = mediaType.name().toLowerCase();

        FileContent fileContent = new FileContent();
        fileContent.setFileContentType(DEFAULT);
        fileContent.setFileType(IMAGE);
        fileContent.setFilename("contents/test/%s.%s".formatted(someAlphanumericString(20), extension));
        fileContent.setOriginalFilename(someAlphanumericString(20) + "." + extension);
        fileContent.setMimetype(mediaType.getTypeName());
        fileContent.setStatus(Status.ACTIVE);
        fileContent.setDescription(someAlphanumericString(30));
        return fileContentRepository.save(fileContent);
    }

    public Optional<Board> findBoardByStockCodeAndCategory(String stockCode, BoardCategory boardCategory) {
        return boardRepository.findByStockCodeAndCategory(stockCode, boardCategory);
    }

    public Optional<Board> findBoard(String code, BoardCategory boardCategory) {
        return boardRepository.findByStockCodeAndCategory(code, boardCategory);
    }

    public Board findBoardNonNull(String code, BoardCategory boardCategory) {
        return findBoard(code, boardCategory)
            .orElseThrow(() -> new NotFoundException("해당 보드를 찾을 수 없습니다."));
    }

    public Board updateBoard(Board board) {
        return boardRepository.save(board);
    }

    public Board createBoard(Stock stock) {
        BoardCategory boardCategory = someBoardCategoryForStocks();

        return findBoard(stock.getCode(), boardCategory)
            .orElseGet(() -> createBoard(stock, boardCategory.getBoardGroup(), boardCategory));
    }

    public Board createBoard(Stock stock, BoardGroup boardGroup, BoardCategory boardCategory) {
        final Board board = new Board();

        board.setStockCode(stock.getCode());
        board.setGroup(boardGroup);
        board.setCategory(boardCategory);
        board.setStatus(Status.ACTIVE);

        final Board savedBoard = boardRepository.save(board);
        savedBoard.setStock(stock);

        return savedBoard;
    }

    public void createBoardsForAllCategories(Stock stock) {
        final BoardCategory[] allBoardCategories = BoardCategory.activeBoardCategories(); // All includes GLOBALBOARD

        for (BoardCategory boardCategory : allBoardCategories) {
            createBoard(stock, boardCategory.getBoardGroup(), boardCategory);
        }
    }

    public Board createBoardForVirtualBoardGroup(VirtualBoardGroup virtualBoardGroup, Stock stock) {
        VirtualBoardCategory virtualBoardCategory = someThing(
            virtualBoardGroup
                .getVirtualCategories()
                .toArray(VirtualBoardCategory[]::new)
        );
        BoardCategory boardCategory = someThing(
            virtualBoardCategory
                .getSubCategories()
                .toArray(BoardCategory[]::new)
        );

        return createBoard(stock, boardCategory.getBoardGroup(), boardCategory);
    }

    public PostImage createPostImage(Long postId) {
        return postImageRepository.save(PostImage.builder()
            .imageId(createImage().getId())
            .postId(postId)
            .status(Status.ACTIVE)
            .build()
        );
    }

    public Post createPost(Board board, Long userId) {
        return createPost(board, userId, someBoolean());
    }

    public Post createPost(Board board, Long userId, PostUserProfileDto postUserProfileDto) {
        return createPost(board, userId, someAlphanumericString(10), someBoolean(), false, postUserProfileDto);
    }

    public Post createPost(Board board, Long userId, Boolean isAnonymous) {
        return createPost(board, userId, someAlphanumericString(10), isAnonymous, false);
    }

    public Post createPost(Board board, Long userId, String title, Boolean isAnonymous) {
        return createPost(board, userId, title, isAnonymous, false);
    }

    public Post createPost(Board board, Long userId, Boolean isAnonymous, Boolean isPinned) {
        return createPost(board, userId, someAlphanumericString(10), isAnonymous, isPinned);
    }

    public Post createPost(Board board, Long userId, String title, Boolean isAnonymous, Boolean isPinned) {
        return createPost(board, userId, title, isAnonymous, isPinned, null);
    }

    public Post createPost(
        Board board,
        Long userId,
        String title,
        Boolean isAnonymous,
        Boolean isPinned,
        PostUserProfileDto postUserProfileDto
    ) {
        final Post post = new Post();

        post.setBoard(board);
        post.setBoardId(board.getId());
        post.setTitle(title);
        post.setAnonymousName(someAlphanumericString(10));
        post.setIsAnonymous(isAnonymous);
        post.setContent(someAlphanumericString(10));
        post.setStatus(Status.ACTIVE);
        post.setUserId(userId);
        post.setViewCount(someLongBetween(0L, 100L));
        post.setLikeCount(someLongBetween(0L, 100L));
        post.setViewUserCount(someLongBetween(0L, 100L));
        post.setCommentCount(someLongBetween(0L, 100L));
        post.setIsPinned(isPinned);
        post.setClientType(someEnum(ClientType.class));

        final Post savedPost = postRepository.save(post);

        final User user = findUser(userId);

        final PostUserProfile postUserProfile = createPostUserProfile(
            savedPost.getId(),
            Optional
                .ofNullable(postUserProfileDto)
                .orElseGet(() -> new PostUserProfileDto(
                        savedPost.getId(),
                        user.getProfileImageUrl(),
                        user.getNickname(),
                        "1+",
                        "1주+"
                    )
                )
        );
        savedPost.setPostUserProfile(postUserProfile);

        return savedPost;
    }

    public Post createPostWithLikeCount(Board board, Long userId, Long likeCount) {
        Post post = createPost(board, userId);
        post.setLikeCount(likeCount);
        return updatePost(post);
    }

    public Post createDuplicatePost(Long sourcePostId, Board board, Long userId) {
        Post newPost = createPost(board, userId);
        newPost.setSourcePostId(sourcePostId);
        return newPost;
    }

    public Post createDuplicatePostWithPoll(Post sourcePost, Board board) {
        Poll sourcePoll = sourcePost.getFirstPoll();
        List<PollItem> sourcePollItems = sourcePoll.getPollItemList();

        Post duplicatePost = sourcePost.copyOf(board.getId());
        postRepository.save(duplicatePost);
        Poll duplicatePoll = sourcePoll.copyOf(duplicatePost);

        duplicatePoll.setPollItemList(
            sourcePollItems.stream()
                .map(it -> it.copyOf(duplicatePoll))
                .toList()
        );

        pollRepository.save(duplicatePoll);

        duplicatePost.addPoll(duplicatePoll);
        return duplicatePost;
    }

    private PostUserProfile createPostUserProfile(Long postId, PostUserProfileDto postUserProfileDto) {
        postUserProfileDto.setPostId(postId);
        return createPostUserProfile(postUserProfileDto);
    }

    public PostUserProfile createPostUserProfile(PostUserProfileDto postUserProfileDto) {
        return postUserProfileRepository.save(postUserProfileDto.toEntity());
    }

    public Post updatePost(Post post) {
        return postRepository.save(post);
    }

    public Poll updatePoll(Poll poll) {
        return pollRepository.save(poll);
    }

    public Post updateCreatedAtOfPost(Post post, LocalDateTime createdAt) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        final String updateQuery = "update posts set created_at=? where id=?";

        final int updatedCount = jdbcTemplate.update(
            updateQuery,
            createdAt.format(DateTimeFormatter.ISO_DATE_TIME),
            post.getId()
        );

        if (updatedCount <= 0) {
            throw new RuntimeException("Post의 createAt 컬럼을 업데이트하는데 실패하였습니다.");
        }

        post.setCreatedAt(createdAt);
        return post;
    }

    public Comment createComment(
        Long postId, User user,
        CommentType type, Status status
    ) {

        final Comment comment = new Comment();

        comment.setUserId(user.getId());
        comment.setPostId(postId);
        comment.setParentId(null);
        comment.setType(type);
        comment.setContent(someAlphanumericString(10));
        comment.setAnonymousCount(0);
        comment.setLikeCount(0L);
        comment.setReplyCommentCount(0L);
        comment.setIsAnonymous(false);
        comment.setStatus(status);
        comment.setClientType(someEnum(ClientType.class));

        final Comment savedCommend = commentRepository.save(comment);

        final CommentUserProfile savedCommentUserProfile = createCommentUserProfile(savedCommend, user);
        savedCommend.setCommentUserProfile(savedCommentUserProfile);

        return savedCommend;
    }

    public Comment createComment(
        Long postId, Long userId, Long parentId,
        CommentType type, Status status
    ) {
        final Comment comment = new Comment();

        comment.setUserId(userId);
        comment.setPostId(postId);
        comment.setParentId(parentId);
        comment.setType(type);
        comment.setContent(someAlphanumericString(10));
        comment.setAnonymousCount(0);
        comment.setLikeCount(0L);
        comment.setReplyCommentCount(0L);
        comment.setIsAnonymous(someBoolean());
        comment.setStatus(status);
        comment.setClientType(someEnum(ClientType.class));

        final Comment savedCommend = commentRepository.save(comment);

        final CommentUserProfile savedCommentUserProfile = createCommentUserProfile(savedCommend, findUser(userId));
        savedCommend.setCommentUserProfile(savedCommentUserProfile);

        return savedCommend;
    }

    @NotNull
    private CommentUserProfile createCommentUserProfile(Comment savedCommend, User user) {
        CommentUserProfile commentUserProfile = new CommentUserProfile();
        commentUserProfile.setCommentId(savedCommend.getId());
        commentUserProfile.setNickname(user.getNickname());
        commentUserProfile.setProfileImageUrl(user.getProfileImageUrl());
        commentUserProfile.setIndividualStockCountLabel("1주+");
        commentUserProfile.setTotalAssetLabel("1+");

        return commentUserProfileRepository.save(commentUserProfile);
    }

    public Comment updateComment(Comment comment) {
        return commentRepository.save(comment);
    }

    public Post createPoll(Post post) {
        final LocalDateTime targetStartDate = someLocalDateTimeInTheFuture();

        return createPoll(post, 2, someEnum(SelectionOption.class), targetStartDate, targetStartDate.plusDays(someIntegerBetween(1, 10)));
    }

    public Post createPoll(Post post, int addItem, SelectionOption selectionOption) {
        final LocalDateTime targetStartDate = someLocalDateTimeInTheFuture();

        return createPoll(post, addItem, selectionOption, targetStartDate, targetStartDate.plusDays(someIntegerBetween(1, 10)));
    }

    public Post createPoll(Post post, int pollItemCount, SelectionOption selectionOption, LocalDateTime startDate, LocalDateTime endDate) {
        final Poll poll = new Poll();

        poll.setTitle(someAlphanumericString(10));
        poll.setStatus(Status.ACTIVE);
        poll.setVoteType(someEnum(VoteType.class));
        poll.setSelectionOption(selectionOption);
        poll.setTargetStartDate(startDate);
        poll.setTargetEndDate(endDate);

        List<PollItem> pollItems = new ArrayList<>();
        for (int index = 0; index < pollItemCount; index++) {
            pollItems.add(createPollItem(poll));
        }

        poll.setPollItemList(pollItems);
        poll.setPost(post);
        post.addPoll(poll);

        pollRepository.save(poll);
        return postRepository.save(post);
    }

    public void createPolls(
        Post post, int pollCount, int pollItemCount,
        SelectionOption selectionOption, LocalDateTime startDate, LocalDateTime endDate
    ) {
        List<Poll> polls = new ArrayList<>();
        for (int index = 0; index < pollCount; index++) {
            final Poll poll = new Poll();

            poll.setTitle(someAlphanumericString(10));
            poll.setContent(someString(100));
            poll.setStatus(Status.ACTIVE);
            poll.setVoteType(someEnum(VoteType.class));
            poll.setSelectionOption(selectionOption);
            poll.setTargetStartDate(startDate);
            poll.setTargetEndDate(endDate);

            List<PollItem> pollItems = new ArrayList<>();
            for (int innerIndex = 0; innerIndex < pollItemCount; innerIndex++) {
                pollItems.add(createPollItem(poll));
            }

            poll.setPollItemList(pollItems);
            poll.setPost(post);
            if (index == 0) {
                post.addPoll(poll);
            }

            polls.add(pollRepository.save(poll));
        }
        post.setPolls(polls);
        postRepository.save(post);
    }

    public PollAnswer createPollAnswer(Long userId, Long pollId, Long pollItemId) {
        return createPollAnswer(userId, pollId, pollItemId, someLongBetween(1L, 10L));
    }

    public PollAnswer createPollAnswer(Long userId, Long pollId, Long pollItemId, Long stockQuantity) {
        PollAnswer pollAnswer = new PollAnswer();
        pollAnswer.setPollId(pollId);
        pollAnswer.setPollItemId(pollItemId);
        pollAnswer.setUserId(userId);
        pollAnswer.setStockQuantity(stockQuantity);
        pollAnswer.setStatus(Status.ACTIVE);

        pollAnswerRepository.save(pollAnswer);
        return pollAnswer;
    }

    private PollItem createPollItem(Poll poll) {
        PollItem pollItem = new PollItem();
        pollItem.setText(someAlphanumericString(10));
        pollItem.setStatus(Status.ACTIVE);
        pollItem.setPoll(poll);

        return pollItem;
    }

    public SolidarityLeader createSolidarityLeader(Solidarity solidarity, Long userId) {
        final SolidarityLeader solidarityLeader = new SolidarityLeader();
        solidarityLeader.setUserId(userId);
        solidarityLeader.setSolidarity(solidarity);

        solidarity.setSolidarityLeader(solidarityLeader);

        return solidarityLeaderRepository.save(solidarityLeader);
    }

    public SolidarityLeader updateSolidarityLeader(SolidarityLeader solidarityLeader) {
        return solidarityLeaderRepository.save(solidarityLeader);
    }

    public SolidarityLeaderApplicant createSolidarityLeaderApplicant(Long solidarityId, Long userId) {
        final SolidarityLeaderApplicant solidarityLeaderApplicant = new SolidarityLeaderApplicant();
        solidarityLeaderApplicant.setUserId(userId);
        solidarityLeaderApplicant.setSolidarityId(solidarityId);
        solidarityLeaderApplicant.setVersion(SOLIDARITY_APPLY_DEFAULT_VERSION);
        solidarityLeaderApplicant.setApplyStatus(SolidarityLeaderElectionApplyStatus.COMPLETE);

        return solidarityLeaderApplicantRepository.save(solidarityLeaderApplicant);
    }

    public SolidarityLeaderApplicant createSolidarityLeaderApplicant(
        Long solidarityId,
        Long userId,
        SolidarityLeaderElectionApplyStatus saveStatus,
        Long solidarityLeaderElectionId
    ) {
        final SolidarityLeaderApplicant solidarityLeaderApplicant = new SolidarityLeaderApplicant();
        solidarityLeaderApplicant.setUserId(userId);
        solidarityLeaderApplicant.setSolidarityId(solidarityId);
        solidarityLeaderApplicant.setVersion(SOLIDARITY_APPLY_NEW_VERSION);
        solidarityLeaderApplicant.setApplyStatus(saveStatus);
        solidarityLeaderApplicant.setReasonsForApplying(someSolidarityLeaderElectionApplicationItem());
        solidarityLeaderApplicant.setKnowledgeOfCompanyManagement(someSolidarityLeaderElectionApplicationItem());
        solidarityLeaderApplicant.setGoals(someSolidarityLeaderElectionApplicationItem());
        solidarityLeaderApplicant.setCommentsForStockHolder(someSolidarityLeaderElectionApplicationItem());
        solidarityLeaderApplicant.setSolidarityLeaderElectionId(solidarityLeaderElectionId);

        return solidarityLeaderApplicantRepository.save(solidarityLeaderApplicant);
    }

    public SolidarityLeaderApplicant createSolidarityLeaderApplicant(
        Long solidarityElectionId,
        Long solidarityId,
        Long userId,
        SolidarityLeaderElectionApplyStatus saveStatus,
        String reasonsForApplying,
        String knowledgeOfCompanyManagement,
        String goals,
        String commentsForStockHolder
    ) {
        final SolidarityLeaderApplicant solidarityLeaderApplicant = new SolidarityLeaderApplicant();
        solidarityLeaderApplicant.setUserId(userId);
        solidarityLeaderApplicant.setSolidarityLeaderElectionId(solidarityElectionId);
        solidarityLeaderApplicant.setSolidarityId(solidarityId);
        solidarityLeaderApplicant.setVersion(SOLIDARITY_APPLY_NEW_VERSION);
        solidarityLeaderApplicant.setApplyStatus(saveStatus);
        solidarityLeaderApplicant.setReasonsForApplying(reasonsForApplying);
        solidarityLeaderApplicant.setKnowledgeOfCompanyManagement(knowledgeOfCompanyManagement);
        solidarityLeaderApplicant.setGoals(goals);
        solidarityLeaderApplicant.setCommentsForStockHolder(commentsForStockHolder);

        return solidarityLeaderApplicantRepository.save(solidarityLeaderApplicant);
    }

    public SolidarityLeaderApplicant updateSolidarityLeaderApplicant(SolidarityLeaderApplicant solidarityLeaderApplicant) {
        return solidarityLeaderApplicantRepository.save(solidarityLeaderApplicant);
    }

    private LocalDateTime calculateCandidateEndDateTime(LocalDateTime candidateStartDateTime) {
        if (candidateStartDateTime == null) {
            return null;
        }

        final int durationDays = SolidarityLeaderElectionProcedure.RECRUIT_APPLICANTS.getDurationDays();
        final int daysToMinus = candidateStartDateTime.getHour() >= END_HOURS_IN_UTC ? 0 : 1;

        return candidateStartDateTime.plusDays(durationDays)
            .minusDays(daysToMinus)
            .withHour(END_HOURS_IN_UTC)
            .withMinute(0)
            .withSecond(0)
            .withNano(0);
    }

    public SolidarityLeaderElection createSolidarityElection(String stockCode, SolidarityLeaderElectionStatusGroup statusGroup) {
        SolidarityLeaderElection solidarityLeaderElection = new SolidarityLeaderElection();
        solidarityLeaderElection.setStockCode(stockCode);
        solidarityLeaderElection.setElectionStatusGroup(statusGroup);
        return solidarityLeaderElectionRepository.save(solidarityLeaderElection);
    }

    public SolidarityLeaderElection createSolidarityElection(String stockCode, LocalDateTime candidateStartDateTime) {
        return createSolidarityElection(
            stockCode,
            new SolidarityLeaderElectionStatusGroup(
                SolidarityLeaderElectionStatus.CANDIDATE_REGISTRATION_PENDING_PERIOD,
                SolidarityLeaderElectionStatusDetails.IN_PROGRESS
            ),
            candidateStartDateTime
        );
    }

    public SolidarityLeaderElection createSolidarityElection(
        String stockCode,
        SolidarityLeaderElectionStatusGroup statusGroup,
        LocalDateTime candidateStartDateTime
    ) {
        final LocalDateTime candidateEndDateTime = calculateCandidateEndDateTime(candidateStartDateTime);

        return createSolidarityElection(
            stockCode,
            statusGroup,
            candidateStartDateTime,
            candidateEndDateTime
        );
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    public SolidarityLeaderElection createSolidarityElection(
        String stockCode,
        SolidarityLeaderElectionStatusGroup statusGroup,
        LocalDateTime candidateStartDateTime,
        LocalDateTime candidateEndDateTime
    ) {
        SolidarityLeaderElection solidarityLeaderElection = new SolidarityLeaderElection();
        solidarityLeaderElection.setStockCode(stockCode);
        solidarityLeaderElection.setElectionStatusGroup(statusGroup);

        solidarityLeaderElection.setCandidateRegistrationStartDateTime(candidateStartDateTime);
        solidarityLeaderElection.setCandidateRegistrationEndDateTime(candidateEndDateTime);

        if (candidateEndDateTime != null) {
            final LocalDateTime voteStartDateTime = candidateEndDateTime;
            final LocalDateTime voteEndDateTime = voteStartDateTime.plusDays(SolidarityLeaderElectionProcedure.START_VOTING.getDurationDays());
            solidarityLeaderElection.setVoteStartDateTime(voteStartDateTime);
            solidarityLeaderElection.setVoteEndDateTime(voteEndDateTime);
        }

        return solidarityLeaderElectionRepository.save(solidarityLeaderElection);
    }

    public void startSolidarityLeaderElection(
        SolidarityLeaderElection solidarityLeaderElection
    ) {
        final LocalDateTime today = KoreanDateTimeUtil.getTodayLocalDate().atStartOfDay();
        final LocalDateTime candidateRegistrationStartDateTime =
            today.minusDays(someIntegerBetween(1, SolidarityLeaderElectionProcedure.RECRUIT_APPLICANTS.getDurationDays()));

        startSolidarityLeaderElection(
            solidarityLeaderElection,
            candidateRegistrationStartDateTime,
            candidateRegistrationStartDateTime.plusDays(SolidarityLeaderElectionProcedure.RECRUIT_APPLICANTS.getDurationDays()));
    }

    public void startSolidarityLeaderElection(
        SolidarityLeaderElection solidarityLeaderElection,
        LocalDateTime candidateRegistrationStartDateTime,
        LocalDateTime candidateRegistrationEndDateTime
    ) {
        solidarityLeaderElection.setElectionStatusGroup(
            new SolidarityLeaderElectionStatusGroup(
                SolidarityLeaderElectionStatus.CANDIDATE_REGISTRATION_PERIOD,
                SolidarityLeaderElectionStatusDetails.IN_PROGRESS
            )
        );
        solidarityLeaderElection.setCandidateRegistrationStartDateTime(candidateRegistrationStartDateTime);
        solidarityLeaderElection.setCandidateRegistrationEndDateTime(candidateRegistrationEndDateTime);
        updateSolidarityElection(solidarityLeaderElection);
    }

    public SolidarityLeaderElection updateCandidateCount(SolidarityLeaderElection election, int count) {
        election.setCandidateCount(count);
        return updateSolidarityElection(election);
    }

    public SolidarityLeaderElection updateSolidarityElection(SolidarityLeaderElection solidarityLeaderElection) {
        return solidarityLeaderElectionRepository.save(solidarityLeaderElection);
    }

    public Optional<SolidarityLeaderElection> findSolidarityLeaderElectionByStockCode(String stockCode) {
        return solidarityLeaderElectionRepository.findTopByStockCodeOrderByCreatedAtDesc(stockCode);
    }

    public List<SolidarityLeaderElection> findAllSolidarityLeaderElections() {
        return solidarityLeaderElectionRepository.findAll();
    }

    public List<SolidarityLeaderElectionPollItemMapping> findAllSolidarityLeaderElectionPollItemMappingBySolidarityLeaderElectionId(
        Long solidarityLeaderElectionId
    ) {
        return solidarityLeaderElectionPollItemMappingRepository.findAllBySolidarityLeaderElectionId(solidarityLeaderElectionId);
    }

    public SolidarityLeaderElectionPollItemMapping createSolidarityLeaderElectionPollItemMapping(
        Long solidarityLeaderElectionId, Long solidarityLeaderApplicantId, Long pollItemId, SolidarityLeaderElectionAnswerType electionAnswerType
    ) {
        SolidarityLeaderElectionPollItemMapping mapping = new SolidarityLeaderElectionPollItemMapping();
        mapping.setSolidarityLeaderElectionId(solidarityLeaderElectionId);
        mapping.setSolidarityLeaderApplicantId(solidarityLeaderApplicantId);
        mapping.setPollItemId(pollItemId);
        mapping.setElectionAnswerType(electionAnswerType);
        return solidarityLeaderElectionPollItemMappingRepository.save(mapping);
    }

    public Post createSolidarityLeaderElectionPost(SolidarityLeaderElection solidarityElection) {
        final String stockCode = solidarityElection.getStockCode();
        final Stock stock = findStock(stockCode);
        final Board board = findSolidarityElectionBoard(stockCode).orElseThrow();

        Post post = createPost(board, ActUserProvider.getSystemUserId());

        post.setTitle("%s 주주대표 선출 투표".formatted(stock.getName()));
        post.setStatus(Status.INACTIVE_BY_ADMIN);
        post = updatePost(post);

        return post;
    }

    public Post createSolidarityLeaderElectionPostAndPoll(
        SolidarityLeaderElection solidarityElection,
        List<SolidarityLeaderApplicant> solidarityLeaderApplicants
    ) {
        Post post = createSolidarityLeaderElectionPost(solidarityElection);

        post = createPoll(post, solidarityLeaderApplicants.size() * 2, SelectionOption.MULTIPLE_ITEMS);

        Poll poll = post.getFirstPoll();

        final LocalDateTime currentDateTime = LocalDateTime.now();
        poll.setTargetStartDate(currentDateTime);
        poll.setTargetEndDate(currentDateTime.plusDays(3));
        poll.setTitle("주주대표 선출 투표");

        List<PollItem> pollItemList = poll.getPollItemList();

        for (int pollItemIndex = 0; pollItemIndex < pollItemList.size(); pollItemIndex++) {
            createSolidarityLeaderElectionPollItemMapping(
                solidarityElection.getId(),
                solidarityLeaderApplicants.get(pollItemIndex / 2).getId(),
                pollItemList.get(pollItemIndex).getId(),
                (pollItemIndex % 2 == 0) ? SolidarityLeaderElectionAnswerType.APPROVAL : SolidarityLeaderElectionAnswerType.REJECTION
            );
        }

        solidarityElection.setPostId(post.getId());

        return post;
    }

    private Optional<Board> findSolidarityElectionBoard(String stockCode) {
        return boardRepository.findByStockCodeAndGroupAndCategory(stockCode, BoardGroup.ANALYSIS, BoardCategory.SOLIDARITY_LEADER_ELECTION);
    }

    public DigitalProxy createDigitalProxy(Post post) {
        final LocalDateTime targetStartDate = someLocalDateTimeInTheFuture();
        final LocalDateTime targetEndDate = targetStartDate.plusDays(someIntegerBetween(1, 10));
        return createDigitalProxy(post, targetStartDate, targetEndDate);
    }

    public DigitalProxy createDigitalProxy(Post post, LocalDateTime targetStartDate, LocalDateTime targetEndDate) {
        final DigitalProxy digitalProxy = new DigitalProxy();

        digitalProxy.setPostId(post.getId());
        digitalProxy.setTemplateId(someString(20));
        digitalProxy.setTemplateName(someString(17));
        digitalProxy.setTemplateRole(someString(15));
        digitalProxy.setTargetStartDate(targetStartDate);
        digitalProxy.setTargetEndDate(targetEndDate);
        digitalProxy.setStatus(Status.ACTIVE);

        DigitalProxy savedDigitalProxy = digitalProxyRepository.save(digitalProxy);
        connectPostAndDigitalProxy(post, savedDigitalProxy);

        return savedDigitalProxy;
    }

    private void connectPostAndDigitalProxy(Post post, DigitalProxy savedDigitalProxy) {
        post.setDigitalProxy(savedDigitalProxy);

        postRepository.save(post);
    }

    public Post createDigitalProxyApproval(Post post, Long userId, String documentId, String participantId) {

        final DigitalProxy digitalProxy = post.getDigitalProxy();
        if (CollectionUtils.isEmpty(digitalProxy.getDigitalProxyApprovalList())) {
            digitalProxy.setDigitalProxyApprovalList(new ArrayList<>());
        }

        final DigitalProxyApproval approval = new DigitalProxyApproval();
        approval.setUserId(userId);
        approval.setStatus(Status.ACTIVE);
        approval.setDocumentId(documentId);
        approval.setParticipantId(participantId);
        digitalProxy.getDigitalProxyApprovalList().add(approval);

        return postRepository.save(post);
    }

    public Report createReport(Long contentId, User user, ReportType type) {
        return createReport(contentId, user, type, ReportStatus.READY);
    }

    public Report createReport(Long contentId, User user, ReportType type, ReportStatus status) {
        Report report = new Report();
        report.setContentId(contentId);
        report.setUserId(user.getId());
        report.setReportStatus(status);
        report.setReason(someAlphanumericString(10));
        report.setType(type);
        return reportRepository.save(report);
    }

    public Report createReportAndHistory(Long contentId, User user, ReportType type, ReportStatus status) {
        Report report = new Report();
        report.setContentId(contentId);
        report.setUserId(user.getId());
        report.setReportStatus(status);
        report.setReason(someAlphanumericString(10));
        report.setType(type);
        report.setReportHistoryList(getReportHistoryList(user.getId()));
        reportRepository.save(report);
        return report;
    }

    private ReportHistory getReportHistory(Long userId, ReportStatus reportStatus) {
        ReportHistory reportHistory = new ReportHistory();
        reportHistory.setUserId(userId);
        reportHistory.setResult(someString(10));
        reportHistory.setReportStatus(reportStatus);
        return reportHistory;
    }

    public List<ReportHistory> getReportHistoryList(Long userId) {
        return List.of(
            getReportHistory(userId, ReportStatus.PROCESSING),
            getReportHistory(userId, ReportStatus.COMPLETE),
            getReportHistory(userId, ReportStatus.REJECT)
        );
    }

    public Optional<Report> findReportById(Long id) {
        return reportRepository.findById(id);
    }

    public Optional<Comment> findCommentById(Long id) {
        return commentRepository.findById(id);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public Optional<Post> findPostAndDigitalProxy(Long postId) {
        final Optional<Post> optionalPost = findPost(postId);
        optionalPost.ifPresent(post -> post.getDigitalProxy().getDigitalProxyApprovalList().size());
        return optionalPost;
    }

    public User createUserRole(User user, RoleType roleType) {
        UserRole userRole = new UserRole();
        userRole.setUserId(user.getId());

        Role role = findRoleByType(roleType);
        userRole.setRoleId(role.getId());
        userRole.setRole(role);

        final UserRole savedUserRole = userRoleRepository.save(userRole);

        final List<UserRole> userRoles = Optional.ofNullable(user.getRoles()).orElse(new ArrayList<>());
        userRoles.add(savedUserRole);
        user.setRoles(userRoles);

        if (List.of(RoleType.ADMIN, RoleType.ACCEPTOR_USER).contains(roleType)) {
            final String hashedPhoneNumber = user.getHashedPhoneNumber();
            final String adminPassword = decrypt(hashedPhoneNumber);
            user.setPassword(passwordEncoder.encode(adminPassword));
        }

        user.setAdmin(isAdmin(user));
        user.setAcceptor(isAcceptorUser(user));

        return updateUser(user);
    }

    private boolean isAcceptorUser(User user) {
        return hasRole(user, RoleType.ACCEPTOR_USER)
            || userRoleService.isAcceptorUser(user.getId());
    }

    private boolean isAdmin(User user) {
        return hasRole(user, RoleType.ADMIN)
            || userRoleService.isAdmin(user.getId());
    }

    private boolean hasRole(User user, RoleType roleType) {
        return emptyIfNull(user.getRoles()).stream()
            .anyMatch(userRole -> userRole.getRole().getType() == roleType);
    }

    public User someAdminUser() {
        return createUserWithRole(someThing(RoleType.ADMIN, RoleType.SUPER_ADMIN));
    }

    public User createAdminUser() {
        return createUserWithRole(RoleType.ADMIN);
    }

    public User createSuperAdminUser() {
        return createUserWithRole(RoleType.SUPER_ADMIN);
    }

    public User createAcceptorUser() {
        return createUserWithRole(RoleType.ACCEPTOR_USER);
    }

    public User createUserWithRole(RoleType roleType) {
        return createUserRole(createUser(), roleType);
    }

    public Role findRoleByType(RoleType roleType) {
        return roleRepository.findByType(roleType)
            .orElseThrow(() -> new RuntimeException("[TEST] 회원 role 을 찾을 수 없습니다."));
    }

    public Optional<Post> findPost(Long postId) {
        final Optional<Post> postOptional = postRepository.findById(postId);

        postOptional.ifPresent(this::lazyLoadRelationsAndReturn);

        return postOptional;
    }

    public Post findPostNoneNull(Long postId) {
        final Post post = postRepository.findById(postId).orElseThrow();

        return lazyLoadRelationsAndReturn(post);
    }

    private Post lazyLoadRelationsAndReturn(Post post) {
        Objects.nonNull(post);

        post.getDigitalDocument();
        post.getDigitalProxy();
        post.getBoard();
        post.getPolls();
        post.getFirstPoll();
        post.getPostUserProfile();

        return post;
    }

    public Optional<Post> findOnePostByBoardId(Long boardId) {
        List<Post> posts = postRepository.findAllByBoardId(boardId);
        if (CollectionUtils.isEmpty(posts)) {
            return Optional.empty();
        }

        if (posts.size() > 1) {
            throw new RuntimeException("[TEST] 게시글이 2개 이상입니다.");
        }

        return Optional.of(lazyLoadRelationsAndReturn(posts.get(0)));
    }

    public List<Post> findPostListByBoardId(Long boardId) {
        return postRepository.findAllByBoardId(boardId);
    }

    public List<PostImage> findPostImagesByPostId(Long postId) {
        return postImageRepository.findAllByPostIdAndStatusNotIn(postId, StatusUtil.getDeleteStatuses());
    }

    public FileContent getFileContent(Long fileContentId) {
        return fileContentRepository.findByIdIn(List.of(fileContentId)).get(0);
    }

    public User findUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("[TEST] 회원을 찾을 수 없습니다."));

        // To fetch roles/userHoldingStocks in Lazy mode.
        emptyIfNull(user.getRoles()).stream()
            .map(UserRole::getRole)
            .forEach(Role::getType);
        emptyIfNull(user.getUserHoldingStocks())
            .forEach(userHoldingStock -> userHoldingStock.getStock().getName());

        user.setAdmin(isAdmin(user));
        user.setAcceptor(isAcceptorUser(user));

        return user;
    }

    public Optional<BatchLog> findLastBatchLog(String batchName) {
        return batchLogRepository.findFirstByBatchNameOrderByUpdatedAtDesc(batchName);
    }

    public Optional<BatchLog> findBatchLogById(Long batchLogId) {
        return batchLogRepository.findById(batchLogId);
    }

    public List<BoardGroup> findAllBoardGroups() {
        return List.of(BoardGroup.values());
    }

    public BatchLog updateBatchLog(BatchLog batchLog) {
        return batchLogRepository.save(batchLog);
    }

    public void deleteBatchLog(String batchName) {
        findLastBatchLog(batchName).ifPresent(batchLog -> batchLogRepository.delete(batchLog));
    }

    public SolidarityDailySummary createSolidarityDailySummary() {
        final SolidarityDailySummary solidarityDailySummary = new SolidarityDailySummary();
        solidarityDailySummary.setStockQuantity(someLongBetween(1L, 10000L));
        solidarityDailySummary.setStake(someIntegerBetween(0, 10000) * 0.0001);
        solidarityDailySummary.setMemberCount(someIntegerBetween(0, 300));
        solidarityDailySummary.setMarketValue(someLongBetween(1L, 100000000L));

        return solidarityDailySummaryRepository.save(solidarityDailySummary);
    }

    public SolidarityDailySummary updateSolidarityDailySummary(SolidarityDailySummary summary) {
        return solidarityDailySummaryRepository.save(summary);
    }

    public void assertSimpleOkay(MvcResult response)
        throws UnsupportedEncodingException, JsonProcessingException {
        assertSimpleMessage(response, "ok");
    }

    public void assertSimpleMessage(MvcResult response, String message)
        throws UnsupportedEncodingException, JsonProcessingException {

        final SimpleStringResponse result1 = getResult(response, SimpleStringResponse.class);
        assertThat(result1.getStatus(), is(message));
    }

    public void assertErrorResponse(MvcResult response, int httpStatus, int errorCode, String message)
        throws UnsupportedEncodingException, JsonProcessingException {

        final ErrorResponse result = getResult(response, ErrorResponse.class);

        assertThat(result.getStatusCode(), is(httpStatus));
        assertThat(result.getMessage(), is(message));
        assertThat(result.getErrorCode(), is(errorCode));
    }

    public void assertErrorResponse(MvcResult response, int httpStatus, String message)
        throws JsonProcessingException, UnsupportedEncodingException {

        final ErrorResponse result = getResult(response, ErrorResponse.class);

        assertThat(result.getStatusCode(), is(httpStatus));
        assertThat(result.getMessage(), is(message));
    }

    public void assertErrorResponse(MvcResult response, int httpStatus, String message, ActErrorCode actErrorCode)
        throws JsonProcessingException, UnsupportedEncodingException {
        assertErrorResponse(response, httpStatus, message, actErrorCode.getCode());
    }

    public void assertErrorResponse(MvcResult response, int httpStatus, String message, ActErrorCode actErrorCode, Map<String, Object> errorData)
        throws JsonProcessingException, UnsupportedEncodingException {
        assertErrorResponse(response, httpStatus, message, actErrorCode.getCode(), errorData);
    }

    public void assertErrorResponse(MvcResult response, int httpStatus, String message, int errorCode)
        throws JsonProcessingException, UnsupportedEncodingException {
        assertErrorResponse(response, httpStatus, message, errorCode, null);
    }

    public void assertErrorResponse(
        MvcResult response,
        int httpStatus,
        String message,
        int errorCode,
        @Nullable Map<String, Object> errorData
    ) throws JsonProcessingException, UnsupportedEncodingException {

        final ErrorResponse result = getResult(response, ErrorResponse.class);

        assertThat(result.getStatusCode(), is(httpStatus));
        assertThat(result.getErrorCode(), is(errorCode));
        assertThat(result.getMessage(), is(message));

        if (errorData == null) {
            assertThat(result.getErrorData(), nullValue());
        } else {
            assertThat(result.getErrorData(), notNullValue());
            assertThat("ErrorData is not matched.", result.getErrorData().toString(), is(errorData.toString()));
        }
    }

    public void assertErrorResponse(MvcResult response, int httpStatus, Matcher<String> messageMatcher)
        throws JsonProcessingException, UnsupportedEncodingException {

        final ErrorResponse result = getResult(response, ErrorResponse.class);

        assertThat(result.getStatusCode(), is(httpStatus));
        assertThat(result.getMessage(), is(messageMatcher));
    }

    public void assertErrorResponseContainsString(MvcResult response, int httpStatus, String message)
        throws JsonProcessingException, UnsupportedEncodingException {

        final ErrorResponse result = getResult(response, ErrorResponse.class);

        assertThat(result.getStatusCode(), is(httpStatus));
        assertThat(result.getMessage(), is(containsString(message)));
    }

    public void assertPostTitleAndContent(Post post, PostResponse postResponse) {
        if (post.getIsExclusiveToHolders()) {
            assertThat(postResponse.getTitle(), is("주주에게만 공개된 제한된 글입니다."));
            assertThat(postResponse.getContent(), is("주주에게만 공개된 제한된 글입니다."));
        } else if (post.getStatus() == Status.DELETED_BY_ADMIN) {
            assertThat(postResponse.getTitle(), is("관리자에 의해 삭제된 게시글입니다."));
            assertThat(postResponse.getContent(), is("관리자에 의해 삭제된 게시글입니다."));
        } else if (post.getStatus() == Status.DELETED_BY_USER) {
            assertThat(postResponse.getTitle(), is("삭제된 게시글입니다."));
            assertThat(postResponse.getContent(), is("삭제된 게시글입니다."));
        } else {
            assertThat(postResponse.getTitle(), is(post.getTitle()));
            assertThat(postResponse.getDeleted(), is(false));
        }

        assertPostDeleted(post, postResponse);
        // TODO isReported 도 추가하면 좋겠다.
    }

    public void assertPostTitleAndContentForAdmin(Post post, PostResponse postResponse) {
        assertThat(postResponse.getTitle(), is(post.getTitle()));
    }

    public void assertPostTitleAndContentForAdmin(Post post, PostDetailsResponse postResponse) {
        assertThat(postResponse.getTitle(), is(post.getTitle()));
    }

    public void assertPostBoardGroupAndCategory(Post post, PostDetailsResponse postResponse) {
        assertThat(postResponse.getBoardGroup(), is(post.getBoard().getGroup().name()));
        assertThat(postResponse.getBoardCategory().getName(), is(post.getBoard().getCategory().name()));
    }

    private void assertPostDeleted(Post post, PostResponse postResponse) {
        assertThat(
            postResponse.getDeleted(),
            is(is(List.of(Status.DELETED_BY_ADMIN, Status.DELETED_BY_USER).contains(post.getStatus())))
        );
    }

    public void assertSort(String actualSort, String expectedSort) {
        final String[] splitExceptedSort = expectedSort.split(":");
        assertSort(actualSort, splitExceptedSort[0], splitExceptedSort[1]);
    }

    public void assertSort(String actualSort, String expectedSortName, String expectedSortDirection) {
        assertThat(actualSort, startsWith(expectedSortName));
        assertThat(actualSort, endsWithIgnoringCase(expectedSortDirection));
    }

    public <T> T getResult(MvcResult response, Class<T> responseType) throws JsonProcessingException, UnsupportedEncodingException {
        return objectMapperUtil.toResponse(response.getResponse().getContentAsString(), responseType);
    }

    public PostUserView createPostUserView(User user, Post post) {
        final PostUserView postUserView = new PostUserView();
        postUserView.setUserId(user.getId());
        postUserView.setPostId(post.getId());
        postUserView.setCount(somePositiveLong());

        return postUserViewRepository.save(postUserView);
    }

    public PostUserLike createPostUserLike(User user, Post post) {
        final PostUserLike postUserView = new PostUserLike();
        postUserView.setUserId(user.getId());
        postUserView.setPostId(post.getId());

        return postUserLikeRepository.save(postUserView);
    }

    public boolean existsByPostIdAndUserId(Long postId, Long userId) {
        return postUserLikeRepository.existsByPostIdAndUserId(postId, userId);
    }

    public List<Comment> findComments(Long postId, Long userId) {
        return commentRepository.findAll().stream()
            .filter(comment -> comment.getPostId().equals(postId))
            .filter(comment -> comment.getUserId().equals(userId))
            .filter(comment -> comment.getCommentUserProfile() != null)
            .toList();
    }

    private Push newPush(String title, String content, PushTargetType pushTargetType, String topicName) {
        Push push = newPush(content, pushTargetType, topicName);
        push.setTitle(title);
        return push;
    }

    private Push newPush(String content, PushTargetType pushTargetType, String topicName) {
        Push push = new Push();
        push.setTitle(someAlphanumericString(10));
        push.setContent(content);
        push.setResult(someAlphanumericString(10));
        push.setSendType(someEnum(PushSendType.class));
        push.setTopic(topicName);
        push.setSendStatus(someEnum(PushSendStatus.class));
        push.setSentEndDatetime(someLocalDateTimeInTheFuture());
        push.setSentStartDatetime(someLocalDateTimeInTheFuture());
        push.setTargetDatetime(someLocalDateTimeInTheFuture());
        push.setResult(someAlphanumericString(20));
        push.setPushTargetType(pushTargetType);

        push.setLinkType(AppLinkType.NONE);
        push.setLinkUrl(someAlphanumericString(30));
        return push;
    }

    public Push createPush(String content, PushTargetType pushTargetType) {
        Push push = newPush(content, pushTargetType, PushTopic.NOTICE.name());

        return pushRepository.save(push);
    }

    public Push createPush(String title, String content, PushTargetType pushTargetType) {
        Push push = newPush(title, content, pushTargetType, PushTopic.NOTICE.name());

        return pushRepository.save(push);
    }

    public Push createPush(String content, Stock stock, PushTargetType pushTargetType) {
        Push push = newPush(content, pushTargetType, null);
        push.setStock(stock);
        push.setStockCode(stock.getCode());

        return pushRepository.save(push);
    }

    public Push createPush(String content, StockGroup stockGroup, PushTargetType pushTargetType) {
        Push push = newPush(content, pushTargetType, null);
        push.setStockGroup(stockGroup);
        push.setStockGroupId(stockGroup.getId());

        return pushRepository.save(push);
    }

    public Push createPush(String content, PushTargetType pushTargetType, Long postId) {
        Push push = newPush(content, pushTargetType, null);
        push.setPostId(postId);

        return pushRepository.save(push);
    }

    public Push createPush(String title, String content, PushTargetType pushTargetType, Long postId) {
        Push push = newPush(title, content, pushTargetType, null);
        push.setPostId(postId);

        return pushRepository.save(push);
    }

    public Push updatePush(Push push) {
        return pushRepository.save(push);
    }

    public Popup updatePopup(Popup popup) {
        return popupRepository.save(popup);
    }

    public Popup createPopup(String title) {
        return createPopup(title, someLocalDateTimeInThePast(), someLocalDateTimeInTheFuture());
    }

    public Popup createPopup(LocalDateTime targetStartDate, LocalDateTime targetEndDate) {
        return createPopup(someString(10), targetStartDate, targetEndDate);
    }

    public Popup createPopup(String title, LocalDateTime targetStartDate, LocalDateTime targetEndDate) {
        return createPopup(
            title, someEnum(PopupDisplayTargetType.class), someEnum(PushTargetType.class), someEnum(AppLinkType.class),
            null, null, targetStartDate, targetEndDate);
    }

    public Popup createPopup(
        String title, PopupDisplayTargetType displayTargetType, PushTargetType stockTargetType, AppLinkType linkType,
        String stockCode, Long stockGroupId, LocalDateTime targetStartDate, LocalDateTime targetEndDate
    ) {
        Popup popup = new Popup();
        popup.setTitle(title);
        popup.setContent(someAlphanumericString(10));
        popup.setTargetStartDatetime(targetStartDate);
        popup.setTargetEndDatetime(targetEndDate);
        popup.setDisplayTargetType(displayTargetType);
        popup.setStockTargetType(stockTargetType);
        popup.setLinkType(linkType);
        popup.setLinkTitle(someAlphanumericString(10));
        popup.setLinkUrl(someAlphanumericString(30));
        popup.setStockCode(stockCode);
        popup.setStockGroupId(stockGroupId);
        popup.setStatus(ag.act.model.Status.ACTIVE);

        return popupRepository.save(popup);
    }

    public Optional<Popup> findPopup(Long popupId) {
        return popupRepository.findById(popupId);
    }

    public Optional<Push> findPush(Long pushId) {
        return pushRepository.findById(pushId);
    }

    public List<Push> findAllPushesByPostId(Long postId) {
        return pushRepository.findAllByPostId(postId);
    }

    public List<Push> findAllPushesByPostIdAndSendStatus(Long postId, PushSendStatus sendStatus) {
        return pushRepository.findAllByPostIdAndSendStatus(postId, sendStatus);
    }

    public List<Push> findAllPushes() {
        return pushRepository.findAll();
    }

    public void deleteAllPushes() {
        pushRepository.deleteAll();
    }

    public void deleteAllUserHoldingStock() {
        userHoldingStockRepository.deleteAll();
    }

    public void deleteAllMyDataSummary() {
        myDataSummaryRepository.deleteAll();
    }

    public PrivateStock updatePrivateStock(PrivateStock privateStock) {
        return privateStockRepository.save(privateStock);
    }

    public record JsonMyDataStockDto(Long userId, String code, Long quantity, LocalDate referenceDate) {
    }

    public MyDataSummary createMyDataSummary(User user) {
        MyDataSummary myDataSummary = createBasicMyDataSummary(user);

        final JsonMyData jsonMyData = new JsonMyData();
        jsonMyData.setJsonMyDataStockList(List.of(createJsonMyDataStock()));
        myDataSummary.setJsonMyData(jsonMyData);
        return myDataSummaryRepository.save(myDataSummary);
    }

    public MyDataSummary createMyDataSummary(User user, List<JsonMyDataStockDto> jsonMyDataStockDtos) {
        MyDataSummary myDataSummary = createBasicMyDataSummary(user);

        final JsonMyData jsonMyData = new JsonMyData();

        final List<JsonMyDataStock> jsonMyDataStocks = jsonMyDataStockDtos.stream().map(jsonMyDataStockDto -> {
            final JsonMyDataStock jsonMyDataStock = new JsonMyDataStock();
            jsonMyDataStock.setCode(jsonMyDataStockDto.code());
            jsonMyDataStock.setQuantity(jsonMyDataStockDto.quantity());
            jsonMyDataStock.setMyDataProdCode(jsonMyDataStockDto.code());
            jsonMyDataStock.setReferenceDate(jsonMyDataStockDto.referenceDate());
            return jsonMyDataStock;
        }).toList();
        jsonMyData.setJsonMyDataStockList(jsonMyDataStocks);
        myDataSummary.setJsonMyData(jsonMyData);

        return myDataSummaryRepository.save(myDataSummary);
    }

    public MyDataSummary createMyDataSummary(User user, String stockCode, LocalDate referenceDate) {
        MyDataSummary myDataSummary = createBasicMyDataSummary(user);

        final JsonMyData jsonMyData = new JsonMyData();
        final JsonMyDataStock jsonMyDataStock = new JsonMyDataStock();
        jsonMyDataStock.setCode(stockCode);
        jsonMyDataStock.setQuantity(someLongBetween(1_000_000L, 9_999_999L));
        jsonMyDataStock.setMyDataProdCode(stockCode);
        jsonMyDataStock.setReferenceDate(referenceDate);
        jsonMyData.setJsonMyDataStockList(List.of(jsonMyDataStock));
        myDataSummary.setJsonMyData(jsonMyData);
        return myDataSummaryRepository.save(myDataSummary);
    }

    public MyDataSummary updateMyDataSummary(MyDataSummary myDataSummary) {
        return myDataSummaryRepository.save(myDataSummary);
    }

    public MyDataSummary createMyDataSummaryWithStockListSize(User user, int size) {
        List<JsonMyDataStock> jsonMyDataStockList = createJsonMyDataStockList(size);

        final JsonMyData jsonMyData = new JsonMyData();
        jsonMyData.setJsonMyDataStockList(jsonMyDataStockList);

        MyDataSummary myDataSummary = createBasicMyDataSummary(user);
        myDataSummary.setJsonMyData(jsonMyData);
        return myDataSummaryRepository.save(myDataSummary);
    }

    public List<JsonMyDataStock> createJsonMyDataStockList(int size) {
        return IntStream.range(0, size).mapToObj(i -> createJsonMyDataStock()).collect(Collectors.toList());
    }

    public JsonMyDataStock createJsonMyDataStock() {
        String stockCode = someStockCode();
        LocalDate referenceDate = LocalDate.now();
        final JsonMyDataStock jsonMyDataStock = new JsonMyDataStock();
        jsonMyDataStock.setCode(stockCode);
        jsonMyDataStock.setQuantity(someLongBetween(1L, 999L));
        jsonMyDataStock.setMyDataProdCode(stockCode);
        jsonMyDataStock.setReferenceDate(referenceDate);
        jsonMyDataStock.setRegisterDate(referenceDate);
        jsonMyDataStock.setName(stockCode + "보통주");
        return jsonMyDataStock;
    }

    private MyDataSummary createBasicMyDataSummary(User user) {
        MyDataSummary myDataSummary = new MyDataSummary();
        myDataSummary.setUserId(user.getId());
        myDataSummary.setPensionPaidAmount(someLongBetween(1_000L, 9_999L));
        myDataSummary.setLoanPrice(someLongBetween(100L, 1000L));

        return myDataSummary;
    }

    public Optional<MyDataSummary> findMyDataSummary(Long myDataSummaryId) {
        return myDataSummaryRepository.findById(myDataSummaryId);
    }

    public Optional<MyDataSummary> findMyDataSummaryByUserId(Long userId) {
        return myDataSummaryRepository.findByUserId(userId);
    }

    public StockReferenceDate createStockReferenceDate(String stockCode, LocalDate referenceDate) {
        StockReferenceDate stockReferenceDate = stockReferenceDateRepository.findByStockCodeAndReferenceDate(
            stockCode, referenceDate
        ).orElse(new StockReferenceDate());
        stockReferenceDate.setStockCode(stockCode);
        stockReferenceDate.setReferenceDate(referenceDate);
        return stockReferenceDateRepository.save(stockReferenceDate);
    }

    public StockReferenceDate updateStockReferenceDate(StockReferenceDate stockReferenceDate) {
        return stockReferenceDateRepository.save(stockReferenceDate);
    }

    public Optional<StockReferenceDate> findStockReferenceDate(Long stockReferenceDateId) {
        return stockReferenceDateRepository.findById(stockReferenceDateId);
    }

    public Optional<StockReferenceDate> findRegularGeneralMeetingStockReferenceDate(String stockCode, LocalDate referenceDate) {
        return stockReferenceDateRepository.findByStockCodeAndReferenceDate(stockCode, referenceDate);
    }

    public List<StockReferenceDate> findAllStockReferenceDate() {
        return stockReferenceDateRepository.findAll();
    }

    public String decrypt(String encryptedText) {
        try {
            return cryptoHelper.decrypt(encryptedText);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public DigitalDocument createDigitalDocument(Post post, Stock stock, User acceptUser) {
        return createDigitalDocument(post, stock, acceptUser, someEnum(DigitalDocumentType.class));
    }

    public DigitalDocument createDigitalDocument(
        Post post, Stock stock, User acceptUser, DigitalDocumentType type
    ) {
        return createDigitalDocument(
            post,
            stock,
            acceptUser,
            type,
            KoreanDateTimeUtil.getTodayLocalDate()
        );
    }

    public DigitalDocument createDigitalDocument(
        Post post, Stock stock, User acceptUser, DigitalDocumentType type, LocalDate referenceDate
    ) {
        final LocalDateTime targetStartDate = someLocalDateTimeInTheFuture();

        return createDigitalDocument(
            post,
            stock,
            acceptUser,
            type,
            LocalDateTime.now(),
            targetStartDate,
            targetStartDate.plusDays(someIntegerBetween(1, 10)),
            referenceDate,
            null
        );
    }

    public DigitalDocument createDigitalDocument(
        Post post, Stock stock, User acceptUser, LocalDateTime shareHolderMeetingDate, DigitalDocumentType type
    ) {
        final LocalDateTime targetStartDate = someLocalDateTimeInTheFuture();

        return createDigitalDocument(
            post,
            stock,
            acceptUser,
            type,
            shareHolderMeetingDate,
            targetStartDate,
            targetStartDate.plusDays(someIntegerBetween(1, 10)),
            KoreanDateTimeUtil.getTodayLocalDate(),
            null
        );
    }

    public DigitalDocument createDigitalDocument(
        Post post, Stock stock, User acceptUser, DigitalDocumentType type,
        ag.act.model.JsonAttachOption jsonAttachOption
    ) {
        final LocalDateTime targetStartDate = someLocalDateTimeInTheFuture();

        return createDigitalDocument(
            post, stock, acceptUser, type, LocalDateTime.now(),
            targetStartDate, targetStartDate.plusDays(someIntegerBetween(1, 10)), KoreanDateTimeUtil.getTodayLocalDate(),
            jsonAttachOption
        );
    }

    public DigitalDocument createDigitalDocument(
        Post post, Stock stock, User acceptUser, DigitalDocumentType type,
        LocalDateTime targetStartDate, LocalDateTime targetEndDate, LocalDate referenceDate
    ) {
        return createDigitalDocument(
            post, stock, acceptUser, type, LocalDateTime.now(),
            targetStartDate, targetEndDate, referenceDate,
            null
        );
    }

    public DigitalDocument createDigitalDocument(
        Post post, Stock stock, User acceptUser, DigitalDocumentType type,
        LocalDateTime targetStartDate, LocalDateTime targetEndDate, LocalDate referenceDate,
        ag.act.model.JsonAttachOption jsonAttachOption
    ) {
        return createDigitalDocument(post, stock, acceptUser, type, LocalDateTime.now(),
            targetStartDate, targetEndDate, referenceDate, jsonAttachOption);
    }

    public DigitalDocument createDigitalDocument(
        Post post, Stock stock, User acceptUser, DigitalDocumentType type, LocalDateTime shareHolderMeetingDate,
        LocalDateTime targetStartDate, LocalDateTime targetEndDate, LocalDate referenceDate,
        ag.act.model.JsonAttachOption jsonAttachOption
    ) {
        DigitalDocument digitalDocument = new DigitalDocument();

        digitalDocument.setPostId(post.getId());
        digitalDocument.setStockCode(stock.getCode());
        digitalDocument.setTitle(someAlphanumericString(10));
        digitalDocument.setContent(someAlphanumericString(10));
        digitalDocument.setCompanyName(stock.getName());
        digitalDocument.setCompanyRegistrationNumber(someCompanyRegistrationNumber());
        digitalDocument.setShareholderMeetingType(someAlphanumericString(10));
        digitalDocument.setShareholderMeetingName(someAlphanumericString(10));
        digitalDocument.setShareholderMeetingDate(shareHolderMeetingDate);
        digitalDocument.setDesignatedAgentNames(someAlphanumericString(10));
        digitalDocument.setAcceptUserId(Optional.ofNullable(acceptUser).map(User::getId).orElse(null));
        digitalDocument.setJoinStockSum(0L);
        digitalDocument.setJoinUserCount(0);
        digitalDocument.setType(type);
        digitalDocument.setStatus(Status.ACTIVE);
        digitalDocument.setTargetStartDate(targetStartDate);
        digitalDocument.setTargetEndDate(targetEndDate);
        digitalDocument.setShareholdingRatio((double) 0);
        digitalDocument.setStockReferenceDate(referenceDate);
        digitalDocument.setJsonAttachOption(jsonAttachOption);
        digitalDocument.setVersion(DigitalDocumentVersion.V1);
        digitalDocument.setIsDisplayStockQuantity(Boolean.FALSE);

        if (digitalDocument.getType() == DigitalDocumentType.DIGITAL_PROXY) {
            createStockReferenceDate(stock.getCode(), digitalDocument.getStockReferenceDate());
        }
        final DigitalDocument savedDigitalDocument = digitalDocumentRepository.save(digitalDocument);

        createDigitalDocumentNumber(savedDigitalDocument.getId());
        return savedDigitalDocument;
    }

    private DigitalDocumentNumber createDigitalDocumentNumber(Long digitalDocumentId) {
        final DigitalDocumentNumber digitalDocumentNumber = new DigitalDocumentNumber();
        digitalDocumentNumber.setDigitalDocumentId(digitalDocumentId);
        digitalDocumentNumber.setLastIssuedNumber(0L);
        return digitalDocumentNumberRepository.save(digitalDocumentNumber);
    }

    public List<DigitalDocumentItem> createDigitalDocumentItemList(DigitalDocument digitalDocument) {
        DigitalDocumentItem digitalDocumentItemGroup = new DigitalDocumentItem();
        digitalDocumentItemGroup.setDigitalDocumentId(digitalDocument.getId());
        digitalDocumentItemGroup.setDigitalDocument(digitalDocument);
        digitalDocumentItemGroup.setTitle("제1안");
        digitalDocumentItemGroup.setContent("1안상세");
        digitalDocumentItemGroup.setDefaultSelectValue(null);
        digitalDocumentItemGroup.setParentId(null);
        digitalDocumentItemGroup.setItemLevel(1);
        digitalDocumentItemGroup.setIsLastItem(false);
        digitalDocumentItemRepository.save(digitalDocumentItemGroup);
        digitalDocumentItemGroup.setGroupId(digitalDocumentItemGroup.getId());

        DigitalDocumentItem digitalDocumentItemChild1 = new DigitalDocumentItem();
        digitalDocumentItemChild1.setDigitalDocumentId(digitalDocument.getId());
        digitalDocumentItemChild1.setDigitalDocument(digitalDocument);
        digitalDocumentItemChild1.setTitle("제1-1안");
        digitalDocumentItemChild1.setContent("1-1안상세");
        digitalDocumentItemChild1.setDefaultSelectValue(DigitalAnswerType.ABSTENTION);
        digitalDocumentItemChild1.setParentId(digitalDocumentItemGroup.getId());
        digitalDocumentItemChild1.setGroupId(digitalDocumentItemGroup.getId());
        digitalDocumentItemChild1.setItemLevel(2);
        digitalDocumentItemChild1.setIsLastItem(true);
        digitalDocumentItemChild1.setLeaderDescription("제1-1안 주주대표의견");
        digitalDocumentItemRepository.save(digitalDocumentItemChild1);

        DigitalDocumentItem digitalDocumentItemChild2 = new DigitalDocumentItem();
        digitalDocumentItemChild2.setDigitalDocumentId(digitalDocument.getId());
        digitalDocumentItemChild2.setDigitalDocument(digitalDocument);
        digitalDocumentItemChild2.setTitle("제1-2안");
        digitalDocumentItemChild2.setContent("1-2안상세");
        digitalDocumentItemChild2.setDefaultSelectValue(DigitalAnswerType.ABSTENTION);
        digitalDocumentItemChild2.setParentId(digitalDocumentItemGroup.getId());
        digitalDocumentItemChild2.setGroupId(digitalDocumentItemGroup.getId());
        digitalDocumentItemChild2.setItemLevel(2);
        digitalDocumentItemChild2.setIsLastItem(true);
        digitalDocumentItemChild1.setLeaderDescription("제1-2안 주주대표의견");
        digitalDocumentItemRepository.save(digitalDocumentItemChild2);

        digitalDocument.setDigitalDocumentItemList(List.of(
            digitalDocumentItemGroup,
            digitalDocumentItemChild1,
            digitalDocumentItemChild2
        ));

        return digitalDocument.getDigitalDocumentItemList();
    }

    public List<DigitalDocumentItemUserAnswer> createDigitalDocumentItemUserAnswerList(
        Long userId, List<DigitalDocumentItem> itemList
    ) {
        return itemList.stream().filter(
            element -> element.getDefaultSelectValue() != null
        ).map(item -> {
            DigitalDocumentItemUserAnswer answer = new DigitalDocumentItemUserAnswer();
            answer.setDigitalDocumentItemId(item.getId());
            answer.setUserId(userId);
            answer.setDigitalDocumentItem(item);
            answer.setAnswerSelectValue(item.getDefaultSelectValue());
            digitalDocumentItemUserAnswerRepository.save(answer);
            return answer;
        }).collect(Collectors.toList());
    }

    public Optional<DigitalDocument> findByDigitalDocument(Long id) {
        return digitalDocumentRepository.findById(id);
    }

    public List<DigitalDocumentItem> findDigitalDocumentItemsByDigitalDocumentId(Long id) {
        return digitalDocumentItemRepository.findDigitalDocumentItemsByDigitalDocumentId(id);
    }

    public Optional<DigitalDocumentUser> findDigitalDocumentUserById(Long id) {
        return digitalDocumentUserRepository.findById(id);
    }

    public Optional<DigitalDocumentUser> findDigitalDocumentByDigitalDocumentIdAndUserId(Long digitalDocumentId, Long userId) {
        return digitalDocumentUserRepository.findByDigitalDocumentIdAndUserId(digitalDocumentId, userId);
    }

    public List<DigitalDocumentUser> findAllDigitalDocumentUsersByUserId(Long userId) {
        return digitalDocumentUserRepository.findAllByUserId(userId);
    }

    public List<DigitalDocumentUser> findAllDigitalDocumentUsersByDigitalDocumentId(Long digitalDocumentId) {
        return digitalDocumentUserRepository.findAllByDigitalDocumentId(digitalDocumentId);
    }

    public List<HolderListReadAndCopy> createHolderListReadAndCopyList(Long digitalDocumentId, String referenceDateByLeader) {
        final List<HolderListReadAndCopy> itemWithValues = Arrays.stream(HolderListReadAndCopyItemType.values())
            .sorted(Comparator.comparingInt(HolderListReadAndCopyItemType::getDisplayOrder))
            .map(itemType -> {
                HolderListReadAndCopy holderListReadAndCopy = new HolderListReadAndCopy();
                holderListReadAndCopy.setDigitalDocumentId(digitalDocumentId);
                holderListReadAndCopy.setStatus(Status.ACTIVE);
                holderListReadAndCopy.setItemType(itemType);
                if (itemType == HolderListReadAndCopyItemType.REFERENCE_DATE_BY_LEADER) {
                    holderListReadAndCopy.setItemValue(referenceDateByLeader);
                } else {
                    holderListReadAndCopy.setItemValue(someAlphanumericString(10));
                }
                return holderListReadAndCopy;
            })
            .toList();
        return holderListReadAndCopyRepository.saveAll(itemWithValues);
    }

    public List<DigitalDocumentUserAnswerDto> findUserAnswerList(Long digitalDocumentId, Long userId) {
        return digitalDocumentItemUserAnswerRepository.findUserAnswerList(digitalDocumentId, userId);
    }

    public DigitalDocument updateDigitalDocument(DigitalDocument digitalDocument) {
        return digitalDocumentRepository.save(digitalDocument);
    }

    public DigitalDocumentUser createDigitalDocumentUser(DigitalDocument digitalDocument, User user, Stock stock) {
        return createDigitalDocumentUser(digitalDocument, user, stock, null);
    }

    public DigitalDocumentUser createDigitalDocumentUser(
        DigitalDocument digitalDocument, User user, Stock stock, String pdfPath
    ) {
        return createDigitalDocumentUser(digitalDocument, user, stock, pdfPath, someEnum(DigitalDocumentAnswerStatus.class));
    }

    public DigitalDocumentUser createDigitalDocumentUser(
        DigitalDocument digitalDocument, User user, Stock stock, String pdfPath, DigitalDocumentAnswerStatus status
    ) {
        DigitalDocumentUser digitalDocumentUser = new DigitalDocumentUser();

        digitalDocumentUser.setDigitalDocumentId(digitalDocument.getId());
        digitalDocumentUser.setUserId(user.getId());
        digitalDocumentUser.setDigitalDocument(digitalDocument);
        digitalDocumentUser.setDigitalDocumentAnswerStatus(status);
        digitalDocumentUser.setStockCode(stock.getCode());
        digitalDocumentUser.setStockName(stock.getName());
        digitalDocumentUser.setName(user.getName());
        digitalDocumentUser.setHashedPhoneNumber(user.getHashedPhoneNumber());
        digitalDocumentUser.setBirthDate(user.getBirthDate());
        digitalDocumentUser.setGender(user.getGender());
        digitalDocumentUser.setAddress(user.getAddress());
        digitalDocumentUser.setAddressDetail(user.getAddressDetail());
        digitalDocumentUser.setZipcode(user.getZipcode());
        digitalDocumentUser.setStockCount(someLongBetween(0L, 100L));
        digitalDocumentUser.setPurchasePrice(someLongBetween(0L, 100L));
        digitalDocumentUser.setLoanPrice(someLongBetween(0L, 100L));
        digitalDocumentUser.setPdfPath(pdfPath);
        digitalDocumentUser.setStockReferenceDate(KoreanDateTimeUtil.getTodayLocalDate());
        digitalDocumentUser.setFirstNumberOfIdentification(
            TestUtil.getFirstNumberOfIdentification(user.getBirthDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")), user.getGender())
        );

        digitalDocumentUser.setIssuedNumber(increaseAndGetLastIssuedNumber(digitalDocument.getId()).getLastIssuedNumber());

        return digitalDocumentUserRepository.save(digitalDocumentUser);
    }

    private DigitalDocumentNumber increaseAndGetLastIssuedNumber(Long digitalDocumentId) {

        final DigitalDocumentNumber digitalDocumentNumber = findByDigitalDocumentId(digitalDocumentId)
            .orElseGet(() -> createDigitalDocumentNumber(digitalDocumentId));

        digitalDocumentNumber.setLastIssuedNumber(digitalDocumentNumber.getLastIssuedNumber() + 1);

        return digitalDocumentNumberRepository.save(digitalDocumentNumber);
    }

    public DigitalDocumentUser updateDigitalDocumentUser(DigitalDocumentUser digitalDocumentUser) {
        return digitalDocumentUserRepository.save(digitalDocumentUser);
    }

    public Campaign findCampaign(Long campaignId) {
        final Campaign campaign = campaignRepository.findById(campaignId).orElseThrow();
        campaign.getLatestCampaignDownload();
        return campaign;
    }

    public CampaignDownload createCampaignDownload(Long campaignId, Long userId, boolean isLatest) {
        CampaignDownload campaignDownload = new CampaignDownload();
        campaignDownload.setCampaignId(campaignId);
        campaignDownload.setRequestUserId(userId);
        campaignDownload.setIsLatest(isLatest);
        campaignDownload.setZipFileStatus(ZipFileStatus.REQUEST);

        return campaignDownloadRepository.save(campaignDownload);
    }

    public CampaignDownload updateCampaignDownload(CampaignDownload digitalDocumentDownload) {
        return campaignDownloadRepository.save(digitalDocumentDownload);
    }

    public List<CampaignDownload> findAllCampaignDownload(Long digitalDocumentId) {
        return campaignDownloadRepository.findAllByCampaignId(digitalDocumentId);
    }

    public DigitalDocument findDigitalDocumentByPostId(Long sourcePostId) {
        return loadAndReturn(digitalDocumentRepository.findByPostId(sourcePostId).orElseThrow());
    }

    private DigitalDocument loadAndReturn(DigitalDocument digitalDocument) {
        digitalDocument.getLatestDigitalDocumentDownload();
        digitalDocument.getDigitalDocumentItemList().size();
        digitalDocument.getDigitalDocumentUserList().size();
        return digitalDocument;
    }

    public DigitalDocument findDigitalDocument(Long digitalDocumentId) {
        return loadAndReturn(digitalDocumentRepository.findById(digitalDocumentId).orElseThrow());
    }

    public List<DigitalDocument> findAllDigitalDocuments() {
        return digitalDocumentRepository.findAll();
    }

    public DigitalDocumentDownload createDigitalDocumentDownload(Long digitalDocumentId, Long userId, boolean isLatest) {
        DigitalDocumentDownload digitalDocumentDownload = new DigitalDocumentDownload();
        digitalDocumentDownload.setDigitalDocumentId(digitalDocumentId);
        digitalDocumentDownload.setRequestUserId(userId);
        digitalDocumentDownload.setIsLatest(isLatest);
        digitalDocumentDownload.setZipFileStatus(ZipFileStatus.REQUEST);

        return digitalDocumentDownloadRepository.save(digitalDocumentDownload);
    }

    public DigitalDocumentDownload updateDigitalDocumentDownload(DigitalDocumentDownload digitalDocumentDownload) {
        return digitalDocumentDownloadRepository.save(digitalDocumentDownload);
    }

    public List<DigitalDocumentDownload> findAllDigitalDocumentDownload(Long digitalDocumentId) {
        return digitalDocumentDownloadRepository.findAllByDigitalDocumentId(digitalDocumentId);
    }

    public StockGroup updateStockGroup(StockGroup stockGroup) {
        return stockGroupRepository.saveAndFlush(stockGroup);
    }

    public StockGroup createStockGroup(String groupName) {
        return stockGroupRepository.save(
            StockGroup.builder()
                .name(groupName.trim())
                .status(Status.ACTIVE)
                .description(groupName + " 종목 그룹 설명")
                .build()
        );
    }

    public StockGroup createDeletedStockGroup(String groupName) {
        return stockGroupRepository.save(
            StockGroup.builder()
                .name(groupName.trim())
                .status(Status.DELETED)
                .description(groupName + " 종목 그룹 설명")
                .deletedAt(LocalDateTime.now())
                .build()
        );
    }

    public StockGroupMapping createStockGroupMapping(String stockCode, long stockGroupId) {
        return stockGroupMappingRepository.save(
            StockGroupMapping.builder()
                .stockCode(stockCode)
                .stockGroupId(stockGroupId)
                .build()
        );
    }

    public DigitalDocumentItemUserAnswer createDigitalDocumentItemUserAnswer(DigitalDocumentItem digitalDocumentItem, Long userId) {
        DigitalDocumentItemUserAnswer digitalDocumentItemUserAnswer = new DigitalDocumentItemUserAnswer();

        digitalDocumentItemUserAnswer.setDigitalDocumentItemId(digitalDocumentItem.getId());
        digitalDocumentItemUserAnswer.setDigitalDocumentItem(digitalDocumentItem);
        digitalDocumentItemUserAnswer.setUserId(userId);
        digitalDocumentItemUserAnswer.setAnswerSelectValue(someEnum(DigitalAnswerType.class));

        return digitalDocumentItemUserAnswerRepository.save(digitalDocumentItemUserAnswer);
    }

    public List<NicknameHistory> findAllNicknameHistoriesByUserId(Long userId) {
        return nicknameHistoryRepository.findAllByUserId(userId);
    }

    public List<DigitalDocumentUser> findDigitalDocumentUsersByUserId(Long userId) {
        return digitalDocumentUserRepository.findAllByUserId(userId);
    }

    public List<DigitalDocumentItemUserAnswer> findDigitalDocumentItemUserAnswersByUserId(Long userId) {
        return digitalDocumentItemUserAnswerRepository.findAllByUserId(userId);
    }

    public ag.act.model.CreatePostRequest generateDigitalProxyCreatePostRequest(Board board) {
        ag.act.model.CreatePostRequest request = new ag.act.model.CreatePostRequest();
        request.setBoardCategory(board.getCategory().name());
        request.setTitle(someString(10));
        request.setContent(someAlphanumericString(300));
        request.setIsAnonymous(someBoolean());
        request.setIsActive(someThing(Boolean.TRUE, Boolean.FALSE, null, null));

        Instant endDate = someInstantInTheFuture();
        Instant startDate = endDate.minus(Period.ofDays(3));

        final ag.act.model.CreateDigitalProxyRequest digitalProxy = new ag.act.model.CreateDigitalProxyRequest()
            .templateId(someAlphanumericString(10))
            .templateName(someAlphanumericString(10))
            .templateRole(someAlphanumericString(10))
            .targetStartDate(startDate)
            .targetEndDate(endDate);

        request.setDigitalProxy(digitalProxy);

        return request;
    }

    public <T> T callPostApi(
        MockMvc mockMvc,
        String jwt,
        String requestBody,
        Class<T> responseType,
        String uriTemplate,
        Object... uriVariables
    ) throws Exception {
        MvcResult response = mockMvc
            .perform(
                post(uriTemplate, uriVariables)
                    .content(requestBody)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .accept(org.springframework.http.MediaType.ALL_VALUE)
                    .header("Authorization", "Bearer " + jwt)
            )
            .andExpect(status().isOk())
            .andReturn();

        return objectMapperUtil.toResponse(response.getResponse().getContentAsString(), responseType);
    }

    public Optional<TestStock> findTestStock(String testStockCode) {
        return testStockRepository.findByCode(testStockCode);
    }

    public void deleteTestStocks(List<String> stockCodes) {
        stockCodes.forEach(
            stockCode -> testStockRepository.findByCode(stockCode)
                .ifPresent(testStock -> testStockRepository.delete(testStock))
        );
    }

    public Optional<SolidarityLeader> findSolidarityLeader(String stockCode) {
        return solidarityLeaderService.findLeader(stockCode);
    }

    public Optional<SolidarityLeaderApplicant> findSolidarityLeaderApplicant(Long solidarityId, Long userId) {
        return solidarityLeaderApplicantRepository.findBySolidarityIdAndUserIdAndVersion(solidarityId, userId, SOLIDARITY_APPLY_NEW_VERSION);
    }

    public List<SolidarityLeaderApplicant> findAllSolidarityLeaderApplicantsByLeaderElectionId(Long leaderElectionId) {
        return solidarityLeaderApplicantRepository.findAll().stream()
            .filter(it -> Objects.equals(it.getSolidarityLeaderElectionId(), leaderElectionId))
            .toList();
    }

    public Optional<SolidarityLeaderApplicant> findDefaultVersionSolidarityLeaderApplicant(Long solidarityId, Long userId) {
        return solidarityLeaderApplicantRepository.findBySolidarityIdAndUserIdAndVersion(solidarityId, userId, SOLIDARITY_APPLY_DEFAULT_VERSION);
    }

    public Optional<Solidarity> findSolidarity(String stockCode) {
        return solidarityRepository.findByStockCode(stockCode);
    }

    public DigitalDocumentUserSummary findDigitalDocumentSummary(Long digitalDocumentId) {
        return digitalDocumentUserRepository.findDigitalDocumentUserSummary(digitalDocumentId);
    }

    public void createAllUserVerificationHistories(User user) {
        createUserVerificationHistory(user, VerificationType.USER, VerificationOperationType.REGISTER);
        createUserVerificationHistory(user, VerificationType.SMS, VerificationOperationType.REGISTER);
        createUserVerificationHistory(user, VerificationType.SMS, VerificationOperationType.VERIFICATION);
        createUserVerificationHistory(user, VerificationType.PIN, VerificationOperationType.REGISTER);
        createUserVerificationHistory(user, VerificationType.PIN, VerificationOperationType.VERIFICATION);
    }

    public UserVerificationHistory createUserVerificationHistory(
        User user,
        VerificationType verificationType,
        VerificationOperationType verificationOperationType
    ) {
        return createUserVerificationHistory(user, verificationType, verificationOperationType, null);
    }

    public UserVerificationHistory createUserVerificationHistory(
        User user,
        VerificationType verificationType,
        VerificationOperationType verificationOperationType,
        Long digitalDocumentUserId
    ) {
        UserVerificationHistory userVerificationHistory = new UserVerificationHistory();
        userVerificationHistory.setUserId(user.getId());
        userVerificationHistory.setUserIp("127.0.0.1");
        userVerificationHistory.setVerificationType(verificationType);
        userVerificationHistory.setOperationType(verificationOperationType);
        userVerificationHistory.setDigitalDocumentUserId(digitalDocumentUserId);

        return userVerificationHistoryRepository.save(userVerificationHistory);
    }

    public UserVerificationHistory updateUserVerificationHistory(UserVerificationHistory userVerificationHistory) {
        return userVerificationHistoryRepository.save(userVerificationHistory);
    }

    public UserVerificationHistory findFirstVerificationHistoryRepository(Long userId) {
        return userVerificationHistoryRepository.findFirstByUserIdOrderByIdDesc(userId).orElseThrow();
    }

    public List<Post> findAllPostsWithoutLazyLoading() {
        return postRepository.findAll()
            .stream()
            .filter(it -> getStatusByPostListForAdmin().contains(it.getStatus()))
            .toList();
    }

    public List<Post> findAllPosts() {
        return findAllPostsWithoutLazyLoading()
            .stream()
            .map(this::lazyLoadRelationsAndReturn)
            .toList();
    }

    private List<ag.act.model.Status> getStatusByPostListForAdmin() {
        List<Status> statuses = new ArrayList<>(StatusUtil.getPostStatusesVisibleToUsers());
        statuses.add(Status.DELETED_BY_ADMIN);
        return statuses;
    }

    public List<Stock> findAllStocksThatHaveSolidarity() {
        return stockRepository.findAll()
            .stream()
            .filter(stock -> stock.getSolidarity() != null)
            .toList();
    }

    public List<SolidarityDailySummary> findAllSolidarityDailySummary() {
        return solidarityDailySummaryRepository.findAll();
    }

    public List<StockGroup> findAllActiveStockGroups() {
        return stockGroupRepository.findAll()
            .stream()
            .filter(stockGroup -> stockGroup.getStatus() == Status.ACTIVE)
            .toList();
    }

    public Optional<StockGroup> findStockGroupByName(String stockGroupName) {
        return stockGroupRepository.findByName(stockGroupName);
    }

    public Optional<StockGroup> findStockGroupById(Long stockGroupId) {
        return stockGroupRepository.findById(stockGroupId);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public List<UserRole> findAllUserRoles() {
        return userRoleRepository.findAll();
    }

    public void updateUserRole(UserRole userRole) {
        userRoleRepository.save(userRole);
    }

    public List<User> findUsersNotSuperAdminAndNotStatusIn(List<Status> statuses) {
        return userRepository.findAll()
            .stream()
            .filter(user -> user.getRoles()
                .stream()
                .noneMatch(userRole -> userRole.getRole().getType() == RoleType.SUPER_ADMIN))
            .filter(user -> findCorporateUserByUserId(user.getId()).isEmpty())
            .filter(user -> !statuses.contains(user.getStatus()))
            .peek(user -> {
                user.setAdmin(isAdmin(user));
                user.setAcceptor(isAcceptorUser(user));
            })
            .toList();
    }

    public List<MyDataSummary> findAllMyDataSummary() {
        return myDataSummaryRepository.findAll();
    }

    public List<Solidarity> findAllSolidarities() {
        return solidarityRepository.getAllSolidarities();
    }

    public List<Stock> findAllStocks() {
        return stockRepository.findAll();
    }

    public List<Stock> findAllStocksWithoutTestStocks() {
        return stockRepository.findAllStocksWithoutTestStocks();
    }


    public List<Stock> findAllStocksWithoutGlobalBoardStock() {
        return findAllStocks()
            .stream()
            .filter(stock -> !stock.getCode().equals(globalBoardManager.getStockCode()))
            .toList();
    }

    public void deleteAllSolidarityLeaderElections() {
        solidarityLeaderElectionRepository.deleteAll();
    }

    public SolidarityDailyStatistics createSolidarityDailyStatistics(String stockCode, Long solidarityId) {
        return createSolidarityDailyStatistics(stockCode, solidarityId, LocalDate.now());
    }

    public SolidarityDailyStatistics createSolidarityDailyStatistics(String stockCode, Long solidarityId, LocalDate date) {
        final SolidarityDailyStatistics solidarityDailyStatistics = new SolidarityDailyStatistics();
        solidarityDailyStatistics.setStockCode(stockCode);
        solidarityDailyStatistics.setSolidarityId(solidarityId);
        solidarityDailyStatistics.setStockQuantity(someLongBetween(1L, 10000L));
        solidarityDailyStatistics.setDate(date);
        solidarityDailyStatistics.setStake(someIntegerBetween(0, 100000) * 0.0001);
        solidarityDailyStatistics.setMemberCount(someIntegerBetween(0, 100));
        solidarityDailyStatistics.setMarketValue(someLongBetween(1L, 100000000L));

        return updateSolidarityDailyStatistics(solidarityDailyStatistics);
    }

    public void deleteAllSolidarityDailyStatistics() {
        solidarityDailyStatisticsRepository.deleteAll();
    }

    public List<SolidarityDailyStatistics> findAllSolidarityDailyStatistics() {
        return solidarityDailyStatisticsRepository.findAll();
    }

    public SolidarityDailyStatistics updateSolidarityDailyStatistics(SolidarityDailyStatistics solidarityDailyStatistics) {
        return solidarityDailyStatisticsRepository.saveAndFlush(solidarityDailyStatistics);
    }

    public void deleteSolidarityDailyStatistics(SolidarityDailyStatistics solidarityDailyStatistics) {
        solidarityDailyStatisticsRepository.delete(solidarityDailyStatistics);
    }

    public List<UserVerificationHistory> findAllUserVerificationHistories() {
        return userVerificationHistoryRepository.findAll();
    }

    public List<UserVerificationHistory> findUserVerificationHistoriesByDigitalDocumentUserId(Long digitalDocumentUserId) {
        return userVerificationHistoryRepository.findByDigitalDocumentUserId(digitalDocumentUserId);
    }

    public void deleteUserVerificationHistory(UserVerificationHistory userVerificationHistory) {
        userVerificationHistoryRepository.delete(userVerificationHistory);
    }

    public List<Post> findAllDuplicatedPost(Long postId) {
        return postRepository.findAllBySourcePostId(postId);
    }

    public List<Post> findAllDuplicatedPostIncludingSourcePost(Long sourcePostId) {
        final List<Post> posts = findAllDuplicatedPost(sourcePostId);
        posts.add(findPost(sourcePostId).orElseThrow());

        return posts;
    }

    public CampaignStockMapping createCampaignStockMapping(Long campaignId, String stockCode) {
        CampaignStockMapping campaignStockMapping = CampaignStockMapping
            .builder()
            .campaignId(campaignId)
            .stockCode(stockCode)
            .build();
        return campaignStockMappingRepository.save(campaignStockMapping);
    }

    public List<CampaignStockMapping> findAllCampaignStockMappings(Long campaignId) {
        return campaignStockMappingRepository.findAllByCampaignId(campaignId)
            .stream().sorted(Comparator.comparing(CampaignStockMapping::getStockCode)).toList();
    }

    public Campaign createCampaign(
        String campaignTitle,
        List<Stock> stocksInGroup,
        LocalDateTime targetStartDate,
        LocalDateTime targetEndDate,
        LocalDate referenceDate
    ) {
        final StockGroup stockGroup = createStockGroup(someString(10));
        final long stockGroupSize = stocksInGroup.size();
        final Stock sourceStock = stocksInGroup.stream()
            .findFirst()
            .orElseThrow(() -> new BadRequestException("stocksInGroup의 length를 1 이상으로 설정해주세요."));
        final BoardCategory boardCategory = BoardCategory.ETC;
        final Board board = createBoard(sourceStock, boardCategory.getBoardGroup(), boardCategory);
        final User user = createAdminUser();
        final Post sourcePost = createPost(board, user.getId());

        // stockGroupSize - 1: since sourcePost should not be included
        LongStream.range(0, stockGroupSize - 1)
            .forEach(i -> postRepository.save(createDuplicatePost(sourcePost.getId(), board, user.getId())));

        mapStocksToStockGroup(stocksInGroup, stockGroup);

        createDigitalDocument(
            sourcePost,
            sourceStock,
            user,
            DigitalDocumentType.ETC_DOCUMENT,
            targetStartDate,
            targetEndDate,
            referenceDate
        );

        final Campaign campaign = campaignRepository.saveAndFlush(Campaign.builder()
            .title(campaignTitle)
            .status(Status.ACTIVE)
            .sourcePostId(sourcePost.getId())
            .sourceStockGroupId(stockGroup.getId())
            .build());

        mapStocksToCampaign(stocksInGroup, campaign);

        return campaign;
    }

    public Campaign createCampaign(
        String campaignTitle,
        Long stockGroupSize,
        LocalDateTime targetStartDate,
        LocalDateTime targetEndDate,
        LocalDate referenceDate
    ) {
        final List<Stock> stocksInGroup = new ArrayList<>();

        LongStream.range(0L, stockGroupSize).forEach(i -> {
            Stock newStock = createStock();
            stocksInGroup.add(newStock);
        });

        return createCampaign(
            campaignTitle,
            stocksInGroup,
            targetStartDate,
            targetEndDate,
            referenceDate
        );
    }

    public Campaign createCampaign(
        String givenTitle,
        Long sourcePostId,
        Long sourceStockGroupId
    ) {
        return campaignRepository.save(
            Campaign.builder()
                .title(givenTitle)
                .sourcePostId(sourcePostId)
                .sourceStockGroupId(sourceStockGroupId)
                .status(Status.ACTIVE)
                .build()
        );
    }

    private void mapStocksToStockGroup(List<Stock> stocksInGroup, StockGroup stockGroup) {
        stocksInGroup
            .forEach((stock) -> createStockGroupMapping(stock.getCode(), stockGroup.getId()));
    }

    private void mapStocksToCampaign(List<Stock> stocksInGroup, Campaign campaign) {
        stocksInGroup
            .forEach((stock) -> createCampaignStockMapping(campaign.getId(), stock.getCode()));
    }

    public String getExpectedCsvRecord(
        User user,
        DigitalDocumentUser digitalDocumentUser,
        List<DigitalDocumentItemUserAnswer> userAnswerList
    ) {
        return String.join(",",
            List.of(
                String.valueOf(digitalDocumentUser.getIssuedNumber()),
                user.getName(),
                DateTimeUtil.formatLocalDateTime(user.getBirthDate(), "yyyy/MM/dd"),
                ag.act.model.Gender.M == user.getGender() ? "남" : "여",
                Optional.ofNullable(user.getAddress()).map(Object::toString).orElse("").replaceAll(",", " "),
                Optional.ofNullable(user.getAddressDetail()).map(Object::toString).orElse("").replaceAll(",", " "),
                Optional.ofNullable(user.getZipcode()).map(Object::toString).orElse(""),
                decryptColumnConverter.convert(user.getHashedPhoneNumber()).replaceAll("(\\d{3})(\\d{4})(\\d{4})", "$1-$2-$3"),
                digitalDocumentUser.getStockName(),
                digitalDocumentUser.getStockCount().toString(),
                digitalDocumentUser.getPurchasePrice().toString(),
                digitalDocumentUser.getLoanPrice().toString(),
                getUpdatedAtInKoreanTime(digitalDocumentUser),
                getRegisteredAtInKoreanTime(user.getCreatedAt()),
                userAnswerList.get(0).getAnswerSelectValue().getDisplayName(),
                userAnswerList.get(1).getAnswerSelectValue().getDisplayName()
            ));
    }

    public String getExpectedCsvRecordForAcceptor(
        User user,
        DigitalDocumentUser digitalDocumentUser,
        List<DigitalDocumentItemUserAnswer> userAnswerList
    ) {
        return String.join(",",
            List.of(
                String.valueOf(digitalDocumentUser.getIssuedNumber()),
                user.getName(),
                DateTimeUtil.formatLocalDateTime(user.getBirthDate(), "yyyy/MM/dd"),
                ag.act.model.Gender.M == user.getGender() ? "남" : "여",
                digitalDocumentUser.getStockName(),
                digitalDocumentUser.getStockCount().toString(),
                getUpdatedAtInKoreanTime(digitalDocumentUser),
                userAnswerList.get(0).getAnswerSelectValue().getDisplayName(),
                userAnswerList.get(1).getAnswerSelectValue().getDisplayName()
            ));
    }

    public String getUpdatedAtInKoreanTime(DigitalDocumentUser digitalDocumentUser) {
        return DateTimeUtil.getFormattedKoreanTime(
            "yyyy-MM-dd HH:mm:ss",
            KoreanDateTimeUtil.toKoreanTime(digitalDocumentUser.getUpdatedAt()).toInstant()
        );
    }

    public String getRegisteredAtInKoreanTime(LocalDateTime registeredAt) {
        return DateTimeUtil.getFormattedKoreanTime(
            "yyyy-MM-dd",
            KoreanDateTimeUtil.toKoreanTime(registeredAt).toInstant()
        );
    }

    public List<Report> findAllReportsByType(ReportType reportType) {
        return reportRepository.findAll()
            .stream()
            .filter(report -> report.getType() == reportType)
            .toList();
    }

    public <T> Answer<Object> mockReturnValueOnCallerCondition(Class<T> clazz, String methodName, Object returnValue) {
        return invocation -> {
            if (((InterceptedInvocation) invocation).getLocation().toString().startsWith(
                "-> at %s.%s(".formatted(clazz.getName(), methodName)
            )) {
                return returnValue;
            } else {
                return invocation.callRealMethod();
            }
        };
    }

    public DashboardAgeStatistics createDashboardAgeStatistics(DashboardStatisticsType type, String date) {
        DashboardAgeStatistics dashboardAgeStatistics = findByDashboardAgeStatisticsTypeAndDate(type, date)
            .orElse(new DashboardAgeStatistics());
        dashboardAgeStatistics.setType(type);
        dashboardAgeStatistics.setDate(date);
        dashboardAgeStatistics.setAge10Value(someLongBetween(1L, 100L));
        dashboardAgeStatistics.setAge20Value(someLongBetween(1L, 100L));
        dashboardAgeStatistics.setAge30Value(someLongBetween(1L, 100L));
        dashboardAgeStatistics.setAge40Value(someLongBetween(1L, 100L));
        dashboardAgeStatistics.setAge50Value(someLongBetween(1L, 100L));
        dashboardAgeStatistics.setAge60Value(someLongBetween(1L, 100L));
        dashboardAgeStatistics.setAge70Value(someLongBetween(1L, 100L));
        dashboardAgeStatistics.setAge80Value(someLongBetween(1L, 100L));
        dashboardAgeStatistics.setAge90Value(someLongBetween(1L, 100L));
        return dashboardAgeStatisticsRepository.save(dashboardAgeStatistics);
    }

    public Optional<DashboardAgeStatistics> findByDashboardAgeStatisticsTypeAndDate(DashboardStatisticsType type, String date) {
        return dashboardAgeStatisticsRepository.findByTypeAndDate(type, date);
    }

    public Optional<DashboardStatistics> findByDashboardStatisticsTypeAndDate(DashboardStatisticsType type, String date) {
        return dashboardStatisticsRepository.findByTypeAndDate(type, date);
    }

    public List<DashboardStatisticsAgeCountDto> findByAgeTypeAndInDate(List<String> dateList) {
        return dashboardAgeStatisticsRepository.findByTypeAndInDate(dateList);
    }

    public List<DashboardStatisticsGenderCountDto> findByGenderTypeAndInDate(List<String> dateList) {
        return dashboardGenderStatisticsRepository.findByTypeAndInDate(dateList);
    }

    public DashboardGenderStatistics createDashboardGenderStatistics(DashboardStatisticsType type, String date) {
        DashboardGenderStatistics dashboardGenderStatistics = findByDashboardGenderStatisticsTypeAndDate(type, date)
            .orElse(new DashboardGenderStatistics());
        dashboardGenderStatistics.setType(type);
        dashboardGenderStatistics.setDate(date);
        dashboardGenderStatistics.setMaleValue(someLongBetween(1L, 100L));
        dashboardGenderStatistics.setFemaleValue(someLongBetween(1L, 100L));
        return dashboardGenderStatisticsRepository.save(dashboardGenderStatistics);
    }

    public Optional<DashboardGenderStatistics> findByDashboardGenderStatisticsTypeAndDate(DashboardStatisticsType type, String date) {
        return dashboardGenderStatisticsRepository.findByTypeAndDate(type, date);
    }

    public DashboardStockStatistics createDashboardStockStatistics(DashboardStatisticsType type, String date, String stockCode) {
        DashboardStockStatistics dashboardStockStatistics = dashboardStockStatisticsRepository.findByTypeAndDateAndStockCode(
            type, date, stockCode
        ).orElse(new DashboardStockStatistics());
        dashboardStockStatistics.setType(type);
        dashboardStockStatistics.setDate(date);
        dashboardStockStatistics.setStockCode(stockCode);
        dashboardStockStatistics.setValue(someLongBetween(1L, 100L).doubleValue());
        return dashboardStockStatisticsRepository.save(dashboardStockStatistics);
    }

    public List<DashboardStockStatistics> getDashboardStockStatisticsListByTypeAndStockCode(
        DashboardStatisticsType type, String stockCode
    ) {
        return dashboardStockStatisticsRepository.findByTypeAndStockCodeOrderByDateDesc(
            type, stockCode
        );
    }

    public DashboardStatistics createDashboardStatistics(DashboardStatisticsType type, String date) {
        DashboardStatistics dashboardStatistics = dashboardStatisticsRepository.findByTypeAndDate(type, date)
            .orElse(new DashboardStatistics());
        dashboardStatistics.setType(type);
        dashboardStatistics.setDate(date);
        dashboardStatistics.setValue(someLongBetween(1L, 100L).doubleValue());
        return dashboardStatisticsRepository.save(dashboardStatistics);
    }

    public List<Notification> findAllRecentNotifications() {
        return notificationRepository.findAll()
            .stream()
            .filter(this::isRecentNotification)
            .toList();
    }

    private boolean isRecentNotification(Notification notification) {
        return notification.getCreatedAt().isAfter(DateTimeUtil.getPastMonthFromCurrentLocalDateTime(RECENT_NOTIFICATION_TIME_PERIOD_MONTHS))
            || notification.getCreatedAt().equals(DateTimeUtil.getPastMonthFromCurrentLocalDateTime(RECENT_NOTIFICATION_TIME_PERIOD_MONTHS));
    }

    public Optional<Notification> findNotificationByPostId(Long postId) {
        return notificationRepository.findByPostId(postId);
    }

    public Notification createNotification(Long postId) {
        return createNotification(postId, NotificationCategory.STOCKHOLDER_ACTION, NotificationType.POST);
    }

    public Notification createNotification(Long postId, NotificationCategory notificationCategory, NotificationType notificationType) {
        Notification notification = new Notification();
        notification.setPostId(postId);
        notification.setCategory(notificationCategory);
        notification.setType(notificationType);
        notification.setStatus(Status.ACTIVE);

        final Post post = findPost(postId).orElseThrow();
        notification.setActiveStartDate(post.getActiveStartDate());
        notification.setActiveEndDate(post.getActiveEndDate());

        return notificationRepository.saveAndFlush(notification);
    }

    public Notification updateNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    public NotificationUserView createNotificationUserView(Long userId, Long notificationId) {
        NotificationUserView notificationUserView = new NotificationUserView();
        notificationUserView.setNotificationId(notificationId);
        notificationUserView.setUserId(userId);

        return notificationUserViewRepository.saveAndFlush(notificationUserView);
    }

    public Optional<NotificationUserView> findByNotificationIdAndUserId(Long notificationId, Long userId) {
        return notificationUserViewRepository.findByNotificationIdAndUserId(notificationId, userId);
    }

    public List<Popup> findAllPopups() {
        return popupRepository.findAll();
    }

    public void deletePost(Post post) {
        postRepository.delete(post);
    }

    public Optional<JoinCount> findPollCountByPostId(Long postId) {
        return pollAnswerRepository.findPollCountByPostId(postId);
    }

    public Optional<JoinCount> findDigitalDocumentCountByPostId(Long postId) {
        return digitalDocumentRepository.findDigitalDocumentCountByPostId(postId);
    }

    public void deleteAllLatestPostTimestamps() {
        latestPostTimestampRepository.deleteAll();
    }

    public void deleteAllLatestUserPostsViews() {
        latestUserPostsViewRepository.deleteAll();
    }

    public LatestPostTimestamp createOrUpdateLatestPostTimestamp(
        Stock stock,
        BoardGroup boardGroup
    ) {
        return createOrUpdateLatestPostTimestamp(stock, boardGroup, someBoardCategory(boardGroup));
    }

    public LatestPostTimestamp createOrUpdateLatestPostTimestamp(
        Stock stock,
        BoardGroup boardGroup,
        BoardCategory boardCategory
    ) {
        LatestPostTimestamp latestPostTimestamp =
            latestPostTimestampRepository.findByStockCodeAndBoardGroupAndBoardCategory(
                stock.getCode(),
                boardGroup,
                boardCategory
            ).orElseGet(() -> {
                LatestPostTimestamp newLatestPostTimestamp = new LatestPostTimestamp();
                newLatestPostTimestamp.setStock(stock);
                newLatestPostTimestamp.setBoardGroup(boardGroup);
                newLatestPostTimestamp.setBoardCategory(boardCategory);

                return newLatestPostTimestamp;
            });

        latestPostTimestamp.setTimestamp(LocalDateTime.now());
        return latestPostTimestampRepository.save(latestPostTimestamp);
    }

    public LatestUserPostsView getLatestUserPostsView(
        String stockCode,
        Long userId,
        BoardGroup boardGroup,
        BoardCategory boardCategory,
        PostsViewType postsViewType
    ) {
        return latestUserPostsViewRepository.findByStockCodeAndUserIdAndBoardGroupAndBoardCategoryAndPostsViewType(
            stockCode,
            userId,
            boardGroup,
            boardCategory,
            postsViewType
        ).orElseThrow();
    }

    public LatestUserPostsView createOrUpdateLatestUserPostsView(
        Stock stock,
        User user,
        BoardGroup boardGroup,
        BoardCategory boardCategory,
        PostsViewType postsViewType
    ) {
        LatestUserPostsView latestUserPostsView =
            latestUserPostsViewRepository.findByStockCodeAndUserIdAndBoardGroupAndBoardCategoryAndPostsViewType(
                stock.getCode(),
                user.getId(),
                boardGroup,
                boardCategory,
                postsViewType
            ).orElseGet(() -> {
                LatestUserPostsView newLatestUserPostsView = new LatestUserPostsView();
                newLatestUserPostsView.setStock(stock);
                newLatestUserPostsView.setUser(user);
                newLatestUserPostsView.setPostsViewType(postsViewType);

                if (postsViewType != PostsViewType.STOCK_HOME) {
                    newLatestUserPostsView.setBoardGroup(boardGroup);
                    if (postsViewType == PostsViewType.BOARD_CATEGORY) {
                        newLatestUserPostsView.setBoardCategory(boardCategory);
                    }
                }

                return newLatestUserPostsView;
            });

        latestUserPostsView.setTimestamp(LocalDateTime.now());
        return latestUserPostsViewRepository.save(latestUserPostsView);
    }

    public Optional<LatestPostTimestamp> findLatestPostTimestamp(String code, BoardGroup boardGroup, BoardCategory boardCategory) {
        return latestPostTimestampRepository.findByStockCodeAndBoardGroupAndBoardCategory(code, boardGroup, boardCategory);
    }

    public List<LatestPostTimestamp> findAllLatestPostTimestamps() {
        return latestPostTimestampRepository.findAll();
    }

    public List<UserHoldingStockOnReferenceDate> findAllUserHoldingStockOnReferenceDatesByUserId(Long userId) {
        return userHoldingStockOnReferenceDateRepository.findAllByUserId(userId);
    }

    public UserHoldingStockOnReferenceDate createUserHoldingStockOnReferenceDate(String stockCode, Long userId, LocalDate referenceDate) {
        final UserHoldingStockOnReferenceDate userHoldingStockOnReferenceDate = new UserHoldingStockOnReferenceDate();
        userHoldingStockOnReferenceDate.setUserId(userId);
        userHoldingStockOnReferenceDate.setStockCode(stockCode);
        userHoldingStockOnReferenceDate.setReferenceDate(referenceDate);
        userHoldingStockOnReferenceDate.setQuantity(someLongBetween(100L, 1000L));
        userHoldingStockOnReferenceDate.setStatus(Status.ACTIVE);

        return userHoldingStockOnReferenceDateRepository.save(userHoldingStockOnReferenceDate);
    }

    public Optional<DigitalDocumentNumber> findByDigitalDocumentId(Long digitalDocumentId) {
        return digitalDocumentNumberRepository.findByDigitalDocumentId(digitalDocumentId);
    }

    public List<AutomatedAuthorPush> findAllAutomatedAuthorPushesByContentId(Long contentId) {
        return automatedAuthorPushRepository.findAllByContentId(contentId);
    }

    public List<AutomatedAuthorPush> findAllAutomatedAuthorPushes() {
        return automatedAuthorPushRepository.findAll();
    }

    public AutomatedAuthorPush createAutomatedAuthorPush(
        Long contentId, Long pushId, Integer criteriaValue, AutomatedPushCriteria criteria, AutomatedPushContentType contentType
    ) {
        final AutomatedAuthorPush automatedAuthorPush = new AutomatedAuthorPush();
        automatedAuthorPush.setContentId(contentId);
        automatedAuthorPush.setPushId(pushId);
        automatedAuthorPush.setCriteriaValue(criteriaValue);
        automatedAuthorPush.setCriteria(criteria);
        automatedAuthorPush.setContentType(contentType);
        return automatedAuthorPushRepository.save(automatedAuthorPush);
    }

    public void deleteAllAutomatedAuthorPushes() {
        automatedAuthorPushRepository.deleteAll();
    }

    public StockDartCorporation createStockDartCorporation(String corpCode, String corpName, String stockCode, String modifyDate) {
        final StockDartCorporation stockDartCorporation = new StockDartCorporation();
        stockDartCorporation.setCorpCode(corpCode);
        stockDartCorporation.setCorpName(corpName);
        stockDartCorporation.setStockCode(stockCode);
        stockDartCorporation.setModifyDate(modifyDate);
        stockDartCorporation.setStatus(Status.ACTIVE);

        return stockDartCorporationRepository.save(stockDartCorporation);
    }

    public StockDartCorporation createStockDartCorporation(String stockCode) {
        final StockDartCorporation stockDartCorporation = createStockDartCorporation(
            someAlphanumericString(10),
            someAlphanumericString(15),
            stockCode,
            someAlphanumericString(20)
        );
        stockDartCorporation.setCeoName(someAlphanumericString(10));
        stockDartCorporation.setCorpClass(someAlphanumericString(10));
        stockDartCorporation.setJurisdictionalRegistrationNumber(someAlphanumericString(10));
        stockDartCorporation.setBusinessRegistrationNumber(someAlphanumericString(10));
        stockDartCorporation.setAddress(someAlphanumericString(10));
        stockDartCorporation.setHomepageUrl(someAlphanumericString(10));
        stockDartCorporation.setIrUrl(someAlphanumericString(10));
        stockDartCorporation.setRepresentativePhoneNumber(someAlphanumericString(10));
        stockDartCorporation.setRepresentativeFaxNumber(someAlphanumericString(10));
        stockDartCorporation.setIndustryCode(someAlphanumericString(10));
        stockDartCorporation.setEstablishmentDate(someAlphanumericString(10));
        stockDartCorporation.setAccountSettlementMonth(someAlphanumericString(10));

        return stockDartCorporationRepository.save(stockDartCorporation);
    }

    public StockDartCorporation createStockDartCorporation(Stock stock) {
        return createStockDartCorporation(
            someAlphanumericString(10),
            someAlphanumericString(15),
            stock.getCode(),
            someAlphanumericString(20)
        );
    }

    public StockDartCorporation updateStockDartCorporation(StockDartCorporation stockDartCorporation) {
        return stockDartCorporationRepository.save(stockDartCorporation);
    }

    public List<StockDartCorporation> findAllStockDartCorporations() {
        return stockDartCorporationRepository.findAll().stream()
            .sorted(Comparator.comparing(StockDartCorporation::getUpdatedAt))
            .toList();
    }

    public List<StockDartCorporation> getAllDartCorporationsWithStock() {
        return stockDartCorporationRepository.getAllDartCorporationsWithStock();
    }

    public void deleteAllStockDartCorporations() {
        stockDartCorporationRepository.deleteAll(stockDartCorporationRepository.findAll());
    }

    public CorporateUser createCorporateUser(String corporateNo, String corporateName) {
        final CorporateUser corporateUser = new CorporateUser();
        corporateUser.setUserId(somePositiveLong());
        corporateUser.setCorporateNo(corporateNo);
        corporateUser.setCorporateName(corporateName);
        corporateUser.setStatus(Status.ACTIVE);
        return corporateBusinessRepository.save(corporateUser);
    }

    public CorporateUser updateCorporateUser(CorporateUser corporateUser) {
        return corporateBusinessRepository.save(corporateUser);
    }

    public Optional<CorporateUser> findCorporateUser(Long id) {
        return corporateBusinessRepository.findById(id);
    }

    public Optional<CorporateUser> findCorporateUserByUserId(Long userId) {
        return corporateBusinessRepository.findByUserIdAndStatus(userId, Status.ACTIVE);
    }

    public UserPushAgreement createUserPushAgreement(
        User user,
        UserPushAgreementType type,
        boolean agreeToReceive
    ) {
        UserPushAgreement userPushAgreement = new UserPushAgreement();
        userPushAgreement.setUser(user);
        userPushAgreement.setType(type);
        userPushAgreement.setAgreeToReceive(agreeToReceive);
        userPushAgreement.setStatus(Status.ACTIVE);

        return userPushAgreementRepository.save(userPushAgreement);
    }

    public UserPushAgreement findUserPushAgreement(User user, UserPushAgreementType type) {
        return userPushAgreementRepository.findByUserIdAndType(user.getId(), type)
            .orElseThrow(() -> new NotFoundException("해당 UserPushAgreement를 찾을 수 없습니다."));
    }

    public void createUserBadgeVisibility(Long userId) {
        final UserBadgeVisibility userBadgeVisibilityStockQuantity = new UserBadgeVisibility();
        userBadgeVisibilityStockQuantity.setIsVisible(someBoolean());
        userBadgeVisibilityStockQuantity.setType(UserBadgeType.STOCK_QUANTITY);
        userBadgeVisibilityStockQuantity.setUserId(userId);
        userBadgeVisibilityStockQuantity.setStatus(Status.ACTIVE);
        userBadgeVisibilityRepository.save(userBadgeVisibilityStockQuantity);

        final UserBadgeVisibility userBadgeVisibilityTotalAsset = new UserBadgeVisibility();
        userBadgeVisibilityTotalAsset.setIsVisible(someBoolean());
        userBadgeVisibilityTotalAsset.setType(UserBadgeType.TOTAL_ASSET);
        userBadgeVisibilityTotalAsset.setUserId(userId);
        userBadgeVisibilityTotalAsset.setStatus(Status.ACTIVE);
        userBadgeVisibilityRepository.save(userBadgeVisibilityTotalAsset);
    }

    public UserBadgeVisibility updateUserBadgeVisibility(UserBadgeVisibility userBadgeVisibility) {
        return userBadgeVisibilityRepository.save(userBadgeVisibility);
    }

    public List<UserBadgeVisibility> findAllUserBadgeVisibilityByUserId(Long userId) {
        return userBadgeVisibilityRepository.findAllByUserId(userId);
    }

    public Map<String, Boolean> getUserBadgeVisibilityMapByUserId(Long userId) {
        return findAllUserBadgeVisibilityByUserId(userId)
            .stream()
            .collect(Collectors.toMap(
                userBadgeVisibility -> userBadgeVisibility.getType().getLabel(),
                UserBadgeVisibility::getIsVisible)
            );
    }

    public Optional<StockAcceptorUser> findStockAcceptorUser(String stockCode) {
        return stockAcceptorUserRepository.findByStockCode(stockCode);
    }

    public Optional<StockAcceptorUser> findStockAcceptorUser(String stockCode, Long userId) {
        return stockAcceptorUserRepository.findByStockCodeAndUserId(stockCode, userId);
    }

    public Optional<StockAcceptorUserHistory> findStockAcceptorUserHistory(String stockCode, Long userId) {
        return stockAcceptorUserHistoryRepository.findFirstByStockCodeAndUserIdOrderByCreatedAtDesc(stockCode, userId);
    }

    public List<StockAcceptorUserHistory> getAllStockAcceptorUserHistory(String stockCode, Long userId) {
        return stockAcceptorUserHistoryRepository.findAllByStockCodeAndUserId(stockCode, userId);
    }

    public List<StockRanking> getStockRankingListByDate(LocalDate searchDate) {
        return stockRankingRepository.findAllByDate(searchDate);
    }

    public StockRanking createStockRanking(String stockCode, LocalDate date, Integer stakeRank) {
        final StockRanking stockRanking = new StockRanking();
        stockRanking.setStockCode(stockCode);
        stockRanking.setDate(date);
        stockRanking.setStake(someLongBetween(10L, 1000L).doubleValue());
        stockRanking.setStakeRank(stakeRank);
        stockRanking.setStakeRankDelta(0);
        stockRanking.setMarketValue(someLongBetween(10L, 1000L));
        stockRanking.setMarketValueRank(someIntegerBetween(10, 1000));
        stockRanking.setMarketValueRankDelta(0);
        return stockRankingRepository.save(stockRanking);
    }

    public StockRanking createStockRanking(String stockCode, LocalDate date, Integer stakeRank, Integer marketValueRank) {
        return createStockRanking(stockCode, stockCode, date, stakeRank, marketValueRank);
    }

    public StockRanking createStockRanking(Stock stock, LocalDate date, Integer stakeRank, Integer marketValueRank) {
        return createStockRanking(stock.getCode(), stock.getName(), date, stakeRank, marketValueRank);
    }

    public StockRanking createStockRanking(String stockCode, String stockName, LocalDate date, Integer stakeRank, Integer marketValueRank) {
        final StockRanking stockRanking = new StockRanking();
        stockRanking.setStockCode(stockCode);
        stockRanking.setStockName(stockName);
        stockRanking.setDate(date);
        stockRanking.setStake(someLongBetween(10L, 1000L).doubleValue());
        stockRanking.setStakeRank(stakeRank);
        stockRanking.setStakeRankDelta(0);
        stockRanking.setMarketValue(someLongBetween(10L, 1000L));
        stockRanking.setMarketValueRank(marketValueRank);
        stockRanking.setMarketValueRankDelta(0);
        return stockRankingRepository.save(stockRanking);
    }

    public StockRanking updateStockRanking(StockRanking stockRanking) {
        return stockRankingRepository.save(stockRanking);
    }

    public void cleanUpAllStockRanking() {
        stockRankingRepository.findAll()
            .stream()
            .sorted(Comparator.comparing(StockRanking::getDate))
            .peek(it -> it.setDate(it.getDate().minusDays(10)))
            .forEach(stockRankingRepository::save);
    }

    public void cleanUpAllPosts() {
        findAllPostsWithoutLazyLoading().parallelStream()
            .peek(it -> it.setStatus(ag.act.model.Status.DELETED))
            .forEach(this::updatePost);
    }

    public StockAcceptorUser createStockAcceptorUser(String stockCode, User user) {
        final StockAcceptorUser stockAcceptorUser = new StockAcceptorUser();
        stockAcceptorUser.setStockCode(stockCode);
        stockAcceptorUser.setUserId(user.getId());

        createStockAcceptorUserHistory(stockCode, user);
        return stockAcceptorUserRepository.save(stockAcceptorUser);
    }

    public void deleteStockAcceptorUser(String stockCode, Long userId) {
        final StockAcceptorUser stockAcceptorUser = stockAcceptorUserRepository.findByStockCodeAndUserId(stockCode, userId).orElseThrow();
        stockAcceptorUserRepository.delete(stockAcceptorUser);
    }

    public StockAcceptorUserHistory createStockAcceptorUserHistory(String stockCode, User user) {
        final StockAcceptorUserHistory stockAcceptorUserHistory = new StockAcceptorUserHistory();
        stockAcceptorUserHistory.setStockCode(stockCode);
        stockAcceptorUserHistory.setUserId(user.getId());
        stockAcceptorUserHistory.setStatus(Status.ACTIVE);
        stockAcceptorUserHistory.setName(user.getName());
        stockAcceptorUserHistory.setBirthDate(user.getBirthDate());
        stockAcceptorUserHistory.setHashedPhoneNumber(user.getHashedPhoneNumber());
        stockAcceptorUserHistory.setFirstNumberOfIdentification(user.getFirstNumberOfIdentification());
        return stockAcceptorUserHistoryRepository.save(stockAcceptorUserHistory);
    }

    public StockAcceptorUserHistory updateStockAcceptorUserHistory(StockAcceptorUserHistory stockAcceptorUserHistory) {
        return stockAcceptorUserHistoryRepository.save(stockAcceptorUserHistory);
    }

    public Long getTotalAssetAmount(Long userId) {
        final User user = userRepository.findById(userId).orElseThrow();
        return user.getUserHoldingStocks().stream()
            .filter(this::isActive)
            .filter(it -> Objects.nonNull(it.getQuantity()))
            .mapToLong(it -> it.getQuantity() * it.getStock().getClosingPrice())
            .reduce(0, Long::sum);
    }

    public List<BlockedUser> findAllBlockedUsers() {
        return blockedUserRepository.findAll();
    }

    public BlockedUser createBlockedUser(Long userId, Long blockedUserId) {
        BlockedUser blockedUser = new BlockedUser();
        blockedUser.setUserId(userId);
        blockedUser.setBlockedUserId(blockedUserId);
        return blockedUserRepository.save(blockedUser);
    }

    public Optional<BlockedUser> findBlockedUserById(Long blockedUserEntityId) {
        return blockedUserRepository.findById(blockedUserEntityId);
    }

    public String convertImageUrl(FileContent fileContent) {
        return "%s/%s".formatted(s3Environment.getBaseUrl(), fileContent.getFilename());
    }

    public String getThumbnailImageUrl(String originalPath) {
        String pathWithTailingSlash = FilenameUtils.getFullPath(originalPath);
        String basename = FilenameUtils.getBaseName(originalPath);
        String extension = FilenameUtils.getExtension(originalPath);
        return "%s%s_thumbnail.%s".formatted(pathWithTailingSlash, basename, extension);
    }

    public boolean isActive(ActEntity actEntity) {
        return actEntity.getStatus() == Status.ACTIVE;
    }

    public List<AppPreference> findAllAppPreferences() {
        return appPreferenceRepository.findAll();
    }

    public AppPreference findAppPreferenceById(Long id) {
        return appPreferenceRepository.findById(id).orElseThrow(() -> new RuntimeException("[TEST] AppPreference를 찾을 수 없습니다."));
    }

    public AppPreference findAppPreferenceByType(AppPreferenceType type) {
        return appPreferenceRepository.findByType(type)
            .orElseThrow(() -> new RuntimeException("[TEST] AppPreference를 찾을 수 없습니다."));
    }

    public List<StockSearchTrend> findAllStockSearchTrends(String stockCode, Long id) {
        return stockSearchTrendRepository.findAllByStockCodeAndUserId(stockCode, id);
    }

    public StockSearchTrend createStockSearchTrend(Stock stock, User user) {
        return stockSearchTrendRepository.save(StockSearchTrend.of(stock.getCode(), user.getId()));
    }

    public StockSearchTrend createStockSearchTrend(Stock stock, User user, LocalDateTime createdAt) {
        final StockSearchTrend stockSearchTrend = createStockSearchTrend(stock, user);
        stockSearchTrend.setCreatedAt(createdAt);

        return stockSearchTrendRepository.save(stockSearchTrend);
    }

    public void cleanUpAllStockSearchTrends() {
        stockSearchTrendRepository.findAll()
            .stream()
            .sorted(Comparator.comparing(StockSearchTrend::getCreatedAt))
            .peek(it -> it.setCreatedAt(it.getCreatedAt().minusDays(10)))
            .forEach(stockSearchTrendRepository::save);
    }

    public UserHoldingStockHistoryOnDate createUserHoldingStockHistoryOnDate(Long userId, String stockCode, LocalDate date) {
        final UserHoldingStockHistoryOnDate userHoldingStockHistoryOnDate = new UserHoldingStockHistoryOnDate();
        userHoldingStockHistoryOnDate.setUserId(userId);
        userHoldingStockHistoryOnDate.setStockCode(stockCode);
        userHoldingStockHistoryOnDate.setQuantity(500L);
        userHoldingStockHistoryOnDate.setDate(date);

        return userHoldingStockHistoryOnDateRepository.save(userHoldingStockHistoryOnDate);
    }

    public UserHoldingStockHistoryOnDate createUserHoldingStockHistoryOnDate(Long userId, String stockCode, long quantity, LocalDate date) {
        final UserHoldingStockHistoryOnDate stockHistoryOnDate = new UserHoldingStockHistoryOnDate();
        stockHistoryOnDate.setUserId(userId);
        stockHistoryOnDate.setStockCode(stockCode);
        stockHistoryOnDate.setQuantity(quantity);
        stockHistoryOnDate.setDate(date);
        return userHoldingStockHistoryOnDateRepository.save(stockHistoryOnDate);
    }

    public List<UserHoldingStockHistoryOnDate> findAllByUserIdAndStockCodeOrderByStockCodeAscDate(Long userId, String stockCode) {
        return userHoldingStockHistoryOnDateRepository.findAllByUserIdAndStockCodeOrderByStockCodeAscDateDesc(userId, stockCode);
    }

    private LocalDateTime getEndOfDay(LocalDateTime date) {
        return date.with(LocalTime.MAX).withNano(0);
    }

    private LocalDateTime getStartOfDay(LocalDateTime dateTime) {
        return dateTime.with(LocalTime.MIN);
    }

    public List<HolderListReadAndCopy> findAllHolderListReadAndCopyByDigitalDocumentId(Long digitalDocumentId) {
        return holderListReadAndCopyRepository.findAllByDigitalDocumentId(digitalDocumentId);
    }

    public Optional<WebVerification> findWebVerificationByAuthenticationReference(final UUID authenticationReference) {
        return findAllWebVerificationsByAuthenticationReference(authenticationReference)
            .stream()
            .findFirst();
    }

    public List<WebVerification> findAllWebVerificationsByAuthenticationReference(final UUID authenticationReference) {
        return webVerificationRepository.findAll()
            .stream()
            .filter(it -> Objects.equals(authenticationReference, it.getAuthenticationReference()))
            .toList();
    }

    public WebVerification createWebVerification(final UUID authenticationReference) {
        final LocalDateTime now = LocalDateTime.now();
        WebVerification webVerification = new WebVerification();
        webVerification.setAuthenticationReference(authenticationReference);
        webVerification.setVerificationCode(someWebVerificationCode());
        webVerification.setVerificationCodeBaseDateTime(now);
        webVerification.setVerificationCodeStartDateTime(now);
        webVerification.setVerificationCodeEndDateTime(now.plusMinutes(VERIFICATION_CODE_DURATION_MINUTES));

        return webVerificationRepository.saveAndFlush(webVerification);
    }

    public WebVerification updateWebVerification(WebVerification webVerification) {
        return webVerificationRepository.saveAndFlush(webVerification);
    }

    public UserAnonymousCount createUserAnonymousCount(Long userId, String writeDate, int commentCount, int postCount) {
        UserAnonymousCount userAnonymousCount = new UserAnonymousCount();
        userAnonymousCount.setUserId(userId);
        userAnonymousCount.setWriteDate(writeDate);
        userAnonymousCount.setCommentCount(commentCount);
        userAnonymousCount.setPostCount(postCount);
        return userAnonymousCountRepository.save(userAnonymousCount);
    }

    public int countBestPosts(List<BoardCategory> boardCategories) {
        return postRepository.countByBoardCategoryInAndStatusInAndLikeCountGreaterThanEqual(
            boardCategories,
            StatusUtil.getPostStatusesVisibleToUsers(),
            10L
        );
    }

    public Optional<BlockedSolidarityLeaderApplicant> findBlockedSolidarityLeaderApplicant(String stockCode, Long userId) {
        return blockedSolidarityLeaderApplicantRepository.findByStockCodeAndUserId(stockCode, userId);
    }

    public BlockedSolidarityLeaderApplicant createBlockedSolidarityLeaderApplicant(String stockCode, Long userId) {
        BlockedSolidarityLeaderApplicant blockedSolidarityLeaderApplicant = new BlockedSolidarityLeaderApplicant();
        blockedSolidarityLeaderApplicant.setUserId(userId);
        blockedSolidarityLeaderApplicant.setStockCode(stockCode);
        blockedSolidarityLeaderApplicant.setSolidarityId(somePositiveLong());
        blockedSolidarityLeaderApplicant.setReasons(someString(100));
        return blockedSolidarityLeaderApplicantRepository.save(blockedSolidarityLeaderApplicant);
    }
}
