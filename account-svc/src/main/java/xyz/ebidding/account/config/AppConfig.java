package xyz.ebidding.account.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import xyz.ebidding.common.config.EbiddingRestConfig;

@Configuration
@EnableAsync
@Import(value = {EbiddingRestConfig.class})
public class AppConfig {
}
