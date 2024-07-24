package ag.act.entity;

import ag.act.enums.BoardCategory;
import ag.act.enums.BoardGroup;
import ag.act.enums.PostsViewType;
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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "latest_user_posts_views")
@Getter
@Setter
public class LatestUserPostsView {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_code", updatable = false)
    private Stock stock;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", updatable = false)
    private User user;

    @Column(name = "board_group")
    @Enumerated(EnumType.STRING)
    private BoardGroup boardGroup;

    @Column(name = "board_category")
    @Enumerated(EnumType.STRING)
    private BoardCategory boardCategory;

    @Column(name = "posts_view_type")
    @Enumerated(EnumType.STRING)
    private PostsViewType postsViewType;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "unique_combined_id", nullable = false)
    private String uniqueCombinedId;

    @PrePersist
    public void onPrePersist() {
        if (uniqueCombinedId == null) {
            uniqueCombinedId = genUniqueCombinedId();
        }
    }

    private String genUniqueCombinedId() {
        return "%s__%s__%s__%s__%s".formatted(
            stock.getCode(),
            user.getId(),
            boardGroup,
            boardCategory,
            postsViewType
        );
    }
}
