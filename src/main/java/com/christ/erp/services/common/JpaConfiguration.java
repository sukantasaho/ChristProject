package com.christ.erp.services.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
public class JpaConfiguration {

    @Autowired
    DatabaseConfig loadDetails;

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        factoryBean.setJpaVendorAdapter(vendorAdapter);
        factoryBean.setDataSource(dataSource());
        factoryBean.setJpaProperties(jpaProperties());
        String [] packages = {"com.christ.erp.services.dbobjects"};
        factoryBean.setPackagesToScan(packages);
        return factoryBean;
    }


    @Bean
    @Primary
    public DriverManagerDataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUsername(loadDetails.getUsername());
        dataSource.setPassword(loadDetails.getPassword());
        dataSource.setUrl(loadDetails.getUrl());
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        return dataSource;
    }

    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory emf){
        return new JpaTransactionManager(emf);
    }

    private final Properties jpaProperties() {
        Properties jpaProperties = new Properties();
        jpaProperties.setProperty("spring.jpa.hibernate.ddl-auto", "validate");
        jpaProperties.setProperty("spring.jpa.properties.hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        return jpaProperties;
    }
}
