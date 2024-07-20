package ag.act.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

public enum KrxServiceType {

    STK_BYDD_TRD("stk_bydd_trd", "유가증권 일별매매정보"),
    KSQ_BYDD_TRD("ksq_bydd_trd", "코스닥 일별매매정보"),
    KNX_BYDD_TRD("knx_bydd_trd", "코넥스 일별매매정보"),
    STK_ISU_BASE_INFO("stk_isu_base_info", "유가증권 종목기본정보"),
    KSQ_ISU_BASE_INFO("ksq_isu_base_info", "코스닥 종목기본정보"),
    KNX_ISU_BASE_INFO("knx_isu_base_info", "코넥스 종목기본정보");

    @Getter
    private final String serviceType;
    @Getter
    private final String serviceName;

    KrxServiceType(String serviceType, String serviceName) {
        this.serviceType = serviceType;
        this.serviceName = serviceName;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static KrxServiceType fromValue(String value) {
        for (KrxServiceType b : KrxServiceType.values()) {
            if (b.name().equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("지원하지 않는 KrxServiceType '%s' 타입입니다.".formatted(value));
    }
}
