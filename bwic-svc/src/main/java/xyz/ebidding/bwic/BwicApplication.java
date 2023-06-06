package xyz.ebidding.bwic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import xyz.ebidding.common.config.EbiddingConfig;

@SpringBootApplication
@EnableFeignClients(basePackages = {"xyz.ebidding.bid", "xyz.ebidding.pricer"})
public class BwicApplication {

    public static void main(String[] args) {
        SpringApplication.run(BwicApplication.class, args);
    }
}

