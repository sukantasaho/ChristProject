package com.christ.erp.services.controllers.common;
import com.christ.erp.services.dto.common.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;

import javax.persistence.EntityManagerFactory;
import java.util.Arrays;
import java.util.Objects;
public class BaseApiController {
   /* @Autowired
    private EntityManagerFactory _entityManagerFactory;
    private final Mono<SecurityContext> _context  = ReactiveSecurityContextHolder.getContext();

    public Mono<String> getUserID() {
        try {

            return this._context
                    .filter(context -> Objects.nonNull(context.getAuthentication()))
                    .map(context -> context.getAuthentication().getPrincipal())
                    .cast(UserDTO.class)
                    .map(userInfo -> userInfo.id);
        }
        catch(Exception ex) { }
        return Utils.monoFromObject("");
    }
    public Mono<UserDTO> getUserInfo() {
        try {
            return this._context
                    .filter(context -> Objects.nonNull(context.getAuthentication()))
                    .map(context -> context.getAuthentication().getPrincipal())
                    .cast(UserDTO.class);
        }
        catch(Exception ex) { }
        return Utils.monoFromObject(new UserDTO());
    }*/
}