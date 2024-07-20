package ag.act.entity.digitaldocument;

import ag.act.enums.DigitalAnswerType;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "digital_document_items")
public class DigitalDocumentItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "digital_document_id")
    private Long digitalDocumentId;

    @ManyToOne
    @JoinColumn(name = "digital_document_id", insertable = false, updatable = false)
    private DigitalDocument digitalDocument;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "leader_description")
    private String leaderDescription;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "item_level")
    private Integer itemLevel;

    @Column(name = "is_last_item")
    private Boolean isLastItem;

    @Column(name = "default_select_value")
    @Enumerated(EnumType.STRING)
    private DigitalAnswerType defaultSelectValue;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @BatchSize(size = 100)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "digital_document_item_id")
    private List<DigitalDocumentItemUserAnswer> digitalDocumentItemUserAnswerList;
}
