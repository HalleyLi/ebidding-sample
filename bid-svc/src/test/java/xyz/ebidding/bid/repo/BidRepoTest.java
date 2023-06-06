package xyz.ebidding.bid.repo;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;
import xyz.ebidding.bid.model.Bid;
import xyz.ebidding.bid.model.PopularBwicProjection;
import xyz.ebidding.common.utils.Helper;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RunWith(SpringRunner.class)
@DataJpaTest
public class BidRepoTest extends TestCase {

    @Autowired
    private BidRepo bidRepo;

    @Autowired
    EntityManager entityManager;

    @Test
    public void activeFilterWorks() {
        bidRepo.save(Bid.builder().active(false).build());
        assertEquals(0, allBids().size());
        bidRepo.save(Bid.builder().active(true).build());
        assertEquals(1, allBids().size());
    }

    @Test
    public void versionWorks() {
        Bid saved = bidRepo.save(Bid.builder().active(true).build());
        assertEquals(0, saved.getVersion().longValue());
        saved.setClientId("update");
        bidRepo.save(saved);
        entityManager.flush();
        assertEquals(1, bidRepo.findById(saved.getId()).map(Bid::getVersion).orElse(-1L).longValue());
    }

    @Test
    public void byBidId() {
        bidRepo.save(Bid.builder().bwicId("bond1").active(true).build());
        bidRepo.save(Bid.builder().bwicId("bond1").active(true).build());
        bidRepo.save(Bid.builder().bwicId("bond2").active(true).build());

        assertEquals(3, allBids().size());

        assertEquals(2, bidRepo.findByBwicId("bond1", Pageable.unpaged()).getTotalElements());
    }

    private List<Bid> allBids() {
        return StreamSupport.stream(bidRepo.findAll().spliterator(), false).collect(Collectors.toList());
    }

    @Test
    public void testFindByBondId() {
        bidRepo.save(Bid.builder().bwicId("test-page").active(true).build());
        bidRepo.save(Bid.builder().bwicId("test-page").active(true).build());
        bidRepo.save(Bid.builder().bwicId("test-page").active(true).build());
        bidRepo.save(Bid.builder().bwicId("test-page").active(true).build());
        bidRepo.save(Bid.builder().bwicId("test-page").active(true).build());

        Page<Bid> byBondId = bidRepo.findByBwicId("test-page", PageRequest.of(0, 2));

        assertEquals(5, byBondId.getTotalElements());

        assertEquals(3, byBondId.getTotalPages());
        assertEquals(2, byBondId.get().count());

    }

    @Test
    public void testPopularBwics() {
        bidRepo.save(Bid.builder().bwicId("bwic1").active(true).build());
        bidRepo.save(Bid.builder().bwicId("bwic1").active(true).effectiveTime(Instant.ofEpochMilli(1)).build());
        bidRepo.save(Bid.builder().bwicId("bwic2").active(true).build());
        bidRepo.save(Bid.builder().bwicId("bwic2").active(true).effectiveTime(Instant.ofEpochMilli(2)).build());
        bidRepo.save(Bid.builder().bwicId("bwic3").active(true).build());
        bidRepo.save(Bid.builder().bwicId("bwic3").active(true).build());
        bidRepo.save(Bid.builder().bwicId("bwic3").active(true).build());

        List<PopularBwicProjection> bwics = bidRepo.popularBwics();
        assertEquals(3, bwics.size());
        assertEquals("bwic3", bwics.get(0).getId());
        assertEquals(3, bwics.get(0).getNumberOfBids().longValue());
        assertEquals("bwic2", bwics.get(1).getId());
        assertEquals(2, bwics.get(2).getNumberOfBids().longValue());
    }

    @Test
    public void testFindByEffectiveTimeBetween() {
        bidRepo.save(Bid.builder().effectiveTime(Instant.ofEpochMilli(1)).clientId("client").active(true).build());
        bidRepo.save(Bid.builder().effectiveTime(Instant.ofEpochMilli(2)).clientId("client").active(true).build());
        bidRepo.save(Bid.builder().effectiveTime(Instant.ofEpochMilli(5)).clientId("client").active(true).build());

        assertEquals(1, bidRepo
                .findByEffectiveTimeBetweenAndClientId(Instant.ofEpochMilli(2), Instant.ofEpochMilli(3), "client", Pageable.unpaged())
                .stream().count());
    }

    @Test
    public void testFindByEffectiveTimeBetweenMinMax() {
        bidRepo.save(Bid.builder().effectiveTime(Instant.now()).clientId("client").active(true).build());

        assertEquals(1, bidRepo
                .findByEffectiveTimeBetweenAndClientId(Helper.MYSQL_MIN_INSTANT, Helper.MYSQL_MAX_INSTANT,
                        "client", Pageable.unpaged())
                .stream().count());
    }

    @Test
    public void testDeleteBidsByBwic() {
        bidRepo.save(Bid.builder().bwicId("bwic1").active(true).build());
        bidRepo.save(Bid.builder().bwicId("bwic1").active(true).build());
        bidRepo.save(Bid.builder().bwicId("bwic2").active(true).build());

        assertEquals(3, allBids().size());

        bidRepo.deleteBidsByBwic("bwic1");
        assertEquals(1, allBids().size());

    }

}