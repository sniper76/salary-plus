package ag.act.facade;

import ag.act.configuration.security.ActUserProvider;
import ag.act.converter.UserStockResponseConverter;
import ag.act.dto.SimplePageDto;
import ag.act.entity.MyDataSummary;
import ag.act.entity.User;
import ag.act.entity.mydata.JsonMyData;
import ag.act.entity.mydata.JsonMyDataStock;
import ag.act.facade.user.MyDataFacade;
import ag.act.module.mydata.MyDataService;
import ag.act.service.JsonMyDataStockService;
import ag.act.service.user.UserHoldingStockOnReferenceDateSyncService;
import ag.act.util.InMemoryPaginator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mockStatic;
import static shiver.me.timbers.data.random.RandomIntegers.someIntegerBetween;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class MyDataFacadeTest {

    private List<MockedStatic<?>> statics;
    @InjectMocks
    private MyDataFacade facade;
    @Mock
    private FileFacade fileFacade;
    @Mock
    private MyDataService myDataService;
    @Mock
    private ag.act.model.UpdateMyDataRequest updateMyDataRequest;
    @Mock
    private JsonMyDataStockService jsonMyDataStockService;
    @Mock
    private UserHoldingStockOnReferenceDateSyncService userHoldingStockOnReferenceDateSyncService;
    @Mock
    private UserStockResponseConverter userStockResponseConverter;
    @Mock
    private InMemoryPaginator inMemoryPaginator;
    @Mock
    private MyDataSummary myDataSummary;
    @Mock
    private User user;
    private Long userId;
    private String jsonStringData;

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(ActUserProvider.class));
        userId = someLong();
        jsonStringData = someString(10);

        given(ActUserProvider.getNoneNull()).willReturn(user);
        given(user.getId()).willReturn(userId);
        given(updateMyDataRequest.getJsonData()).willReturn(jsonStringData);
        given(myDataService.getMyDataSummary(userId)).willReturn(myDataSummary);
        willDoNothing().given(fileFacade).uploadMyDataJson(jsonStringData, userId);
        willDoNothing().given(myDataService).updateMyData(jsonStringData);
        willDoNothing().given(userHoldingStockOnReferenceDateSyncService).syncUserHoldingStockOnReferenceDate(myDataSummary, userId);
    }

    @Nested
    class UpdateMyData {
        @BeforeEach
        void setUp() {
            facade.updateMyData(updateMyDataRequest);
        }

        @Test
        void shouldUploadMyDataJson() {
            fileFacade.uploadMyDataJson(jsonStringData, userId);
        }

        @Test
        void shouldUpdateMyData() {
            myDataService.updateMyData(jsonStringData);
        }
    }

    @Nested
    class GetUserStocks {

        @Mock
        private JsonMyData jsonMyData;
        @Mock
        private JsonMyDataStock jsonMyDataStock;
        @Mock
        private ag.act.model.UserStockResponse userStockResponse;
        @Mock
        private List<JsonMyDataStock> jsonMyDataStockList;
        private PageRequest pageRequest;

        @BeforeEach
        void setUp() {
            int page = someIntegerBetween(1, 5);
            int size = someIntegerBetween(10, 20);
            pageRequest = PageRequest.of(page, size);
        }

        @Test
        void shouldReturnEmptyPageWhenJsonDataIsNull() {
            given(jsonMyDataStockService.getJsonMyData(userId)).willReturn(Optional.empty());
            given(inMemoryPaginator.paginate(List.of(), pageRequest)).willReturn(Page.empty(pageRequest));

            SimplePageDto<ag.act.model.UserStockResponse> actual = facade.getUserStocks(userId, pageRequest);

            assertThat(actual.getTotalElements(), is(0L));
            assertThat(actual.getContent().isEmpty(), is(true));
        }

        @Test
        void shouldReturnEmptyPageWhenJsonMyDataStockListIsEmpty() {
            given(jsonMyDataStockService.getJsonMyData(userId)).willReturn(Optional.of(jsonMyData));
            given(jsonMyData.getJsonMyDataStockList()).willReturn(List.of());
            given(inMemoryPaginator.paginate(List.of(), pageRequest)).willReturn(Page.empty(pageRequest));

            SimplePageDto<ag.act.model.UserStockResponse> actual = facade.getUserStocks(userId, pageRequest);

            assertThat(actual.getTotalElements(), is(0L));
            assertThat(actual.getContent().isEmpty(), is(true));
        }

        @Test
        void shouldReturnTheSameLengthWithJsonMyData() {
            int totalElements = 5;
            final List<JsonMyDataStock> pageContent = new ArrayList<>();
            given(jsonMyDataStockService.getJsonMyData(userId)).willReturn(Optional.of(jsonMyData));
            given(userStockResponseConverter.apply(jsonMyDataStock)).willReturn(userStockResponse);
            given(jsonMyData.getJsonMyDataStockList()).willReturn(jsonMyDataStockList);
            given(jsonMyDataStockList.size()).willReturn(totalElements);
            final PageImpl<JsonMyDataStock> pageImpl = new PageImpl<>(pageContent, pageRequest, totalElements);
            given(inMemoryPaginator.paginate(jsonMyDataStockList, pageRequest)).willReturn(pageImpl);

            SimplePageDto<ag.act.model.UserStockResponse> actual = facade.getUserStocks(userId, pageRequest);

            assertThat(actual.getContent(), is(pageContent));
        }
    }
}
