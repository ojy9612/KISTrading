package com.example.kistrading.config;

import com.example.kistrading.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BeanConfig {

    private final TokenService tokenService;

    @Bean
    public PropertiesMapping propertiesMapping() {
        return new PropertiesMapping(tokenService);
    }
}
