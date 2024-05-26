package com.christ.erp.services.dto.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwsObjectDTO {
    private Boolean isVerified;
    private String payload;
}
