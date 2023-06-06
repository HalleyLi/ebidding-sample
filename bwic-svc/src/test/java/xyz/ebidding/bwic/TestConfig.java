package xyz.ebidding.bwic;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
public class TestConfig {

    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }

}

