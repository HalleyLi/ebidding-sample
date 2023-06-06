package xyz.ebidding.bwic.controller;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.ebidding.bid.client.BidClient;
import xyz.ebidding.bwic.dto.*;
import xyz.ebidding.bwic.model.BondReference;
import xyz.ebidding.bwic.model.Bwic;
import xyz.ebidding.bwic.service.BwicService;
import xyz.ebidding.common.auth.AuthConstant;
import xyz.ebidding.common.auth.AuthContext;
import xyz.ebidding.common.auth.Authorize;
import xyz.ebidding.common.model.dto.*;
import xyz.ebidding.common.utils.Helper;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/v1/bwic")
@Validated
@Slf4j
public class BwicController {

    @Autowired
    private BwicService bwicService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BidClient bidClient;

    @PostMapping("/create")
    @Authorize({"TRADER"})
    public GenericResponse<BwicDto> create(@Valid @RequestBody CreateBwicRequest createBwicRequest) {
        return new GenericResponse<>(toDto(bwicService.saveBwic(createBwicRequest)));
    }

    @PostMapping("/delete")
    @Authorize({"TRADER"})
    public GenericResponse<BwicDto> delete(@Valid @RequestBody DeleteBwicRequest deleteBwicRequest) {
        bidClient.deleteBwicBids(deleteBwicRequest.getId());
        return new GenericResponse<>(toDto(bwicService.deleteBwic(deleteBwicRequest.getId())));
    }

    @GetMapping("/list")
    @Authorize({"TRADER"})
    public GenericResponse<PageableDto<BwicDto>> list(@RequestParam(defaultValue = "0") @Min(0) int offset,
                                                      @RequestParam(defaultValue = "1000") @Min(1) int limit,
                                                      @RequestParam(required = false) Instant startDate,
                                                      @RequestParam(required = false) Instant endDate
    ) {
        Page<Bwic> bwicPage = bwics(startDate, endDate, offset, limit);
        return pageableBwics(bwicPage, offset, limit);
    }

    @GetMapping("/popular")
    @Authorize({"TRADER"})
    public GenericResponse<List<PopularBwicDto>> popularBwics() {
        GenericResponse<List<PopularBwicDto>> res = bidClient.popularBwics();
        if (!res.isSuccess()) {
            log.error("fail to get popular bwics from bid service: {}", res);
            GenericResponse<List<PopularBwicDto>> failRes = new GenericResponse<>();
            failRes.setCode(res.getCode());
            failRes.setMessage(res.getMessage());
            return failRes;
        }
        List<PopularBwicDto> thinPopularBwicDtos = res.getData();
        Map<String, Long> bwicBids = thinPopularBwicDtos.stream()
                .collect(Collectors.toMap(PopularBwicDto::getId, PopularBwicDto::getNumberOfBids));
        Iterable<Bwic> bwics = bwicService.byIds(thinPopularBwicDtos.stream().map(PopularBwicDto::getId)
                .collect(Collectors.toList()));
        List<PopularBwicDto> bwicDtos = StreamSupport.stream(bwics.spliterator(), false)
                .map(this::toDto)
                .map(bwicDto -> {
                    PopularBwicDto popularBwicDto = modelMapper.map(bwicDto, PopularBwicDto.class);
                    popularBwicDto.setNumberOfBids(bwicBids.get(popularBwicDto.getId()));
                    return popularBwicDto;
                })
                .collect(Collectors.toList());
        return new GenericResponse<>(bwicDtos);
    }

    @GetMapping("/bwic-bid-details")
    @Authorize({"TRADER", "CLIENT"})
    public GenericResponse<PageableDto<BwicDetailsDto>> bwicBidDetails(@RequestParam(defaultValue = "0") @Min(0) int offset,
                                                                       @RequestParam(defaultValue = "10000") @Min(1) int limit,
                                                                       @RequestParam(required = false) Instant startDate,
                                                                       @RequestParam(required = false) Instant endDate
    ) {
        log.info("fetching bwic bid details");
        Page<Bwic> bwics = bwics(startDate, endDate, offset, limit);
        List<BwicDto> bwicDtos = bwics
                .stream().map(this::toDto)
                .collect(Collectors.toList());

        final Map<String, List<BidDto>> bwicBids = new HashMap<>();
        if (AuthConstant.TRADER.equals(AuthContext.getUserRole())) {
            GenericResponse<Map<String, List<BidDto>>> topBidsResponse = bidClient.bwicTopBids(bwicDtos.stream()
                    .map(BwicDto::getId).collect(Collectors.toList()));
            if (topBidsResponse.isSuccess()) {
                bwicBids.putAll(topBidsResponse.getData());
            }
        } else if (AuthConstant.CLIENT.equals(AuthContext.getUserRole())) {
            GenericResponse<Map<String, List<BidDto>>> bwicClientBidResponse = bidClient.bwicClientBid(bwicDtos.stream()
                    .map(BwicDto::getId).collect(Collectors.toList()));
            if (bwicClientBidResponse.isSuccess()) {
                bwicBids.putAll(bwicClientBidResponse.getData());
            }
        }

        List<BwicDetailsDto> bwicDetailsDtos = bwicDtos.stream().map(bwicDto ->
                BwicDetailsDto.builder().bwicDto(bwicDto)
                        .bids(bwicBids.getOrDefault(bwicDto.getId(), Collections.emptyList())).build()
        ).collect(Collectors.toList());
        return new GenericResponse<>(PageableDto.<BwicDetailsDto>builder()
                .limit(limit)
                .offset(offset)
                .totalElements(bwics.getTotalElements())
                .totalPages(bwics.getTotalPages())
                .rows(bwicDetailsDtos)
                .build());
    }

    @GetMapping("/{id}")
    @Authorize({AuthConstant.TRADER, AuthConstant.BID_SERVICE})
    public GenericResponse<BwicDto> bwic(@PathVariable("id") String id) {
        return new GenericResponse<>(toDto(bwicService.getBwic(id)));
    }

    Page<Bwic> bwics(Instant startDate, Instant endDate, int offset, int limit) {
        boolean useDateFilter = null != startDate || null != endDate;
        if (null == startDate) {
            startDate = Helper.MYSQL_MIN_INSTANT;
        }
        if (null == endDate) {
            endDate = Helper.MYSQL_MAX_INSTANT;
        }
        PageRequest pageRequest = PageRequest.of(offset, limit);
        return useDateFilter ?
                bwicService.byDueDate(startDate, endDate, pageRequest)
                : bwicService.all(pageRequest)
                ;
    }

    GenericResponse<PageableDto<BwicDto>> pageableBwics(Page<Bwic> bwicPage, int offset, int limit) {
        List<BwicDto> bwicDtos = bwicPage
                .stream().map(this::toDto)
                .collect(Collectors.toList());
        return new GenericResponse<>(
                PageableDto.<BwicDto>builder()
                        .rows(bwicDtos)
                        .limit(limit)
                        .offset(offset)
                        .totalElements(bwicPage.getTotalElements())
                        .totalPages(bwicPage.getTotalPages())
                        .build()
        );
    }

    BwicDto toDto(Bwic bwic) {
        BwicDto bwicDto = modelMapper.map(bwic, BwicDto.class);
        BondReference bondReference = bwic.getBondReference();
        if (null != bondReference) {
            bwicDto.setIssuer(bondReference.getIssuer());
            bwicDto.setCusip(bondReference.getCusip());
            bwicDto.setCoupon(bondReference.getCoupon());
            bwicDto.setRating(bondReference.getRating());
            bwicDto.setMaturitydate(bondReference.getMaturitydate());
        }
        bwicDto.setIsOverDue(Instant.now().isAfter(
                Optional.ofNullable(bwic.getDueDate()).orElse(Instant.MAX)
        ));
        return bwicDto;
    }
}
