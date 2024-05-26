package com.christ.erp.services.controllers.common;

import com.christ.erp.services.common.RedisGateway;
import com.christ.erp.services.common.Utils;
import com.christ.erp.services.dto.common.AuthRequestDTO;
import com.christ.erp.services.dto.common.AuthResponseDTO;
import com.christ.erp.services.security.JWTPasswordEncoder;

import com.christ.utility.lib.jwt.JwtUser;
import com.christ.utility.lib.jwt.JwtUtils;
import org.hibernate.reactive.mutiny.Mutiny;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.persistence.Tuple;

@RestController
@RequestMapping(value = "/Public/Security")
public class SecurityApiController {
    //private final String SP_VALIDATE_USER_LOGIN = "erp_sp_validate_user_login";

    @Autowired
    private JWTPasswordEncoder _passwordEncoder;


    @Autowired
    private Mutiny.SessionFactory sessionFactory;

    @Autowired
    RedisGateway redisGateway;

    @RequestMapping(value = "/Auth", method = RequestMethod.POST)
    public Mono<ResponseEntity<?>> auth(@RequestBody AuthRequestDTO request) {
        String str = "select erp_users.erp_users_id,erp_users.erp_users_name,erp_users.user_name,concat(group_concat(erp_users_campus.erp_campus_id),'') as campusIds from erp_users" +
                " left join erp_users_campus on erp_users_campus.erp_users_id = erp_users.erp_users_id and erp_users_campus.record_status = 'A' and erp_users_campus.is_preferred=1" +
                " where erp_users.user_name = :userName and erp_users.passwd = :password and erp_users.record_status = 'A' group by erp_users.erp_users_id";
        Mono<Tuple> tuple = Mono.fromFuture(sessionFactory.withSession(s->s.createNativeQuery(str, Tuple.class)
                        .setParameter("userName",request.loginId).setParameter("password",this._passwordEncoder.encode(request.loginPassword)).getSingleResultOrNull())
                .subscribeAsCompletionStage());
        return tuple.filter(user-> !Utils.isNullOrEmpty(user) && !Utils.isNullOrEmpty(user.get("erp_users_name"))).map(user -> {
            if(!Utils.isNullOrEmpty(user.get("campusIds"))) {
                JwtUser userDTO = new JwtUser();
                userDTO.id = String.valueOf(user.get("erp_users_id"));
                userDTO.name = user.get("erp_users_name").toString();
                String token = JWTUtil.generateToken(userDTO);
                String refreshToken = JWTUtil.generateRefreshToken(userDTO);
                redisGateway.setUserDataToRedis(userDTO.id);//This line should be removed.
                return ResponseEntity.ok(new AuthResponseDTO(token,user.get("campusIds").toString(),refreshToken));
            }
            else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        }).defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @RequestMapping(value = "/isServiceAvailable", method = RequestMethod.POST)
    public ResponseEntity<?> isServiceAvailable() {
        return ResponseEntity.ok(new AuthResponseDTO(true));
    }

    @RequestMapping(value = "/refresh", method = RequestMethod.POST)
    public Mono<ResponseEntity<AuthResponseDTO>> refresh(@RequestBody AuthRequestDTO request) {
        return Mono.justOrEmpty(JwtUtils.getUserFromToken(request.getRefreshToken()))
                .map(jwtUser -> new JwtUser(jwtUser.id,jwtUser.name))
                .filter(jwtUser -> !Utils.isNullOrEmpty(jwtUser.id))
                .map(jwtUser -> JWTUtil.generateToken(jwtUser))
                //.onErrorMap(error -> new RuntimeException(""))
                .map(s -> ResponseEntity.ok(new AuthResponseDTO(s,request.getRefreshToken())))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.BAD_GATEWAY).build());
    }
        /*List<Object> args = new ArrayList<>();
        args.add(request.loginId);
        args.add(this._passwordEncoder.encode(request.loginPassword));
        return DBGateway.executeQuery(SP_VALIDATE_USER_LOGIN, args)
            .map((table) -> {
                if(table != null &&
                    table.getColumnNames() != null &&
                    table.getColumnNames().size() > 0 &&
                    table.getRows() != null &&
                    table.getRows().size() > 0) {
                    JwtUser userDTO = new JwtUser();
                    userDTO.id = table.getRows().get(0).getInt("ID").toString();
                    //userDTO.loginId = table.getRows().get(0).getString("LoginID");
                    userDTO.name = table.getRows().get(0).getString("UserName");
                    //userDTO.role = table.getRows().get(0).getInt("RoleID").toString();
                    //userDTO.role = "nivin";
                    userDTO.preferredCampusIds ="2";
                    String string = JwtUtils.generateToken(userDTO);
                    System.out.println(string);
                    return ResponseEntity.ok(new AuthResponseDTO(string));
                }
                else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                }
            }).defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }*/
}