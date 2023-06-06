package xyz.ebidding.bwic.repo;


import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.junit4.SpringRunner;
import xyz.ebidding.bwic.model.Bwic;
import xyz.ebidding.bwic.model.BondReference;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.time.Instant;
import java.util.Objects;
import java.util.stream.StreamSupport;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@Slf4j
public class BwicRepoTest {

    @Autowired
    BwicRepo bwicRepo;

    @Autowired
    BondReferenceRepo bondReferenceRepo;

    @Autowired
    EntityManager entityManager;

    @Test
    public void idWorks() {
        BondReference bondReference = bondReferenceRepo.saveAndFlush(BondReference.builder().cusip("cusip").build());

        Bwic bwic = bwicRepo.save(Bwic.builder().bondReference(bondReference).build());
        assertNotNull(bwic.getId());
    }

    @Test
    public void versionWorks() {
        BondReference bondReference = bondReferenceRepo.saveAndFlush(BondReference.builder().cusip("cusip").build());

        Bwic bwic = bwicRepo.save(Bwic.builder().bondReference(bondReference).build());
        assertNotNull(bwic.getVersion());
        assertEquals(0, bwic.getVersion().longValue());

        Bwic updateRequest = Bwic.builder().id(bwic.getId()).bondReference(bondReference).clientId("client").version(0L).build();

        Bwic updated = bwicRepo.save(updateRequest);
        entityManager.flush();
        assertEquals(1, updated.getVersion().longValue());

        try {
            bwicRepo.save(Bwic.builder().id(bwic.getId()).version(0L)
                    .build());
            fail("should failure when use old version");
        } catch (ObjectOptimisticLockingFailureException e) {
            log.info("version lock works");
        }
    }

    @Test
    public void deleteBondReferenceStillExists() {

        BondReference bondReference = bondReferenceRepo.saveAndFlush(BondReference.builder().cusip("cusip").build());
        bwicRepo.save(Bwic.builder().active(true).bondReference(bondReference).build());
        assertEquals(1, StreamSupport.stream(bwicRepo.findAll().spliterator(), false).count());
        assertEquals(1, bondReferenceRepo.findAll().size());

        bwicRepo.deleteAll();
        assertEquals(0, StreamSupport.stream(bwicRepo.findAll().spliterator(), false).count());

        assertEquals(1, bondReferenceRepo.findAll().size());
    }

    @Test
    public void softDelete() {
        BondReference bondReference = bondReferenceRepo.saveAndFlush(BondReference.builder().cusip("cusip").build());
        Bwic bwic = bwicRepo.save(Bwic.builder().active(true).bondReference(bondReference).build());
        assertTrue(bwicRepo.existsById(bwic.getId()));


        bwicRepo.save(Objects.requireNonNull(bwicRepo.findById(bwic.getId()).map(b -> {
            b.setActive(false);
            return b;
        }).orElse(null)));
        assertFalse(bwicRepo.existsById(bwic.getId()));

        entityManager.detach(bwic);
        assertFalse(bwicRepo.findById(bwic.getId()).isPresent());

        Query namedQuery = entityManager.createNativeQuery("select active from bwic where id=:id");
        namedQuery.setParameter("id", bwic.getId());

        assertEquals(Boolean.FALSE, namedQuery.getSingleResult());
    }

    @Test
    public void testPage() {
        BondReference bondReference = bondReferenceRepo.saveAndFlush(BondReference.builder().cusip("cusip").build());

        bwicRepo.save(Bwic.builder().active(true).bondReference(bondReference).build());
        bwicRepo.save(Bwic.builder().active(true).bondReference(bondReference).build());
        bwicRepo.save(Bwic.builder().active(true).bondReference(bondReference).build());
        bwicRepo.save(Bwic.builder().active(true).bondReference(bondReference).build());
        bwicRepo.save(Bwic.builder().active(true).bondReference(bondReference).build());

        entityManager.flush();
        Page<Bwic> firstPage = bwicRepo.findAll(PageRequest.of(0, 2));
        assertEquals(5, firstPage.getTotalElements());
        assertEquals(3, firstPage.getTotalPages());
        assertEquals(2, firstPage.get().count());

        Page<Bwic> lastPage = bwicRepo.findAll(PageRequest.of(2, 2));
        assertEquals(5, lastPage.getTotalElements());
        assertEquals(3, lastPage.getTotalPages());
        assertEquals(1, lastPage.get().count());

    }

    @Test
    public void testFindByDueDateBetweenAnd() {
        BondReference bondReference = bondReferenceRepo.saveAndFlush(BondReference.builder().cusip("cusip").build());
        bwicRepo.save(Bwic.builder().active(true).dueDate(Instant.ofEpochMilli(1)).bondReference(bondReference).build());
        bwicRepo.save(Bwic.builder().active(true).dueDate(Instant.ofEpochMilli(2)).bondReference(bondReference).build());
        bwicRepo.save(Bwic.builder().active(true).dueDate(Instant.ofEpochMilli(5)).bondReference(bondReference).build());

        assertEquals(1, bwicRepo
                .findByDueDateBetween(Instant.ofEpochMilli(2), Instant.ofEpochMilli(3), Pageable.unpaged())
                .stream().count());
    }
}