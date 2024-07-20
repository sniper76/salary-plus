package ag.act.handler.admin.post;

import ag.act.converter.PageDataConverter;
import ag.act.dto.GetPostsSearchDto;
import ag.act.facade.admin.post.AdminPostFacade;
import ag.act.model.GetBoardGroupPostResponse;
import ag.act.util.AppRenewalDateProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static ag.act.TestUtil.someLocalDate;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomStrings.someString;
import static shiver.me.timbers.data.random.RandomTimes.someTime;

@MockitoSettings(strictness = Strictness.LENIENT)
class AdminBoardGroupPostApiDelegateImplTest {
    @InjectMocks
    private AdminBoardGroupPostApiDelegateImpl apiDelegate;
    @Mock
    private AdminPostFacade adminPostFacade;
    @Mock
    private PageDataConverter pageDataConverter;
    @Mock
    private AppRenewalDateProvider appRenewalDateProvider;

    @Nested
    class GetPosts {

        private Integer page;
        private Integer size;
        private List<String> sorts;
        @Mock
        private PageRequest pageRequest;
        private ResponseEntity<GetBoardGroupPostResponse> actualResponse;
        @Mock
        private GetBoardGroupPostResponse response;

        @BeforeEach
        void setUp() {
            page = someIntegerBetween(1, 1000);
            size = someIntegerBetween(10, 100);
            sorts = List.of(someString(10));

            final String boardGroup = someString(10);
            final String boardCategory = someString(10);
            final String searchType = someString(10);
            final String searchKeyword = someString(10);
            final String statusName = someString(10);

            final LocalDate appRenewalDate = someLocalDate();
            final Instant searchStartDate = someTime().toInstant();
            final Instant searchEndDate = searchStartDate.plus(someIntegerBetween(1, 100), ChronoUnit.DAYS);

            given(pageDataConverter.convert(page, size, sorts)).willReturn(pageRequest);
            given(adminPostFacade.getPosts(any(GetPostsSearchDto.class))).willReturn(response);
            given(appRenewalDateProvider.get()).willReturn(appRenewalDate);

            actualResponse = apiDelegate.getPosts(
                boardGroup,
                boardCategory,
                searchType,
                searchKeyword,
                searchStartDate,
                searchEndDate,
                statusName,
                page,
                size,
                sorts
            );
        }

        @Test
        void shouldReturnResponse() {
            assertThat(actualResponse.getBody(), is(response));
        }

        @Test
        void shouldConvertPageRequest() {
            then(pageDataConverter).should().convert(page, size, sorts);
        }

        @Test
        void shouldCallAdminPostFacade() {
            then(adminPostFacade).should().getPosts(any(GetPostsSearchDto.class));
        }
    }

}