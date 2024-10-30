package com.photoism.photoism_server.config.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateClient {
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        // RestTemplateBuilder를 사용하여 RestTemplate 객체를 생성하고 반환
        // RestTemplateBuilder는 RestTemplate의 설정을 쉽게 구성할 수 있도록 도와줌
        return builder.build();
    }
}
