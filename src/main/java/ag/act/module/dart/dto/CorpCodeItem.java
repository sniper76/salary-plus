package ag.act.module.dart.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CorpCodeItem {

    @JacksonXmlProperty(localName = "corp_code")
    private String corpCode;
    @JacksonXmlProperty(localName = "corp_name")
    private String corpName;
    @JacksonXmlProperty(localName = "stock_code")
    private String stockCode;
    @JacksonXmlProperty(localName = "modify_date")
    private String modifyDate;
}
