package com.christ.erp.services.dto.employee.salary;

import com.christ.erp.services.dto.common.LookupItemDTO;

import java.util.List;

public class AnnualIncrementSubmitDTO {
    public List<LookupItemDTO> reviewerOrApproversArray;
    public List<AnnualIncrementDTO> data;
}
