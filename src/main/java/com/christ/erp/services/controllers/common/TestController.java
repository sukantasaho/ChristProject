package com.christ.erp.services.controllers.common;

import com.christ.erp.services.common.RedisSysPropertiesData;
import com.christ.erp.services.common.SysProperties;
import com.christ.erp.services.dto.common.ModuleDTO;
import com.christ.erp.services.handlers.common.TestHandlerWebflux;
import com.christ.erp.services.transactions.common.TestTransactionWebflux;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/Protected/Common/TestScreen")
public class TestController {

    @Autowired
    TestHandlerWebflux testHandlerWebflux;

    @Autowired
    TestTransactionWebflux testTransactionWebflux;


    @PostMapping("/getCountryMap")
    public Mono<Map<Integer, String>> getCountryMap() {
        return testHandlerWebflux.getCountryMap();
    }

    @PostMapping("/getCountryMap1")
    public Mono<String> getCountryMap1() {
        return testHandlerWebflux.getCountryMap1();
    }

    @PostMapping("/getScreenList")
    public Mono<List<ModuleDTO>> getScreenList() {
        return testHandlerWebflux.getScreenList();
    }
    @PostMapping("/dateFunctions")
    public void dateFunctions() {
         testHandlerWebflux.dateTimeFunctions();
    }

    @PostMapping("/testMethod")
    public void checkException() {
       // return Flux.just("hello","world").single().subscribeOn(Schedulers.boundedElastic());
        testTransactionWebflux.testMethod();
    }

    @Autowired
    RedisSysPropertiesData redisSysPropertiesData;

    @PostMapping(value = "/sysProperties")
    public void sysProperties() {
        String str = redisSysPropertiesData.getSysProperties(SysProperties.ACCOUNT.name(), "C",1);
        String str1 = redisSysPropertiesData.getSysProperties(SysProperties.ACCOUNT.name(), "C",2);
        String str2 = redisSysPropertiesData.getSysProperties(SysProperties.UNIVERSITY_NAME.name(),null,null);
        System.out.println(str+"_____"+str1+"_____"+str2);
    }


}
