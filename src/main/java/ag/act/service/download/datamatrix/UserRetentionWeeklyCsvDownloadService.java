package ag.act.service.download.datamatrix;

import ag.act.core.holder.RequestContextHolder;
import ag.act.dto.datamatrix.UserRetentionWeeklyCsvRequestDto;
import ag.act.dto.download.DownloadFile;
import ag.act.enums.download.matrix.UserRetentionWeeklyCsvDataType;
import ag.act.exception.InternalServerException;
import ag.act.service.aws.S3Service;
import ag.act.service.download.DownloadService;
import ag.act.service.io.UploadService;
import ag.act.util.DateTimeUtil;
import ag.act.util.DownloadFileUtil;
import ag.act.util.FilenameUtil;
import ag.act.util.KoreanDateTimeUtil;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static ag.act.enums.FileType.MATRIX;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserRetentionWeeklyCsvDownloadService {

    private static final String CSV_EXTENSION = "csv";
    private static final String CSV_MIME_TYPE = "text/csv";
    private final UserRetentionWeeklyCsvDownloadProcessor userRetentionWeeklyCsvDownloadProcessor;
    private final UploadService uploadService;
    private final S3Service s3Service;
    private final DownloadService downloadService;


    //TODO: 모든 기존의 CSV가 배치저장 + 다운로드로 변경되면 해당 메소드는 제거해도 무방할 것으로 보임
    @SuppressWarnings("DataFlowIssue")
    public DownloadFile createAndDownloadCsv(UserRetentionWeeklyCsvDataType userRetentionWeeklyCsvDataType) {
        final HttpServletResponse response = RequestContextHolder.getResponse();
        final String csvFilename = FilenameUtil.getFilenameWithDate(userRetentionWeeklyCsvDataType.getFileName(), CSV_EXTENSION);

        try {
            DownloadFileUtil.setFilename(response, csvFilename);
            userRetentionWeeklyCsvDownloadProcessor.download(response, userRetentionWeeklyCsvDataType);
        } catch (Exception e) {
            throw new InternalServerException("유저 주차별 리텐션 CSV 다운로드 중 알 수 없는 오류가 발생했습니다.", e);
        }

        return DownloadFile.builder().fileName(csvFilename).build();
    }

    @SuppressWarnings("DataFlowIssue")
    public DownloadFile createAndDownloadCsv(
        UserRetentionWeeklyCsvDataType userRetentionWeeklyCsvDataType,
        UserRetentionWeeklyCsvRequestDto userRetentionWeeklyCsvRequestDto
    ) {
        final HttpServletResponse response = RequestContextHolder.getResponse();
        final String csvFilename = FilenameUtil.getFilenameWithDate(userRetentionWeeklyCsvDataType.getFileName(), CSV_EXTENSION);

        try {
            DownloadFileUtil.setFilename(response, csvFilename);
            userRetentionWeeklyCsvDownloadProcessor.download(
                response,
                userRetentionWeeklyCsvDataType,
                userRetentionWeeklyCsvRequestDto
            );
        } catch (Exception e) {
            throw new InternalServerException("유저 주차별 리텐션 CSV 다운로드 중 알 수 없는 오류가 발생했습니다.", e);
        }

        return DownloadFile.builder().fileName(csvFilename).build();
    }

    public DownloadFile downloadUserRetentionWeeklyCsv(UserRetentionWeeklyCsvDataType userRetentionWeeklyCsvDataType) {
        final LocalDate date = KoreanDateTimeUtil.getYesterdayLocalDate();
        final String csvFilename = FilenameUtil.getFilenameWithDate(
            userRetentionWeeklyCsvDataType.getFileName(),
            CSV_EXTENSION,
            date
        );
        final String csvFilePath = getFormattedFileFullPath(
            date,
            csvFilename
        );

        return downloadService.downloadFile(csvFilePath, csvFilename, CSV_MIME_TYPE);
    }

    // 기준일로부터 하루 전의 csv 파일을 읽어와서 기존 레코드와 업데이트된 데이터를 병합하여 파일을 생성하고 업로드
    public String createUserRetentionWeeklyCsv(UserRetentionWeeklyCsvDataType userRetentionWeeklyCsvDataType) {
        final String fileName = userRetentionWeeklyCsvDataType.getFileName();
        final LocalDate referenceDate = KoreanDateTimeUtil.getYesterdayLocalDate(); // 어제가 기준일

        final byte[] csvBytes = createCsvBytes(
            userRetentionWeeklyCsvDataType,
            getFileFullPath(referenceDate.minusDays(1), fileName),
            referenceDate
        );
        return uploadService.uploadCsv(
            csvBytes,
            getFileFullPath(referenceDate, fileName)
        );
    }

    private byte[] createCsvBytes(
        UserRetentionWeeklyCsvDataType userRetentionWeeklyCsvDataType,
        String fileFullPath,
        LocalDate referenceDate
    ) {
        try {
            return userRetentionWeeklyCsvDownloadProcessor.create(
                userRetentionWeeklyCsvDataType,
                getCsvData(fileFullPath),
                referenceDate
            ).toByteArray();
        } catch (Exception e) {
            throw new InternalServerException("유저 주차별 리텐션 CSV 파일을 생성하는 중에 오류가 발생했습니다.", e);
        }
    }

    private String getFileFullPath(LocalDate date, String fileName) {
        final String filenameWithDate = FilenameUtil.getFilenameWithDate(fileName, CSV_EXTENSION, date);
        return getFormattedFileFullPath(date, filenameWithDate);
    }

    private String getFormattedFileFullPath(LocalDate date, String filenameWithDate) {
        return String.format(
            "%s/%s/%s",
            MATRIX.getPathPrefix(),
            DateTimeUtil.formatLocalDate(date, "yyyy/MM/dd"),
            filenameWithDate
        );
    }

    private List<String[]> getCsvData(String fullPath) {
        final Optional<InputStream> inputStream = s3Service.findObjectFromPrivateBucket(fullPath);

        if (inputStream.isEmpty()) {
            return List.of();
        }

        try (CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream.get()))) {
            return csvReader.readAll();
        } catch (IOException | CsvException e) {
            throw new InternalServerException("파일을 읽어오는 중에 오류가 발생했습니다.", e);
        }
    }
}
