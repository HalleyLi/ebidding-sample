package xyz.ebidding.bid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateBidRequest {
    @NotEmpty
    private String bwicId;
    @NotNull
    @Positive
    private Double price;
    @NotNull
    @Positive
    private Double size;
}
