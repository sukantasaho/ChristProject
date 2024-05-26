package com.christ.erp.services.dto.employee.appraisal;

import com.christ.erp.services.dto.common.ExModelBaseDTO;

import java.util.List;

public class EmpAppraisalTemplateDTO {

    public int id;
    public String templateName;
    public String templateCode;
    public ExModelBaseDTO appraisalType;
    public ExModelBaseDTO employeeCategory;
    public List<EmpAppraisalElementsDTO> appraisalList;
}
