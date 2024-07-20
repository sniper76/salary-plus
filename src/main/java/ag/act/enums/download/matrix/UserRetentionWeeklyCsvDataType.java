package ag.act.enums.download.matrix;

import lombok.Getter;

import java.util.Optional;

import static ag.act.enums.download.matrix.UserRetentionWeeklyCsvHeaderWeekUnit.THREE_WEEKS;
import static ag.act.enums.download.matrix.UserRetentionWeeklyCsvHeaderWeekUnit.WEEK;
import static ag.act.enums.download.matrix.UserRetentionWeeklyCsvRowDataType.USER;
import static ag.act.enums.download.matrix.UserRetentionWeeklyCsvRowDataType.WEEKLY;
import static ag.act.enums.download.matrix.UserRetentionWeeklyCsvRowDataType.WEEKLY_OF_DIGITAL_DOCUMENT_PERIOD;

@Getter
@SuppressWarnings("LineLength")
public enum UserRetentionWeeklyCsvDataType {
    PIN_VERIFICATION_GIVEN_REGISTER(WEEK, WEEKLY, "유저_로그인수_추이"),
    DIGITAL_DOCUMENT_PARTICIPATION_RATE_GIVEN_REGISTER(WEEK, WEEKLY, "전자문서_참여율_추이"),
    PIN_VERIFICATION_GIVEN_FIRST_DIGITAL_DOCUMENT_COMPLETE(WEEK, WEEKLY, "전자문서_최초_참여_후_리텐션_추이"),
    PIN_VERIFICATION_GIVEN_REGISTER_AND_DIGITAL_DOCUMENT_COMPLETE(WEEK, WEEKLY, "전자문서에_참여한_유저의_리텐션_추이"),
    PIN_VERIFICATION_GIVEN_REGISTER_AND_NO_DIGITAL_DOCUMENT_COMPLETE(WEEK, WEEKLY, "전자문서에_참여하지_않은_유저의_리텐션_추이"),
    NON_RETAINED_USER_GIVEN_REGISTER_AND_DIGITAL_DOCUMENT_COMPLETE(WEEK, WEEKLY, "전자문서에_참여한_유저의_비접속_추이"),
    NON_RETAINED_USER_GIVEN_REGISTER_AND_NO_DIGITAL_DOCUMENT_COMPLETE(WEEK, WEEKLY, "전자문서에_참여하지_않은_유저의_비접속_추이"),
    ALL_USERS_GIVEN_ALL_CONDITIONS(WEEK, USER, "모든_유저별_특정_주간의_리텐션_결과"),
    ALL_USERS_SIGNATURE_OPPORTUNITY_GIVEN_HAVE_STOCK_IN_ALL_DIGITAL_DOCUMENTS_PROGRESS_PERIOD(WEEK, USER, "모든_유저별_특정_주간의_모든_전자문서_서명_기회"),
    ALL_USERS_SIGNATURE_OPPORTUNITY_GIVEN_HAVE_STOCK_IN_DIGITAL_DOCUMENTS_EXCEPT_ETC_PROGRESS_PERIOD(WEEK, USER, "모든_유저별_특정_주간의_기타문서_제외한_전자문서_서명_기회"),
    PIN_VERIFICATION_FOR_THREE_WEEKS_IN_A_ROW_GIVEN_REGISTER_AND_DIGITAL_DOCUMENT_COMPLETE(THREE_WEEKS, WEEKLY, "전자문서에_참여한_유저의_3주_연속_리텐션_추이"),
    PIN_VERIFICATION_FOR_THREE_WEEKS_IN_A_ROW_GIVEN_REGISTER_AND_NO_DIGITAL_DOCUMENT_COMPLETE(THREE_WEEKS, WEEKLY, "전자문서에_참여하지_않은_유저의_3주_연속_리텐션_추이"),
    PIN_VERIFICATION_GIVEN_REGISTER_AND_SPECIFIC_DIGITAL_DOCUMENT_COMPLETE(WEEK, WEEKLY_OF_DIGITAL_DOCUMENT_PERIOD, "특정_전자문서에_참여한_신규_유저의_리텐션_추이"),
    PIN_VERIFICATION_GIVEN_REGISTER_AND_SPECIFIC_DIGITAL_DOCUMENT_NOT_COMPLETE(WEEK, WEEKLY_OF_DIGITAL_DOCUMENT_PERIOD, "특정_전자문서에_참여하지_않은_신규_유저의_리텐션_추이"),
    ;

    private final UserRetentionWeeklyCsvHeaderWeekUnit weekUnit;
    private final UserRetentionWeeklyCsvRowDataType rowDataType;
    private final String fileName;

    UserRetentionWeeklyCsvDataType(
        UserRetentionWeeklyCsvHeaderWeekUnit weekUnit,
        UserRetentionWeeklyCsvRowDataType userRetentionWeeklyCsvRowDataType,
        String fileName
    ) {
        this.weekUnit = weekUnit;
        this.rowDataType = userRetentionWeeklyCsvRowDataType;
        this.fileName = fileName;
    }

    public static UserRetentionWeeklyCsvDataType fromPath(String path) {
        return UserRetentionWeeklyCsvDataType.valueOf(path.toUpperCase().replace("-", "_"));
    }

    public static Optional<UserRetentionWeeklyCsvDataType> fromValue(String targetTypeName) {
        try {
            return Optional.of(UserRetentionWeeklyCsvDataType.valueOf(targetTypeName.toUpperCase()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
