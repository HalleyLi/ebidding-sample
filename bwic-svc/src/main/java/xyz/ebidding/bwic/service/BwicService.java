package xyz.ebidding.bwic.service;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import xyz.ebidding.bwic.dto.CreateBwicRequest;
import xyz.ebidding.bwic.model.Bwic;
import xyz.ebidding.bwic.model.BondReference;
import xyz.ebidding.bwic.repo.BondReferenceRepo;
import xyz.ebidding.bwic.repo.BwicRepo;
import xyz.ebidding.common.error.ServiceException;
import xyz.ebidding.common.model.dto.GenericResponse;
import xyz.ebidding.pricer.client.PricerClient;

import javax.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class BwicService {
    @Autowired
    private BwicRepo bwicRepo;

    @Autowired
    private BondReferenceRepo bondReferenceRepo;

    @Autowired
    private PricerClient pricerClient;

    @Autowired
    private ModelMapper modelMapper;

    public Bwic getBwic(String bwicId) {
        return bwicRepo.findById(bwicId).orElseThrow(() ->
                new ServiceException("Invalid bwicId: " + bwicId));
    }

    public Bwic saveBwic(CreateBwicRequest createBwicRequest) {
        Bwic bwic = modelMapper.map(createBwicRequest, Bwic.class);

        BondReference bondReference = bondReferenceRepo.findByCusip(createBwicRequest.getCusip())
                .orElseThrow(() ->
                        new ConstraintViolationException(String.format("reference for cusip: %s not exists",
                                createBwicRequest.getCusip()), Collections.emptySet())
                );
        bwic.setBondReference(bondReference);
        GenericResponse<Double> priceResponse = pricerClient.price();
        if (priceResponse.isSuccess()) {
            bwic.setStartingPrice(priceResponse.getData());
        } else {
            log.warn("fail to get auto price {}", priceResponse);
            bwic.setStartingPrice(100.0);
        }
        bwic.setActive(true);
        return bwicRepo.save(bwic);
    }

    public Bwic deleteBwic(String bwicId) {
        Bwic bwic = bwicRepo.findById(bwicId).orElseThrow(() ->
                new ConstraintViolationException(String.format("bond does not exist, id: %s", bwicId),
                        Collections.emptySet()));
        bwic.setActive(false);
        return bwicRepo.save(bwic);
    }

    public Iterable<Bwic> byIds(List<String> ids) {
        return bwicRepo.findAllById(ids);
    }

    public Page<Bwic> all(Pageable pageable) {
        return bwicRepo.findAll(pageable);
    }

    public Page<Bwic> byDueDate(Instant from, Instant to, Pageable pageable) {
        return bwicRepo.findByDueDateBetween(from, to, pageable);
    }
}
