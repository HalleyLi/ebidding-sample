package xyz.ebidding.common.model;

import com.fasterxml.jackson.core.type.TypeReference;
import xyz.ebidding.common.model.dto.BidDto;
import xyz.ebidding.common.model.dto.GenericResponse;
import xyz.ebidding.common.model.dto.PageableDto;

public class TYPES {
    private TYPES() {
    }

    public static TypeReference<GenericResponse<BidDto>> BID_RESPONSE =
            new TypeReference<GenericResponse<BidDto>>() {
            };

    public static TypeReference<GenericResponse<PageableDto<BidDto>>> PAGEABLE_BID_RESPONSE =
            new TypeReference<GenericResponse<PageableDto<BidDto>>>() {
            };
}
