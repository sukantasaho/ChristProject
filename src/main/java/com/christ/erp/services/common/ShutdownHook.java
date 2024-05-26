package com.christ.erp.services.common;

import com.christ.utility.lib.caching.CacheUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

//@Component
public class ShutdownHook implements DisposableBean {
        @Override
        public void destroy() {
            // System.out.println("Shutdown hook");
            //CacheUtils.instance.clearAllMap();
        }
}
