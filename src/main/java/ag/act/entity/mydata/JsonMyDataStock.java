package ag.act.entity.mydata;

import ag.act.util.KoreanDateTimeUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JsonMyDataStock {
    private String code; // 종목코드
    private String myDataProdCode; // 마이데이터 종목코드
    private String name; // 종목명
    private Long quantity; // 주식수
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate referenceDate; // 기준일
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate registerDate; // 등록일
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    private LocalDateTime updatedAt; // 마지막 체크한 날짜를 기록해 두고, 더 이상 변동이 없으면, 업데이트 하지 않는다.
    @JsonIgnore
    @Builder.Default
    private boolean isTemp = false;

    public JsonMyDataStock copyOf(LocalDate referenceDate) {
        return copyOf(referenceDate, this.getQuantity());
    }

    public JsonMyDataStock copyOf(LocalDate referenceDate, Long quantity) {
        return copyOf(referenceDate, quantity, Boolean.FALSE);
    }

    public JsonMyDataStock copyOf(LocalDate referenceDate, Long quantity, Boolean isTemp) {
        final JsonMyDataStock newJsonMyDataStock = new JsonMyDataStock();
        newJsonMyDataStock.setName(this.getName());
        newJsonMyDataStock.setCode(this.getCode());
        newJsonMyDataStock.setMyDataProdCode(this.getMyDataProdCode());
        newJsonMyDataStock.setQuantity(quantity);
        newJsonMyDataStock.setReferenceDate(referenceDate);
        newJsonMyDataStock.setRegisterDate(KoreanDateTimeUtil.getTodayLocalDate());
        newJsonMyDataStock.setUpdatedAt(LocalDateTime.now());
        newJsonMyDataStock.setTemp(isTemp);
        return newJsonMyDataStock;
    }
}
