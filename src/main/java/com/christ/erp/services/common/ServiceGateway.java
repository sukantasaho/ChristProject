package com.christ.erp.services.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ServiceGateway {

    @Bean
    public WebClient studentServices() {
        return WebClient.create("http://localhost:8084/");
    }

    @Bean
    public WebClient otherServices() {
        return WebClient.create("http://localhost:8085/");
    }
    
    @Bean
    public WebClient postalDatas() {
    	return WebClient.create("https://api.data.gov.in/resource/5c2f62fe-5afa-4119-a499-fec9d604d5bd?");
    }

    @Bean
    public WebClient sendSMS() {
        return WebClient.create("https://api-alerts.kaleyra.com/v4/?");
    }
}
