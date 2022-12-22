package com.example.kistrading.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public PropertiesMapping propertiesMapping(){
        return new PropertiesMapping();
    }
}
