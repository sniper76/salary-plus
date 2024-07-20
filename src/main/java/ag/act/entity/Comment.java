package ag.act.entity;

import ag.act.enums.CommentType;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
public class Comment implements ActEntity, Content, CommentExtension {

    private static final int NO_REPLY = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "post_id")
    private Long postId;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private CommentType type;

    @Column(name = "content", columnDefinition = "text")
    private String content;

    @Column(name = "anonymous_count")
    private int anonymousCount;

    @Column(name = "like_count")
    private Long likeCount;

    @Column(name = "reply_comment_count")
    private Long replyCommentCount;

    @Column(name = "is_anonymous", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isAnonymous;

    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'ACTIVE'")
    @Enumerated(EnumType.STRING)
    private ag.act.model.Status status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @CreationTimestamp
    @Column(name = "edited_at")
    private LocalDateTime editedAt;

    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinColumn(name = "id")
    private CommentUserProfile commentUserProfile;

    @Override
    public String getTitle() {
        return this.content;
    }

    @Override
    public String getDeletedMessage() {
        return "삭제된 댓글입니다.";
    }

    @Override
    public String getDeletedByAdminMessage() {
        return "관리자에 의해 삭제된 댓글입니다.";
    }

    @Override
    public String getReportedMessage() {
        return "신고된 댓글입니다.";
    }

    @Override
    public String getExclusiveToHoldersMessage() {
        return "-";
    }

    public Boolean isDeletedByUser() {
        return this.status == ag.act.model.Status.DELETED_BY_USER;
    }
}
