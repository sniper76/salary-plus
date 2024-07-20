package ag.act.converter.notification;

import ag.act.converter.DateTimeConverter;
import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.notification.NotificationCategory;
import ag.act.enums.notification.NotificationType;
import ag.act.model.UserNotificationResponse;
import ag.act.repository.interfaces.UserNotificationDetails;
import ag.act.util.AppLinkUrlGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;

import static ag.act.TestUtil.someLocalDateTime;
import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static shiver.me.timbers.data.random.RandomBooleans.someBoolean;
import static shiver.me.timbers.data.random.RandomEnums.someEnum;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class UserNotificationResponseConverterTest {

    @InjectMocks
    private UserNotificationResponseConverter converter;
    @Mock
    private UserNotificationDetails userNotificationDetails;
    @Mock
    private AppLinkUrlGenerator appLinkUrlGenerator;
    private UserNotificationResponse actualResponse;
    private Long notificationId;
    private NotificationCategory notificationCategory;
    private NotificationType notificationType;
    private LocalDateTime createdAt;
    private Boolean isRead;
    private String stockCode;
    private String linkUrl;
    private BoardGroup boardGroup;
    private Long postId;

    @BeforeEach
    void setUp() {
        notificationId = someLong();
        notificationCategory = someEnum(NotificationCategory.class);
        notificationType = someEnum(NotificationType.class);
        createdAt = someLocalDateTime();
        isRead = someBoolean();
        stockCode = someStockCode();
        postId = someLong();
        boardGroup = someEnum(BoardGroup.class);
        linkUrl = someString(10);

        given(userNotificationDetails.getId()).willReturn(notificationId);
        given(userNotificationDetails.getCategory()).willReturn(notificationCategory);
        given(userNotificationDetails.getType()).willReturn(notificationType);
        given(userNotificationDetails.getCreatedAt()).willReturn(createdAt);
        given(userNotificationDetails.getRead()).willReturn(isRead);
        given(userNotificationDetails.getBoardGroup()).willReturn(boardGroup);
        given(userNotificationDetails.getStockCode()).willReturn(stockCode);
        given(appLinkUrlGenerator.generateBoardGroupPostLinkUrl(stockCode, boardGroup.name(), postId))
            .willReturn(linkUrl);
    }

    @Nested
    class WhenNotificationWithoutPostData extends DefaultTestCases {

        @BeforeEach
        void setUp() {
            postId = null;

            given(userNotificationDetails.getPostId()).willReturn(postId);
            given(appLinkUrlGenerator.generateBoardGroupPostLinkUrl(stockCode, boardGroup.name(), postId))
                .willReturn(linkUrl);

            actualResponse = converter.convert(userNotificationDetails);
        }

        @Test
        void shouldNotHaveAnyPostRelatedData() {
            assertThat(actualResponse.getPost(), nullValue());
        }
    }

    @Nested
    class WhenNotificationIncludesPostData extends DefaultTestCases {
        private String postTitle;
        private BoardCategory boardCategory;
        private String stockName;

        @BeforeEach
        void setUp() {
            postTitle = someString(10);
            boardCategory = someEnum(BoardCategory.class);
            stockName = someString(15);

            given(userNotificationDetails.getPostId()).willReturn(postId);
            given(userNotificationDetails.getPostTitle()).willReturn(postTitle);
            given(userNotificationDetails.getBoardCategory()).willReturn(boardCategory);
            given(userNotificationDetails.getStockName()).willReturn(stockName);

            actualResponse = converter.convert(userNotificationDetails);
        }

        @Test
        void shouldHavePostRelatedData() {
            assertThat(actualResponse.getPost().getId(), is(postId));
            assertThat(actualResponse.getPost().getTitle(), is(postTitle));
            assertThat(actualResponse.getPost().getBoardCategory(), is(boardCategory.name()));
            assertThat(actualResponse.getPost().getStockCode(), is(stockCode));
            assertThat(actualResponse.getPost().getStockName(), is(stockName));
        }
    }

    @SuppressWarnings("unused")
    class DefaultTestCases {
        @Test
        void shouldReturnTheSameUserInfo() {
            assertThat(actualResponse.getId(), is(notificationId));
            assertThat(actualResponse.getCreatedAt(), is(DateTimeConverter.convert(createdAt)));
            assertThat(actualResponse.getIsRead(), is(isRead));
            assertThat(actualResponse.getType(), is(notificationType.name()));
            assertThat(actualResponse.getCategory(), is(notificationCategory.name()));
            assertThat(actualResponse.getLinkUrl(), is(linkUrl));
        }
    }
}
