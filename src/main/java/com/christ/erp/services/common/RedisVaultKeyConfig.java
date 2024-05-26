package com.christ.erp.services.common;

import com.christ.utility.lib.Constants;
import com.christ.utility.lib.caching.CacheUtils;
import org.springframework.stereotype.Component;

@Component
public class RedisVaultKeyConfig {

    public String getServiceKeys(String propertyName) {
        String val= "";
        if(!Utils.isNullOrEmpty(propertyName)) {
            val = CacheUtils.instance.get(Constants.SERVICE_MAP, propertyName);
        }
        return val;
    }
}
