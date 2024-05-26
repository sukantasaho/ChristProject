package com.christ.erp.services.common;

import org.hibernate.reactive.mutiny.Mutiny;
import org.hibernate.reactive.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
public class HibernateConfig {


    @Autowired
    private DatabaseConfig loadDetails;

    @Bean("reactive_hibernate")
    public EntityManagerFactory entityManagerFactory() {
        Map<String,String> map = new HashMap<>();
        map.put("javax.persistence.jdbc.url", loadDetails.getUrl());
        map.put("javax.persistence.jdbc.user", loadDetails.getUsername());
        map.put("javax.persistence.jdbc.password", loadDetails.getPassword());
        return Persistence.createEntityManagerFactory("ReactivePU",map);
    }

    @Bean
    public Mutiny.SessionFactory reactiveSessionFactory(final @Qualifier("reactive_hibernate") EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory.unwrap(Mutiny.SessionFactory.class);
    }

    @Bean
    public Stage.SessionFactory futureSessionFactory(final @Qualifier("reactive_hibernate") EntityManagerFactory entityManagerFactory) {
        return entityManagerFactory.unwrap(Stage.SessionFactory.class);
    }

    private static class Wrapper {
        EntityManagerFactory entityManagerFactory;
    }
}
