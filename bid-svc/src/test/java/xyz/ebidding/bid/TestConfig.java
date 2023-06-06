package xyz.ebidding.bid;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import xyz.ebidding.bid.config.ModelMapperConfig;

@Configuration
@Import(ModelMapperConfig.class)
public class TestConfig {
}
