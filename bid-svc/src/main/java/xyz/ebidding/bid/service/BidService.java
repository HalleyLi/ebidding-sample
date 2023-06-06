package xyz.ebidding.bid.service;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import xyz.ebidding.bid.dto.CreateBidRequest;
import xyz.ebidding.bid.dto.UpdateBidRequest;
import xyz.ebidding.bid.model.Bid;
import xyz.ebidding.bid.model.BidHistory;
import xyz.ebidding.bid.model.PopularBwicProjection;
import xyz.ebidding.bid.repo.BidHistoryRepo;
import xyz.ebidding.bid.repo.BidRepo;
import xyz.ebidding.bwic.client.BwicClient;
import xyz.ebidding.common.error.ServiceException;
import xyz.ebidding.common.model.dto.BwicDto;
import xyz.ebidding.common.model.dto.GenericResponse;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BidService {

    @Autowired
    private BwicClient bwicClient;

    @Autowired
    private BidRepo bidRepo;

    @Autowired
    private BidHistoryRepo bidHistoryRepo;
    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    public Bid createBid(CreateBidRequest createBidRequest, String clientId) {
        Bid bid = modelMapper.map(createBidRequest, Bid.class);
        GenericResponse<BwicDto> bondResponse = bwicClient.getBwic(bid.getBwicId());
        if (!bondResponse.isSuccess()) {
            throw new ServiceException(String.format("Invalid Bond, id: %s, msg: %s",
                    bid.getBwicId(), bondResponse.getMessage()));
        }

        if (bidRepo.existsByClientIdAndBwicId(clientId, createBidRequest.getBwicId())) {
            throw new ServiceException(String.format("Already bid for bwic: %s", createBidRequest.getBwicId()));
        }

        bid.setClientId(clientId);
        bid.setEffectiveTime(Instant.now());
        bid.setActive(true);
        Bid saved = bidRepo.save(bid);
        reRankBids(bid.getBwicId());
        return bidRepo.findById(saved.getId()).orElseThrow(
                () -> new ServiceException("invalid state, after re-rank, bid is missing: " + saved));
    }

    @Transactional
    public void deleteBid(String bidId, String clientId) {
        Bid bid = bidRepo.findById(bidId).orElseThrow(() -> new ServiceException("invalid bid id: " + bidId));
        if (!StringUtils.hasText(clientId) || !clientId.equals(bid.getClientId())) {
            throw new IllegalStateException(clientId + " has no permission to operate bid: " + bidId);
        }
        bid.setActive(false);
        bidRepo.save(bid);
        reRankBids(bid.getBwicId());
    }

    @Transactional
    public void deleteBwicBids(String bwicId) {
        bidRepo.deleteBidsByBwic(bwicId);
    }

    @Transactional
    public Bid updateBid(UpdateBidRequest updateBidRequest, String clientId) {

        Bid bid = bidRepo.findById(updateBidRequest.getId()).orElseThrow(
                () -> new ServiceException("invalid bid id: " + updateBidRequest.getId())
        );

        if (!StringUtils.hasText(clientId) || !clientId.equals(bid.getClientId())) {
            throw new IllegalStateException(clientId + " has no permission to operate bid: " + bid.getId());
        }
        bid.setPrice(updateBidRequest.getPrice());
        bid.setEffectiveTime(Instant.now());
        Bid saved = bidRepo.save(bid);
        reRankBids(bid.getBwicId());
        return bidRepo.findById(saved.getId()).orElseThrow(
                () -> new ServiceException("invalid state, after re-rank, bid is missing: " + saved));
    }

    public Page<Bid> bidsByBwicId(String bwicId, int offset, int limit) {
        return bidRepo.findByBwicId(bwicId, PageRequest.of(offset, limit));
    }

    public Page<Bid> bidsByClientId(String clientId, int offset,  int limit) {
        return bidRepo.findByClientId(clientId, PageRequest.of(offset, limit));
    }

    public Optional<Bid> clientBwicBid(String bwicId, String clientId) {
        return bidRepo.findByBwicIdAndClientId(bwicId, clientId);
    }

    public List<Bid> bwicClientBids(List<String> bwicIds, String clientId) {
        return bidRepo.findByBwicIdInAndClientId(bwicIds, clientId);
    }

    @Transactional(readOnly = true)
    public List<Bid> bwicTopBids(List<String> bwicIds, int topN) {
        return bidRepo.findByBwicIdInAndRankLessThanOrderByRankAsc(bwicIds, topN + 1);
    }

    public Page<Bid> listBids(Pageable pageable, String clientId) {
        return bidRepo.findByClientId(clientId, pageable);
    }

    public List<Bid> historyBids(List<String> ids) {
        return bidHistoryRepo.findByIdIdIn(ids)
                .stream()
                .map(this::toBid)
                .collect(Collectors.toList());
    }

    private Bid toBid(BidHistory bidHistory) {
        Bid bid = modelMapper.map(bidHistory, Bid.class);
        bid.setId(bidHistory.getId().getId());
        bid.setVersion(bidHistory.getId().getVersion());
        return bid;
    }

    public List<PopularBwicProjection> popularBwics() {
        return bidRepo.popularBwics();
    }

    public Page<Bid> bidsBetween(Instant from, Instant to, String clientId, Pageable pageable) {
        return bidRepo.findByEffectiveTimeBetweenAndClientId(from, to, clientId, pageable);
    }

    void reRankBids(String bondId) {
        List<Bid> bids = bidRepo.findByBwicId(bondId, Pageable.unpaged()).stream()
                .sorted(Comparator.comparingDouble(Bid::getPrice).reversed().thenComparing(Bid::getEffectiveTime))
                .collect(Collectors.toList());
        long rank = 1;
        for (Bid bid : bids) {
            bid.setRank(rank);
            bid.setFeedback(String.format("Rank is %d of %d", rank, bids.size()));
            rank++;
        }
        if (bids.size() > 1) {
            bids.get(0).setFeedback(String.format(
                    "You are the best of %d, cover price is %,.2f", bids.size(), bids.get(1).getPrice())
            );
        }
        bidRepo.saveAll(bids);
    }
}
