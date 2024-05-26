package com.christ.erp.services.common;

import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.christ.erp.services.dto.common.ScreenInfoDTO;

public class ServerCacheUtils {
    private static List<String> RestrictedRequestMappings;
    private static Map<String, List<String>> GrantedRequestMappings;
    private static List<ScreenInfoDTO> Screens;

    static {
        /*----- All restricted control details and its visibility from master data -----*/
        ServerCacheUtils.RestrictedRequestMappings = new ArrayList<>();
        /*----- All roles and its granted control access (only based on restricted mappings master data -----*/
        ServerCacheUtils.GrantedRequestMappings = new HashMap<>();
        ServerCacheUtils.Screens = new ArrayList<>();
    }

    public static Mono<List<ScreenInfoDTO>> getScreenConfigDetails() {
        return Utils.monoFromObject(ServerCacheUtils.Screens);
    }
    public static Mono<Boolean> isValidUserAccess(String userId, String requestMapping) {
        boolean valid = false;
        try {
            if(ServerCacheUtils.RestrictedRequestMappings.contains(requestMapping)) {
                List<String> mappings = ServerCacheUtils.GrantedRequestMappings.get(userId);
                if(mappings != null && mappings.contains(requestMapping)) {
                    valid = true;
                }
            } else { valid = true; }
        }
        catch(Exception ex) { }
        return Utils.monoFromObject(valid);
    }
    public static Mono<Boolean> loadFunctionAccess(String userId, List<String> deniedRequestMappings) {
        boolean valid = false;
        try {
            ServerCacheUtils.GrantedRequestMappings.put(userId, deniedRequestMappings);
            valid = true;
        }
        catch(Exception ex) { }
        return Utils.monoFromObject(valid);
    }
    public static Mono<Boolean> loadScreenConfigDetails(List<ScreenInfoDTO> screens) {
        boolean valid = false;
        try {
            ServerCacheUtils.Screens = screens;
            valid = true;
        }
        catch(Exception ex) { }
        return Utils.monoFromObject(valid);
    }
    public static Mono<ScreenInfoDTO> getScreenConfig(String screenId) {
        ScreenInfoDTO info = null;
        try {
            if(ServerCacheUtils.Screens != null) {
                for(int i = 0; i < ServerCacheUtils.Screens.size(); i++) {
                    if(ServerCacheUtils.Screens.get(i).id.compareToIgnoreCase(screenId) == 0) {
                        info = ServerCacheUtils.Screens.get(i);
                        break;
                    }
                }
            }
        }
        catch(Exception ex) { }
        return Utils.monoFromObject(info);
    }
}
