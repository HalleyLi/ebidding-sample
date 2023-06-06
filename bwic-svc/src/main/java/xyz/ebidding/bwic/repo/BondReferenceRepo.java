package xyz.ebidding.bwic.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import xyz.ebidding.bwic.model.BondReference;

import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "bond-reference", path = "bond-reference")
public interface BondReferenceRepo extends JpaRepository<BondReference, String> {
    Optional<BondReference> findByCusip(String cusip);
}
