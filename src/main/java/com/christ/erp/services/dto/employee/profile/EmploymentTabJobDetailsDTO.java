package com.christ.erp.services.dto.employee.profile;

import com.christ.erp.services.dto.common.SelectDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EmploymentTabJobDetailsDTO {
    private SelectDTO leaveCategory;
    private Boolean vacation;
    private Boolean showInWebsite;
    private Boolean isHolidayWorking;
    private Boolean holidayTimeZoneApplicable;
    private SelectDTO holidayTimeZone;
    private Boolean isRosterAllotmentApplicable;
    private Boolean isPunchingExempted;
    private LocalDate retirementDate;
}
