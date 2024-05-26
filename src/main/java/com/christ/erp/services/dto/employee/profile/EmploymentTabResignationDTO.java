package com.christ.erp.services.dto.employee.profile;

import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;
import org.apache.xpath.operations.Bool;

import java.time.LocalDate;

@Getter
@Setter
public class EmploymentTabResignationDTO {
    private LocalDate dateOfLeaving;
    private LocalDate approvalDate;
    private LocalDate relievingOrderDate;
    private SelectDTO reasonForLeaving;
    private String reasonForLeavingOther;
    private Integer noticePeriodServedDays;
    private LocalDate resignationDate;
    private String recommendation;
    private Boolean isExitInterviewAttended;

}
