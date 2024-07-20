package ag.act.entity;

import ag.act.enums.AppLinkType;
import ag.act.enums.BoardCategory;
import ag.act.enums.popup.PopupDisplayTargetType;
import ag.act.enums.push.PushTargetType;
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

@Entity
@Table(name = "popups")
@Getter
@Setter
public class Popup implements UserAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", length = 1000)
    private String title;

    @Column(name = "content", columnDefinition = "text")
    private String content;

    @Column(name = "target_start_datetime", nullable = false)
    private LocalDateTime targetStartDatetime;

    @Column(name = "target_end_datetime", nullable = false)
    private LocalDateTime targetEndDatetime;

    @Column(name = "link_type", nullable = false, columnDefinition = "VARCHAR(255) default 'NONE'")
    @Enumerated(EnumType.STRING)
    private AppLinkType linkType;

    @Column(name = "link_title")
    private String linkTitle;

    @Column(name = "link_url", length = 1000)
    private String linkUrl;

    @Column(name = "display_target_type", nullable = false, columnDefinition = "VARCHAR(255) default 'MAIN_HOME'")
    @Enumerated(EnumType.STRING)
    private PopupDisplayTargetType displayTargetType;

    @Column(name = "stock_target_type", nullable = false, columnDefinition = "VARCHAR(255) default 'ALL'")
    @Enumerated(EnumType.STRING)
    private PushTargetType stockTargetType;

    @Column(name = "stock_code")
    private String stockCode;

    @Column(name = "stock_group_id")
    private Long stockGroupId;

    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(255) DEFAULT 'ACTIVE'")
    @Enumerated(EnumType.STRING)
    private ag.act.model.Status status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "post_id")
    private Long postId;

    @Column(name = "board_category")
    @Enumerated(EnumType.STRING)
    private BoardCategory boardCategory;
}
