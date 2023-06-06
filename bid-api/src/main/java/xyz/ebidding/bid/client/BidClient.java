package xyz.ebidding.bid.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.ebidding.bid.BidConstant;
import xyz.ebidding.common.model.dto.BidDto;
import xyz.ebidding.common.model.dto.GenericResponse;
import xyz.ebidding.common.model.dto.PopularBwicDto;

import java.util.List;
import java.util.Map;

@FeignClient(name = BidConstant.SERVICE_NAME, path = "/v1/bid", url = "${ebidding.bid-service-endpoint}")
// TODO Client side validation can be enabled as needed
// @Validated
public interface BidClient {

    @GetMapping(path = "/bwic-top-bids")
    GenericResponse<Map<String, List<BidDto>>> bwicTopBids(@RequestParam List<String> bwicIds);

    @GetMapping(path = "/bwic-client-bid")
    GenericResponse<Map<String, List<BidDto>>> bwicClientBid(@RequestParam List<String> bwicIds);

    @GetMapping(path = "/popular-bwics")
    GenericResponse<List<PopularBwicDto>> popularBwics();

    @PostMapping(path = "/delete-bwic-bids/{bwic-id}")
    GenericResponse<Void> deleteBwicBids(@PathVariable("bwic-id") String bwicId);
}
