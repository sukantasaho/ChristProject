package com.christ.erp.services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.reactive.config.EnableWebFlux;
import reactor.core.publisher.Hooks;
import reactor.tools.agent.ReactorDebugAgent;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@EnableWebFlux
@ComponentScan({"com.christ.erp.services","com.christ.erp.services.controllers","com.christ.erp.services.common"})
@SpringBootApplication
public class AdminServicesApplication {
	public static void main(String[] args) {
		//ReactorDebugAgent.init();
		// Setting timeZone globally
		TimeZone.setDefault(TimeZone.getTimeZone("IST"));
		Hooks.onOperatorDebug();
		SpringApplication.run(AdminServicesApplication.class, args);
	}

	//@PostConstruct
	public void init(){

	}
}
