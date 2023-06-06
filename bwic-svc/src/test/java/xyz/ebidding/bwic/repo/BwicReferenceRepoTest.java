package xyz.ebidding.bwic.repo;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import xyz.ebidding.bwic.model.BondReference;
import xyz.ebidding.pricer.client.PricerClient;

@RunWith(SpringRunner.class)
@DataJpaTest
public class BwicReferenceRepoTest extends TestCase {
    @Autowired
    private BondReferenceRepo bondReferenceRepo;

    @MockBean
    private PricerClient pricerClient;

    @Test
    public void testSave() {
        String id = bondReferenceRepo.saveAndFlush(BondReference.builder().cusip("cusip").build())
                .getId();

        assertEquals("cusip", bondReferenceRepo.findById(id).map(BondReference::getCusip).orElse(""));
    }

    @Test
    public void testFindByCusip() {
        bondReferenceRepo.saveAndFlush(BondReference.builder().issuer("issuer").cusip("cusip saved").build());

        assertEquals("issuer", bondReferenceRepo.findByCusip("cusip saved").map(BondReference::getIssuer)
                .orElse(""));

    }
}