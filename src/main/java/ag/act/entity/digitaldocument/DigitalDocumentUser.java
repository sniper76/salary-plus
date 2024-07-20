package ag.act.entity.digitaldocument;

import ag.act.enums.DigitalDocumentAnswerStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "digital_document_users")
public class DigitalDocumentUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "digital_document_id")
    private Long digitalDocumentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "digital_document_id", insertable = false, updatable = false)
    private DigitalDocument digitalDocument;

    @Column(name = "digital_document_answer_status", nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'SAVE'")
    @Enumerated(EnumType.STRING)
    private DigitalDocumentAnswerStatus digitalDocumentAnswerStatus;

    @Column(name = "stock_code")
    private String stockCode;

    @Column(name = "stock_name")
    private String stockName;

    @Column(name = "name")
    private String name;

    @Column(name = "hashed_phone_number")
    private String hashedPhoneNumber;

    @Column(name = "birth_date")
    private LocalDateTime birthDate;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private ag.act.model.Gender gender;

    @Column(name = "first_number_of_identification", nullable = false)
    private Integer firstNumberOfIdentification;

    @Column(name = "address")
    private String address;

    @Column(name = "address_detail")
    private String addressDetail;

    @Column(name = "zipcode")
    private String zipcode;

    @Column(name = "stock_count")
    private Long stockCount;

    @Column(name = "purchase_price")
    private Long purchasePrice;

    @Column(name = "loan_price")
    private Long loanPrice;

    @Column(name = "pdf_path")
    private String pdfPath;

    @Column(name = "stock_reference_date")
    private LocalDate stockReferenceDate;

    @Column(name = "issued_number", columnDefinition = "BIGINT DEFAULT '0'")
    private Long issuedNumber;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "original_page_count")
    private Long originalPageCount;

    @Column(name = "attachment_page_count")
    private Long attachmentPageCount;
}
