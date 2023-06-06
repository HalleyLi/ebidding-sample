package xyz.ebidding.bid.controller;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.ebidding.bid.dto.CreateBidRequest;
import xyz.ebidding.bid.dto.UpdateBidRequest;
import xyz.ebidding.bid.model.Bid;
import xyz.ebidding.bid.service.BidService;
import xyz.ebidding.bwic.client.BwicClient;
import xyz.ebidding.common.auth.AuthConstant;
import xyz.ebidding.common.auth.AuthContext;
import xyz.ebidding.common.auth.Authorize;
import xyz.ebidding.common.error.ServiceException;
import xyz.ebidding.common.model.dto.*;
import xyz.ebidding.common.utils.Helper;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/v1/bid")
@Validated
@Slf4j
public class BidController {
    @Autowired
    private BidService bidService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private BwicClient bwicClient;

    @PostMapping(path = "/create")
    @Authorize(AuthConstant.CLIENT)
    public GenericResponse<BidDto> createBid(@RequestBody @Valid CreateBidRequest request) {
        return genericBidResponse(bidService.createBid(request, AuthContext.getUserId()));
    }

    @PostMapping(path = "/delete/{id}")
    @Authorize(AuthConstant.CLIENT)
    public GenericResponse<BidDto> deleteBid(@PathVariable("id") String id) {
        bidService.deleteBid(id, AuthContext.getUserId());
        return new GenericResponse<>(null);
    }

    @PostMapping(path = "/delete-bwic-bids/{bwic-id}")
    @Authorize(AuthConstant.BWIC_SERVICE)
    public GenericResponse<Void> deleteBwicBids(@PathVariable("bwic-id") String bwicId) {
        bidService.deleteBwicBids(bwicId);
        return new GenericResponse<>(null);
    }

    @PostMapping(path = "/update")
    @Authorize(AuthConstant.CLIENT)
    public GenericResponse<BidDto> updateBid(@RequestBody @Valid UpdateBidRequest updateBidRequest) {
        return genericBidResponse(bidService.updateBid(updateBidRequest, AuthContext.getUserId()));
    }

    @GetMapping(path = "/list")
    @Authorize(AuthConstant.CLIENT)
    public GenericResponse<PageableDto<BidDto>> listBidResponse(@RequestParam(defaultValue = "0") @Min(0) int offset,
                                                                @RequestParam(defaultValue = "10000") @Min(1) int limit,
                                                                @RequestParam(required = false) Instant startDate,
                                                                @RequestParam(required = false) Instant endDate
    ) {
        boolean useDateFilter = null != startDate || null != endDate;
        if (null == startDate) {
            startDate = Helper.MYSQL_MIN_INSTANT;
        }
        if (null == endDate) {
            endDate = Helper.MYSQL_MAX_INSTANT;
        }
        PageRequest pageRequest = PageRequest.of(offset, limit);
        Page<Bid> bidPage = useDateFilter ?
                bidService.bidsBetween(startDate, endDate, AuthContext.getUserId(), pageRequest)
                : bidService.listBids(pageRequest, AuthContext.getUserId());
        return listBidResponse(bidPage, offset, limit);
    }

    @GetMapping(path = "/list/{bwic-id}")
    @Authorize(AuthConstant.CLIENT)
    public GenericResponse<PageableDto<BidDto>> listBidResponse(@PathVariable("bwic-id") String bwicId,
                                                                @RequestParam(defaultValue = "0") @Min(0) int offset,
                                                                @RequestParam(defaultValue = "10000") @Min(1) int limit) {
        return listBidResponse(bidService.bidsByBwicId(bwicId, offset, limit), offset, limit);
    }


    @GetMapping(path = "/bwic-top-bids")
    @Authorize(AuthConstant.BWIC_SERVICE)
    public GenericResponse<Map<String, List<BidDto>>> topBids(@RequestParam List<String> bwicIds) {
        return new GenericResponse<>(
                bidService.bwicTopBids(bwicIds, 2)
                        .stream()
                        .map(b -> modelMapper.map(b, BidDto.class))
                        .collect(Collectors.groupingBy(BidDto::getBwicId))
        );
    }

    @GetMapping(path = "/popular-bwics")
    @Authorize(AuthConstant.BWIC_SERVICE)
    public GenericResponse<List<PopularBwicDto>> popularBwics() {
        return new GenericResponse<>(bidService.popularBwics().stream()
                .map(p -> modelMapper.map(p, PopularBwicDto.class)).collect(Collectors.toList())
        );
    }

    @GetMapping(path = "/bwic-client-bid")
    @Authorize(AuthConstant.BWIC_SERVICE)
    public GenericResponse<Map<String, List<BidDto>>> bwicClientBid(@RequestParam List<String> bwicIds) {
        return new GenericResponse<>(
                bidService.bwicClientBids(bwicIds, AuthContext.getUserId())
                        .stream()
                        .map(b -> modelMapper.map(b, BidDto.class))
                        .collect(Collectors.groupingBy(BidDto::getBwicId)
                        )
        );
    }

    @GetMapping(path = "/client-bid")
    @Authorize(AuthConstant.CLIENT)
    public GenericResponse<Optional<BidDto>> clientBid(@RequestParam String bwicId) {
        return new GenericResponse<>(bidService.clientBwicBid(bwicId, AuthContext.getUserId()).map(this::bidDto));
    }

    @GetMapping(path = "/client-bids")
    @Authorize(AuthConstant.CLIENT)
    public GenericResponse<PageableDto<BwicDetailsDto>> clientBids(
                                                           @RequestParam(defaultValue = "0") @Min(0) int offset,
                                                           @RequestParam(defaultValue = "10000") @Min(1) int limit) {
        Page<Bid> bidPage = bidService.bidsByClientId(AuthContext.getUserId(), offset, limit);

        List<BwicDetailsDto> dtoList = bidPage.stream().map(this::bidDto).map(this::bwicDetailsDto)
                .collect(Collectors.toList());
        return new GenericResponse<>(PageableDto.<BwicDetailsDto>builder()
                .rows(dtoList)
                .limit(limit)
                .offset(offset)
                .totalElements(bidPage.getTotalElements())
                .totalPages(bidPage.getTotalPages())
                .build());
    }

    GenericResponse<BidDto> genericBidResponse(Bid bid) {
        return new GenericResponse<>(bidDto(bid));
    }

    BidDto bidDto(Bid bid) {
        BidDto bidDto = modelMapper.map(bid, BidDto.class);
        GenericResponse<BwicDto> bwicResponse = bwicClient.getBwic(bid.getBwicId());
        if (!bwicResponse.isSuccess()) {
            throw new ServiceException(String.format("Invalid bwic, id: %s, msg: %s",
                    bid.getBwicId(), bwicResponse.getMessage()));
        }
        bidDto.setBwic(bwicResponse.getData());
        return bidDto;
    }

    BwicDetailsDto bwicDetailsDto(BidDto bidDto) {
        return BwicDetailsDto.builder()
                .bwicDto(bidDto.getBwic())
                .bids(Collections.singletonList(bidDto))
                .build();
    }

    GenericResponse<PageableDto<BidDto>> listBidResponse(Page<Bid> bidPage, int offset, int limit) {
        // TODO for bids, fetch bwics one time
        List<Bid> bids = bidPage.stream().collect(Collectors.toList());
        List<BidDto> bidDtos = Stream.concat(bids.stream(), bidService.historyBids(bids.stream().map(Bid::getId).collect(Collectors.toList())).stream())
                .map(this::bidDto).collect(Collectors.toList());
        return new GenericResponse<>(PageableDto.<BidDto>builder()
                .rows(bidDtos)
                .totalElements(bidPage.getTotalElements())
                .totalPages(bidPage.getTotalPages())
                .limit(limit)
                .offset(offset)
                .build());
    }
}
