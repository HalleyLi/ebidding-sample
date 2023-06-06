package xyz.ebidding.bid.repo;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import xyz.ebidding.bid.model.BidHistory;
import xyz.ebidding.bid.model.BidHistoryId;

import java.util.Collections;

@DataJpaTest
@RunWith(SpringRunner.class)
public class BidHistoryRepoTest extends TestCase {

    @Autowired
    private BidHistoryRepo bidHistoryRepo;

    @Test
    public void testFindByIdIn() {
        bidHistoryRepo.save(BidHistory.builder().id(BidHistoryId.builder().id("1").version(1L).build()).build());
        assertEquals(1, bidHistoryRepo.findByIdIdIn(Collections.singletonList("1")).size());
    }
}