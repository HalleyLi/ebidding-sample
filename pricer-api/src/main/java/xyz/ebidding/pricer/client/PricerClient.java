package xyz.ebidding.pricer.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import xyz.ebidding.common.model.dto.GenericResponse;

@FeignClient(name = "pricer", path = "/v1/pricer", url = "${ebidding.pricer-service-endpoint}")
public interface PricerClient {
    @GetMapping(path = "/")
    GenericResponse<Double> price();
}
