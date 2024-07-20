package ag.act.entity;

import ag.act.enums.FileContentType;
import ag.act.enums.FileType;
import ag.act.model.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "file_contents")
public class FileContent implements ActEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "original_filename", nullable = false)
    private String originalFilename;

    @Column(name = "filename", nullable = false)
    private String filename;

    @Column(name = "mimetype")
    private String mimetype;

    @Column(name = "description")
    private String description;

    @Column(name = "file_content_type", columnDefinition = "VARCHAR(255) DEFAULT 'DEFAULT'")
    @Enumerated(EnumType.STRING)
    private FileContentType fileContentType;

    @Column(name = "file_type", nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'IMAGE'")
    @Enumerated(EnumType.STRING)
    private FileType fileType;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}

