package ag.act.util;

public class QueryUtil {

    /**
     * nativequery 는 %:value% 가 가능하고 jpql 만 사용한다.
     *
     * @param value 검색 키워드 문자
     * @return 결과값
     */
    public static String toLikeString(String value) {
        return "%%%s%%".formatted(value);
    }
}
