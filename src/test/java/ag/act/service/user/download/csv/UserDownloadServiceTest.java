package ag.act.service.user.download.csv;

import ag.act.core.holder.RequestContextHolder;
import ag.act.dto.user.UserWithStockDto;
import ag.act.entity.User;
import ag.act.repository.UserRepository;
import ag.act.util.CSVWriterFactory;
import ag.act.util.DownloadFileUtil;
import com.opencsv.CSVWriter;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

import static ag.act.TestUtil.someStockCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mockStatic;
import static shiver.me.timbers.data.random.RandomLongs.someLong;
import static shiver.me.timbers.data.random.RandomStrings.someString;

@MockitoSettings(strictness = Strictness.LENIENT)
class UserDownloadServiceTest {

    @InjectMocks
    private UserDownloadService service;
    private List<MockedStatic<?>> statics;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserCsvRecordGenerator userCsvRecordGenerator;
    @Mock
    private CSVWriterFactory csvWriterFactory;
    @Mock
    private UserWithStockDto userWithStockDto;
    @Mock
    private HttpServletResponse response;
    @Mock
    private ServletOutputStream outputStream;
    @Mock
    private CSVWriter csvWriter;
    @Mock
    private Page<UserWithStockDto> userWithStockDtoPage;
    @Mock
    private User user;

    @AfterEach
    void tearDown() {
        statics.forEach(MockedStatic::close);
    }

    @BeforeEach
    void setUp() {
        statics = List.of(mockStatic(RequestContextHolder.class), mockStatic(DownloadFileUtil.class));
    }

    @Nested
    class DownloadCsvFile {

        private String[] firstRecord;

        @BeforeEach
        void setUp() throws IOException {
            String stockCode = someStockCode();
            firstRecord = new String[] {someString(50)};
            final Long userId = someLong();
            final List<UserWithStockDto> userWithStockDtoList = List.of(userWithStockDto);

            given(userWithStockDto.user()).willReturn(user);
            given(user.getId()).willReturn(userId);
            given(response.getOutputStream()).willReturn(outputStream);

            given(csvWriterFactory.create(outputStream)).willReturn(csvWriter);

            given(userRepository.findUserWitStocksByStockCode(
                eq(stockCode),
                any(Pageable.class)
            )).willReturn(userWithStockDtoPage);

            given(userWithStockDtoPage.getNumber()).willReturn(0);
            given(userWithStockDtoPage.getSize()).willReturn(2);
            given(userWithStockDtoPage.getContent()).willReturn(userWithStockDtoList);
            given(userWithStockDtoPage.iterator()).willReturn(userWithStockDtoList.iterator());
            given(userWithStockDtoPage.hasNext()).willReturn(false);
            given(userWithStockDtoPage.isEmpty()).willReturn(true);

            given(userCsvRecordGenerator.toCsvRecord(anyInt(), eq(userWithStockDto)))
                .willReturn(firstRecord);

            service.downloadUsersByStockCodeCsv(stockCode, response);
        }

        @Test
        void shouldCallResponseFlushBuffer() throws IOException {
            then(response).should().flushBuffer();
        }

        @Test
        void shouldWriteHeaders() {
            then(csvWriter).should().writeNext(new String[] {
                "번호", "이름", "생년월일", "주소", "상세주소", "우편번호", "전화번호", "주식수"
            });
        }

        @Test
        void shouldWriteFirstRecord() {
            then(csvWriter).should().writeNext(firstRecord);
        }
    }
}