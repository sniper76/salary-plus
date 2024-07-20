package ag.act.service.krx;

import ag.act.dto.krx.StkItemPriceDto;
import ag.act.dto.krx.StockBaseInfoDto;
import ag.act.dto.krx.StockItemDto;
import ag.act.dto.krx.StockPriceInfoDto;
import ag.act.enums.KrxServiceType;
import ag.act.module.krx.KrxHttpClientUtil;
import ag.act.module.krx.KrxService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.stream.Stream;

import static ag.act.enums.KrxServiceType.KNX_BYDD_TRD;
import static ag.act.enums.KrxServiceType.KNX_ISU_BASE_INFO;
import static ag.act.enums.KrxServiceType.KSQ_BYDD_TRD;
import static ag.act.enums.KrxServiceType.KSQ_ISU_BASE_INFO;
import static ag.act.enums.KrxServiceType.STK_BYDD_TRD;
import static ag.act.enums.KrxServiceType.STK_ISU_BASE_INFO;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class KrxServiceTest {

    @InjectMocks
    private KrxService krxService;
    @Mock
    private KrxHttpClientUtil krxHttpClientUtil;
    private String basDd;

    @BeforeEach
    void setUp() {
        // Given
        basDd = someString(8);
    }

    @Nested
    class SendKrxDailyInfoRequest {

        @Mock
        private StockPriceInfoDto stockPriceInfoDto;

        @ParameterizedTest(name = "{index} => krxServiceType=''{0}''")
        @MethodSource("dailyInfoTypeProvider")
        void shouldReturnStockPriceInfoDto(KrxServiceType krxServiceType) {

            // Given
            given(krxHttpClientUtil.callApi(krxServiceType, basDd, StockPriceInfoDto.class)).willReturn(stockPriceInfoDto);

            // When
            final StockPriceInfoDto actual = krxService.getDailyInfo(krxServiceType, basDd);

            // Then
            assertThat(actual, is(stockPriceInfoDto));
        }

        private static Stream<Arguments> dailyInfoTypeProvider() {
            return Stream.of(
                Arguments.of(STK_BYDD_TRD),
                Arguments.of(KSQ_BYDD_TRD),
                Arguments.of(KNX_BYDD_TRD)
            );
        }
    }

    @Nested
    class SendKrxBaseInfoRequest {

        @Mock
        private StockBaseInfoDto stockBaseInfoDto;

        @ParameterizedTest(name = "{index} => krxServiceType=''{0}''")
        @MethodSource("baseInfoTypeProvider")
        void shouldReturnStockPriceInfoDto(KrxServiceType krxServiceType) {

            // Given
            given(krxHttpClientUtil.callApi(krxServiceType, basDd, StockBaseInfoDto.class)).willReturn(stockBaseInfoDto);

            // When
            final StockBaseInfoDto actual = krxService.getBaseInfo(krxServiceType, basDd);

            // Then
            assertThat(actual, is(stockBaseInfoDto));
        }

        private static Stream<Arguments> baseInfoTypeProvider() {
            return Stream.of(
                Arguments.of(STK_ISU_BASE_INFO),
                Arguments.of(KSQ_ISU_BASE_INFO),
                Arguments.of(KNX_ISU_BASE_INFO)
            );
        }
    }

    @Nested
    class GetAllIsuBasicInfos {
        @Mock
        private StockBaseInfoDto stockBaseInfoDto1;
        @Mock
        private StockBaseInfoDto stockBaseInfoDto2;
        @Mock
        private StockBaseInfoDto stockBaseInfoDto3;
        @Mock
        private StockItemDto stockItemDto1;
        @Mock
        private StockItemDto stockItemDto2;
        @Mock
        private StockItemDto stockItemDto3;

        @Test
        void shouldReturnAllStockItemDtos() {

            // Given
            given(krxHttpClientUtil.callApi(STK_ISU_BASE_INFO, basDd, StockBaseInfoDto.class)).willReturn(stockBaseInfoDto1);
            given(krxHttpClientUtil.callApi(KSQ_ISU_BASE_INFO, basDd, StockBaseInfoDto.class)).willReturn(stockBaseInfoDto2);
            given(krxHttpClientUtil.callApi(KNX_ISU_BASE_INFO, basDd, StockBaseInfoDto.class)).willReturn(stockBaseInfoDto3);
            given(stockBaseInfoDto1.getOutBlock_1()).willReturn(List.of(stockItemDto1));
            given(stockBaseInfoDto2.getOutBlock_1()).willReturn(List.of(stockItemDto2));
            given(stockBaseInfoDto3.getOutBlock_1()).willReturn(List.of(stockItemDto3));

            // When
            final List<StockItemDto> actual = krxService.getAllIsuBasicInfos(basDd);

            // Then
            assertThat(actual, contains(stockItemDto1, stockItemDto2, stockItemDto3));
            then(krxHttpClientUtil).should().callApi(STK_ISU_BASE_INFO, basDd, StockBaseInfoDto.class);
            then(krxHttpClientUtil).should().callApi(KSQ_ISU_BASE_INFO, basDd, StockBaseInfoDto.class);
            then(krxHttpClientUtil).should().callApi(KNX_ISU_BASE_INFO, basDd, StockBaseInfoDto.class);
        }
    }

    @Nested
    class GetAllIsuDailyInfos {
        @Mock
        private StockPriceInfoDto stockPriceInfoDto1;
        @Mock
        private StockPriceInfoDto stockPriceInfoDto2;
        @Mock
        private StockPriceInfoDto stockPriceInfoDto3;
        @Mock
        private StkItemPriceDto stockItemPriceDto1;
        @Mock
        private StkItemPriceDto stockItemPriceDto2;
        @Mock
        private StkItemPriceDto stockItemPriceDto3;


        @Test
        void shouldReturnAllStockPriceItemDtos() {

            // Given
            given(krxHttpClientUtil.callApi(STK_BYDD_TRD, basDd, StockPriceInfoDto.class)).willReturn(stockPriceInfoDto1);
            given(krxHttpClientUtil.callApi(KSQ_BYDD_TRD, basDd, StockPriceInfoDto.class)).willReturn(stockPriceInfoDto2);
            given(krxHttpClientUtil.callApi(KNX_BYDD_TRD, basDd, StockPriceInfoDto.class)).willReturn(stockPriceInfoDto3);
            given(stockPriceInfoDto1.getOutBlock_1()).willReturn(List.of(stockItemPriceDto1));
            given(stockPriceInfoDto2.getOutBlock_1()).willReturn(List.of(stockItemPriceDto2));
            given(stockPriceInfoDto3.getOutBlock_1()).willReturn(List.of(stockItemPriceDto3));

            // When
            final List<StkItemPriceDto> actual = krxService.getAllIsuDailyInfos(basDd);

            // Then
            assertThat(actual, contains(stockItemPriceDto1, stockItemPriceDto2, stockItemPriceDto3));
            then(krxHttpClientUtil).should().callApi(STK_BYDD_TRD, basDd, StockPriceInfoDto.class);
            then(krxHttpClientUtil).should().callApi(KSQ_BYDD_TRD, basDd, StockPriceInfoDto.class);
            then(krxHttpClientUtil).should().callApi(KNX_BYDD_TRD, basDd, StockPriceInfoDto.class);
        }
    }
}
