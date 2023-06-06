package xyz.ebidding.bid.service;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;
import xyz.ebidding.bid.TestConfig;
import xyz.ebidding.bid.dto.CreateBidRequest;
import xyz.ebidding.bid.dto.UpdateBidRequest;
import xyz.ebidding.bid.model.Bid;
import xyz.ebidding.bid.repo.BidHistoryRepo;
import xyz.ebidding.bid.repo.BidRepo;
import xyz.ebidding.bwic.client.BwicClient;
import xyz.ebidding.common.api.ResultCode;
import xyz.ebidding.common.error.ServiceException;
import xyz.ebidding.common.model.dto.BwicDto;
import xyz.ebidding.common.model.dto.GenericResponse;

import java.time.Instant;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@Import({TestConfig.class, BidService.class})
public class BidServiceTest extends TestCase {
    @Autowired
    BidService bidService;
    @MockBean
    BidRepo bidRepo;

    @MockBean
    BwicClient bwicClient;

    @MockBean
    BidHistoryRepo bidHistoryRepo;

    @Captor
    ArgumentCaptor<List<Bid>> bidsCaptor;

    @Autowired
    ModelMapper modelMapper;

    @Test
    public void testReRankBids() {
        when(bidRepo.findByBwicId("bondId", Pageable.unpaged())).thenReturn(
                new PageImpl<>(
                        Arrays.asList(
                                Bid.builder().id("id-rank:3").price(1.0).bwicId("id").active(true).effectiveTime(Instant.ofEpochMilli(1)).build(),
                                Bid.builder().id("id-rank:2").price(2.0).bwicId("id").active(true).effectiveTime(Instant.ofEpochMilli(2)).build(),
                                Bid.builder().id("id-rank:1").price(2.0).bwicId("id").active(true).effectiveTime(Instant.ofEpochMilli(1)).build()
                        )));

        when(bidRepo.saveAll(bidsCaptor.capture())).thenReturn(null);
        bidService.reRankBids("bondId");

        List<Bid> bids = bidsCaptor.getValue();

        assertEquals(3, bids.size());
        for (Bid bid : bids) {
            assertEquals(Long.parseLong(bid.getId().split(":")[1]), bid.getRank().longValue());
        }
    }

    @Test
    public void testCreateBid_BondMissing() {
        GenericResponse<BwicDto> notFound = new GenericResponse<>();
        notFound.setCode(ResultCode.NOT_FOUND);
        when(bwicClient.getBwic("invalid-bond-id"))
                .thenReturn(notFound);

        try {
            bidService.createBid(CreateBidRequest.builder().bwicId("invalid-bond-id").build(), "client");
            fail("should throw exception before");
        } catch (ServiceException e) {
            assertTrue(e.getMessage().contains("Invalid Bond"));
        }
    }

    @Test
    public void testCreateBid() {
        when(bwicClient.getBwic("bond"))
                .thenReturn(new GenericResponse<>());
        when(bidRepo.findByBwicId(any(), any())).thenReturn(Page.empty());

        ArgumentCaptor<Bid> bidCaptor = ArgumentCaptor.forClass(Bid.class);
        when(bidRepo.save(bidCaptor.capture())).thenAnswer(ans -> ans.getArgument(0));
        when(bidRepo.findById(any())).thenReturn(Optional.of(Bid.builder().build()));

        bidService.createBid(CreateBidRequest.builder()
                .bwicId("bond").size(100.0).price(1.0).build(), "client");

        Bid bid = bidCaptor.getValue();

        assertEquals("client", bid.getClientId());
        assertEquals("bond", bid.getBwicId());
        assertEquals(1.0, bid.getPrice());
        assertEquals(100.0, bid.getSize());
        assertNotNull(bid.getEffectiveTime());
        assertTrue(bid.getActive());
    }

//    @Test
    public void testDeleteBid() {
        when(bidRepo.findById("bid-id")).thenReturn(
                Optional.of(Bid.builder().id("bid-id").active(true).build())
        );
        when(bidRepo.findByBwicId(any(), any())).thenReturn(Page.empty());

        ArgumentCaptor<Bid> bidArgumentCaptor = ArgumentCaptor.forClass(Bid.class);
        when(bidRepo.save(bidArgumentCaptor.capture())).thenReturn(null);

        bidService.deleteBid("bid-id", "client");

        Bid captorValue = bidArgumentCaptor.getValue();
        assertEquals("bid-id", captorValue.getId());
        assertFalse(captorValue.getActive());
    }

//    @Test
    public void testUpdateBid() {
        when(bidRepo.findById("update-bid-id")).thenReturn(Optional.of(Bid.builder().id("update-bid-id").price(1.0)
                .build()));
        when(bidRepo.findByBwicId(any(), any())).thenReturn(Page.empty());

        ArgumentCaptor<Bid> bidArgumentCaptor = ArgumentCaptor.forClass(Bid.class);
        when(bidRepo.save(bidArgumentCaptor.capture())).thenAnswer(ans -> ans.getArgument(0));

        bidService.updateBid(UpdateBidRequest.builder().id("update-bid-id").price(100.0).build(), "client");
        Bid captorValue = bidArgumentCaptor.getValue();
        assertEquals(100.0, captorValue.getPrice());
        assertNotNull(captorValue.getEffectiveTime());
    }
}