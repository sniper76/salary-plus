package ag.act.entity.digitaldocument;

import ag.act.entity.ActEntity;
import ag.act.enums.digitaldocument.HolderListReadAndCopyItemType;
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

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "holder_list_read_and_copies")
public class HolderListReadAndCopy implements ActEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "digital_document_id")
    private Long digitalDocumentId;

    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'ACTIVE'")
    @Enumerated(EnumType.STRING)
    private ag.act.model.Status status;

    @Column(name = "item_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private HolderListReadAndCopyItemType itemType;

    @Column(name = "item_value")
    private String itemValue;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
