package ag.act.entity.digitaldocument;

import ag.act.entity.Post;
import ag.act.enums.DigitalDocumentType;
import ag.act.enums.DigitalDocumentVersion;
import ag.act.enums.digitaldocument.IdCardWatermarkType;
import ag.act.model.Status;
import io.hypersistence.utils.hibernate.type.json.JsonType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@Entity
@Table(name = "digital_documents")
public class DigitalDocument implements IDigitalProxy, IJointOwnershipDocument, IOtherDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "post_id")
    private Long postId;

    @OneToOne
    @JoinColumn(name = "post_id", insertable = false, updatable = false)
    private Post post;

    @Column(name = "stock_code")
    private String stockCode;

    @Column(name = "stock_reference_date")
    private LocalDate stockReferenceDate;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "company_registration_number")
    private String companyRegistrationNumber;

    @Column(name = "shareholder_meeting_type")
    private String shareholderMeetingType;//주총구분

    @Column(name = "shareholder_meeting_name")
    private String shareholderMeetingName;//주총명

    @Column(name = "shareholder_meeting_date", nullable = false)
    private LocalDateTime shareholderMeetingDate;//주총일

    @Column(name = "designated_agent_names")
    private String designatedAgentNames;//수임인지정대리인

    @Column(name = "accept_user_id")
    private Long acceptUserId;//수임인

    @Column(name = "join_stock_sum", columnDefinition = "BIGINT DEFAULT 0")
    private long joinStockSum;

    @Column(name = "join_user_count", columnDefinition = "INTEGER DEFAULT 0")
    private int joinUserCount;

    @Column(name = "shareholding_ratio")
    private Double shareholdingRatio;

    @Column(name = "is_display_stock_quantity", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isDisplayStockQuantity;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private DigitalDocumentType type;

    @Column(name = "json_attach_option", columnDefinition = "json")
    @Type(JsonType.class)
    private ag.act.model.JsonAttachOption jsonAttachOption = new ag.act.model.JsonAttachOption();

    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'ACTIVE'")
    @Enumerated(EnumType.STRING)
    private ag.act.model.Status status;

    @Column(name = "target_start_date", nullable = false)
    private LocalDateTime targetStartDate;

    @Column(name = "target_end_date", nullable = false)
    private LocalDateTime targetEndDate;

    @Column(name = "version", nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'V1'")
    @Enumerated(EnumType.STRING)
    private DigitalDocumentVersion version;

    @Column(name = "id_card_watermark_type", nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'NONE'")
    @Enumerated(EnumType.STRING)
    private IdCardWatermarkType idCardWatermarkType = IdCardWatermarkType.ACT_LOGO;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @BatchSize(size = 100)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "digital_document_id")
    @OrderBy("id ASC")
    private List<DigitalDocumentItem> digitalDocumentItemList;

    @BatchSize(size = 100)
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "digital_document_id")
    private List<DigitalDocumentUser> digitalDocumentUserList;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "digital_document_id", insertable = false, updatable = false)
    @SQLRestriction("is_latest = true")
    @Getter(AccessLevel.PRIVATE)
    private List<DigitalDocumentDownload> onlyOneLatestDigitalDocumentDownloads;

    public Optional<DigitalDocumentDownload> getLatestDigitalDocumentDownload() {
        if (CollectionUtils.isEmpty(onlyOneLatestDigitalDocumentDownloads)) {
            return Optional.empty();
        }
        return Optional.of(onlyOneLatestDigitalDocumentDownloads.get(0));
    }

    public DigitalDocument copyOf(Post newPost, String stockCode) {
        final DigitalDocument digitalDocument = new DigitalDocument();
        digitalDocument.setPost(newPost);
        digitalDocument.setPostId(newPost.getId()); // TODO [멀티설문] 이 부분도 필요 없는 부분이 아닐까? 나중에 더 체크해 봐야 할듯.
        digitalDocument.setStockCode(stockCode);
        digitalDocument.setStockReferenceDate(stockReferenceDate);
        digitalDocument.setContent(content);
        digitalDocument.setTitle(title);
        digitalDocument.setDesignatedAgentNames(designatedAgentNames);
        digitalDocument.setAcceptUserId(acceptUserId);
        digitalDocument.setShareholderMeetingDate(shareholderMeetingDate);
        digitalDocument.setShareholderMeetingName(shareholderMeetingName);
        digitalDocument.setShareholderMeetingType(shareholderMeetingType);
        digitalDocument.setCompanyName(companyName);
        digitalDocument.setType(type);
        digitalDocument.setVersion(version);
        digitalDocument.setShareholdingRatio(0.0);
        digitalDocument.setJoinUserCount(0);
        digitalDocument.setJoinStockSum(0L);
        digitalDocument.setStatus(Status.ACTIVE);
        digitalDocument.setDeletedAt(null);
        digitalDocument.setTargetStartDate(targetStartDate);
        digitalDocument.setTargetEndDate(targetEndDate);
        digitalDocument.setJsonAttachOption(jsonAttachOption);
        digitalDocument.setIsDisplayStockQuantity(isDisplayStockQuantity);

        newPost.setDigitalDocument(digitalDocument);
        return digitalDocument;
    }
}
