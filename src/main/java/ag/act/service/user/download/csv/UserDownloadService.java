package ag.act.service.user.download.csv;

import ag.act.dto.user.UserWithStockDto;
import ag.act.exception.InternalServerException;
import ag.act.repository.UserRepository;
import ag.act.util.CSVWriterFactory;
import com.opencsv.CSVWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.util.List;

@SuppressWarnings({"RightCurly"})
@Service
public class UserDownloadService {
    private static final int PAGE_SIZE = 50;
    private static final List<String> DEFAULT_HEADERS = List.of(
        "번호", "이름", "생년월일", "주소", "상세주소", "우편번호", "전화번호", "주식수"
    );
    private final UserRepository userRepository;
    private final CSVWriterFactory csvWriterFactory;
    private final UserCsvRecordGenerator userCsvRecordGenerator;

    public UserDownloadService(
        UserRepository userRepository,
        CSVWriterFactory csvWriterFactory,
        UserCsvRecordGenerator userCsvRecordGenerator
    ) {
        this.userRepository = userRepository;
        this.csvWriterFactory = csvWriterFactory;
        this.userCsvRecordGenerator = userCsvRecordGenerator;
    }

    public void downloadUsersByStockCodeCsv(String stockCode, HttpServletResponse response) {
        Pageable pageable = PageRequest.of(0, PAGE_SIZE, Sort.by(Sort.Direction.ASC, "updatedAt"));

        try (CSVWriter csvWriter = initializeCsvWriter(response.getOutputStream())) {
            writeHeaders(csvWriter);

            Page<UserWithStockDto> page;
            do {
                page = getUserWithStocks(stockCode, pageable);
                generateCsvChunk(page, csvWriter);
                response.flushBuffer();
                pageable = pageable.next();
            } while (!page.isEmpty());
        } catch (Exception e) {
            throw new InternalServerException("CSV 전자문서 다운로드 중 오류가 발생했습니다.", e);
        }
    }

    private Page<UserWithStockDto> getUserWithStocks(String stockCode, Pageable pageable) {
        return userRepository.findUserWitStocksByStockCode(stockCode, pageable);
    }

    private void generateCsvChunk(Page<UserWithStockDto> items, CSVWriter csvWriter) {

        int rowNum = items.getNumber() * items.getSize();

        for (UserWithStockDto userWithStockDto : items) {
            csvWriter.writeNext(
                userCsvRecordGenerator.toCsvRecord(++rowNum, userWithStockDto)
            );
        }
    }

    private void writeHeaders(CSVWriter csvWriter) {
        csvWriter.writeNext(DEFAULT_HEADERS.toArray(String[]::new));
    }

    private CSVWriter initializeCsvWriter(OutputStream outputStream) {
        return csvWriterFactory.create(outputStream);
    }
}
