package com.christ.erp.services.dto.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponseDTO {
    public String token;
    public boolean validCaptcha;
    public Integer empApplicationRegistrationId;
    public String campusIds;
    private String refreshToken;
    private boolean isServiceAvailable;

    public AuthResponseDTO() {

    }

    public AuthResponseDTO(boolean isServiceAvailable) {
        this.isServiceAvailable = isServiceAvailable;
    }
    public AuthResponseDTO(String token, String campusIds,String refreshToken) {
        this.token = token;
        this.campusIds = campusIds;
        this.refreshToken = refreshToken;
    }

    public AuthResponseDTO(String token, String refreshToken) {
        this.token = token;
        this.refreshToken = refreshToken;
    }
}
