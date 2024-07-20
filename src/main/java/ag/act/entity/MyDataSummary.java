package ag.act.entity;

import ag.act.entity.mydata.JsonMyData;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "my_data_summaries")
public class MyDataSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "pension_paid_amount", nullable = false)
    private Long pensionPaidAmount;

    @Column(name = "loan_price", nullable = false)
    private Long loanPrice;

    @Column(name = "json_my_data", columnDefinition = "json")
    @Type(JsonType.class)
    private JsonMyData jsonMyData = new JsonMyData();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static MyDataSummary empty(Long userId) {
        final MyDataSummary myDataSummary = new MyDataSummary();
        myDataSummary.setUserId(userId);
        myDataSummary.setPensionPaidAmount(0L);
        myDataSummary.setJsonMyData(new JsonMyData());
        return myDataSummary;
    }

}
