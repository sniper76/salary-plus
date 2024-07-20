package ag.act.converter.digitaldocument;

import ag.act.converter.Converter;
import ag.act.enums.digitaldocument.AttachOptionType;
import ag.act.model.JsonAttachOption;
import org.springframework.stereotype.Component;

@Component
public class DigitalDocumentJsonAttachOptionConverter implements Converter<JsonAttachOption, JsonAttachOption> {

    private JsonAttachOption convert(JsonAttachOption attachOption) {
        if (attachOption == null) {
            return null;
        }

        JsonAttachOption jsonAttachOption = new JsonAttachOption();

        jsonAttachOption.setSignImage(attachOption.getSignImage());
        jsonAttachOption.setIdCardImage(attachOption.getIdCardImage());
        jsonAttachOption.setBankAccountImage(attachOption.getBankAccountImage());
        jsonAttachOption.setHectoEncryptedBankAccountPdf(
            attachOption.getHectoEncryptedBankAccountPdf() == null
                ? AttachOptionType.NONE.name() :
                attachOption.getHectoEncryptedBankAccountPdf()
        );

        return jsonAttachOption;
    }

    @Override
    public JsonAttachOption apply(JsonAttachOption jsonAttachOption) {
        return convert(jsonAttachOption);
    }
}
