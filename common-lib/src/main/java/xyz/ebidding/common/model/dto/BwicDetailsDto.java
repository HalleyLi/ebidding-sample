package xyz.ebidding.common.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BwicDetailsDto {
    private BwicDto bwicDto;
    private List<BidDto> bids;
}
