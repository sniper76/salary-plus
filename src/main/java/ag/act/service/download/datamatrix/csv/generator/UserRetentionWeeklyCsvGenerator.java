package ag.act.service.download.datamatrix.csv.generator;

import ag.act.dto.datamatrix.UserRetentionWeeklyCsvGenerateRequestInput;
import ag.act.enums.download.matrix.UserRetentionWeeklyCsvRowDataType;

public interface UserRetentionWeeklyCsvGenerator<T> {
    void generate(UserRetentionWeeklyCsvGenerateRequestInput userRetentionWeeklyCsvGenerateRequestInput);

    void process(T csvGenerateRequestInput);

    boolean supports(UserRetentionWeeklyCsvRowDataType userRetentionWeeklyCsvRowDataType);
}
