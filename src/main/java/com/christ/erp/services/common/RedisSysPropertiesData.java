package com.christ.erp.services.common;

import com.christ.erp.services.transactions.common.CommonApiTransaction;
import com.christ.utility.lib.Constants;
import com.christ.utility.lib.caching.CacheUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;

@Component
public class RedisSysPropertiesData {

    @Autowired
    CommonApiTransaction commonApiTransaction;

    @PostConstruct
    public void setERPPropertiesDataToRedis() {
       /* Mono.fromRunnable(() -> {
            CacheUtils.instance.clearMap("__PROPERTY_MAP_");
            commonApiTransaction.getERPProperties().flatMapMany(Flux::fromIterable).map(tuple -> {
                //System.out.println("____"+tuple.get("is_common_property"));
                if(!Utils.isNullOrEmpty(tuple.get("is_common_property")) && tuple.get("is_common_property").toString().equals("1")
                        && !Utils.isNullOrEmpty(tuple.get("property_name")) &&  !Utils.isNullOrEmpty(tuple.get("property_value"))) {
                    CacheUtils.instance.set("__PROPERTY_MAP_", "_G_"+ tuple.get("property_name").toString(),tuple.get("property_value").toString());
                }
                else {
                    if(!Utils.isNullOrEmpty(tuple.get("property_name")) && !Utils.isNullOrEmpty(tuple.get("property_detail_value"))) {
                        if(!Utils.isNullOrEmpty(tuple.get("erp_location_id"))) {
                            CacheUtils.instance.set("__PROPERTY_MAP_",tuple.get("erp_location_id").toString()+"_L_"+ tuple.get("property_name").toString(),tuple.get("property_detail_value").toString());
                        }
                        else if(!Utils.isNullOrEmpty(tuple.get("erp_campus_id"))) {
                            CacheUtils.instance.set("__PROPERTY_MAP_", tuple.get("erp_campus_id").toString()+"_C_"+tuple.get("property_name").toString(),tuple.get("property_detail_value").toString());
                        }
                    }
                }
                return Mono.empty();
            }).subscribeOn(Schedulers.boundedElastic()).subscribe();
        }).subscribeOn(Schedulers.boundedElastic()).subscribe();*/

        Mono.fromRunnable(() -> {
                    CacheUtils.instance.clearMap(Constants.PROPERTY_MAP);
                    commonApiTransaction.getERPProperties()
                            .flatMapMany(properties -> Flux.fromIterable(properties))
                            .flatMap(tuple -> {
                                if (!Utils.isNullOrEmpty(tuple.get("is_common_property"))
                                        && tuple.get("is_common_property").toString().equals("1")
                                        && !Utils.isNullOrEmpty(tuple.get("property_name"))
                                        && !Utils.isNullOrEmpty(tuple.get("property_value"))) {
                                    return Mono.fromRunnable(() -> CacheUtils.instance.set(Constants.PROPERTY_MAP, Constants.PROPERTY_MAP_G_PREFIX + tuple.get("property_name").toString(), tuple.get("property_value").toString()));
                                } else {
                                    if (!Utils.isNullOrEmpty(tuple.get("property_name")) && !Utils.isNullOrEmpty(tuple.get("property_detail_value"))) {
                                        if (!Utils.isNullOrEmpty(tuple.get("erp_location_id"))) {
                                            return Mono.fromRunnable(() -> CacheUtils.instance.set(Constants.PROPERTY_MAP, tuple.get("erp_location_id").toString() + Constants.PROPERTY_MAP_L_PREFIX + tuple.get("property_name").toString(), tuple.get("property_detail_value").toString()));
                                        } else if (!Utils.isNullOrEmpty(tuple.get("erp_campus_id"))) {
                                            return Mono.fromRunnable(() -> CacheUtils.instance.set(Constants.PROPERTY_MAP, tuple.get("erp_campus_id").toString() + Constants.PROPERTY_MAP_C_PREFIX + tuple.get("property_name").toString(), tuple.get("property_detail_value").toString()));
                                        }
                                    }
                                }
                                return Mono.empty();
                            })
                            .subscribeOn(Schedulers.boundedElastic())
                            .subscribe();
                })
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();



    }

    public String getSysProperties(String propertyName, String lc, Integer lcID) {
        String val= "";
        if(!Utils.isNullOrEmpty(propertyName)) {
            if(Utils.isNullOrEmpty(lc)) {
                   val =  CacheUtils.instance.get(Constants.PROPERTY_MAP,Constants.PROPERTY_MAP_G_PREFIX+ propertyName);
            }
            else if(lc.equalsIgnoreCase("L")) {
                val =  CacheUtils.instance.get(Constants.PROPERTY_MAP, lcID+Constants.PROPERTY_MAP_L_PREFIX+propertyName);
            }
            else if(lc.equalsIgnoreCase("C")) {
                val =  CacheUtils.instance.get(Constants.PROPERTY_MAP,lcID+Constants.PROPERTY_MAP_C_PREFIX+ propertyName);
            }
        }
       return val;
    }

    public void setSysProperties(String propertyName, String propertyValue, String lc, Integer lcID) {
        String val= "";
        if(!Utils.isNullOrEmpty(propertyName)) {
            if(Utils.isNullOrEmpty(lc)) {
                CacheUtils.instance.set(Constants.PROPERTY_MAP,Constants.PROPERTY_MAP_G_PREFIX+ propertyName, propertyValue);
            }
            else if(lc.equalsIgnoreCase("L")) {
                CacheUtils.instance.set(Constants.PROPERTY_MAP, lcID+Constants.PROPERTY_MAP_L_PREFIX+propertyName, propertyValue);
            }
            else if(lc.equalsIgnoreCase("C")) {
                CacheUtils.instance.set(Constants.PROPERTY_MAP,lcID+Constants.PROPERTY_MAP_C_PREFIX+ propertyName, propertyValue);
            }
        }
    }
}
