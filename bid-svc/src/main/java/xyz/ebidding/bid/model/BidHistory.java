package xyz.ebidding.bid.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class BidHistory {
    @EmbeddedId
    private BidHistoryId id;

    private String clientId;
    private String bwicId;
    private Double price;
    private Double size;
    private String transactionId;
    @Column(name= "_rank")
    private Long rank;
    private String feedback;
    private Instant effectiveTime;


    private Boolean active;
}
