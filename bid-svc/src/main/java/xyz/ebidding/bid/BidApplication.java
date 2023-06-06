package xyz.ebidding.bid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import xyz.ebidding.common.config.EbiddingConfig;

@SpringBootApplication
@EnableFeignClients(basePackages = {"xyz.ebidding.bwic"})
public class BidApplication {

    public static void main(String[] args) {
        SpringApplication.run(BidApplication.class, args);
    }
}

