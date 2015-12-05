package com.pochemuto.pikabu;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author Alexander Kramarev (pochemuto@gmail.com)
 * @date 21.11.2015
 */
@Configuration
@PropertySource("classpath:/configuration.properties")
@PropertySource(value = "file:pikabu.properties", ignoreResourceNotFound = true)
public class AppConfig {

    @Bean
    public ObjectMapper jsonMapper() {
        return new ObjectMapper();
    }
}
