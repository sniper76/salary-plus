package ag.act.entity;


import ag.act.entity.digitaldocument.DigitalDocument;
import ag.act.enums.ClientType;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
@Getter
@Setter
public class Post implements ActEntity, Content, PostExtension {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "source_post_id")
    private Long sourcePostId;

    @Column(name = "board_id", nullable = false)
    private Long boardId;

    @Column(name = "title")
    private String title;

    @Column(name = "anonymous_name")
    private String anonymousName;

    @Column(name = "is_anonymous", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isAnonymous;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'ACTIVE'")
    @Enumerated(EnumType.STRING)
    private ag.act.model.Status status;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "push_id")
    private Long pushId;

    @Column(name = "thumbnail_image_url")
    private String thumbnailImageUrl;

    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;

    @Column(name = "view_user_count", nullable = false, columnDefinition = "사용자별 뷰 카운트")
    private Long viewUserCount = 0L;

    @Column(name = "like_count", nullable = false)
    private Long likeCount = 0L;

    @Column(name = "comment_count", nullable = false)
    private Long commentCount = 0L;

    @Column(name = "is_notification", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isNotification;

    @Column(name = "is_pinned", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isPinned = false;

    @Column(name = "is_exclusive_to_holders", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isExclusiveToHolders = false;

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Poll> polls = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private DigitalProxy digitalProxy;

    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL)
    private DigitalDocument digitalDocument;

    @Column(name = "client_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ClientType clientType;

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

    @ManyToOne
    @JoinColumn(name = "board_id", insertable = false, updatable = false)
    private Board board;

    @OneToOne(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinColumn(name = "id")
    private PostUserProfile postUserProfile;

    @Column(name = "active_start_date", nullable = false)
    private LocalDateTime activeStartDate;

    @Column(name = "active_end_date")
    private LocalDateTime activeEndDate;

    @PrePersist
    @PreUpdate
    public void onPreUpdate() {
        if (activeStartDate == null) {
            activeStartDate = LocalDateTime.now();
        }
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        this.activeStartDate = createdAt;
    }

    @Override
    public String getDeletedMessage() {
        return "삭제된 게시글입니다.";
    }

    @Override
    public String getDeletedByAdminMessage() {
        return "관리자에 의해 삭제된 게시글입니다.";
    }

    @Override
    public String getReportedMessage() {
        return "신고된 게시글입니다.";
    }

    @Override
    public String getExclusiveToHoldersMessage() {
        return "주주에게만 공개된 제한된 글입니다.";
    }

    public Post copyOf(Long boardId) {
        final Post post = new Post();
        post.setSourcePostId(id);
        post.setBoardId(boardId);
        post.setTitle(title);
        post.setAnonymousName(anonymousName);
        post.setIsAnonymous(isAnonymous);
        post.setContent(content);
        post.setStatus(status);
        post.setUserId(userId);
        post.setThumbnailImageUrl(thumbnailImageUrl);
        post.setViewCount(0L);
        post.setViewUserCount(0L);
        post.setLikeCount(0L);
        post.setCommentCount(0L);
        post.setIsNotification(isNotification);
        post.setIsExclusiveToHolders(isExclusiveToHolders);
        post.setActiveStartDate(activeStartDate);
        post.setClientType(clientType);
        return post;
    }

    public void addPoll(Poll poll) {
        if (getPolls() == null) {
            polls = new ArrayList<>();
        }
        polls.add(poll);
    }

    public Poll getFirstPoll() {
        return CollectionUtils.isEmpty(getPolls()) ? null : polls.get(0);
    }

    public Boolean getIsNotification() {
        return Boolean.TRUE.equals(isNotification);
    }

    public Boolean isDeletedByUser() {
        return this.status == ag.act.model.Status.DELETED_BY_USER;
    }

    public boolean hasPolls() {
        return !getPolls().isEmpty();
    }
}
