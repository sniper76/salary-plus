package ag.act.module.dart;

import ag.act.exception.InternalServerException;
import ag.act.module.dart.dto.CorpCodeResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DartCorpCodeXmlParser {
    private final XmlMapper xmlMapper;

    public CorpCodeResult parseXml(String xmlContent) {
        try {
            return xmlMapper.readValue(xmlContent, CorpCodeResult.class);
        } catch (JsonProcessingException e) {
            throw new InternalServerException("Error occur during parsing the Dart Corp Code XML", e);
        }
    }
}
