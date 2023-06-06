package xyz.ebidding.bwic.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import xyz.ebidding.bwic.BwicConstant;
import xyz.ebidding.common.model.dto.BwicDto;
import xyz.ebidding.common.model.dto.GenericResponse;

@FeignClient(name = BwicConstant.SERVICE_NAME, path = "/v1/bwic", url = "${ebidding.bwic-service-endpoint}")
public interface BwicClient {
    @GetMapping(path ="/{id}")
    GenericResponse<BwicDto> getBwic(@PathVariable("id") String id);
}
