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
public class BwicDto {
    private String id;
    private String clientId;
    private String cusip;
    private Double size;
    private Double startingPrice;
    private String issuer;
    private String rating;
    private String coupon;
    private String maturitydate;

    private Boolean isOverDue;

    private Instant dueDate;
    private Long version;
}
