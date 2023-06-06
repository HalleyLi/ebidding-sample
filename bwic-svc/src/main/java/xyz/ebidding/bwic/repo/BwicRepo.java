package xyz.ebidding.bwic.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import xyz.ebidding.bwic.model.Bwic;

import java.time.Instant;

@Repository
public interface BwicRepo extends PagingAndSortingRepository<Bwic, String> {
    Page<Bwic> findByDueDateBetween(Instant from, Instant to, Pageable pageable);
}
