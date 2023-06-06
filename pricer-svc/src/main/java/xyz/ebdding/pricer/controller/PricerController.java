package xyz.ebdding.pricer.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.ebidding.common.model.dto.GenericResponse;

import java.util.Random;

@RestController
@RequestMapping("/v1/pricer")
@Validated
@Slf4j
public class PricerController {

    @GetMapping
    public GenericResponse<Double> price() {
        return new GenericResponse<>(100.0 + new Random().nextDouble() * 10);
    }
}
