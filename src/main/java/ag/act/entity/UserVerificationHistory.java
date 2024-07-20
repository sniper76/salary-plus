package ag.act.entity;

import ag.act.enums.verification.VerificationOperationType;
import ag.act.enums.verification.VerificationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "user_verification_histories")
public class UserVerificationHistory implements ActEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_ip", nullable = false)
    private String userIp;

    @Column(name = "verification_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private VerificationType verificationType;

    @Column(name = "operation_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private VerificationOperationType operationType;

    @Column(name = "digital_document_user_id")
    private Long digitalDocumentUserId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}