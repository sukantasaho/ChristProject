package com.christ.erp.services.dto.employee.salary;

import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleComponentsDBO;
import com.christ.erp.services.dbobjects.employee.salary.EmpPayScaleDetailsComponentsDBO;
import com.christ.erp.services.dto.common.LookupItemDTO;

import java.math.BigDecimal;
import java.util.List;

public class AnnualIncrementDTO {
    public String ID;
    public Boolean isIncremented;
    public Boolean sentToApproval;
    public Boolean sentToReview;
    public Boolean isApproved;
    public String empId;
    public String empName;
    public String dateOfjoin;
    public LookupItemDTO department;
    public LookupItemDTO designation;
    public BigDecimal currentWagePerDay;
    public BigDecimal revisedWagePerDay;
    public String statusId;
    public String payScaleType;
    public String dailyWageSlabId;
    public String currentLevelAndCell;
    public LookupItemDTO grade;
    public LookupItemDTO currentLevel;
    public LookupItemDTO currentCell;
    public LookupItemDTO revisedLevel;
    public LookupItemDTO revisedCell;
    public LookupItemDTO location;
    public List<SalaryComponentDTO> allowances;
    public BigDecimal currentTotal;
    public BigDecimal revisedTotal;
    public String percentage;
    public String lastModified;
    public String effectiveDate;
    public List<EmpPayScaleDetailsCommentsDTO> comments;
    public String applnEntryId;
}