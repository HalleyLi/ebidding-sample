package xyz.ebidding.bid.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import xyz.ebidding.bid.model.Bid;
import xyz.ebidding.bid.model.PopularBwicProjection;

import java.time.Instant;
import java.util.List;
import java.util.Optional;


@Repository
public interface BidRepo extends PagingAndSortingRepository<Bid, String> {

    Page<Bid> findByBwicId(String bwicId, Pageable pageable);
    Page<Bid> findByClientId(String clientId, Pageable pageable);
    List<Bid> findByBwicIdInAndRankLessThanOrderByRankAsc(List<String> bwicIds, long lessThan);

    boolean existsByClientIdAndBwicId(String clientId, String bwicId);

    Optional<Bid> findByBwicIdAndClientId(String bwicId, String clientId);

    List<Bid> findByBwicIdInAndClientId(List<String> bwicId, String clientId);

    @Query(
            value = "SELECT bwic_id as id, count(*) as numberOfBids, max(effective_time) as latestBidTime " +
                    "from bid group by bwic_id " +
                    "order by numberOfBids desc, latestBidTime desc  limit 10",
            nativeQuery = true
            )
    List<PopularBwicProjection> popularBwics();

    Page<Bid> findByEffectiveTimeBetweenAndClientId(Instant from, Instant to, String clientId, Pageable pageable);

    @Modifying
    @Query("update Bid set active=false where bwicId=:bwicId")
    void deleteBidsByBwic(String bwicId);

}
