package com.christ.erp.services.dto.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErpInstitutionDTO {

    private int id;
    private String institutionName;
    private ErpPincodeDTO erpPincodeDTO;
}
