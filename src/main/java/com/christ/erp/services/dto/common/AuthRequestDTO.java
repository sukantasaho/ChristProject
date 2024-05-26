package com.christ.erp.services.dto.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequestDTO {
    public String loginId;
    public String loginPassword;
    public String name;
    public String email;
    public String password;
    private String otp;
    private String refreshToken;
    private String clientId;
}
