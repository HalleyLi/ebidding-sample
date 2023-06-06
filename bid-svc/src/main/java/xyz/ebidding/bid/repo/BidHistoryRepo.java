package xyz.ebidding.bid.repo;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import xyz.ebidding.bid.model.BidHistory;
import xyz.ebidding.bid.model.BidHistoryId;

import java.util.List;

@Repository
public interface BidHistoryRepo extends PagingAndSortingRepository<BidHistory, BidHistoryId> {
    List<BidHistory> findByIdIdIn(List<String> ids);
}
