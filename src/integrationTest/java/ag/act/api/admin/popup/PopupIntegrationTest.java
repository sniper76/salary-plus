package ag.act.api.admin.popup;

import ag.act.AbstractCommonIntegrationTest;
import ag.act.entity.Popup;
import ag.act.enums.popup.PopupStatus;

import static ag.act.TestUtil.assertTime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

abstract class PopupIntegrationTest extends AbstractCommonIntegrationTest {
    protected void assertPopupDetailResponse(ag.act.model.PopupDetailsResponse popupDetailResponse, Popup expectedPopup) {
        assertThat(popupDetailResponse.getId(), is(expectedPopup.getId()));
        assertThat(popupDetailResponse.getTitle(), is(expectedPopup.getTitle()));
        assertThat(popupDetailResponse.getContent(), is(expectedPopup.getContent()));
        assertTime(popupDetailResponse.getTargetStartDatetime(), expectedPopup.getTargetStartDatetime());
        assertTime(popupDetailResponse.getTargetEndDatetime(), expectedPopup.getTargetEndDatetime());
        assertPopupStatus(popupDetailResponse, expectedPopup);
        assertThat(popupDetailResponse.getLinkType(), is(expectedPopup.getLinkType().name()));
        assertThat(popupDetailResponse.getLinkTitle(), is(expectedPopup.getLinkTitle()));
        assertThat(popupDetailResponse.getLinkUrl(), is(expectedPopup.getLinkUrl()));
        assertThat(popupDetailResponse.getDisplayTargetType(), is(expectedPopup.getDisplayTargetType().name()));
        assertThat(popupDetailResponse.getStockTargetType(), is(expectedPopup.getStockTargetType().name()));
        assertThat(popupDetailResponse.getStockCode(), is(expectedPopup.getStockCode()));
        assertThat(popupDetailResponse.getStockGroupId(), is(expectedPopup.getStockGroupId()));
        assertTime(popupDetailResponse.getCreatedAt(), expectedPopup.getCreatedAt());
        assertTime(popupDetailResponse.getUpdatedAt(), expectedPopup.getUpdatedAt());
    }

    protected void assertPopupStatus(ag.act.model.PopupDetailsResponse popupDetailResponse, Popup expectedPopup) {
        assertThat(
            popupDetailResponse.getPopupStatus(),
            is(PopupStatus.fromTargetDatetime(expectedPopup.getTargetStartDatetime(), expectedPopup.getTargetEndDatetime()))
        );
    }

}

