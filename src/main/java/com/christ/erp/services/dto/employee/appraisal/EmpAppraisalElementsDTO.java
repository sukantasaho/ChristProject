package com.christ.erp.services.dto.employee.appraisal;

import com.christ.erp.services.dto.common.ExModelBaseDTO;

public class EmpAppraisalElementsDTO {

    public int id;
    public String elementName;
    public String elementDescription;
    public String elementIdentity;
    public String elementParentIdentity;
    public Integer elementOrder;
    public Integer elementLevel;
    public String isGroupNotDisplayed;
    public ExModelBaseDTO type;
    public ExModelBaseDTO answerOptionSelectionType;
    public ExModelBaseDTO empAppraisalElementsOption;
}
