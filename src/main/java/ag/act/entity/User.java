package ag.act.entity;

import ag.act.model.AuthType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User extends ActUser implements ActEntity, Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "my_speech")
    private String mySpeech;

    @Column(name = "hashed_phone_number", nullable = false)
    private String hashedPhoneNumber;

    @Column(name = "birth_date", nullable = false)
    private LocalDateTime birthDate;

    @Column(name = "gender", nullable = false)
    @Enumerated(EnumType.STRING)
    private ag.act.model.Gender gender;

    @Column(name = "first_number_of_identification", nullable = false)
    private Integer firstNumberOfIdentification;

    @Column(name = "hashed_ci", nullable = false)
    private String hashedCI;

    @Column(name = "hashed_di", nullable = false)
    private String hashedDI;

    @Column(name = "is_agree_to_receive_mail")
    private Boolean isAgreeToReceiveMail;

    @Column(name = "is_change_password_required")
    private Boolean isChangePasswordRequired;

    @Column(name = "hashed_pin_number")
    private String hashedPinNumber;

    @Column(name = "last_pin_number_verified_at")
    private LocalDateTime lastPinNumberVerifiedAt;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "job_title")
    private String jobTitle;

    @Column(name = "address")
    private String address;

    @Column(name = "address_detail")
    private String addressDetail;

    @Column(name = "zipcode")
    private String zipcode;

    @Column(name = "total_asset_amount")
    private Long totalAssetAmount = 0L;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "push_token")
    private String pushToken;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ag.act.model.Status status;

    @BatchSize(size = 100)
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @Column
    private List<UserRole> roles;

    @OneToOne(fetch = FetchType.EAGER)
    @SQLRestriction("status = 'ACTIVE'")
    private NicknameHistory nicknameHistory;

    @BatchSize(size = 100)
    @OneToMany
    @JoinColumn(name = "user_id")
    private List<NicknameHistory> nicknameHistories;

    @OneToOne(fetch = FetchType.LAZY)
    private MyDataSummary myDataSummary;

    @BatchSize(size = 100)
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @Column
    private List<UserHoldingStock> userHoldingStocks;

    @Column(name = "auth_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthType authType = AuthType.PIN;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @CreationTimestamp
    @Column(name = "edited_at")
    private LocalDateTime editedAt;

    @Column(name = "is_solidarity_leader_confidential_agreement_signed")
    private Boolean isSolidarityLeaderConfidentialAgreementSigned;

    @Override
    public String getTitle() {
        return nickname;
    }

    @Override
    public String getDeletedMessage() {
        return "탈퇴한 회원입니다.";
    }

    @Override
    public String getDeletedByAdminMessage() {
        return "관리자에 의해 탈퇴된 회원입니다.";
    }

    @Override
    public String getReportedMessage() {
        return "신고된 회원입니다.";
    }

    @Override
    public String getExclusiveToHoldersMessage() {
        return "-";
    }

    @Override
    public Boolean isDeletedByUser() {
        return this.status == ag.act.model.Status.DELETED_BY_USER;
    }

    @Override
    public String toString() {
        return "User {"
               + " id=" + id
               + ", email='" + email + '\''
               + ", lastPinNumberVerifiedAt=" + lastPinNumberVerifiedAt
               + ", nickname='" + nickname + '\''
               + ", status=" + status
               + ", isAdmin=" + isAdmin()
               + ", isAcceptor=" + isAcceptor()
               + ", isGuest=" + isGuest()
               + '}';
    }
}
