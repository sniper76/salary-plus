package ag.act.entity;

import ag.act.enums.AppLinkType;
import ag.act.enums.push.PushSendStatus;
import ag.act.enums.push.PushSendType;
import ag.act.enums.push.PushTargetType;
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
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "pushes")
@Getter
@Setter
public class Push implements UserAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false, columnDefinition = "VARCHAR(255) default '액트'")
    private String title;

    @Column(columnDefinition = "text")
    private String content;

    @Column(name = "stock_code")
    private String stockCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "stock_code", referencedColumnName = "code", insertable = false, updatable = false)
    private Stock stock;

    @Column(name = "stock_group_id")
    private Long stockGroupId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(insertable = false, updatable = false)
    private StockGroup stockGroup;

    @Column(name = "topic")
    private String topic;

    @Column(name = "link_type", nullable = false, columnDefinition = "VARCHAR(255) default 'NONE'")
    @Enumerated(EnumType.STRING)
    private AppLinkType linkType;

    @Column(name = "link_url")
    private String linkUrl;

    @Column(name = "post_id")
    private Long postId;

    @Column(name = "push_target_type")
    @Enumerated(EnumType.STRING)
    private PushTargetType pushTargetType;

    @Column(name = "send_type")
    @Enumerated(EnumType.STRING)
    private PushSendType sendType;

    @Column(name = "send_status")
    @Enumerated(EnumType.STRING)
    private PushSendStatus sendStatus;

    @Column(name = "result")
    private String result;

    @Column(name = "target_datetime")
    private LocalDateTime targetDatetime;

    @Column(name = "sent_start_datetime")
    private LocalDateTime sentStartDatetime;

    @Column(name = "sent_end_datetime")
    private LocalDateTime sentEndDatetime;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
