package com.christ.erp.services.dto.common;

import java.util.List;
import java.util.Set;

import com.christ.erp.services.dto.admission.applicationprocess.AdmWeightageDefinitionDetailDTO;
import com.christ.erp.services.dto.admission.applicationprocess.AdmWeightageDefinitionGeneralDTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErpGenderDTO {
	
    private Integer erpGenderId;
    private String genderName;
}
