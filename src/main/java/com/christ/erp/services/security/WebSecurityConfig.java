/*
package com.christ.erp.services.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.config.EnableWebFlux;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFlux
@ComponentScan({"com.christ.erp.services","com.christ.erp.services.controllers","com.christ.erp.services.common"})
public class WebSecurityConfig {


    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .exceptionHandling()
                .authenticationEntryPoint((swe, e) -> {
                    return Mono.fromRunnable(() -> {
                        swe.getResponse().getHeaders().set("Access-Control-Allow-Origin", "*");
                        swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    });
                }).accessDeniedHandler((swe, e) -> {
                    return Mono.fromRunnable(() -> {
                        swe.getResponse().getHeaders().set("Access-Control-Allow-Origin", "*");
                        swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    });
                }).and()
                .cors().disable()
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                //.authenticationManager(this._authenticationManager)
               // .securityContextRepository(this._securityContextRepository)
                .authorizeExchange()
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .pathMatchers(HttpMethod.GET).permitAll()
                .pathMatchers(HttpMethod.POST).permitAll()
                //.pathMatchers("/Security/*").permitAll()
                //.pathMatchers("/ApplicantSecurity/*","/Employee/CommonEmployee/getGenerateLatterofAppointmentPDF","/Employee/Recruitment/GenerateLetterForAppointment/getEmpAppointmentLetter","/Employee/Recruitment/GenerateLetterForAppointment/getGenerateLatterofAppointmentPDF","/Employee/Recruitment/FinalInterviewComments/offerLetter","/Common/ping").permitAll()
                .anyExchange().authenticated()
                .and().build();
    }
}
*/
