package ag.act.dto.datamatrix;

import ag.act.exception.BadRequestException;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Objects;

@Getter
public class UserRetentionDataMapPeriodDto {

    private final LocalDate startDate;
    private final LocalDate endDate;
    private final LocalDate referenceWeek;

    private UserRetentionDataMapPeriodDto(LocalDate startDate, LocalDate endDate, LocalDate referenceWeek) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.referenceWeek = referenceWeek;
    }

    private static void validateRetentionDataMapReferenceWeek(LocalDate referenceWeek) {
        if (Objects.isNull(referenceWeek)) {
            throw new BadRequestException("리텐션 조회 기준일은 반드시 주어져야 합니다.");
        }
    }

    private static void validateRetentionDataMapPeriod(LocalDate startDate, LocalDate endDate) {
        if (Objects.isNull(startDate) || Objects.isNull(endDate)) {
            throw new BadRequestException("리텐션 조회 시작 일자와 종료 일자는 반드시 주어져야 합니다.");
        }
    }

    public static UserRetentionDataMapPeriodDto newInstance(LocalDate startDate, LocalDate endDate, LocalDate referenceWeek) {
        validateRetentionDataMapPeriod(startDate, endDate);
        validateRetentionDataMapReferenceWeek(referenceWeek);

        return create(startDate, endDate, referenceWeek);
    }

    public static UserRetentionDataMapPeriodDto newInstance(LocalDate startDate, LocalDate endDate) {
        validateRetentionDataMapPeriod(startDate, endDate);
        return create(startDate, endDate, null);
    }

    private static UserRetentionDataMapPeriodDto create(LocalDate startDate, LocalDate endDate, LocalDate referenceWeek) {
        return new UserRetentionDataMapPeriodDto(startDate, endDate, referenceWeek);
    }
}
