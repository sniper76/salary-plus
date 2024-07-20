package ag.act.enums;

import ag.act.constants.DigitalDocumentTemplateNames;
import ag.act.entity.User;
import ag.act.enums.digitaldocument.AttachOptionType;
import ag.act.exception.BadRequestException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum DigitalDocumentType implements DigitalDocumentTemplateNames {
    DIGITAL_PROXY(DIGITAL_PROXY_TEMPLATE, "위임장", "수임인", false) {
        @Override
        public void validate(String answerData) {
            if (StringUtils.isBlank(answerData)) {
                throw new BadRequestException("의결권위임 필수 찬성/반대/기권 정보가 없습니다.");
            }
        }

        @Override
        public String generateDocumentNo(long digitalDocumentId, String yyMMdd, long issuedNumber) {
            return "ACT-A-%d-%s-%d호".formatted(digitalDocumentId, yyMMdd, issuedNumber);
        }
    },
    JOINT_OWNERSHIP_DOCUMENT(JOINT_OWNERSHIP_DOCUMENT_TEMPLATE, "공동보유", "수임인", true) {
        @Override
        public void validate(User user) {
            if (StringUtils.isBlank(user.getAddress())
                || StringUtils.isBlank(user.getAddressDetail())
                || StringUtils.isBlank(user.getZipcode())) {
                throw new BadRequestException("공동보유약정 위임자의 주소 정보가 없습니다.");
            }
        }

        @Override
        public String generateDocumentNo(long digitalDocumentId, String yyMMdd, long issuedNumber) {
            return "ACT-C-%d-%s-%d호".formatted(digitalDocumentId, yyMMdd, issuedNumber);
        }
    },
    ETC_DOCUMENT(OTHER_DOCUMENT_TEMPLATE, "ETC", "", false) {
        @Override
        public String generateDocumentNo(long digitalDocumentId, String yyMMdd, long issuedNumber) {
            return "ACT-T-%d-%s-%d호".formatted(digitalDocumentId, yyMMdd, issuedNumber);
        }
    },
    HOLDER_LIST_READ_AND_COPY_DOCUMENT(HOLDER_LIST_READ_AND_COPY_TEMPLATE, "주주명부 열람/등사", "", false) {
        @Override
        public String generateDocumentNo(long digitalDocumentId, String yyMMdd, long issuedNumber) {
            return "";
        }

        @Override
        public void validate(List<MultipartFile> bankAccountImages, MultipartFile hectoEncryptedBankAccountPdf) {
            // TODO 이걸 사실 디비에 저장해야 한다. 여기서 처리하면 안된다. DigitalDocument의 JsonAttachOption 에 정보를 저장해야 한다.
            if (CollectionUtils.isEmpty(bankAccountImages) && hectoEncryptedBankAccountPdf == null) {
                AttachOptionType.REQUIRED.validate(List.of(), "잔고증명서");
            }
        }
    };

    private final String templateName;
    private final String displayName;
    private final String acceptUserDisplayName;
    private final boolean isNeedAddress;

    public static DigitalDocumentType fromValue(String type) {
        try {
            return DigitalDocumentType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("지원하지 않는 DigitalDocumentType '%s' 타입입니다.".formatted(type));
        }
    }

    public static List<String> getNames() {
        return Arrays.stream(DigitalDocumentType.values())
            .map(DigitalDocumentType::name)
            .toList();
    }

    public static List<String> getNamesExceptEtcDocument() {
        return Arrays.stream(DigitalDocumentType.values())
            .filter(type -> type != ETC_DOCUMENT)
            .map(DigitalDocumentType::name)
            .toList();
    }

    public void validate(String answerData) {
        // ignore
    }

    public void validate(User user) {
        // ignore
    }

    public void validate(List<MultipartFile> bankAccountImages, MultipartFile hectoEncryptedBankAccountPdf) {
        // ignore
    }

    public abstract String generateDocumentNo(long digitalDocumentId, String yyMMdd, long issuedNumber);

    public boolean isMatch(String name) {
        try {
            return DigitalDocumentType.fromValue(name) == this;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
