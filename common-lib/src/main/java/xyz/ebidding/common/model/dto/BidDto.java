package xyz.ebidding.common.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BidDto {
    private String id;
    private String clientId;
    private String bwicId;
    private Double price;
    private Double size;
    private String transactionId;
    private Long rank;
    private String feedback;
    private Long version;
    private Instant effectiveTime;

    private BwicDto bwic;
}
