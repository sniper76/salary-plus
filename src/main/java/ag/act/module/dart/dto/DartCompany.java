package ag.act.module.dart.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Optional;


@Getter
public class DartCompany {

    private static final String SUCCESS_STATUS = "000";
    private static final String EMPTY_PHONE_OR_FAX_NUMBER = "";
    private static final String DEFAULT_ACCOUNT_SETTLEMENT_MONTH = "12";

    @JsonProperty("status")
    private String status;

    @JsonProperty("message")
    private String message;

    @JsonProperty("corp_code")
    private String corpCode;

    @JsonProperty("corp_name")
    private String corpName;

    @JsonProperty("corp_name_eng")
    private String corpNameEng;

    @JsonProperty("stock_name")
    private String stockName;

    @JsonProperty("stock_code")
    private String stockCode;

    @JsonProperty("ceo_nm")
    private String ceoName;

    @JsonProperty("corp_cls")
    private String corpClass;

    @JsonProperty("jurir_no")
    private String jurisdictionalRegistrationNumber;

    @JsonProperty("bizr_no")
    private String businessRegistrationNumber;

    @JsonProperty("adres")
    private String address;

    @JsonProperty("hm_url")
    private String homepageUrl;

    @JsonProperty("ir_url")
    private String irUrl;

    @JsonProperty("phn_no")
    private String phoneNumber;

    @JsonProperty("fax_no")
    private String faxNumber;

    @JsonProperty("induty_code")
    private String industryCode;

    @JsonProperty("est_dt")
    private String establishmentDate;

    @JsonProperty("acc_mt")
    private String accountingSettlementMonth;

    public boolean isSuccess() {
        return SUCCESS_STATUS.equals(status);
    }

    private String getPhoneNumber() {
        return Optional.ofNullable(phoneNumber).orElse("");
    }

    private String getAccountingSettlementMonth() {
        return Optional.ofNullable(accountingSettlementMonth).orElse(DEFAULT_ACCOUNT_SETTLEMENT_MONTH);
    }

    public String getErrorMessage() {
        return "[%s] corpCode:%s, corpName:%s, message:%s".formatted(status, corpCode, corpName, message);
    }

    public String getAccountSettlementMonth() {
        return Optional.ofNullable(getAccountingSettlementMonth())
            .map(String::trim)
            .orElse(DEFAULT_ACCOUNT_SETTLEMENT_MONTH);
    }

    public String getRepresentativePhoneNumber() {
        return Optional.of(getPhoneNumber())
            .map(this::getValidPhoneOrFaxNumber)
            .orElse(EMPTY_PHONE_OR_FAX_NUMBER);
    }

    public String getRepresentativeFaxNumber() {
        return Optional.of(getFaxNumber())
            .map(this::getValidPhoneOrFaxNumber)
            .orElse(EMPTY_PHONE_OR_FAX_NUMBER);
    }

    private String getValidPhoneOrFaxNumber(String input) {
        String phoneOrFaxNumber = input.trim().split(",")[0];

        if (phoneOrFaxNumber.matches(".*\\d+.*")) {
            return phoneOrFaxNumber.trim().replaceAll("[ \\t]+", "");
        }

        return EMPTY_PHONE_OR_FAX_NUMBER;
    }
}
