package xyz.ebidding.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import xyz.ebidding.common.error.GlobalExceptionTranslator;

/**
 * Use this common config for Rest API
 */
@Configuration
@Import(value = {EbiddingConfig.class, GlobalExceptionTranslator.class})
public class EbiddingRestConfig {
}
