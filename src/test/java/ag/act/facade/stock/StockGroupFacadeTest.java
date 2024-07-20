package ag.act.facade.stock;

import ag.act.converter.stock.StockGroupItemResponseConverter;
import ag.act.converter.stock.StockGroupRequestConverter;
import ag.act.entity.Stock;
import ag.act.entity.StockGroup;
import ag.act.entity.StockGroupMapping;
import ag.act.exception.BadRequestException;
import ag.act.exception.NotFoundException;
import ag.act.facade.stock.group.StockGroupFacade;
import ag.act.facade.stock.group.StockGroupResultItem;
import ag.act.model.CreateStockGroupRequest;
import ag.act.model.StockGroupResponse;
import ag.act.model.UpdateStockGroupRequest;
import ag.act.service.stock.StockGroupMappingService;
import ag.act.service.stock.StockGroupService;
import ag.act.service.stock.StockService;
import ag.act.validator.StockGroupValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;

import static ag.act.TestUtil.assertException;
import static ag.act.TestUtil.someStockCode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static shiver.me.timbers.data.random.RandomLongs.someLong;

@MockitoSettings(strictness = Strictness.LENIENT)
class StockGroupFacadeTest {

    @InjectMocks
    private StockGroupFacade facade;

    @Mock
    private StockGroupService stockGroupService;
    @Mock
    private StockGroupMappingService stockGroupMappingService;
    @Mock
    private StockService stockService;
    @Mock
    private StockGroupItemResponseConverter stockGroupItemResponseConverter;
    @Mock
    private StockGroupRequestConverter stockGroupRequestConverter;
    @Mock
    private StockGroupValidator stockGroupValidator;

    @Nested
    class CreateStockGroup {

        @Mock
        private CreateStockGroupRequest request;
        @Mock
        private StockGroup stockGroup;
        @Mock
        private StockGroupResponse stockGroupResponse;
        private StockGroupResponse actualResponse;
        private Long stockGroupId;
        private List<String> stockCodes;
        @Mock
        private StockGroupMapping stockGroupMapping;
        @Mock
        private Stock stock;

        @BeforeEach
        void setUp() {
            stockGroupId = someLong();
            stockCodes = List.of(someStockCode());
            List<Stock> stocks = List.of(stock);

            given(stockGroupRequestConverter.convert(request)).willReturn(stockGroup);
            given(request.getStockCodes()).willReturn(stockCodes);
            given(stockGroupService.save(stockGroup)).willReturn(stockGroup);
            given(stockGroup.getId()).willReturn(stockGroupId);
            given(stockGroupItemResponseConverter.convert(any(StockGroupResultItem.class))).willReturn(stockGroupResponse);
            given(stockGroupMappingService.createMappings(stockGroupId, stockCodes)).willReturn(List.of(stockGroupMapping));
            given(stockService.findAllByCodes(stockCodes)).willReturn(stocks);
            willDoNothing().given(stockGroupValidator).validateStockCodes(stockCodes, stocks);
        }

        @Nested
        class CreateSuccessfully {
            @BeforeEach
            void setUp() {
                actualResponse = facade.createStockGroup(request);
            }

            @Test
            void shouldReturnStockGroupResponse() {
                assertThat(actualResponse, is(stockGroupResponse));
            }

            @Test
            void shouldCallCreateMappings() {
                given(stockGroupMappingService.createMappings(stockGroupId, stockCodes)).willReturn(List.of(stockGroupMapping));
            }

            @Test
            void shouldCallSave() {
                given(stockGroupService.save(stockGroup)).willReturn(stockGroup);
            }
        }

        @Nested
        class WhenNotFoundSomeStocks {

            @Test
            void shouldThrowBadRequestException() {
                willThrow(BadRequestException.class)
                    .given(stockGroupValidator).validateStockCodes(anyList(), anyList());

                assertThrows(
                    BadRequestException.class,
                    () -> actualResponse = facade.createStockGroup(request)
                );
            }
        }
    }

    @Nested
    class UpdateStockGroup {

        @Mock
        private UpdateStockGroupRequest request;
        @Mock
        private StockGroup stockGroup;
        @Mock
        private StockGroupResponse stockGroupResponse;
        private StockGroupResponse actualResponse;
        private Long stockGroupId;
        private List<String> stockCodes;
        @Mock
        private StockGroupMapping stockGroupMapping;
        @Mock
        private Stock stock;

        @BeforeEach
        void setUp() {
            stockGroupId = someLong();
            stockCodes = List.of(someStockCode());
            List<Stock> stocks = List.of(stock);

            given(stockGroupService.findById(stockGroupId)).willReturn(java.util.Optional.of(stockGroup));
            given(stockGroupRequestConverter.convert(stockGroupId, request)).willReturn(stockGroup);
            given(request.getStockCodes()).willReturn(stockCodes);
            given(stockGroupService.save(stockGroup)).willReturn(stockGroup);
            given(stockGroup.getId()).willReturn(stockGroupId);
            given(stockGroupItemResponseConverter.convert(any(StockGroupResultItem.class))).willReturn(stockGroupResponse);
            given(stockGroupMappingService.createMappings(stockGroupId, stockCodes)).willReturn(List.of(stockGroupMapping));
            given(stockService.findAllByCodes(stockCodes)).willReturn(stocks);
            willDoNothing().given(stockGroupValidator).validateStockCodes(stockCodes, stocks);
            given(stockGroupRequestConverter.map(request, stockGroup)).willReturn(stockGroup);
        }

        @Nested
        class CreateSuccessfully {
            @BeforeEach
            void setUp() {
                actualResponse = facade.updateStockGroup(stockGroupId, request);
            }

            @Test
            void shouldReturnStockGroupResponse() {
                assertThat(actualResponse, is(stockGroupResponse));
            }

            @Test
            void shouldCallCreateMappings() {
                given(stockGroupMappingService.createMappings(stockGroupId, stockCodes)).willReturn(List.of(stockGroupMapping));
            }

            @Test
            void shouldCallSave() {
                given(stockGroupService.save(stockGroup)).willReturn(stockGroup);
            }
        }

        @Nested
        class WhenNotFoundStockGroup {

            @Test
            void shouldThrowBadRequestException() {
                given(stockGroupService.findById(stockGroupId)).willReturn(Optional.empty());

                assertException(
                    NotFoundException.class,
                    () -> actualResponse = facade.updateStockGroup(stockGroupId, request),
                    "종목그룹을 찾을 수 없습니다."
                );
            }
        }
    }

    @Nested
    class DeleteStockGroup {

        @Nested
        class DeleteSuccess {

            private final Long stockGroupId = someLong();
            @Mock
            private StockGroup stockGroup;

            @BeforeEach
            void setUp() {
                given(stockGroupService.findById(stockGroupId)).willReturn(Optional.of(stockGroup));
            }

            @Test
            void shouldCallSave() {
                facade.deleteStockGroup(stockGroupId);

                then(stockGroupService).should().deleteStockGroup(stockGroup);
            }
        }

        @Nested
        class DeleteFail {
            private final Long notExistStockGroupId = someLong();

            @BeforeEach
            void setUp() {
                given(stockGroupService.findById(notExistStockGroupId)).willReturn(Optional.empty());
            }

            @Test
            void shouldThrowNotFoundException() {
                assertException(
                    NotFoundException.class,
                    () -> facade.deleteStockGroup(notExistStockGroupId),
                    "종목그룹을 찾을 수 없습니다."
                );
            }
        }
    }
}
