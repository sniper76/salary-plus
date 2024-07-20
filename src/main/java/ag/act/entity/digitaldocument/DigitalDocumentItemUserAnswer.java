package ag.act.entity.digitaldocument;

import ag.act.enums.DigitalAnswerType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "digital_document_item_user_answers")
public class DigitalDocumentItemUserAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "digital_document_item_id")
    private Long digitalDocumentItemId;

    @ManyToOne
    @JoinColumn(name = "digital_document_item_id", insertable = false, updatable = false)
    private DigitalDocumentItem digitalDocumentItem;

    @Column(name = "answer_select_value", nullable = false)
    @Enumerated(EnumType.STRING)
    private DigitalAnswerType answerSelectValue;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public static DigitalDocumentItemUserAnswer of(Long userId, Long digitalDocumentItemId, DigitalAnswerType digitalAnswerType) {
        DigitalDocumentItemUserAnswer digitalDocumentItemUserAnswer = new DigitalDocumentItemUserAnswer();
        digitalDocumentItemUserAnswer.setUserId(userId);
        digitalDocumentItemUserAnswer.setDigitalDocumentItemId(digitalDocumentItemId);
        digitalDocumentItemUserAnswer.setAnswerSelectValue(digitalAnswerType);

        return digitalDocumentItemUserAnswer;
    }
}
