package ag.act.entity.digitaldocument;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "digital_document_numbers")
public class DigitalDocumentNumber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "digital_document_id")
    private Long digitalDocumentId;

    @Column(name = "last_issued_number", columnDefinition = "BIGINT DEFAULT '0'")
    private Long lastIssuedNumber;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public DigitalDocumentNumber() {
    }

    public DigitalDocumentNumber(Long lastIssuedNumber) {
        this.lastIssuedNumber = lastIssuedNumber;
    }
}
