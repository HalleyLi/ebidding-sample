package xyz.ebidding.common.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PopularBwicDto extends BwicDto {
    private Long numberOfBids;
}
