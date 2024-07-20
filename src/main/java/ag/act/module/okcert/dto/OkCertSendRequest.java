package ag.act.module.okcert.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("checkstyle:MemberName")
public class OkCertSendRequest {
    private String name;
    private String birthday;
    private String sex_cd;
    private String ntv_frnr_cd;
    private String tel_com_cd;
    private String tel_no;
    private String rqst_caus_cd;
    private String cp_cd;
    private String app_hash_str;
    private String site_name;
    private String site_url;
    private String agree1;
    private String agree2;
    private String agree3;
    private String agree4;
}
