package ag.act.enums;

import ag.act.dto.AppVersion;
import ag.act.exception.BadRequestException;
import lombok.Getter;
import org.apache.commons.lang3.IntegerRange;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unchecked")
@Getter
public enum AppPreferenceType {
    NEW_POST_THRESHOLD_HOURS("새로운 개시물 임계 시간", "48", "([0-9]+)") {
        @Override
        public Integer getValue(String value) {
            return Integer.valueOf(value);
        }
    },
    ADDITIONAL_PUSH_TIME_MINUTES("게시글의 푸시를 자동등록할때 푸시 발송을 위한 추가 시간(분)", "10", "([0-9]+)") {
        @Override
        public Integer getValue(String value) {
            return Integer.valueOf(value);
        }
    },
    PUSH_SAFE_TIME_RANGE_IN_HOURS("푸시 발송 안전 시간 범위(시간단위)", "8-21", "\\d+-\\d+") {
        @Override
        public IntegerRange getValue(String value) {
            final String[] split = value.split("-");
            return IntegerRange.of(Integer.valueOf(split[0]), Integer.valueOf(split[1]));
        }
    },
    MIN_APP_VERSION("최소 필수 앱 버전", "2.2.3", "\\d+\\.\\d+\\.\\d+") {
        @Override
        public AppVersion getValue(String value) {
            return AppVersion.of(value);
        }
    },
    BLOCK_EXCEPT_USER_IDS("차단 제외 사용자 ID 목록", "9,5", "^([0-9]+)(,[0-9]+)*$") {
        @Override
        public List<Long> getValue(String value) {
            if (StringUtils.isBlank(value)) {
                return List.of();
            }

            return Arrays.stream(value.split(","))
                .map(Long::parseLong)
                .toList();
        }
    },
    LEADER_ELECTION_FEATURE_ACTIVE_MIN_THRESHOLD_STAKE("주주대표 선출 기능 활성화 최소 지분율", "5", "([0-9]+(\\.[0-9]+)?)") {
        @Override
        public Float getValue(String value) {
            return Float.valueOf(value);
        }
    },
    LEADER_ELECTION_FEATURE_ACTIVE_MIN_THRESHOLD_MEMBER_COUNT("주주대표 선출 기능 활성화 최소 주주 수", "1000", "([0-9]+)") {
        @Override
        public Long getValue(String value) {
            return Long.valueOf(value);
        }
    },
    POST_RESTRICTION_INTERVAL_SECONDS("게시물 재등록 제한 시간(초)", "60", "([0-9]+)") {
        @Override
        public Integer getValue(String value) {
            return Integer.valueOf(value);
        }
    },
    COMMENT_RESTRICTION_INTERVAL_SECONDS("댓글/답글 재등록 제한 시간(초)", "5", "([0-9]+)") {
        @Override
        public Integer getValue(String value) {
            return Integer.valueOf(value);
        }
    },
    POST_PREVIEWS_SIZE("게시글 미리보기 개수", "5", "([0-9]+)") {
        @Override
        public Integer getValue(String value) {
            return Integer.valueOf(value);
        }
    },
    ;

    private final String displayName;
    private final String defaultValue;
    private final String valuePattern;
    private final Pattern pattern;

    AppPreferenceType(String displayName, String defaultValue, String valuePattern) {
        this.displayName = displayName;
        this.defaultValue = defaultValue;
        this.valuePattern = valuePattern;
        this.pattern = Pattern.compile(valuePattern);
    }

    public static AppPreferenceType fromValue(String targetTypeName) {
        try {
            return AppPreferenceType.valueOf(targetTypeName.toUpperCase());
        } catch (Exception e) {
            throw new BadRequestException("지원하지 않는 AppPreferenceType %s 입니다.".formatted(targetTypeName));
        }
    }

    public boolean isValueMatches(String value) {
        final Pattern pattern = this.getPattern();
        final Matcher matcher = pattern.matcher(value);

        return matcher.matches();
    }

    public abstract <T> T getValue(String value);
}
