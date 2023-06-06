package xyz.ebidding.bwic.service;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import xyz.ebidding.bwic.TestConfig;
import xyz.ebidding.bwic.dto.CreateBwicRequest;
import xyz.ebidding.bwic.model.Bwic;
import xyz.ebidding.bwic.model.BondReference;
import xyz.ebidding.bwic.repo.BondReferenceRepo;
import xyz.ebidding.bwic.repo.BwicRepo;
import xyz.ebidding.common.model.dto.GenericResponse;
import xyz.ebidding.pricer.client.PricerClient;

import javax.validation.ConstraintViolationException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@Import({TestConfig.class, BwicService.class})
public class BwicServiceTest extends TestCase {

    @Autowired
    private BwicService bwicService;
    @MockBean
    private BwicRepo bwicRepo;
    @MockBean
    BondReferenceRepo bondReferenceRepo;

    @MockBean
    PricerClient pricerClient;

    @Test
    public void saveBond() {
        when(bwicRepo.save(any())).thenAnswer(ans -> ans.getArgument(0));
        when(bondReferenceRepo.findByCusip(any())).thenAnswer(ans ->
                Optional.of(BondReference.builder().cusip(ans.getArgument(0)).build())
        );

        when(pricerClient.price()).thenReturn(new GenericResponse<>(110.0));

        Bwic bwic = bwicService.saveBwic(CreateBwicRequest.builder().clientId("client").cusip("cusip").size(10.0)
                .build());
        assertNotNull(bwic);
        assertEquals(10.0, bwic.getSize());
        assertEquals(110.0, bwic.getStartingPrice());
        assertEquals("client", bwic.getClientId());
        assertEquals("cusip", bwic.getBondReference().getCusip());
    }

    @Test
    public void saveBondFailureWhenReferenceMissing() {

        try {
            bwicService.saveBwic(CreateBwicRequest.builder()
                    .clientId("client").cusip("cusip missing").size(10.0)
                    .build()
            );
            fail("should fail when cusip not exists");
        } catch (ConstraintViolationException e) {
            assertTrue(e.getMessage().contains("cusip missing"));
        }
    }

    @Test
    public void testDeleteBond() {
        when(bwicRepo.findById("idToDelete")).thenReturn(Optional.of(Bwic.builder().clientId("test client").active(true)
                .build()));
        when(bwicRepo.save(eq(Bwic.builder()
                .active(false).clientId("test client").build())))
                .thenReturn(Bwic.builder().version(1L).active(false).build());

        Bwic deleteBwic = bwicService.deleteBwic("idToDelete");
        assertFalse(deleteBwic.getActive());
        assertEquals(1, deleteBwic.getVersion().longValue());

    }
}