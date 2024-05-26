package com.christ.erp.services.dto.admission.settings;

import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentApplnDeclarationsDetailsDTO {
    private int id;
    private Boolean isMandatory;
    private Integer declarationDisplayOrder;
    private SelectDTO declarationTemplate;
}
