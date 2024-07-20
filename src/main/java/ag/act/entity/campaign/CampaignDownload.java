package ag.act.entity.campaign;

import ag.act.entity.ZipFileDownload;
import ag.act.enums.digitaldocument.ZipFileStatus;
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
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "campaign_downloads")
public class CampaignDownload implements ZipFileDownload {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "request_user_id", nullable = false)
    private Long requestUserId;

    @Column(name = "campaign_id", nullable = false)
    private Long campaignId;

    @Column(name = "zip_file_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ZipFileStatus zipFileStatus = ZipFileStatus.REQUEST;

    @Column(name = "download_count", nullable = false)
    private int downloadCount = 0;

    @Column(name = "zip_file_path")
    private String zipFilePath;

    @Column(name = "zip_file_key", unique = true, nullable = false)
    private String zipFileKey;

    @Column(name = "is_latest", nullable = false)
    private Boolean isLatest = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private Date deletedAt;
}
