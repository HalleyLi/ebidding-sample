package xyz.ebidding.bwic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.ebidding.common.validation.Cusip;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateBwicRequest {

    @NotEmpty
    private String clientId;
    @Cusip
    private String cusip;
    @Positive
    @NotNull
    private Double size;
    @NotNull
    private Instant dueDate;
}
