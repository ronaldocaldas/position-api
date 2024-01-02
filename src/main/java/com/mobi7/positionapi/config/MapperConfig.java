package com.mobi7.positionapi.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Model mapper integration
 */
@Configuration
public class MapperConfig {

    /**
     * Customize {@link ModelMapper}
     *
     * @return
     */
    @Bean
    public ModelMapper modelMapper() {
        // Basic setup
        return new ModelMapper();
    }
}


