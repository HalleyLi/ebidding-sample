package xyz.ebidding.bid.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Where(clause="active=1")
public class Bid {
    @Id
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @GeneratedValue(generator = "system-uuid")
    private String id;
    private String clientId;
    private String bwicId;
    private Double price;
    private Double size;
    private String transactionId;
    @Column(name= "_rank")
    private Long rank;
    private String feedback;
    private Instant effectiveTime;

    @Version
    private Long version;

    private Boolean active;
}
