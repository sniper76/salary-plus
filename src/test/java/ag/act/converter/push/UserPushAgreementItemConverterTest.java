package ag.act.converter.push;

import ag.act.dto.push.UserPushAgreementStatusDto;
import ag.act.enums.push.UserPushAgreementType;
import ag.act.model.UserPushAgreementItem;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class UserPushAgreementItemConverterTest {
    private static final List<String> CMS_AGREEMENT_TYPE_NAMES = List.of(
        "ACT_NOTICE",
        "STOCK_NOTICE",
        "RECOMMENDATION"
    );
    private static final List<String> AUTHOR_AGREEMENT_TYPE_NAMES = List.of(
        "ACT_BEST_ENTER",
        "NEW_COMMENT"
    );

    private final UserPushAgreementItemConverter converter = new UserPushAgreementItemConverter();

    @Test
    void shouldConvert() {
        // Given
        final List<UserPushAgreementItem> items = List.of(
            createAgreementItem(CMS_AGREEMENT_TYPE_NAMES, true),
            createAgreementItem(AUTHOR_AGREEMENT_TYPE_NAMES, false)
        );

        // When
        List<UserPushAgreementStatusDto> dtos = converter.convert(items);

        // Then
        assertDto(dtos.get(0), UserPushAgreementType.ACT_NOTICE, true);
        assertDto(dtos.get(1), UserPushAgreementType.STOCK_NOTICE, true);
        assertDto(dtos.get(2), UserPushAgreementType.RECOMMENDATION, true);
        assertDto(dtos.get(3), UserPushAgreementType.ACT_BEST_ENTER, false);
        assertDto(dtos.get(4), UserPushAgreementType.NEW_COMMENT, false);
    }

    private ag.act.model.UserPushAgreementItem createAgreementItem(
        List<String> agreementTypes,
        boolean value
    ) {
        return new UserPushAgreementItem()
            .agreementTypes(agreementTypes)
            .value(value);
    }

    private void assertDto(
        UserPushAgreementStatusDto dto,
        UserPushAgreementType type,
        boolean isAgree
    ) {
        assertThat(dto.getType(), is(type));
        assertThat(dto.isAgree(), is(isAgree));
    }
}
