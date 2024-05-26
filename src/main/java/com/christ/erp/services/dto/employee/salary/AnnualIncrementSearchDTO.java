package com.christ.erp.services.dto.employee.salary;

import com.christ.erp.services.dto.common.LookupItemDTO;

import java.math.BigInteger;
import java.util.List;

public class AnnualIncrementSearchDTO {
    public LookupItemDTO employeeCategory;
    public LookupItemDTO location;
    public LookupItemDTO department;
    public LookupItemDTO designation;
    public LookupItemDTO jobCategory;
    public List<LookupItemDTO> campus;
    public LookupItemDTO payScaleType;
    public LookupItemDTO status;
    public BigInteger notificationsLength;
}