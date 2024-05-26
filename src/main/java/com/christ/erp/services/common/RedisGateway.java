package com.christ.erp.services.common;

import com.christ.erp.services.handlers.common.CommonApiHandler;

import com.christ.erp.services.handlers.common.ScreenConfigHandler;
import com.christ.utility.lib.Constants;
import com.christ.utility.lib.caching.CacheUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import javax.persistence.Cache;
import javax.persistence.Tuple;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class RedisGateway {

    @Autowired
    ScreenConfigHandler screenConfigHandler;

    //@PostConstruct
    public void setUserDataToRedis(String jwtUserId) {
        /*Mono.fromRunnable(() -> {
            Mono<Map<Integer,Map<String,Map<Integer, List<Tuple>>>>> map =  screenConfigHandler.getUserDataForRedis1();
            map.map(userIdMap -> {
                userIdMap.forEach((userId,functionMap) -> {
                    CacheUtils.instance.clearMap("__auth_map_".concat(String.valueOf(userId)));
                    functionMap.forEach((accessToken, campusList) -> {
                        //System.out.println(Constants.AUTH_MAP_PREFIX+userId+"----"+ accessToken+"-----"+ campusList.keySet().stream().map(String::valueOf).collect(Collectors.joining(",")));
                        Mono.fromRunnable(()->{
                            CacheUtils.instance.set("__auth_map_".concat(String.valueOf(userId)), accessToken,campusList.keySet().stream().map(String::valueOf).collect(Collectors.joining(",")));
                        }).publishOn(Schedulers.boundedElastic()).subscribe();
                        //System.out.println("data is" + CacheUtils.instance.get(Constants.AUTH_MAP_PREFIX+userId, accessToken));
                    });
                });
                return Mono.empty();
            }).doFinally(signalType -> {
                screenConfigHandler.getUnAuthMessages().map(errMap-> {
                    errMap.forEach((userId, accessTokenMap) -> {
                        CacheUtils.instance.clearMap(("__authErrMap_".concat(userId)));
                        accessTokenMap.forEach((token, errMsg) -> {
                            if(!Utils.isNullOrEmpty(errMsg)) {
                                Mono.fromRunnable(()-> {
                                    CacheUtils.instance.set("__authErrMap_".concat(userId),token,errMsg);
                                }).publishOn(Schedulers.boundedElastic()).subscribe();

                                //System.out.println("Result is "+userId+"----"+ token.trim()+"--------------"+CacheUtils.instance.get("__authErrMap_".concat(userId),token));
                            }
                        });
                    });
                    return Mono.empty();
                }).publishOn(Schedulers.boundedElastic()).subscribe();*//*.subscribe(*//**//*s-> System.out.println("thread is"+Thread.currentThread().getName())*//**//*);*//*
            }).publishOn(Schedulers.boundedElastic()).subscribe();*//*.subscribe(*//**//*s-> System.out.println("thread is"+Thread.currentThread().getName())*//**//*);*//*
            }).publishOn(Schedulers.boundedElastic()).subscribe();*//*.subscribe(*//**//*s-> System.out.println("thread is"+Thread.currentThread().getName())*//**//*);*/


        Mono.fromRunnable(() -> {
            screenConfigHandler.getUserDataForRedis1(jwtUserId)
                    .flatMap(userIdMap -> Flux.fromIterable(userIdMap.entrySet())
                            .flatMap(entry -> {
                                int userId = entry.getKey();
                                Map<String, Map<Integer, List<Tuple>>> functionMap = entry.getValue();
                                CacheUtils.instance.clearMap(Constants.AUTH_MAP_PREFIX.concat(String.valueOf(userId)));
                                return Flux.fromIterable(functionMap.entrySet())
                                        .flatMap(accessEntry -> {
                                            String accessToken = accessEntry.getKey();
                                            Map<Integer, List<Tuple>> campusList = accessEntry.getValue();
                                            CacheUtils.instance.set(
                                                    Constants.AUTH_MAP_PREFIX.concat(String.valueOf(userId)),
                                                    accessToken,
                                                    campusList.keySet().stream().map(String::valueOf).collect(Collectors.joining(","))
                                            );
                                            //System.out.println("data is" + CacheUtils.instance.get(Constants.AUTH_MAP_PREFIX+userId, accessToken));
                                            return Mono.empty();
                                        })
                                        .publishOn(Schedulers.boundedElastic());
                            })
                            .then(screenConfigHandler.getUnAuthMessages())
                            .flatMap(errMap -> Flux.fromIterable(errMap.entrySet())
                                    .flatMap(errEntry -> {
                                        String userId = errEntry.getKey();
                                        Map<String, String> accessTokenMap = errEntry.getValue();
                                        CacheUtils.instance.clearMap(Constants.AUTH_ERR_MAP_PREFIX.concat(userId));
                                        return Flux.fromIterable(accessTokenMap.entrySet())
                                                .flatMap(tokenEntry -> {
                                                    String token = tokenEntry.getKey();
                                                    String errMsg = tokenEntry.getValue();
                                                    if (!Utils.isNullOrEmpty(errMsg)) {
                                                        return Mono.fromRunnable(() -> {
                                                                    CacheUtils.instance.set(Constants.AUTH_ERR_MAP_PREFIX.concat(userId), token, errMsg);
                                                                })
                                                                .subscribeOn(Schedulers.boundedElastic());
                                                    }
                                                    return Mono.empty();
                                                })
                                                .then();
                                    })
                                    .then())
                            .subscribeOn(Schedulers.boundedElastic()))
                    .subscribe();
        }).subscribeOn(Schedulers.boundedElastic()).subscribe();
    }
}
